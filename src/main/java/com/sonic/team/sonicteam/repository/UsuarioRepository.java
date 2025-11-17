package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Page<Usuario> findByStatus(StatusUsuario status, Pageable pageable);
    Page<Usuario> findByStatusNot(StatusUsuario status, Pageable pageable);
    long countByStatus(StatusUsuario status);

        @Query("""
                        SELECT u FROM Usuario u
                        WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
                            AND (:cpf IS NULL OR u.cpf = :cpf)
                            AND (:status IS NULL OR u.status = :status)
                            AND (:categoriaId IS NULL OR u.categoria.id = :categoriaId)
                            AND (:cursoId IS NULL OR u.curso.id = :cursoId)
                        """)
        Page<Usuario> buscarComFiltros(@Param("nome") String nome,
                                                                     @Param("cpf") String cpf,
                                                                     @Param("status") StatusUsuario status,
                                                                     @Param("categoriaId") Long categoriaId,
                                                                     @Param("cursoId") Long cursoId,
                                                                     Pageable pageable);
}
