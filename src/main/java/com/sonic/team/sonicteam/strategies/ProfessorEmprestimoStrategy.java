package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.usuario.Professor;

import java.time.LocalDateTime;

public class ProfessorEmprestimoStrategy extends BaseStrategy<Professor> {

    private static final int PRAZO_DIAS = 40;
    private static final int LIMITE_EMPRESTIMOS = 5;

    public ProfessorEmprestimoStrategy(Professor entity) {
        super(entity, LIMITE_EMPRESTIMOS);
    }

    @Override
    public LocalDateTime calcularPrazo(Livro livro) {
        return LocalDateTime.now().plusDays(PRAZO_DIAS);
    }
}

