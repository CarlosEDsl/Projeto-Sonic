package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.usuario.Professor;

import java.time.LocalDateTime;

public class ProfessorEmprestimoStrategy extends BaseStrategy<Professor> {

    private static final int PRAZO_DIAS = 40;
    private static final int LIMITE_EMPRESTIMOS = 5;

    public ProfessorEmprestimoStrategy(Professor entity) {
        super(entity);
    }

    @Override
    public Emprestimo pegarEmprestimo(Estoque estoque) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setEstoque(estoque);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataDevolucao(LocalDateTime.now().plusDays(PRAZO_DIAS));
        emprestimo.setUsuario(this.getEntity());

        return emprestimo;
    }

    @Override
    public LocalDateTime calcularPrazo(Livro livro) {
        return LocalDateTime.now().plusDays(40);
    }

    public int pegarLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }
}
