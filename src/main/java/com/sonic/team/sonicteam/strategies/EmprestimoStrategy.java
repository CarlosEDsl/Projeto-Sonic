package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;

import java.time.LocalDateTime;

public interface EmprestimoStrategy {
    Emprestimo pegarEmprestimo(Estoque estoque);
    LocalDateTime calcularPrazo(Livro livro);
    int pegarLimiteEmprestimos();
}
