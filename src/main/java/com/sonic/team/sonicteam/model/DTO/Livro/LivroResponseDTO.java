package com.sonic.team.sonicteam.model.DTO.Livro;

public record LivroResponseDTO(
        String isbn,
        String titulo,
        String autor,
        String editora,
        String edicao,
        String categoria
) {}
