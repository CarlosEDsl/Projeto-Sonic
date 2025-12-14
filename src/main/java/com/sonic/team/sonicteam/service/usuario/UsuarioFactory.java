package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.Bibliotecario;
import com.sonic.team.sonicteam.model.usuario.Professor;
import com.sonic.team.sonicteam.model.usuario.TipoUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.util.ConstantesUsuario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UsuarioFactory {
    private final ModelMapper modelMapper;

    public UsuarioFactory(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Usuario criar(UsuarioRequestDTO dto) {
        TipoUsuario tipo;
        try {
            tipo = TipoUsuario.fromString(dto.getTipo());
        } catch (IllegalArgumentException e) {
            throw new DadoInvalidoException(
                String.format(ConstantesUsuario.TIPO_USUARIO_INVALIDO, dto.getTipo())
            );
        }

        return switch (tipo) {
            case ALUNO -> modelMapper.map(dto, Aluno.class);
            case PROFESSOR -> modelMapper.map(dto, Professor.class);
            case BIBLIOTECARIO -> modelMapper.map(dto, Bibliotecario.class);
        };
    }
}
