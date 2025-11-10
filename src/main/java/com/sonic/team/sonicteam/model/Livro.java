package com.sonic.team.sonicteam.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "livro",
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

    public boolean validar(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("O livro não pode ser nulo.");
        }

        if (livro.getIsbn() == null || livro.getIsbn().isBlank()) {
            throw new IllegalArgumentException("O ISBN é obrigatório.");
        }

        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }

        if (livro.getAutor() == null || livro.getAutor().isBlank()) {
            throw new IllegalArgumentException("O autor é obrigatório.");
        }

        if (livro.getEditora() == null || livro.getEditora().isBlank()) {
            throw new IllegalArgumentException("A editora é obrigatória.");
        }

        if (livro.getEdicao() == null || livro.getEdicao().isBlank()) {
            throw new IllegalArgumentException("A edição é obrigatória.");
        }

        if (livro.getCategoria() == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        return true;
    }
}
