package com.sonic.team.sonicteam.model.DTO.Emprestimo;

import java.time.LocalDateTime;

public record EmprestimoResponseDTO(
        Long id,
        Long usuarioId,
        String usuarioNome,
        String usuarioCategoria,
        String livroTitulo,
        String livroIsbn,
        Long estoqueId,
        LocalDateTime dataEmprestimo,
        LocalDateTime dataDevolucaoPrevista
) {}