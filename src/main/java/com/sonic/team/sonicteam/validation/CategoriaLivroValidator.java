package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import com.sonic.team.sonicteam.repository.CategoriaLivroRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoriaLivroValidator {

    private final CategoriaLivroRepository repository;

    public CategoriaLivroValidator(CategoriaLivroRepository repository) {
        this.repository = repository;
    }

    public void validarCadastro(CategoriaLivro categoria) {

        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }


        boolean existe = repository.existsByNomeIgnoreCase(categoria.getNome());
        if (existe) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
        }
    }

    public void validarAtualizacao(CategoriaLivro categoria) {

        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }

        Optional<CategoriaLivro> existente = repository.findByNomeIgnoreCase(categoria.getNome());

        if (existente.isPresent() && !existente.get().getId().equals(categoria.getId())) {
            throw new IllegalArgumentException("Já existe outra categoria com esse nome.");
        }
    }
}
