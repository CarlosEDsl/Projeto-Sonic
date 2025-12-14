package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoResponseDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoMapperTest {

    private EmprestimoMapper emprestimoMapper;
    private Emprestimo emprestimo;
    private Aluno usuario;
    private Livro livro;
    private Estoque estoque;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private LocalDateTime dataEntrega;

    @BeforeEach
    void setUp() {
        emprestimoMapper = new EmprestimoMapper();

        usuario = new Aluno();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setCategoria(new CategoriaUsuario(1L, "Graduação"));

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
        estoque.setDisponivel(false);

        dataEmprestimo = LocalDateTime.of(2024, 1, 1, 10, 0);
        dataDevolucao = LocalDateTime.of(2024, 1, 15, 10, 0);
        dataEntrega = LocalDateTime.of(2024, 1, 14, 10, 0);

        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setUsuario(usuario);
        emprestimo.setEstoque(estoque);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setDataEntrega(dataEntrega);
    }

    // ==================== TESTES paraResponse ====================

    @Test
    void paraResponse_DeveConverterEmprestimoParaDTO() {
        EmprestimoResponseDTO resultado = emprestimoMapper.paraResponse(emprestimo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(1L, resultado.usuarioId());
        assertEquals("João Silva", resultado.usuarioNome());
        assertEquals("Graduação", resultado.usuarioCategoria());
        assertEquals("Java para Iniciantes", resultado.livroTitulo());
        assertEquals("1234567890123", resultado.livroIsbn());
        assertEquals(1L, resultado.estoqueId());
        assertEquals(dataEmprestimo, resultado.dataEmprestimo());
        assertEquals(dataDevolucao, resultado.dataDevolucaoPrevista());
        assertEquals(dataEntrega, resultado.dataDevolucao());
    }

    @Test
    void paraResponse_DeveRetornarNullParaCampos_QuandoUsuarioNull() {
        emprestimo.setUsuario(null);

        EmprestimoResponseDTO resultado = emprestimoMapper.paraResponse(emprestimo);

        assertNull(resultado.usuarioId());
        assertNull(resultado.usuarioNome());
        assertNull(resultado.usuarioCategoria());
    }

    @Test
    void paraResponse_DeveRetornarNullParaCampos_QuandoEstoqueNull() {
        emprestimo.setEstoque(null);

        EmprestimoResponseDTO resultado = emprestimoMapper.paraResponse(emprestimo);

        assertNull(resultado.livroTitulo());
        assertNull(resultado.livroIsbn());
        assertNull(resultado.estoqueId());
    }

    @Test
    void paraResponse_DeveRetornarNull_QuandoDataEntregaNull() {
        emprestimo.setDataEntrega(null);

        EmprestimoResponseDTO resultado = emprestimoMapper.paraResponse(emprestimo);

        assertNull(resultado.dataDevolucao());
    }

    // ==================== TESTES paraListaResponse ====================

    @Test
    void paraListaResponse_DeveConverterListaDeEmprestimos() {
        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);
        emprestimo2.setUsuario(usuario);
        emprestimo2.setEstoque(estoque);
        emprestimo2.setDataEmprestimo(dataEmprestimo);
        emprestimo2.setDataDevolucao(dataDevolucao);

        List<Emprestimo> emprestimos = Arrays.asList(emprestimo, emprestimo2);

        List<EmprestimoResponseDTO> resultado = emprestimoMapper.paraListaResponse(emprestimos);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).id());
        assertEquals(2L, resultado.get(1).id());
    }

    @Test
    void paraListaResponse_DeveRetornarListaVazia_QuandoListaVazia() {
        List<EmprestimoResponseDTO> resultado = emprestimoMapper.paraListaResponse(Collections.emptyList());

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void paraListaResponse_DeveConverterUmUnicoEmprestimo() {
        List<Emprestimo> emprestimos = List.of(emprestimo);

        List<EmprestimoResponseDTO> resultado = emprestimoMapper.paraListaResponse(emprestimos);

        assertEquals(1, resultado.size());
    }
}
