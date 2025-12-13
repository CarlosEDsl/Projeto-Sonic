package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import com.sonic.team.sonicteam.strategies.ProfessorEmprestimoStrategy;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PROFESSOR")
public class Professor extends Usuario {

    @Override
    public PoliticaEmprestimo getPoliticaEmprestimo() {
        return new ProfessorEmprestimoStrategy(this);
    }
    
    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.PROFESSOR;
    }
}
