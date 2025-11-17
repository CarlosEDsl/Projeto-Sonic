package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public abstract EmprestimoStrategy getEmprestimoStrategy();
    
    public boolean isAtivo() {
        return this.status == StatusUsuario.ATIVO;
    }
    
    public TipoUsuario getTipoUsuario() {
        if (this instanceof Aluno) {
            return TipoUsuario.ALUNO;
        } else if (this instanceof Professor) {
            return TipoUsuario.PROFESSOR;
        } else if (this instanceof Bibliotecario) {
            return TipoUsuario.BIBLIOTECARIO;
        }
        throw new IllegalStateException("Tipo de usuário desconhecido");
    }
}
