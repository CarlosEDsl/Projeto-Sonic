package com.sonic.team.sonicteam.model.usuarios;

import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.ProfessorEmprestimoStrategy;

public class Professor extends Usuario {

    @Override
    public EmprestimoStrategy getEmprestimoStrategy() {
        return new ProfessorEmprestimoStrategy(this);
    }
}
