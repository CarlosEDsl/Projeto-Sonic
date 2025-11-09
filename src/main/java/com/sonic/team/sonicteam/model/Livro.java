package com.sonic.team.sonicteam.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "livro",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"autor", "edicao", "editora"})
        }
)

public class Livro {

    @Id
    private String isbn;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false)
    private String editora;

    @Column(nullable = false)
    private String edicao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private CategoriaLivro categoria;

    @Column(nullable = false)
    private boolean disponivel = true;
}
