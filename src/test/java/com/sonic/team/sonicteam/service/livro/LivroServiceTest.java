package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.util.LivroMapper;
import com.sonic.team.sonicteam.validation.LivroValidator;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LivroValidator livroValidator;

    @Mock
    private LivroMapper livroMapper;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;
    private LivroRequestDTO livroRequestDTO;
    private LivroResponseDTO livroResponseDTO;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setIsbn("9783161484100");
        livro.setTitulo("Clean Code");
        livro.setAutor("Robert C. Martin");
        livro.setEditora("Prentice Hall");
        livro.setEdicao("1");
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);
        livro.setDisponivel(true);

        livroRequestDTO = new LivroRequestDTO(
                "9783161484100",
                "Clean Code",
                "Robert C. Martin",
                "Prentice Hall",
                "1",
                CategoriaLivro.COMPUTACAO
        );

        livroResponseDTO = new LivroResponseDTO(
                "9783161484100",
                "Clean Code",
                "Robert C. Martin",
                "Prentice Hall",
                "1",
                "COMPUTACAO"
        );
    }

    // ==================== TESTES criarLivro ====================

    @Test
    void criarLivro_DeveRetornarResponse_QuandoDadosValidos() {
        when(livroMapper.paraEntidade(livroRequestDTO)).thenReturn(livro);
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        LivroResponseDTO resultado = livroService.criarLivro(livroRequestDTO);

        assertNotNull(resultado);
        assertEquals("9783161484100", resultado.isbn());
        assertEquals("Clean Code", resultado.titulo());
        verify(livroValidator).validarCadastro(livro);
        verify(livroRepository).save(livro);
    }

    @Test
    void criarLivro_DeveChamarValidador() {
        when(livroMapper.paraEntidade(livroRequestDTO)).thenReturn(livro);
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        livroService.criarLivro(livroRequestDTO);

        verify(livroValidator, times(1)).validarCadastro(livro);
    }

    @Test
    void criarLivro_DeveSalvarLivro() {
        when(livroMapper.paraEntidade(livroRequestDTO)).thenReturn(livro);
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        livroService.criarLivro(livroRequestDTO);

        verify(livroRepository).save(livro);
    }

    // ==================== TESTES listarLivros ====================

    @Test
    void listarLivros_DeveRetornarTodos_QuandoTodosFiltrosVazios() {
        when(livroRepository.findAll()).thenReturn(List.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        List<LivroResponseDTO> resultado = livroService.listarLivros(null, null, null, null, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(livroRepository).findAll();
    }

    @Test
    void listarLivros_DeveRetornarTodos_QuandoFiltrosSaoVazios() {
        when(livroRepository.findAll()).thenReturn(List.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        List<LivroResponseDTO> resultado = livroService.listarLivros("", "", "", "", "");

        assertEquals(1, resultado.size());
        verify(livroRepository).findAll();
    }

    @Test
    void listarLivros_DeveRetornarListaVazia_QuandoNaoHouverLivros() {
        when(livroRepository.findAll()).thenReturn(Collections.emptyList());

        List<LivroResponseDTO> resultado = livroService.listarLivros(null, null, null, null, null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarLivros_DeveFiltrarPorTitulo() {
        when(livroRepository.buscarComFiltros("Clean", null, null, null, null)).thenReturn(List.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        List<LivroResponseDTO> resultado = livroService.listarLivros("Clean", null, null, null, null);

        assertEquals(1, resultado.size());
        verify(livroRepository).buscarComFiltros("Clean", null, null, null, null);
    }

    @Test
    void listarLivros_DeveFiltrarPorAutor() {
        when(livroRepository.buscarComFiltros(null, "Robert", null, null, null)).thenReturn(List.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        List<LivroResponseDTO> resultado = livroService.listarLivros(null, "Robert", null, null, null);

        assertEquals(1, resultado.size());
        verify(livroRepository).buscarComFiltros(null, "Robert", null, null, null);
    }

    @Test
    void listarLivros_DeveFiltrarPorCategoria() {
        when(livroRepository.buscarComFiltros(null, null, null, null, CategoriaLivro.COMPUTACAO)).thenReturn(List.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        List<LivroResponseDTO> resultado = livroService.listarLivros(null, null, null, null, "COMPUTACAO");

        assertEquals(1, resultado.size());
        verify(livroRepository).buscarComFiltros(null, null, null, null, CategoriaLivro.COMPUTACAO);
    }

    @Test
    void listarLivros_DeveFiltrarPorEdicao() {
        when(livroRepository.buscarComFiltros(null, null, null, 1, null)).thenReturn(List.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        List<LivroResponseDTO> resultado = livroService.listarLivros(null, null, null, "1", null);

        assertEquals(1, resultado.size());
        verify(livroRepository).buscarComFiltros(null, null, null, 1, null);
    }

    @Test
    void listarLivros_DeveLancarExcecao_QuandoEdicaoInvalida() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.listarLivros(null, null, null, "abc", null)
        );
    }

    @Test
    void listarLivros_DeveLancarExcecao_QuandoCategoriaInvalida() {
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> livroService.listarLivros(null, null, null, null, "INVALIDA")
        );
    }

    // ==================== TESTES atualizarLivro ====================

    @Test
    void atualizarLivro_DeveRetornarResponse_QuandoLivroExistir() {
        when(livroRepository.findById("9783161484100")).thenReturn(Optional.of(livro));
        when(livroMapper.paraEntidade(livroRequestDTO)).thenReturn(livro);
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        LivroResponseDTO resultado = livroService.atualizarLivro("9783161484100", livroRequestDTO);

        assertNotNull(resultado);
        assertEquals("9783161484100", resultado.isbn());
        verify(livroValidator).validarAtualizacao(livro);
        verify(livroRepository).save(livro);
    }

    @Test
    void atualizarLivro_DeveLancarExcecao_QuandoIdForNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.atualizarLivro(null, livroRequestDTO)
        );

        verify(livroRepository, never()).findById(any());
    }

    @Test
    void atualizarLivro_DeveLancarExcecao_QuandoIdForVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.atualizarLivro("", livroRequestDTO)
        );
    }

    @Test
    void atualizarLivro_DeveLancarExcecao_QuandoLivroNaoExistir() {
        when(livroRepository.findById("isbn-inexistente")).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> livroService.atualizarLivro("isbn-inexistente", livroRequestDTO)
        );

        verify(livroRepository, never()).save(any());
    }

    @Test
    void atualizarLivro_DeveSetarIsbnNoLivroAtualizado() {
        when(livroRepository.findById("9783161484100")).thenReturn(Optional.of(livro));
        when(livroMapper.paraEntidade(livroRequestDTO)).thenReturn(livro);
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        livroService.atualizarLivro("9783161484100", livroRequestDTO);

        assertEquals("9783161484100", livro.getIsbn());
    }

    // ==================== TESTES excluirLivro ====================

    @Test
    void excluirLivro_DeveDeletarLivro_QuandoExistirEDisponivel() {
        when(livroRepository.findById("9783161484100")).thenReturn(Optional.of(livro));

        livroService.excluirLivro("9783161484100");

        verify(livroValidator).validarRemocao(livro);
        verify(livroRepository).delete(livro);
    }

    @Test
    void excluirLivro_DeveLancarExcecao_QuandoIdForNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.excluirLivro(null)
        );

        verify(livroRepository, never()).delete(any());
    }

    @Test
    void excluirLivro_DeveLancarExcecao_QuandoIdForVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.excluirLivro("")
        );
    }

    @Test
    void excluirLivro_DeveLancarExcecao_QuandoLivroNaoExistir() {
        when(livroRepository.findById("isbn-inexistente")).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> livroService.excluirLivro("isbn-inexistente")
        );

        verify(livroRepository, never()).delete(any());
    }

    // ==================== TESTES buscarLivroPorISBN ====================

    @Test
    void buscarLivroPorISBN_DeveRetornarLivro_QuandoIdExistir() {
        when(livroRepository.findById("9783161484100")).thenReturn(Optional.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        LivroResponseDTO resultado = livroService.buscarLivroPorISBN("9783161484100");

        assertNotNull(resultado);
        assertEquals("9783161484100", resultado.isbn());
        verify(livroRepository).findById("9783161484100");
    }

    @Test
    void buscarLivroPorISBN_DeveRetornarLivro_QuandoIsbnTem10Digitos() {
        when(livroRepository.findById("1234567890")).thenReturn(Optional.of(livro));
        when(livroMapper.paraResponse(livro)).thenReturn(livroResponseDTO);

        LivroResponseDTO resultado = livroService.buscarLivroPorISBN("1234567890");

        assertNotNull(resultado);
    }

    @Test
    void buscarLivroPorISBN_DeveLancarExcecao_QuandoIdForNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.buscarLivroPorISBN(null)
        );
    }

    @Test
    void buscarLivroPorISBN_DeveLancarExcecao_QuandoIdForVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.buscarLivroPorISBN("")
        );
    }

    @Test
    void buscarLivroPorISBN_DeveLancarExcecao_QuandoIdTamanhoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.buscarLivroPorISBN("12345678901")  // 11 dígitos
        );
    }

    @Test
    void buscarLivroPorISBN_DeveLancarExcecao_QuandoLivroNaoExistir() {
        when(livroRepository.findById("9783161484100")).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> livroService.buscarLivroPorISBN("9783161484100")
        );
    }
}
