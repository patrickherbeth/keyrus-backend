package com.example.demo.service;

import com.example.demo.dto.DebtDTO;
import com.example.demo.model.Debt;
import com.example.demo.model.User;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DebtService {

    @Autowired
    private  DebtRepository debtRepository;

    @Autowired
    private UserRepository userRepository;

    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    public Debt createDebt(DebtDTO debtDTO) {
        User user = userRepository.findById(debtDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Debt debt = new Debt();
        debt.setTitle(debtDTO.getTitle());
        debt.setAmount(debtDTO.getAmount());
        debt.setDueDate(debtDTO.getDueDate());
        debt.setStatus(debtDTO.getStatus());
        debt.setObservations(debtDTO.getObservations());
        debt.setUser(user);
        debt.setCreatedAt(LocalDateTime.now());

        return debtRepository.save(debt);
    }

    public List<Debt> getAllDebts(Long userId) {
        return debtRepository.findByUserId(userId);
    }

    public BigDecimal calculateTotalDebt(Long userId) {
        return debtRepository.findByUserId(userId).stream()
                .map(Debt::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deleteDebt(Long id) {
        debtRepository.deleteById(id);
    }

    public Debt updateDebt(Long id, Debt updatedDebt) {
        return debtRepository.findById(id).map(debt -> {
            debt.setTitle(updatedDebt.getTitle());
            debt.setAmount(updatedDebt.getAmount());
            debt.setDueDate(updatedDebt.getDueDate());
            debt.setStatus(updatedDebt.getStatus());
            debt.setObservations(updatedDebt.getObservations());
            return debtRepository.save(debt);
        }).orElseThrow(() -> new RuntimeException("Debt not found"));
    }

    public Map<String, Object> getDashboardSummary() {
        BigDecimal totalDebt = debtRepository.getTotalDebt();
        BigDecimal totalPaidDebt = debtRepository.getTotalPaidDebt();
        BigDecimal totalPendingDebt = totalDebt.subtract(totalPaidDebt);

        long totalDebtsCount = debtRepository.getTotalDebtsCount();
        long paidDebtsCount = debtRepository.getPaidDebtsCount();
        long pendingDebtsCount = totalDebtsCount - paidDebtsCount;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDebt", totalDebt);
        summary.put("totalPaidDebt", totalPaidDebt);
        summary.put("totalPendingDebt", totalPendingDebt);
        summary.put("totalDebtsCount", totalDebtsCount);
        summary.put("paidDebtsCount", paidDebtsCount);
        summary.put("pendingDebtsCount", pendingDebtsCount);

        return summary;
    }
}