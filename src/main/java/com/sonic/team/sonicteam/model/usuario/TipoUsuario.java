package com.sonic.team.sonicteam.model.usuario;

public enum TipoUsuario {
    ALUNO("ALUNO"),
    PROFESSOR("PROFESSOR"),
    BIBLIOTECARIO("BIBLIOTECARIO");

    private final String valor;

    TipoUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoUsuario fromString(String texto) {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.valor.equalsIgnoreCase(texto)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário inválido: " + texto);
    }
}
