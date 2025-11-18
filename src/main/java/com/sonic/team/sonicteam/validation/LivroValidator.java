package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.RecursoJaExisteException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import org.springframework.stereotype.Component;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.LivroRepository;

import java.util.Arrays;

//Princípios: SRP (validação isolada) e ISP (métodos focados em validações específicas).
//Classes de validação: responsáveis apenas por checar regras de negócio/entrada antes de persistir ou alterar entidades — isso mantém serviços mais limpos.

@Component
public class LivroValidator {

    private final LivroRepository livroRepository;

    public LivroValidator(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public void validarCadastro(Livro livro) {
        validarCategoriaValida(livro.getCategoriaLivro());
        validarIsbnNaoCadastrado(livro);
        validarUnicidadeCombinacao(livro);
    }

    public void validarAtualizacao(Livro livro) {
        validarUnicidadeCombinacaoAoAtualizar(livro);
    }

    public void validarRemocao(Livro livro) {
        if (!livro.isDisponivel()) {
            throw new RecursoNaoEncontradoException(
                    "O livro não pode ser removido pois está emprestado."
            );
        }
    }

    private void validarCategoriaValida(CategoriaLivro categoria) {
        if (categoria == null || !Arrays.toString(CategoriaLivro.values()).contains(categoria.toString())) {
            throw new RecursoNaoEncontradoException("Categoria inválida.");
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
