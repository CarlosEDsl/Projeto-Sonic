package com.sonic.team.sonicteam.model;

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

    @Id
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;

    @Column(nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column()
    private LocalDateTime dataDevolucao;
}
