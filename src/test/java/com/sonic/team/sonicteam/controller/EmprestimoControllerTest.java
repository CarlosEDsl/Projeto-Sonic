package com.sonic.team.sonicteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoResponseDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.service.emprestimo.IEmprestimoService;
import com.sonic.team.sonicteam.util.EmprestimoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmprestimoController.class)
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IEmprestimoService emprestimoService;

    @MockBean
    private EmprestimoMapper emprestimoMapper;

    private Emprestimo emprestimo;
    private EmprestimoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setDataEmprestimo(LocalDateTime.now());

        responseDTO = new EmprestimoResponseDTO(
            1L,
            1L,
            "João Silva",
            "Graduação",
            "Java para Iniciantes",
            "1234567890123",
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(15),
            null
        );
    }

    // ==================== TESTES criar ====================

    @Test
    void criar_DeveRetornarEmprestimoCriado() throws Exception {
        EmprestimoRequestDTO requestDTO = new EmprestimoRequestDTO("12345678901", "1234567890123");
        when(emprestimoService.criarEmprestimo(any(EmprestimoRequestDTO.class))).thenReturn(emprestimo);
        when(emprestimoMapper.paraResponse(any(Emprestimo.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.usuarioNome").value("João Silva"));
    }

    // ==================== TESTES listar ====================

    @Test
    void listar_DeveRetornarListaDeEmprestimos() throws Exception {
        List<Emprestimo> emprestimos = Arrays.asList(emprestimo);
        List<EmprestimoResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(emprestimoService.listarEmprestimos()).thenReturn(emprestimos);
        when(emprestimoMapper.paraListaResponse(emprestimos)).thenReturn(responseDTOs);

        mockMvc.perform(get("/emprestimos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void listar_DeveRetornarListaVazia() throws Exception {
        when(emprestimoService.listarEmprestimos()).thenReturn(Collections.emptyList());
        when(emprestimoMapper.paraListaResponse(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/emprestimos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== TESTES buscar ====================

    @Test
    void buscar_DeveRetornarEmprestimo() throws Exception {
        when(emprestimoService.buscarEmprestimoPorId(1L)).thenReturn(emprestimo);
        when(emprestimoMapper.paraResponse(emprestimo)).thenReturn(responseDTO);

        mockMvc.perform(get("/emprestimos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.usuarioNome").value("João Silva"));
    }

    @Test
    void buscar_DeveRetornarNotFound_QuandoNaoEncontrar() throws Exception {
        when(emprestimoService.buscarEmprestimoPorId(999L)).thenReturn(null);

        mockMvc.perform(get("/emprestimos/999"))
            .andExpect(status().isNotFound());
    }

    // ==================== TESTES atualizar (devolução) ====================

    @Test
    void atualizar_DeveRetornarEmprestimoDevolvido() throws Exception {
        EmprestimoResponseDTO devolvido = new EmprestimoResponseDTO(
            1L, 1L, "João Silva", "Graduação", "Java para Iniciantes",
            "1234567890123", 1L, LocalDateTime.now(),
            LocalDateTime.now().plusDays(15), LocalDateTime.now()
        );
        when(emprestimoService.devolverEmprestimo(1L)).thenReturn(emprestimo);
        when(emprestimoMapper.paraResponse(emprestimo)).thenReturn(devolvido);

        mockMvc.perform(put("/emprestimos/1/devolucao"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
