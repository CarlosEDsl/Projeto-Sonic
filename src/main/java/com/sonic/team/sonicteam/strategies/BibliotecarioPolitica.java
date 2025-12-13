package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.usuario.Bibliotecario;
import lombok.Getter;

@Getter
public class BibliotecarioPolitica implements PoliticaEmprestimo {

    private static final int LIMITE_EMPRESTIMOS = 0;
    
    private final Bibliotecario entity;

    public BibliotecarioPolitica(Bibliotecario entity) {
        this.entity = entity;
    }

    @Override
    public int pegarLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }

    @Override
    public boolean podeRealizarEmprestimo() {
        return false;
    }
}
