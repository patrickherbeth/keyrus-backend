package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginDTO {

    // Getters e Setters
    private String username;
    private String password;

    // Construtor padrão
    public LoginDTO() {}

    // Construtor com parâmetros
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
