package com.flight.reservation_system.config;

import java.util.List;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.flight.reservation_system.airplane.DtoAirplaneResponse;
import com.flight.reservation_system.airport.DtoAirportResponse;

/**
 * Per-cache, type-safe JSON serializers for Redis.
 *
 * <p>We don't use {@code GenericJackson2JsonRedisSerializer} here: its built-in default-typing
 * strategy (wrapper-array) collides with newer Jackson defaults when reading the values back,
 * surfacing as
 * "Could not read JSON: Unexpected token (START_OBJECT), expected VALUE_STRING: need String,
 * Number of Boolean value that contains type id".
 *
 * <p>Instead, each cache region gets a {@link Jackson2JsonRedisSerializer} bound to a concrete
 * {@link JavaType} ({@code List<DtoAirportResponse>}, {@code List<DtoAirplaneResponse>}). No
 * polymorphic typing is needed because the cache value type is known at compile time, so a simple
 * JSON envelope is written and read back without type discriminators. This is both faster and
 * stable across Jackson / Spring Data Redis upgrades.
 *
 * <p>Eviction policy is enforced by the service layer via {@code @CacheEvict} on
 * create / update / delete, so the cache never serves a stale listing.
 */
@Configuration
@EnableCaching // production'da da, test'te de caching açık. BaseIntegrationTest'te Redis container ayağa kaldırılıyor.
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        ObjectMapper mapper = cacheObjectMapper();

        Jackson2JsonRedisSerializer<List<DtoAirportResponse>> airportsSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, listType(mapper, DtoAirportResponse.class));
        Jackson2JsonRedisSerializer<List<DtoAirplaneResponse>> airplanesSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, listType(mapper, DtoAirplaneResponse.class));

        RedisCacheConfiguration defaults = baseConfiguration()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(airplanesSerializer));

        return builder -> builder
                .cacheDefaults(defaults)
                .withCacheConfiguration("airports", baseConfiguration()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(airportsSerializer)));
    }

    private RedisCacheConfiguration baseConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()));
    }

    private static ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Local-date / LocalDateTime will be serialized as ISO-8601 strings if a DTO ever pulls
        // them in. Cheap to register up-front and avoids "Java 8 date" surprises later.
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private static <T> JavaType listType(ObjectMapper mapper, Class<T> element) {
        return mapper.getTypeFactory().constructCollectionType(List.class, element);
    }

    // kept for any future cache region that wants polymorphic handling; intentionally unused now
    @SuppressWarnings("unused")
    private static PolymorphicTypeValidator restrictivePolymorphicValidator() {
        return BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.flight.reservation_system.")
                .allowIfSubType("java.util.")
                .allowIfSubType("java.lang.")
                .build();
    }
}
