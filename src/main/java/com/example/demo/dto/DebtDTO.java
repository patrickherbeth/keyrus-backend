package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DebtDTO {

    private String title;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status; // Pendente, Pago, Atrasado
    private String observations;
    private Long userId; // Representa o ID do usuário
}
