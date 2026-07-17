# Flight Reservation System

A backend flight reservation system built with Spring Boot, PostgreSQL, Redis, and JWT authentication. The frontend is a small React app that talks to the API.

## Tech Stack

**Backend**
- Java 21, Spring Boot 4.1.0
- Spring Data JPA + Hibernate
- PostgreSQL 18 (via Flyway migrations)
- Redis (caching)
- Spring Security + JWT (jjwt)
- Bean Validation
- Lombok
- Testcontainers + JUnit 5 (integration tests)
- springdoc-openapi (Swagger UI)

**Frontend**
- React + Vite + TypeScript
- Tailwind CSS
- React Router

**Infra**
- Docker Compose (API, Postgres, Redis, pgAdmin)

## Domain Model

- **Airport** — name, IATA code (unique), city, country
- **Airplane** — model, tail number (unique), capacity, airline
- **User** — email (unique), password (BCrypt hashed), name, phone, role (ADMIN / PASSENGER)
- **Flight** — flight number (unique), airplane, departure airport, arrival airport, departure/arrival time, status
- **Reservation** — passenger, status (a "header" that groups one or more tickets)
- **Ticket** — links a Reservation to a Flight, with a seat number. This is the join table between Reservation and Flight, so a single reservation can hold tickets for more than one flight (e.g. a connecting trip). A seat can only be booked once per flight (`UNIQUE(flight_id, seat_number)`).

Schema is versioned with Flyway migrations (V1–V8), covering airports, airplanes, seed data, users, flights, reservations, tickets, and a migration that moved existing user passwords to BCrypt hashes.

## Auth & Security

- JWT-based auth. `POST /api/auth/register` and `POST /api/auth/login` return a Bearer token.
- Passwords are hashed with BCrypt.
- Role-based access control with `@PreAuthorize`: ADMIN can manage airports, airplanes, and flights (create/update/delete); PASSENGER can only read them. Both can create/view their own reservations; only the owner (or an ADMIN) can cancel a reservation.
- Unauthenticated requests get `401`. Authenticated but not-allowed requests get `403`.
- All non-auth endpoints require a valid token.

## API Docs

Swagger UI is available at `http://localhost:8081/swagger-ui.html` once the API is running. Bearer token auth can be entered directly in Swagger.

## Redis Caching

`GET /api/airports` and `GET /api/airplanes` (the list endpoints) are cached in Redis. The cache is invalidated (evicted) whenever a create, update, or delete happens on that resource, so the cache never serves stale data.

Cache keys used: `airports::allAirports`, `airplanes::allAirplanes`.

### Benchmark (Airport listing, 10 runs each)

| Scenario | Avg response time |
|---|---|
| Without cache (cache cleared before each call) | ~23.75 ms |
| With cache (cache already warm) | ~22.62 ms |

The difference is small here because the test dataset only has 5 airport rows — the database query itself is already very fast (microseconds), so most of the measured time comes from network/serialization overhead that happens either way, not from the database itself. The cache was verified directly with `redis-cli` (key appears after a GET, disappears after eviction on write), so it is working correctly — the benefit would be much larger with a bigger dataset or heavier queries (joins, filters).

## Running the Project

1. Copy `.env.example` to `.env` and fill in real values (DB password, JWT secret).
2. From the project root:
   ```
   docker compose up --build -d
   ```
   This starts the API, PostgreSQL, Redis, and pgAdmin. Flyway migrations run automatically on startup.
3. API: `http://localhost:8081`
4. Swagger: `http://localhost:8081/swagger-ui.html`
5. Frontend (separate, run manually during development):
   ```
   cd frontend
   npm install
   npm run dev
   ```
   Runs at `http://localhost:5173`.

## Environment Variables

See `.env.example` for the full list (DB credentials, JWT secret/expiration). Nothing sensitive is committed to the repo.

## Testing

Integration tests use Testcontainers to spin up a real PostgreSQL container (not an in-memory fake), so tests run against the same database engine used in production.

- `AirportRepositoryTest`, `AirplaneRepositoryTest` — CRUD + unique constraint checks
- `AuthIntegrationTest` — register/login flow
- `ReservationLifecycleIntegrationTest` — full reservation flow: create, double-booking conflict, view, cancel, cancel-again conflict
- `RbacIntegrationTest` — role checks on write endpoints (passenger forbidden, admin allowed, no token unauthorized)

Run all tests:
```
cd backend
.\mvnw.cmd clean test
```

---

## Appendix

Redis outputs here:

