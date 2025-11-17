package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.exception.EmprestimoInvalido;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.usuario.Bibliotecario;

import java.time.LocalDateTime;

public class BibliotecarioEmprestimoStrategy extends BaseStrategy<Bibliotecario> {

    private static final int LIMITE_EMPRESTIMOS = 0;

    public BibliotecarioEmprestimoStrategy(Bibliotecario entity) {
        super(entity);
    }

    @Override
    public Emprestimo pegarEmprestimo(Estoque estoque) {
        throw new EmprestimoInvalido("Bibliotecários não podem pegar livros emprestados");
    }

    @Override
    public LocalDateTime calcularPrazo(Livro livro) {
        throw new EmprestimoInvalido("Bibliotecários não podem pegar livros emprestados");
    }

    @Override
    public int pegarLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }
}
