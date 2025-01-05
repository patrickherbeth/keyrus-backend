package com.example.demo.controller;


import com.example.demo.dto.DebtDTO;
import com.example.demo.model.Debt;
import com.example.demo.service.DebtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debts")
@CrossOrigin(origins = "*")
public class DebtController {
    private final DebtService debtService;

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @PostMapping("/create")
    public ResponseEntity<Debt> createDebt(@RequestBody DebtDTO debtDTO) {
        Debt createdDebt = debtService.createDebt(debtDTO);
        return ResponseEntity.ok(createdDebt);
    }

    @GetMapping("/all-bts/{userId}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<Debt>> getAllDebts(@PathVariable Long userId) {
        List<Debt> debts = debtService.getAllDebts(userId);
        return ResponseEntity.ok(debts);
    }

    @DeleteMapping("/delete/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> deleteDebt(@PathVariable Long id) {
        debtService.deleteDebt(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Debt> updateDebt(@PathVariable Long id, @RequestBody Debt debt) {
        Debt updatedDebt = debtService.updateDebt(id, debt);
        return ResponseEntity.ok(updatedDebt);
    }

    @GetMapping("/dashboard-summary")
    @CrossOrigin(origins = "*")
    public Map<String, Object> getDashboardSummary() {
        return debtService.getDashboardSummary();
    }
}
