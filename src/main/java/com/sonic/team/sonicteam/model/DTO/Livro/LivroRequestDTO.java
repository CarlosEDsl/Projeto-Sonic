package com.sonic.team.sonicteam.model.DTO.Livro;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record LivroRequestDTO(

        @NotBlank(message = "ISBN é obrigatório.")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN deve ter 10 ou 13 dígitos.")
        String isbn,

        @NotBlank(message = "O título é obrigatório.")
        @Size(min = 3, message = "O título deve ter ao menos 3 caracteres.")
        String titulo,

        @NotBlank(message = "O autor é obrigatório.")
        String autor,

        @NotBlank(message = "A editora é obrigatória.")
        String editora,

        @NotBlank(message = "A edição é obrigatória.")
        @Pattern(regexp = "\\d+", message = "A edição deve ser apenas números.")
        String edicao,

        @NotNull(message = "A categoria é obrigatória.")
        CategoriaLivro categoriaLivro
) {}

