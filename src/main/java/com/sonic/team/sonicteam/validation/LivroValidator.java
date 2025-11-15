package com.sonic.team.sonicteam.validation;

import org.springframework.stereotype.Component;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.repository.CategoriaLivroRepository;

@Component
public class LivroValidator {

    private final LivroRepository livroRepository;
    private final CategoriaLivroRepository categoriaRepository;

    public LivroValidator(LivroRepository livroRepository, CategoriaLivroRepository categoriaRepository) {
        this.livroRepository = livroRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public void validarCadastro(Livro livro) {
        validarCategoriaExiste(livro);
        validarIsbnNaoCadastrado(livro);
        validarUnicidadeCombinacao(livro);
    }

    public void validarAtualizacao(Livro livro) {
        validarCategoriaExiste(livro);
        validarUnicidadeCombinacaoAoAtualizar(livro);
    }

    public void validarRemocao(Livro livro) {
        if (!livro.isDisponivel()) {
            throw new IllegalStateException(
                    "O livro não pode ser removido pois está emprestado."
            );
        }
    }


    private void validarCategoriaExiste(Livro livro) {
        if (!categoriaRepository.existsById(livro.getCategoria().getId())) {
            throw new IllegalArgumentException("Categoria informada não existe.");
        }
    }

    private void validarIsbnNaoCadastrado(Livro livro) {
        if (livroRepository.existsById(livro.getIsbn())) {
            throw new IllegalStateException(
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
            throw new IllegalStateException(
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

            throw new IllegalStateException(
                    "Já existe outro livro com mesmo autor, editora e edição."
            );
        }
    }
}
