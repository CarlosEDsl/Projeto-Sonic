package com.sonic.team.sonicteam.model.usuarios;

import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Usuario {
    @Id
    private Long id;

    @Column
    private String categoria;

    @Column
    private String tipo;

    @Column
    private Status status;

    public abstract EmprestimoStrategy getEmprestimoStrategy();
}
