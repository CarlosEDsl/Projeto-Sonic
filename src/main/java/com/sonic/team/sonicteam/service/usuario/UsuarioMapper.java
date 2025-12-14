package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    private final ModelMapper modelMapper;

    public UsuarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = modelMapper.map(usuario, UsuarioResponseDTO.class);
        dto.setId(usuario.getId());
        dto.setStatus(usuario.getStatus().name());
        dto.setCategoria(usuario.getCategoria().getNome());
        dto.setCurso(usuario.getCurso().getNome());
        dto.setTipo(usuario.getTipoUsuario().getValor());
        return dto;
    }
}