With redis:
```
PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 30
Ticks             : 307172
TotalDays         : 3,55523148148148E-07
TotalHours        : 8,53255555555556E-06
TotalMinutes      : 0,000511953333333333
TotalSeconds      : 0,0307172
TotalMilliseconds : 30,7172

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 23
Ticks             : 237486
TotalDays         : 2,74868055555556E-07
TotalHours        : 6,59683333333333E-06
TotalMinutes      : 0,00039581
TotalSeconds      : 0,0237486
TotalMilliseconds : 23,7486

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 23
Ticks             : 230462
TotalDays         : 2,66738425925926E-07
TotalHours        : 6,40172222222222E-06
TotalMinutes      : 0,000384103333333333
TotalSeconds      : 0,0230462
TotalMilliseconds : 23,0462

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 25
Ticks             : 255812
TotalDays         : 2,96078703703704E-07
TotalHours        : 7,10588888888889E-06
TotalMinutes      : 0,000426353333333333
TotalSeconds      : 0,0255812
TotalMilliseconds : 25,5812

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 18
Ticks             : 184295
TotalDays         : 2,13304398148148E-07
TotalHours        : 5,11930555555556E-06
TotalMinutes      : 0,000307158333333333
TotalSeconds      : 0,0184295
TotalMilliseconds : 18,4295

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 19
Ticks             : 193030
TotalDays         : 2,23414351851852E-07
TotalHours        : 5,36194444444444E-06
TotalMinutes      : 0,000321716666666667
TotalSeconds      : 0,019303
TotalMilliseconds : 19,303

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 19
Ticks             : 193946
TotalDays         : 2,24474537037037E-07
TotalHours        : 5,38738888888889E-06
TotalMinutes      : 0,000323243333333333
TotalSeconds      : 0,0193946
TotalMilliseconds : 19,3946

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 18
Ticks             : 188734
TotalDays         : 2,1844212962963E-07
TotalHours        : 5,24261111111111E-06
TotalMinutes      : 0,000314556666666667
TotalSeconds      : 0,0188734
TotalMilliseconds : 18,8734

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 18
Ticks             : 187414
TotalDays         : 2,16914351851852E-07
TotalHours        : 5,20594444444444E-06
TotalMinutes      : 0,000312356666666667
TotalSeconds      : 0,0187414
TotalMilliseconds : 18,7414

PS D:\VSCode\intership\flight-reservation-system> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 28
Ticks             : 283464
TotalDays         : 3,28083333333333E-07
TotalHours        : 7,874E-06
TotalMinutes      : 0,00047244
TotalSeconds      : 0,0283464
TotalMilliseconds : 28,3464
```

Without redis:

```
PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 26
Ticks             : 265754
TotalDays         : 3,07585648148148E-07
TotalHours        : 7,38205555555556E-06
TotalMinutes      : 0,000442923333333333
TotalSeconds      : 0,0265754
TotalMilliseconds : 26,5754

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 21
Ticks             : 218990
TotalDays         : 2,53460648148148E-07
TotalHours        : 6,08305555555556E-06
TotalMinutes      : 0,000364983333333333
TotalSeconds      : 0,021899
TotalMilliseconds : 21,899

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 24
Ticks             : 247112
TotalDays         : 2,86009259259259E-07
TotalHours        : 6,86422222222222E-06
TotalMinutes      : 0,000411853333333333
TotalSeconds      : 0,0247112
TotalMilliseconds : 24,7112

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 23
Ticks             : 230478
TotalDays         : 2,66756944444444E-07
TotalHours        : 6,40216666666667E-06
TotalMinutes      : 0,00038413
TotalSeconds      : 0,0230478
TotalMilliseconds : 23,0478

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 22
Ticks             : 228121
TotalDays         : 2,64028935185185E-07
TotalHours        : 6,33669444444444E-06
TotalMinutes      : 0,000380201666666667
TotalSeconds      : 0,0228121
TotalMilliseconds : 22,8121

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 22
Ticks             : 224253
TotalDays         : 2,59552083333333E-07
TotalHours        : 6,22925E-06
TotalMinutes      : 0,000373755
TotalSeconds      : 0,0224253
TotalMilliseconds : 22,4253

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 24
Ticks             : 249497
TotalDays         : 2,88769675925926E-07
TotalHours        : 6,93047222222222E-06
TotalMinutes      : 0,000415828333333333
TotalSeconds      : 0,0249497
TotalMilliseconds : 24,9497

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 22
Ticks             : 225162
TotalDays         : 2,60604166666667E-07
TotalHours        : 6,2545E-06
TotalMinutes      : 0,00037527
TotalSeconds      : 0,0225162
TotalMilliseconds : 22,5162

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 25
Ticks             : 252782
TotalDays         : 2,92571759259259E-07
TotalHours        : 7,02172222222222E-06
TotalMinutes      : 0,000421303333333333
TotalSeconds      : 0,0252782
TotalMilliseconds : 25,2782

PS D:\VSCode\intership\flight-reservation-system> docker exec -it flight-redis redis-cli DEL "airports::allAirports"
>> Measure-Command { curl.exe -s -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmbGlnaHQuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzg0MDk5MTkxLCJleHAiOjE3ODQxODU1OTF9.M3T6vCo37NCV1tnywagmjtE7aKPswn3cC4xfvgmP29g" http://localhost:8081/api/airports }
(integer) 1

What's next:
    Try Docker Debug for seamless, persistent debugging tools in any container or image → docker debug flight-redis
    Learn more at https://docs.docker.com/go/debug-cli/

Days              : 0
Hours             : 0
Minutes           : 0
Seconds           : 0
Milliseconds      : 23
Ticks             : 232810
TotalDays         : 2,69456018518519E-07
TotalHours        : 6,46694444444444E-06
TotalMinutes      : 0,000388016666666667
TotalSeconds      : 0,023281
TotalMilliseconds : 23,281
```
