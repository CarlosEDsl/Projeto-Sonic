package com.sonic.team.sonicteam.model.catalogos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CategoriaLivro {
    ROMANCE,
    COMPUTACAO,
    LETRAS,
    GESTAO;


    @JsonCreator
    public static CategoriaLivro from(String value) {
        if (value == null) return null;
        try {
            return CategoriaLivro.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Categoria informada não existe: " + value);
        }
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}