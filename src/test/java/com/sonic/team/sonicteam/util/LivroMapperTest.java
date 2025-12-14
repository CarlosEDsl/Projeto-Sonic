package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LivroMapperTest {

    private LivroMapper livroMapper;
    private Livro livro;
    private LivroRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        livroMapper = new LivroMapper();

        livro = new Livro();
        livro.setIsbn("1234567890123");
        livro.setTitulo("Java para Iniciantes");
        livro.setAutor("Autor Teste");
        livro.setEditora("Editora Teste");
        livro.setEdicao("1");
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);

        requestDTO = new LivroRequestDTO(
            "1234567890123",
            "Java para Iniciantes",
            "Autor Teste",
            "Editora Teste",
            "1",
            CategoriaLivro.COMPUTACAO
        );
    }

    // ==================== TESTES paraEntidade ====================

    @Test
    void paraEntidade_DeveConverterDTOParaLivro() {
        Livro resultado = livroMapper.paraEntidade(requestDTO);

        assertNotNull(resultado);
        assertEquals("1234567890123", resultado.getIsbn());
        assertEquals("Java para Iniciantes", resultado.getTitulo());
        assertEquals("Autor Teste", resultado.getAutor());
        assertEquals("Editora Teste", resultado.getEditora());
        assertEquals("1", resultado.getEdicao());
        assertEquals(CategoriaLivro.COMPUTACAO, resultado.getCategoriaLivro());
    }

    @Test
    void paraEntidade_DeveConverterComCategoriaRomance() {
        LivroRequestDTO dto = new LivroRequestDTO(
            "9876543210123",
            "Romance do Século",
            "Autor Romance",
            "Editora Romance",
            "2",
            CategoriaLivro.ROMANCE
        );

        Livro resultado = livroMapper.paraEntidade(dto);

        assertEquals(CategoriaLivro.ROMANCE, resultado.getCategoriaLivro());
    }

    // ==================== TESTES paraResponse ====================

    @Test
    void paraResponse_DeveConverterLivroParaDTO() {
        LivroResponseDTO resultado = livroMapper.paraResponse(livro);

        assertNotNull(resultado);
        assertEquals("1234567890123", resultado.isbn());
        assertEquals("Java para Iniciantes", resultado.titulo());
        assertEquals("Autor Teste", resultado.autor());
        assertEquals("Editora Teste", resultado.editora());
        assertEquals("1", resultado.edicao());
        assertEquals("COMPUTACAO", resultado.categoria());
    }

    @Test
    void paraResponse_DeveConverterCategoria() {
        livro.setCategoriaLivro(CategoriaLivro.LETRAS);

        LivroResponseDTO resultado = livroMapper.paraResponse(livro);

        assertEquals("LETRAS", resultado.categoria());
    }

    @Test
    void paraResponse_DeveConverterTodasCategorias() {
        livro.setCategoriaLivro(CategoriaLivro.GESTAO);
        assertEquals("GESTAO", livroMapper.paraResponse(livro).categoria());

        livro.setCategoriaLivro(CategoriaLivro.ROMANCE);
        assertEquals("ROMANCE", livroMapper.paraResponse(livro).categoria());
    }
}
