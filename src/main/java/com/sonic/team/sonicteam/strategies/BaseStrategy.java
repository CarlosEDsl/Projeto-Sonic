package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.usuario.Usuario;
import lombok.Getter;

public abstract class BaseStrategy <T extends Usuario> implements EmprestimoStrategy {
    private int limiteLivros;

    @Getter
    private T entity;
    public BaseStrategy(T entity) {
        this.entity = entity;
    }

}
