package com.ms.user_service.service;

import com.ms.user_service.dto.LoginRequest;
import com.ms.user_service.dto.PasswordUpdateRequest;
import com.ms.user_service.dto.RegisterRequest;
import com.ms.user_service.dto.UserUpdateRequest;
import com.ms.user_service.entity.User;
import com.ms.user_service.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User registerUser(RegisterRequest request) {
        logger.debug("Attempting to register new user: {}", request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username {} already exists.", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setStatus("active");
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User {} registered successfully with ID {}.", savedUser.getUsername(), savedUser.getUserId());
        return savedUser;
    }

    public String login(LoginRequest request) {
        logger.debug("Attempting to login user: {}", request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        logger.info("User {} logged in successfully.", user.getUsername());
        return token;
    }
    
    public User getUserById(Long userId) {
        logger.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });
    }

    public List<User> getAllUsers() {
        logger.debug("Fetching all users.");
        return userRepository.findAll();
    }

    public User updateUser(Long userId, UserUpdateRequest request) {
        logger.debug("Updating user with ID: {}", userId);
        User user = getUserById(userId);
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        User updatedUser = userRepository.save(user);
        logger.info("User {} updated successfully.", updatedUser.getUsername());
        return updatedUser;
    }

    public void deleteUser(Long userId) {
        logger.debug("Deleting user with ID: {}", userId);
        userRepository.deleteById(userId);
        logger.info("User with ID {} deleted successfully.", userId);
    }

    public void updateUserPassword(Long userId, PasswordUpdateRequest request) {
        logger.debug("Updating password for user with ID: {}", userId);
        User user = getUserById(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            logger.warn("Password update failed for user {}: Invalid old password.", user.getUsername());
            throw new RuntimeException("Invalid old password");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("Password updated successfully for user {}.", user.getUsername());
    }

    public String getUserStatus(Long userId) {
        logger.debug("Fetching status for user with ID: {}", userId);
        return getUserById(userId).getStatus();
    }
}
