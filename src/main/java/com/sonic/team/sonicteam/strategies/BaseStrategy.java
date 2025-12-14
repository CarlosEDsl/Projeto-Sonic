package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import lombok.Getter;

import java.time.LocalDateTime;

public abstract class BaseStrategy<T extends Usuario> implements EmprestimoStrategy {
    
    @Getter
    private final T entity;
    
    private final int limiteLivros;
    
    protected BaseStrategy(T entity, int limiteLivros) {
        this.entity = entity;
        this.limiteLivros = limiteLivros;
    }
    
    @Override
    public int pegarLimiteEmprestimos() {
        return limiteLivros;
    }
    
    @Override
    public Emprestimo pegarEmprestimo(Estoque estoque) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setEstoque(estoque);
        emprestimo.setUsuario(getEntity());
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataDevolucao(calcularPrazo(estoque.getLivro()));
        return emprestimo;
    }
}

