package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;

import java.time.LocalDateTime;

//Princípios: OCP (fácil adicionar novo comportamento sem mudar o orquestrador) e LSP (implementações seguem o contrato da interface).
//Padrão Strategy: cada tipo de usuário tem sua própria implementação das regras de empréstimo. Isso permite estender comportamentos sem alterar o código que cria os empréstimos.
public interface EmprestimoStrategy {
    Emprestimo pegarEmprestimo(Estoque estoque);
    LocalDateTime calcularPrazo(Livro livro);
    int pegarLimiteEmprestimos();
}
