package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.usuario.Usuario;
import lombok.Getter;

//Princípios: LSP (subclasses mantêm o contrato) e SRP (base cuida de aspectos comuns).
//Classe base para reduzir duplicação entre estratégias; mantém estado comum (entidade) e permite que subclasses implementem diferenças
public abstract class BaseStrategy <T extends Usuario> implements EmprestimoStrategy {
    private int limiteLivros;

    @Getter
    private T entity;
    public BaseStrategy(T entity) {
        this.entity = entity;
    }

}
