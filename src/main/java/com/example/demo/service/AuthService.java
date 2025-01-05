package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String password) {
        String passwordHash = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        return userRepository.save(user);
    }

    // Método para realizar o login
    public boolean login(String username, String password) {
        // Buscar o usuário pelo nome de usuário
        User user = userRepository.findByUsername(username);
        if (user == null) {
            // Se o usuário não existir
            return false;
        }

        // Comparar a senha informada com a senha criptografada no banco de dados
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}
