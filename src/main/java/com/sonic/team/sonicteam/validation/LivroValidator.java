package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.RecursoJaExisteException;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import org.springframework.stereotype.Component;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.LivroRepository;

@Component
public class LivroValidator {

    private final LivroRepository livroRepository;

    public LivroValidator(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public void validarCadastro(Livro livro) {
        validarIsbnNaoCadastrado(livro);
        validarUnicidadeCombinacao(livro);
    }

    public void validarAtualizacao(Livro livro) {
        validarUnicidadeCombinacaoAoAtualizar(livro);
    }

    public void validarRemocao(Livro livro) {
        if (!livro.isDisponivel()) {
            throw new IllegalStateException(
                    "O livro não pode ser removido pois está emprestado."
            );
        }
    }

    public CategoriaLivro converterCategoria(String categoria) {
        try {
            return CategoriaLivro.valueOf(categoria.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Categoria informada não existe: " + categoria
            );
        }
    }



    private void validarIsbnNaoCadastrado(Livro livro) {
        if (livroRepository.existsById(livro.getIsbn())) {
            throw new RecursoJaExisteException(
                    "Já existe um livro com esse ISBN cadastrado."
            );
        }
    }

    private void validarUnicidadeCombinacao(Livro livro) {
        boolean existeOutro = livroRepository
                .existsByAutorAndEditoraAndEdicao(
                        livro.getAutor(),
                        livro.getEditora(),
                        livro.getEdicao()
                );

        if (existeOutro) {
            throw new RecursoJaExisteException(
                    "Já existe um livro com o mesmo autor, editora e edição."
            );
        }
    }

    private void validarUnicidadeCombinacaoAoAtualizar(Livro livro) {
        var existente = livroRepository
                .findByAutorAndEditoraAndEdicao(
                        livro.getAutor(),
                        livro.getEditora(),
                        livro.getEdicao()
                );

        if (existente.isPresent() &&
                !existente.get().getIsbn().equals(livro.getIsbn())) {

            throw new RecursoJaExisteException(
                    "Já existe outro livro com mesmo autor, editora e edição."
            );
        }
    }
}
