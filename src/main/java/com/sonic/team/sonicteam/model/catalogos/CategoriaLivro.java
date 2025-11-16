package com.sonic.team.sonicteam.model.catalogos;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CategoriaLivro {
    ROMANCE,
    COMPUTACAO,
    LETRAS,
    GESTAO
}