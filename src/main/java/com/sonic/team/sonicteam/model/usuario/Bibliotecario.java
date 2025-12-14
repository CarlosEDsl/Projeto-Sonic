package com.sonic.team.sonicteam.model.usuario;

import com.sonic.team.sonicteam.strategies.BibliotecarioPolitica;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BIBLIOTECARIO")
public class Bibliotecario extends Usuario {

    @Override
    public PoliticaEmprestimo getPoliticaEmprestimo() {
        return new BibliotecarioPolitica(this);
    }
    
    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.BIBLIOTECARIO;
    }
}
