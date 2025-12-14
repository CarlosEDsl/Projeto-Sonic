package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.model.DTO.Usuario.FiltroUsuarioDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUsuarioService {
    
    UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto);
    
    Page<UsuarioResponseDTO> listarTodos(Pageable pageable);
    
    List<UsuarioResponseDTO> listarTodos();
    
    UsuarioResponseDTO buscarPorCpf(String cpf);
    
    UsuarioResponseDTO atualizar(String cpfPath, UsuarioRequestDTO dto);
    
    void deletar(String cpf);
    
    UsuarioResponseDTO suspender(String cpf, String motivo);
    
    UsuarioResponseDTO reativar(String cpf);
    
    UsuarioResponseDTO inativar(String cpf);
    
    Page<UsuarioResponseDTO> buscarComFiltros(FiltroUsuarioDTO filtro, Pageable pageable);
}
