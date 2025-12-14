package com.sonic.team.sonicteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.service.estoque.IEstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstoqueController.class)
class EstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IEstoqueService estoqueService;

    private EstoqueResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new EstoqueResponseDTO(1L, "1234567890123", true);
    }

    // ==================== TESTES cadastrarExemplar ====================

    @Test
    void cadastrarExemplar_DeveRetornarExemplarCriado() throws Exception {
        EstoqueRequestDTO requestDTO = new EstoqueRequestDTO("1234567890123");
        when(estoqueService.cadastrarNovoExemplar(any(EstoqueRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/estoque")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.livroIsbn").value("1234567890123"))
            .andExpect(jsonPath("$.disponivel").value(true));
    }

    // ==================== TESTES getExemplaresDisponiveis ====================

    @Test
    void getExemplaresDisponiveis_DeveRetornarLista() throws Exception {
        List<EstoqueResponseDTO> exemplares = Arrays.asList(responseDTO);
        when(estoqueService.getExemplaresDisponiveis(null)).thenReturn(exemplares);

        mockMvc.perform(get("/estoque/disponiveis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getExemplaresDisponiveis_DeveRetornarListaVazia() throws Exception {
        when(estoqueService.getExemplaresDisponiveis(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/estoque/disponiveis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getExemplaresDisponiveis_DeveFiltrarPorIsbn() throws Exception {
        List<EstoqueResponseDTO> exemplares = Arrays.asList(responseDTO);
        when(estoqueService.getExemplaresDisponiveis("1234567890123")).thenReturn(exemplares);

        mockMvc.perform(get("/estoque/disponiveis").param("livroIsbn", "1234567890123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].livroIsbn").value("1234567890123"));
    }

    // ==================== TESTES getExemplar ====================

    @Test
    void getExemplar_DeveRetornarExemplar() throws Exception {
        when(estoqueService.getExemplar(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/estoque/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.disponivel").value(true));
    }

    // ==================== TESTES atualizarDisponobilidadeExemplar ====================

    @Test
    void atualizarDisponobilidadeExemplar_DeveRetornarExemplarAtualizado() throws Exception {
        AtualizarEstoqueResquestDTO requestDTO = new AtualizarEstoqueResquestDTO(1L, false);
        EstoqueResponseDTO atualizado = new EstoqueResponseDTO(1L, "1234567890123", false);
        when(estoqueService.atualizarDisponibilidadeExemplar(any(AtualizarEstoqueResquestDTO.class))).thenReturn(atualizado);

        mockMvc.perform(put("/estoque")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.disponivel").value(false));
    }

    // ==================== TESTES deletarExemplar ====================

    @Test
    void deletarExemplar_DeveRetornarNoContent() throws Exception {
        doNothing().when(estoqueService).deletarExemplar(1L);

        mockMvc.perform(delete("/estoque/1"))
            .andExpect(status().isNoContent());

        verify(estoqueService).deletarExemplar(1L);
    }
}
