package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    
    @EntityGraph(attributePaths = {"categoria", "curso"})
    Optional<Usuario> findByCpf(String cpf);
    
    boolean existsByCpf(String cpf);
    
    @EntityGraph(attributePaths = {"categoria", "curso"})
    Page<Usuario> findByStatus(StatusUsuario status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"categoria", "curso"})
    Page<Usuario> findByStatusNot(StatusUsuario status, Pageable pageable);
    
    long countByStatus(StatusUsuario status);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.categoria LEFT JOIN FETCH u.curso")
    List<Usuario> findAllWithRelationships();
    
    @EntityGraph(attributePaths = {"categoria", "curso"})
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Usuario> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);
}
