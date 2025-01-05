package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;

}