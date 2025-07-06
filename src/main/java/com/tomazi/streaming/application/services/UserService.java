package com.tomazi.streaming.application.services;

import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.domain.entities.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(String username, String email, String password, String firstName, String lastName, UserRole role);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User updateProfile(Long userId, String firstName, String lastName);

    User changePassword(Long userId, String currentPassword, String newPassword);

    void activateUser(Long userId);

    void deactivateUser(Long userId);

    Page<User> findAllUsers(Pageable pageable);

    Page<User> searchUsersByName(String name, Pageable pageable);

    List<User> findByRole(UserRole role);

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email);

    boolean authenticateUser(String username, String password);
}
