package com.example.demo.integration;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
        // Limpar o banco de dados antes de cada teste
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Dados de entrada
        String username = "testUser" + System.currentTimeMillis(); // Garante um nome único
        String password = "password123";

        // Chamada do serviço
        User registeredUser = authService.register(username, password);

        // Verificações
        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());
        assertEquals(username, registeredUser.getUsername());
        assertTrue(passwordEncoder.matches(password, registeredUser.getPasswordHash()));
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Preparação: criar um usuário
        String username = "loginUser" + System.currentTimeMillis(); // Garante um nome único
        String password = "securePassword";
        authService.register(username, password);

        // Chamada do serviço
        boolean loginResult = authService.login(username, password);

        // Verificações
        assertTrue(loginResult);
    }

    @Test
    void shouldFailLoginWithInvalidPassword() {
        // Preparação: criar um usuário
        String username = "invalidPasswordUser" + System.currentTimeMillis(); // Garante um nome único
        String password = "validPassword";
        authService.register(username, password);

        // Chamada do serviço
        boolean loginResult = authService.login(username, "wrongPassword");

        // Verificações
        assertFalse(loginResult);
    }
}
