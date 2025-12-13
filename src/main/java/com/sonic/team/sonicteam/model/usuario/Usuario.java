package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario", indexes = {
    @Index(name = "idx_usuario_cpf", columnList = "cpf")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_usuario_id")
    private CategoriaUsuario categoria;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Column
    private LocalDateTime suspensoAte;

    public abstract PoliticaEmprestimo getPoliticaEmprestimo();
    
    public abstract TipoUsuario getTipoUsuario();
    
    public boolean isAtivo() {
        return this.status == StatusUsuario.ATIVO;
    }
}

