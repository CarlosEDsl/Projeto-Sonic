package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstoqueMapperTest {

    private EstoqueMapper estoqueMapper;
    private Estoque estoque;
    private Livro livro;

    @BeforeEach
    void setUp() {
        estoqueMapper = new EstoqueMapper();

        livro = new Livro();
        livro.setIsbn("1234567890123");
        livro.setTitulo("Java para Iniciantes");
        livro.setAutor("Autor Teste");
        livro.setEditora("Editora Teste");
        livro.setEdicao("1");
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setLivro(livro);
        estoque.setDisponivel(true);
    }

    // ==================== TESTES paraEntidade ====================

    @Test
    void paraEntidade_DeveConverterDTOParaEstoque() {
        EstoqueResponseDTO dto = new EstoqueResponseDTO(1L, "1234567890123", true);

        Estoque resultado = estoqueMapper.paraEntidade(dto, livro);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(livro, resultado.getLivro());
        assertTrue(resultado.getDisponivel());
    }

    @Test
    void paraEntidade_DeveConverterComDisponibilidadeFalse() {
        EstoqueResponseDTO dto = new EstoqueResponseDTO(2L, "1234567890123", false);

        Estoque resultado = estoqueMapper.paraEntidade(dto, livro);

        assertFalse(resultado.getDisponivel());
    }

    // ==================== TESTES paraResponse ====================

    @Test
    void paraResponse_DeveConverterEstoqueParaDTO() {
        EstoqueResponseDTO resultado = estoqueMapper.paraResponse(estoque);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("1234567890123", resultado.livroIsbn());
        assertTrue(resultado.disponivel());
    }

    @Test
    void paraResponse_DeveConverterComDisponibilidadeFalse() {
        estoque.setDisponivel(false);

        EstoqueResponseDTO resultado = estoqueMapper.paraResponse(estoque);

        assertFalse(resultado.disponivel());
    }

    // ==================== TESTES paraListaResponse ====================

    @Test
    void paraListaResponse_DeveConverterListaDeEstoques() {
        Estoque estoque2 = new Estoque();
        estoque2.setId(2L);
        estoque2.setLivro(livro);
        estoque2.setDisponivel(false);

        List<Estoque> estoques = Arrays.asList(estoque, estoque2);

        List<EstoqueResponseDTO> resultado = estoqueMapper.paraListaResponse(estoques);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).id());
        assertEquals(2L, resultado.get(1).id());
    }

    @Test
    void paraListaResponse_DeveRetornarListaVazia_QuandoListaVazia() {
        List<EstoqueResponseDTO> resultado = estoqueMapper.paraListaResponse(Collections.emptyList());

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void paraListaResponse_DeveConverterUmUnicoEstoque() {
        List<Estoque> estoques = List.of(estoque);

        List<EstoqueResponseDTO> resultado = estoqueMapper.paraListaResponse(estoques);

        assertEquals(1, resultado.size());
    }
}
