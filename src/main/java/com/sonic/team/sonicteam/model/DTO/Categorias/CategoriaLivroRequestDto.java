package com.sonic.team.sonicteam.model.DTO.Categorias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaLivroRequestDto(
        @NotBlank
        @Pattern(regexp = "[A-Za-zÀ-ú0-9 ]+", message = "O nome da categoria deve conter apenas letras, números e espaços.")
        @Size(min = 3, message = "O nome da categoria deve ter ao menos 3 caracteres.")
        String nome
) { }
