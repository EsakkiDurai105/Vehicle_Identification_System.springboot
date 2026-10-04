package com.vehicle.service;

import com.vehicle.model.UserAccount;
import com.vehicle.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount register(String username, String password) {
        String normalizedUsername = username.trim();
        if (normalizedUsername.length() < 3 || normalizedUsername.length() > 32) {
            throw new IllegalArgumentException("Username must be between 3 and 32 characters.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must be between 8 and 72 bytes.");
        }
        if (userAccountRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new IllegalArgumentException("That username is already registered.");
        }

        return userAccountRepository.save(new UserAccount(normalizedUsername, passwordEncoder.encode(password)));
    }

    public boolean authenticate(String username, String password) {
        return userAccountRepository.findByUsernameIgnoreCase(username.trim())
                .map(account -> passwordEncoder.matches(password, account.getPasswordHash()))
                .orElse(false);
    }
}