package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.service.catalogo.ICategoriaUsuarioService;
import com.sonic.team.sonicteam.service.curso.ICursoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogoController.class)
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICategoriaUsuarioService categoriaUsuarioService;

    @MockBean
    private ICursoService cursoService;

    // ==================== TESTES listarCategoriasLivro ====================

    @Test
    void listarCategoriasLivro_DeveRetornarListaDeCategorias() throws Exception {
        mockMvc.perform(get("/catalogos/categorias-livro"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").value("ROMANCE"))
            .andExpect(jsonPath("$[1]").value("COMPUTACAO"))
            .andExpect(jsonPath("$[2]").value("LETRAS"))
            .andExpect(jsonPath("$[3]").value("GESTAO"));
    }

    // ==================== TESTES listarCategoriaUsuario ====================

    @Test
    void listarCategoriaUsuario_DeveRetornarListaDeCategorias() throws Exception {
        List<CategoriaUsuario> categorias = Arrays.asList(
            new CategoriaUsuario(1L, "Graduação"),
            new CategoriaUsuario(2L, "Pós-Graduação")
        );
        when(categoriaUsuarioService.listar()).thenReturn(categorias);

        mockMvc.perform(get("/catalogos/categorias-usuario"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Graduação"))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void listarCategoriaUsuario_DeveRetornarListaVazia() throws Exception {
        when(categoriaUsuarioService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/catalogos/categorias-usuario"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== TESTES listarCursos ====================

    @Test
    void listarCursos_DeveRetornarListaDeCursos() throws Exception {
        List<Curso> cursos = Arrays.asList(
            new Curso(1L, "Engenharia"),
            new Curso(2L, "Direito")
        );
        when(cursoService.listar()).thenReturn(cursos);

        mockMvc.perform(get("/catalogos/cursos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Engenharia"));
    }

    @Test
    void listarCursos_DeveRetornarListaVazia() throws Exception {
        when(cursoService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/catalogos/cursos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
