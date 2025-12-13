package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.usuario.Usuario;
import lombok.Getter;

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
}
