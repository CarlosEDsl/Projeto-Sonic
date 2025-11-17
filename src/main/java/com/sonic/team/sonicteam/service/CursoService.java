package com.sonic.team.sonicteam.service;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.repository.CursoRepository;
import com.sonic.team.sonicteam.util.MensagensUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {
    
    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public Curso buscarPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new DadoInvalidoException(MensagensUsuario.CURSO_INEXISTENTE));
    }
}
