package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.RecursoJaExisteException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.repository.LivroRepository;
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
class LivroValidatorTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroValidator livroValidator;

    private Livro livro;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setIsbn("1234567890123");
        livro.setTitulo("Java para Iniciantes");
        livro.setAutor("Autor Teste");
        livro.setEditora("Editora Teste");
        livro.setEdicao("1");
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);
        livro.setDisponivel(true);
    }

    // ==================== TESTES validarCadastro ====================

    @Test
    void validarCadastro_DevePassar_QuandoDadosValidos() {
        when(livroRepository.existsById("1234567890123")).thenReturn(false);
        when(livroRepository.existsByAutorAndEditoraAndEdicao("Autor Teste", "Editora Teste", "1")).thenReturn(false);

        assertDoesNotThrow(() -> livroValidator.validarCadastro(livro));
    }

    @Test
    void validarCadastro_DeveLancarExcecao_QuandoIsbnJaExiste() {
        when(livroRepository.existsById("1234567890123")).thenReturn(true);

        RecursoJaExisteException exception = assertThrows(
            RecursoJaExisteException.class,
            () -> livroValidator.validarCadastro(livro)
        );

        assertEquals("Já existe um livro com esse ISBN cadastrado.", exception.getMessage());
    }

    @Test
    void validarCadastro_DeveLancarExcecao_QuandoCombinacaoJaExiste() {
        when(livroRepository.existsById("1234567890123")).thenReturn(false);
        when(livroRepository.existsByAutorAndEditoraAndEdicao("Autor Teste", "Editora Teste", "1")).thenReturn(true);

        RecursoJaExisteException exception = assertThrows(
            RecursoJaExisteException.class,
            () -> livroValidator.validarCadastro(livro)
        );

        assertEquals("Já existe um livro com o mesmo autor, editora e edição.", exception.getMessage());
    }

    @Test
    void validarCadastro_DeveLancarExcecao_QuandoCategoriaNull() {
        livro.setCategoriaLivro(null);

        RecursoNaoEncontradoException exception = assertThrows(
            RecursoNaoEncontradoException.class,
            () -> livroValidator.validarCadastro(livro)
        );

        assertEquals("Categoria inválida.", exception.getMessage());
    }

    // ==================== TESTES validarAtualizacao ====================

    @Test
    void validarAtualizacao_DevePassar_QuandoNaoExisteOutroComMesmaCombinacao() {
        when(livroRepository.findByAutorAndEditoraAndEdicao("Autor Teste", "Editora Teste", "1"))
            .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> livroValidator.validarAtualizacao(livro));
    }

    @Test
    void validarAtualizacao_DevePassar_QuandoExistentePossuiMesmoIsbn() {
        Livro existente = new Livro();
        existente.setIsbn("1234567890123");

        when(livroRepository.findByAutorAndEditoraAndEdicao("Autor Teste", "Editora Teste", "1"))
            .thenReturn(Optional.of(existente));

        assertDoesNotThrow(() -> livroValidator.validarAtualizacao(livro));
    }

    @Test
    void validarAtualizacao_DeveLancarExcecao_QuandoExisteOutroComMesmaCombinacao() {
        Livro existente = new Livro();
        existente.setIsbn("9999999999999");

        when(livroRepository.findByAutorAndEditoraAndEdicao("Autor Teste", "Editora Teste", "1"))
            .thenReturn(Optional.of(existente));

        RecursoJaExisteException exception = assertThrows(
            RecursoJaExisteException.class,
            () -> livroValidator.validarAtualizacao(livro)
        );

        assertEquals("Já existe outro livro com mesmo autor, editora e edição.", exception.getMessage());
    }

    // ==================== TESTES validarRemocao ====================

    @Test
    void validarRemocao_DevePassar_QuandoLivroDisponivel() {
        livro.setDisponivel(true);

        assertDoesNotThrow(() -> livroValidator.validarRemocao(livro));
    }

    @Test
    void validarRemocao_DeveLancarExcecao_QuandoLivroEmprestado() {
        livro.setDisponivel(false);

        RecursoNaoEncontradoException exception = assertThrows(
            RecursoNaoEncontradoException.class,
            () -> livroValidator.validarRemocao(livro)
        );

        assertEquals("O livro não pode ser removido pois está emprestado.", exception.getMessage());
    }
}
