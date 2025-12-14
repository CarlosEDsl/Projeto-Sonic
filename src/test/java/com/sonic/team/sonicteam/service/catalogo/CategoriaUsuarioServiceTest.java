package com.sonic.team.sonicteam.service.catalogo;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.repository.CategoriaUsuarioRepository;
import com.sonic.team.sonicteam.util.ConstantesUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaUsuarioServiceTest {

    @Mock
    private CategoriaUsuarioRepository categoriaUsuarioRepository;

    @InjectMocks
    private CategoriaUsuarioService categoriaUsuarioService;

    private CategoriaUsuario categoriaAluno;
    private CategoriaUsuario categoriaProfessor;

    @BeforeEach
    void setUp() {
        categoriaAluno = new CategoriaUsuario(1L, "ALUNO");
        categoriaProfessor = new CategoriaUsuario(2L, "PROFESSOR");
    }

    @Test
    void buscarPorId_DeveRetornarCategoria_QuandoIdExistir() {
        when(categoriaUsuarioRepository.findById(1L)).thenReturn(Optional.of(categoriaAluno));

        CategoriaUsuario resultado = categoriaUsuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("ALUNO", resultado.getNome());
        verify(categoriaUsuarioRepository).findById(1L);
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoIdNaoExistir() {
        when(categoriaUsuarioRepository.findById(999L)).thenReturn(Optional.empty());

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> categoriaUsuarioService.buscarPorId(999L)
        );

        assertEquals(ConstantesUsuario.CATEGORIA_INEXISTENTE, exception.getMessage());
        verify(categoriaUsuarioRepository).findById(999L);
    }

    @Test
    void listar_DeveRetornarLista_QuandoHouverCategorias() {
        List<CategoriaUsuario> categorias = List.of(categoriaAluno, categoriaProfessor);
        when(categoriaUsuarioRepository.findAll()).thenReturn(categorias);

        List<CategoriaUsuario> resultado = categoriaUsuarioService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(categoriaUsuarioRepository).findAll();
    }

    @Test
    void listar_DeveLancarExcecao_QuandoListaVazia() {
        when(categoriaUsuarioRepository.findAll()).thenReturn(Collections.emptyList());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> categoriaUsuarioService.listar()
        );

        assertEquals("Não há nenhuma categoria cadastrada.", exception.getMessage());
        verify(categoriaUsuarioRepository).findAll();
    }
}
