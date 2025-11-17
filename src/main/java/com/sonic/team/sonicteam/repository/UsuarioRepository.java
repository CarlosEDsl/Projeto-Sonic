package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Page<Usuario> findByStatus(StatusUsuario status, Pageable pageable);
    Page<Usuario> findByStatusNot(StatusUsuario status, Pageable pageable);
    long countByStatus(StatusUsuario status);
}
