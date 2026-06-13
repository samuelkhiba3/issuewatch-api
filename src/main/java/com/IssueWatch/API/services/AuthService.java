package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.LoginRequest;
import com.IssueWatch.API.dto.request.RegisterRequest;
import com.IssueWatch.API.dto.response.MessageResponse;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.BadRequestException;
import com.IssueWatch.API.exceptions.ResourceNotFoundException;
import com.IssueWatch.API.repositories.RoleRepository;
import com.IssueWatch.API.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword,
                Set.of(userRole)
        );

        userRepository.save(user);

        return new MessageResponse("User registered successfully");
    }

    public MessageResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new BadRequestException("Account is disabled");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new BadRequestException("Invalid email or password");
        }

        return new MessageResponse("Login successful");
    }

}
