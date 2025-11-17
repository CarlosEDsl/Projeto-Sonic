package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.strategies.AlunoEmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;

public class Aluno extends Usuario {
    @Override
    public EmprestimoStrategy getEmprestimoStrategy() {
        return new AlunoEmprestimoStrategy(this);
    }
}
