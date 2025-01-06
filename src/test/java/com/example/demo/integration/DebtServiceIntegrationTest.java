package com.example.demo.integration;

import com.example.demo.dto.DebtDTO;
import com.example.demo.model.Debt;
import com.example.demo.model.User;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DebtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class DebtServiceIntegrationTest {

    @Autowired
    private DebtService debtService;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        // Limpar dados para evitar conflitos entre testes
        debtRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateDebtSuccessfully() {
        // Preparação: criar um usuário com username único
        User user = new User();
        user.setUsername("debtUser_" + System.currentTimeMillis()); // Username único
        user.setPasswordHash("hashedPassword");
        User savedUser = userRepository.save(user);

        // Dados de entrada
        DebtDTO debtDTO = new DebtDTO();
        debtDTO.setUserId(savedUser.getId());
        debtDTO.setTitle("Test Debt");
        debtDTO.setAmount(BigDecimal.valueOf(150));
        debtDTO.setDueDate(LocalDate.now().plusDays(7));
        debtDTO.setStatus("Pendente"); // Valor corrigido para atender à restrição
        debtDTO.setObservations("Test observation");

        // Chamada do serviço
        Debt createdDebt = debtService.createDebt(debtDTO);

        // Verificações
        assertNotNull(createdDebt);
        assertEquals("Test Debt", createdDebt.getTitle());
        assertEquals(BigDecimal.valueOf(150), createdDebt.getAmount());
        assertEquals(savedUser.getId(), createdDebt.getUser().getId());
    }

    @Test
    void shouldReturnAllDebtsForUser() {
        // Preparação: criar um usuário e dívidas
        User user = new User();
        user.setUsername("allDebtsUser_" + System.currentTimeMillis()); // Username único
        user.setPasswordHash("hashedPassword");
        User savedUser = userRepository.save(user);

        Debt debt1 = new Debt(null, "Debt 1", BigDecimal.valueOf(300), LocalDate.now(), "Pendente", null, savedUser, LocalDateTime.now());
        Debt debt2 = new Debt(null, "Debt 2", BigDecimal.valueOf(500), LocalDate.now(), "Pago", null, savedUser, LocalDateTime.now());

        debtRepository.save(debt1);
        debtRepository.save(debt2);

        // Chamada do serviço
        List<Debt> debts = debtService.getAllDebts(savedUser.getId());

        // Verificações
        assertNotNull(debts);
        assertEquals(2, debts.size());
    }

    @Test
    void shouldCalculateTotalDebtForUser() {
        // Preparação: criar um usuário e dívidas
        User user = new User();
        user.setUsername("totalDebtUser_" + System.currentTimeMillis()); // Username único
        user.setPasswordHash("hashedPassword");
        User savedUser = userRepository.save(user);

        Debt debt1 = new Debt(null, "Debt 1", BigDecimal.valueOf(300), LocalDate.now(), "Pendente", null, savedUser, LocalDateTime.now());
        Debt debt2 = new Debt(null, "Debt 2", BigDecimal.valueOf(500), LocalDate.now(), "Pago", null, savedUser, LocalDateTime.now());
        debtRepository.save(debt1);
        debtRepository.save(debt2);

        // Chamada do serviço
        BigDecimal totalDebt = debtService.calculateTotalDebt(savedUser.getId());

        // Verificações
        assertEquals(BigDecimal.valueOf(800), totalDebt);
    }

    @Test
    void shouldReturnDashboardSummary() {
        // Mockar o repositório
        DebtRepository mockedDebtRepository = mock(DebtRepository.class);

        // Configurar o mock
        when(mockedDebtRepository.getTotalDebt()).thenReturn(BigDecimal.valueOf(2000));
        when(mockedDebtRepository.getTotalPaidDebt()).thenReturn(BigDecimal.valueOf(800));
        when(mockedDebtRepository.getTotalDebtsCount()).thenReturn(10L);
        when(mockedDebtRepository.getPaidDebtsCount()).thenReturn(4L);

        // Criar o serviço usando o repositório mockado
        DebtService mockedDebtService = new DebtService(mockedDebtRepository);

        // Chamada do serviço
        Map<String, Object> summary = mockedDebtService.getDashboardSummary();

        // Verificações
        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(1200), summary.get("totalPendingDebt")); // totalDebt - totalPaidDebt
        assertEquals(BigDecimal.valueOf(2000), summary.get("totalDebt"));
        assertEquals(10L, summary.get("totalDebtsCount"));
        assertEquals(4L, summary.get("paidDebtsCount"));
    }
}
