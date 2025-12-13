package com.sonic.team.sonicteam.service.curso;

import com.sonic.team.sonicteam.model.Curso;

import java.util.List;

public interface ICursoService {
    
    Curso buscarPorId(Long id);
    
    List<Curso> listar();
}
