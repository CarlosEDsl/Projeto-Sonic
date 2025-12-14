package com.sonic.team.sonicteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.service.livro.ILivroService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ILivroService livroService;

    private LivroRequestDTO requestDTO;
    private LivroResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new LivroRequestDTO(
            "1234567890123",
            "Java para Iniciantes",
            "Autor Teste",
            "Editora Teste",
            "1",
            CategoriaLivro.COMPUTACAO
        );

        responseDTO = new LivroResponseDTO(
            "1234567890123",
            "Java para Iniciantes",
            "Autor Teste",
            "Editora Teste",
            "1",
            "COMPUTACAO"
        );
    }

    // ==================== TESTES criar ====================

    @Test
    void criar_DeveRetornarLivroCriado() throws Exception {
        when(livroService.criarLivro(any(LivroRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.isbn").value("1234567890123"))
            .andExpect(jsonPath("$.titulo").value("Java para Iniciantes"));
    }

    // ==================== TESTES listar ====================

    @Test
    void listar_DeveRetornarListaDeLivros() throws Exception {
        List<LivroResponseDTO> livros = Arrays.asList(responseDTO);
        when(livroService.listarLivros(any(), any(), any(), any(), any())).thenReturn(livros);

        mockMvc.perform(get("/livros"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].isbn").value("1234567890123"));
    }

    @Test
    void listar_DeveRetornarListaVazia() throws Exception {
        when(livroService.listarLivros(any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/livros"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void listar_DeveFiltrarPorTitulo() throws Exception {
        List<LivroResponseDTO> livros = Arrays.asList(responseDTO);
        when(livroService.listarLivros(eq("Java"), any(), any(), any(), any())).thenReturn(livros);

        mockMvc.perform(get("/livros").param("titulo", "Java"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].titulo").value("Java para Iniciantes"));
    }

    // ==================== TESTES buscar ====================

    @Test
    void buscar_DeveRetornarLivro() throws Exception {
        when(livroService.buscarLivroPorISBN("1234567890123")).thenReturn(responseDTO);

        mockMvc.perform(get("/livros/1234567890123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isbn").value("1234567890123"))
            .andExpect(jsonPath("$.titulo").value("Java para Iniciantes"));
    }

    // ==================== TESTES atualizar ====================

    @Test
    void atualizar_DeveRetornarLivroAtualizado() throws Exception {
        when(livroService.atualizarLivro(eq("1234567890123"), any(LivroRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/livros/1234567890123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isbn").value("1234567890123"));
    }

    // ==================== TESTES deletar ====================

    @Test
    void deletar_DeveRetornarNoContent() throws Exception {
        doNothing().when(livroService).excluirLivro("1234567890123");

        mockMvc.perform(delete("/livros/1234567890123"))
            .andExpect(status().isNoContent());

        verify(livroService).excluirLivro("1234567890123");
    }
}
