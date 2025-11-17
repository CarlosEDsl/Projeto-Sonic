package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.strategies.BibliotecarioEmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BIBLIOTECARIO")
public class Bibliotecario extends Usuario {

    @Override
    public EmprestimoStrategy getEmprestimoStrategy() {
        return new BibliotecarioEmprestimoStrategy(this);
    }
}
