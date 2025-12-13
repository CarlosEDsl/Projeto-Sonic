package com.sonic.team.sonicteam.service.curso;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.repository.CursoRepository;
import com.sonic.team.sonicteam.util.MensagensUsuario;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService implements ICursoService {
    
    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public Curso buscarPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new DadoInvalidoException(MensagensUsuario.CURSO_INEXISTENTE));
    }

    @Transactional(readOnly = true)
    public List<Curso> listar(){
        if(cursoRepository.findAll().isEmpty()){
            throw new RecursoNaoEncontradoException("Não há nenhuma categoria cadastrada.");
        }
        return cursoRepository.findAll();
    }
}
