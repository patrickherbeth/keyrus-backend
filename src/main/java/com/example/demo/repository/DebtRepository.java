package com.example.demo.repository;

import com.example.demo.model.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUserId(Long userId);

    // DebtRepository.java
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Debt d")
    BigDecimal getTotalDebt();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Debt d WHERE d.status = 'Pago'")
    BigDecimal getTotalPaidDebt();

    @Query("SELECT COUNT(d) FROM Debt d")
    long getTotalDebtsCount();

    @Query("SELECT COUNT(d) FROM Debt d WHERE d.status = 'Pago'")
    long getPaidDebtsCount();

}
