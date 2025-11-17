package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.strategies.AlunoEmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ALUNO")
public class Aluno extends Usuario {
    @Override
    public EmprestimoStrategy getEmprestimoStrategy() {
        return new AlunoEmprestimoStrategy(this);
    }
}
