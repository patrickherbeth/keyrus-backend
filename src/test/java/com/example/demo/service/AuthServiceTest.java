package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveUserWithHashedPasswordWhenRegisterIsCalled() {
        // Dados de entrada
        String username = "testuser";
        String password = "password123";

        // Mock do comportamento do encoder e repositório
        String passwordHash = "$2a$10$hashedPassword";
        when(passwordEncoder.encode(password)).thenReturn(passwordHash);

        User savedUser = new User();
        savedUser.setUsername(username);
        savedUser.setPasswordHash(passwordHash);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Chamada do método
        User result = authService.register(username, password);

        // Verificações
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(passwordHash, result.getPasswordHash());
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnTrueWhenCredentialsAreCorrectOnLogin() {
        // Dados de entrada
        String username = "testuser";
        String password = "password123";

        // Mock do comportamento do repositório e encoder
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("$2a$10$hashedPassword");
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPasswordHash())).thenReturn(true);

        // Chamada do método
        boolean result = authService.login(username, password);

        // Verificações
        assertTrue(result);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, user.getPasswordHash());
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistOnLogin() {
        // Dados de entrada
        String username = "unknownuser";
        String password = "password123";

        // Mock do comportamento do repositório
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Chamada do método
        boolean result = authService.login(username, password);

        // Verificações
        assertFalse(result);
        verify(userRepository).findByUsername(username);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatchOnLogin() {
        // Dados de entrada
        String username = "testuser";
        String password = "wrongpassword";

        // Mock do comportamento do repositório e encoder
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("$2a$10$hashedPassword");
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPasswordHash())).thenReturn(false);

        // Chamada do método
        boolean result = authService.login(username, password);

        // Verificações
        assertFalse(result);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, user.getPasswordHash());
    }
}
