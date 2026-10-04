package com.vehicle.service;

import com.vehicle.model.UserAccount;
import com.vehicle.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAccountServiceTest {

    private final UserAccountRepository repository = mock(UserAccountRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserAccountService service = new UserAccountService(repository, encoder);

    @Test
    void registrationNormalizesUsernameAndStoresHashedPassword() {
        when(repository.existsByUsernameIgnoreCase("newdriver")).thenReturn(false);
        when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount account = service.register(" newdriver ", "secure-pass-1");

        assertEquals("newdriver", account.getUsername());
        assertNotEquals("secure-pass-1", account.getPasswordHash());
        assertTrue(encoder.matches("secure-pass-1", account.getPasswordHash()));
    }

    @Test
    void loginAcceptsRegisteredPasswordAndRejectsIncorrectPassword() {
        UserAccount account = new UserAccount("newdriver", encoder.encode("secure-pass-1"));
        when(repository.findByUsernameIgnoreCase("newdriver")).thenReturn(Optional.of(account));

        assertTrue(service.authenticate("newdriver", "secure-pass-1"));
        assertFalse(service.authenticate("newdriver", "wrong-password"));
    }

    @Test
    void registrationRejectsDuplicateUsername() {
        when(repository.existsByUsernameIgnoreCase("newdriver")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.register("newdriver", "secure-pass-1"));
        verify(repository, never()).save(any(UserAccount.class));
    }
}