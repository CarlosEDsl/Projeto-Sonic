package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.repository.EstoqueRepository;
import org.springframework.stereotype.Component;

@Component
public class EstoqueValidator {
    private final EstoqueRepository estoqueRepository;

    public EstoqueValidator(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public Boolean validarDisponibilidadeDoExemplar(Long id) {
        var exemplarEstoque = estoqueRepository.findById(id);
        if(exemplarEstoque.isPresent()) {
            if(exemplarEstoque.get().getDisponivel()) return true;
            else return false;
        }

        throw new RecursoNaoEncontradoException("Exemplar não encontrado com o id " + id);
    }

}
