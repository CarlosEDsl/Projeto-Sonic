package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    long countByUsuarioIdAndDataEntregaIsNull(Long usuarioId);

    long countByUsuarioIdAndDataEntregaIsNullAndDataDevolucaoBefore(Long usuarioId, LocalDateTime data);
}
