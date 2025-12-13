package com.sonic.team.sonicteam.service.catalogo;

import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;

import java.util.List;

public interface ICategoriaUsuarioService {
    CategoriaUsuario buscarPorId(Long id);
    List<CategoriaUsuario> listar();
}
