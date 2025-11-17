package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.repository.CategoriaUsuarioRepository;
import com.sonic.team.sonicteam.util.MensagensUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaUsuarioService {
    
    private final CategoriaUsuarioRepository categoriaUsuarioRepository;

    public CategoriaUsuarioService(CategoriaUsuarioRepository categoriaUsuarioRepository) {
        this.categoriaUsuarioRepository = categoriaUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public CategoriaUsuario buscarPorId(Long id) {
        return categoriaUsuarioRepository.findById(id)
                .orElseThrow(() -> new DadoInvalidoException(MensagensUsuario.CATEGORIA_INEXISTENTE));
    }
}
