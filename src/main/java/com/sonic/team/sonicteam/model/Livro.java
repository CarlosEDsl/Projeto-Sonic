package com.sonic.team.sonicteam.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livro {

    @Id
    private String isbn;

    private String titulo;
    private String autor;
    private String editora;
    private String edicao;
    private Categoria categoria;
    private boolean disponivel;
}
