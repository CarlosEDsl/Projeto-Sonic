package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {
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

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_usuario_id")
    private CategoriaUsuario categoria;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id")
    private Curso curso;
}
