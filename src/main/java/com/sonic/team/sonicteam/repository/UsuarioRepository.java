package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sonic.team.sonicteam.model.usuario.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    
    // Buscar por status
    Page<Usuario> findByStatus(StatusUsuario status, Pageable pageable);
    
    // Buscar usuários ativos
    Page<Usuario> findByStatusNot(StatusUsuario status, Pageable pageable);
    
    // Contar por status
    long countByStatus(StatusUsuario status);
}
