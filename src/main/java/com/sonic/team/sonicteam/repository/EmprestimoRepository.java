package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    long countByUsuarioIdAndDataDevolucaoAfter(Long usuarioId, LocalDateTime agora);
}
