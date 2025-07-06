package com.tomazi.streaming.application.services.impl;

import com.tomazi.streaming.application.services.UserService;
import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.domain.entities.UserRole;
import com.tomazi.streaming.domain.repositories.UserRepository;
import com.tomazi.streaming.infrastructure.exceptions.BusinessException;
import com.tomazi.streaming.infrastructure.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String username, String email, String password, String firstName, String lastName, UserRole role) {
        validateUserCreation(username, email);

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, encodedPassword, firstName, lastName, role);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateProfile(Long userId, String firstName, String lastName) {
        User user = getUserById(userId);
        user.updateProfile(firstName, lastName);
        return userRepository.save(user);
    }

    @Override
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);

        return userRepository.save(user);
    }

    @Override
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.activate();
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.deactivate();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsersByName(String name, Pageable pageable) {
        return userRepository.findByNameContaining(name, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    private void validateUserCreation(String username, String email) {
        if (!isUsernameAvailable(username)) {
            throw new BusinessException("Username already exists: " + username);
        }

        if (!isEmailAvailable(email)) {
            throw new BusinessException("Email already exists: " + email);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}
