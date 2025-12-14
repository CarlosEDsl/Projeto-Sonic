package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.repository.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueValidatorTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueValidator estoqueValidator;

    private Estoque estoque;

    @BeforeEach
    void setUp() {
        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setDisponivel(true);
    }

    // ==================== TESTES validarDisponibilidadeDoExemplar ====================

    @Test
    void validarDisponibilidadeDoExemplar_DeveRetornarTrue_QuandoDisponivel() {
        estoque.setDisponivel(true);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        Boolean resultado = estoqueValidator.validarDisponibilidadeDoExemplar(1L);

        assertTrue(resultado);
    }

    @Test
    void validarDisponibilidadeDoExemplar_DeveRetornarFalse_QuandoIndisponivel() {
        estoque.setDisponivel(false);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        Boolean resultado = estoqueValidator.validarDisponibilidadeDoExemplar(1L);

        assertFalse(resultado);
    }

    @Test
    void validarDisponibilidadeDoExemplar_DeveLancarExcecao_QuandoNaoEncontrado() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
            RecursoNaoEncontradoException.class,
            () -> estoqueValidator.validarDisponibilidadeDoExemplar(1L)
        );

        assertEquals("Exemplar não encontrado com o id 1", exception.getMessage());
    }

    @Test
    void validarDisponibilidadeDoExemplar_DeveLancarExcecao_QuandoIdDiferente() {
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
            RecursoNaoEncontradoException.class,
            () -> estoqueValidator.validarDisponibilidadeDoExemplar(999L)
        );

        assertEquals("Exemplar não encontrado com o id 999", exception.getMessage());
    }

    @Test
    void validarDisponibilidadeDoExemplar_DeveChamarRepository() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        estoqueValidator.validarDisponibilidadeDoExemplar(1L);

        verify(estoqueRepository).findById(1L);
    }
}
