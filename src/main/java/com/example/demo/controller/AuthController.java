package com.example.demo.controller;


import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
        User user = authService.register(userDTO.getUsername(), userDTO.getPassword());
        return ResponseEntity.ok(user);
    }


    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginRequest) {
        boolean isAuthenticated = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (isAuthenticated) {
            // Buscar o usuário autenticado
            User user = userRepository.findByUsername(loginRequest.getUsername());
            if (user != null) {
                // Criar o DTO
                UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getPasswordHash(), user.getCreatedAt());
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.status(401).body(null);
            }
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

}
