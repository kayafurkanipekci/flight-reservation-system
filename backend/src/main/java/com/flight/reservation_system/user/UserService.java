package com.flight.reservation_system.user;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<DtoUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(DtoUserResponse::fromEntity)
                .toList();
    }

    public DtoUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return DtoUserResponse.fromEntity(user);
    }
}