package com.sonic.team.sonicteam.model;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoResponseDTO;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emprestimo")
public class Emprestimo {

    public Emprestimo(Usuario usuario, Estoque estoque, LocalDateTime dataDevolucao) {
        this.usuario = usuario;
        this.estoque = estoque;
        this.dataDevolucao = dataDevolucao;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;

    @Column(nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column
    private LocalDateTime dataDevolucao;

    private LocalDateTime dataEntrega;

    public EmprestimoResponseDTO toResponseDTO() {
        Long usuarioId = this.usuario != null ? this.usuario.getId() : null;
        String usuarioNome = this.usuario != null ? this.usuario.getNome() : null;
        String usuarioCategoria = (this.usuario != null && this.usuario.getCategoria() != null)
                ? this.usuario.getCategoria().getNome()
                : null;

        String livroId = (this.estoque != null && this.estoque.getLivro() != null)
                ? this.estoque.getLivro().getIsbn()
                : null;
        String livroTitulo = (this.estoque != null && this.estoque.getLivro() != null)
                ? this.estoque.getLivro().getTitulo()
                : null;
        String livroIsbn = (this.estoque != null && this.estoque.getLivro() != null)
                ? this.estoque.getLivro().getIsbn()
                : null;

        Long estoqueId = this.estoque != null ? this.estoque.getId() : null;

        return new EmprestimoResponseDTO(
                this.id,
                usuarioId,
                usuarioNome,
                usuarioCategoria,
                livroTitulo,
                livroIsbn,
                estoqueId,
                this.dataEmprestimo,
                this.dataDevolucao
        );
    }
}
