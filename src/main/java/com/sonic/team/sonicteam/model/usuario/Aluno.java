package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.strategies.AlunoEmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ALUNO")
public class Aluno extends Usuario {
    
    @Override
    public PoliticaEmprestimo getPoliticaEmprestimo() {
        return new AlunoEmprestimoStrategy(this);
    }
    
    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.ALUNO;
    }
}
