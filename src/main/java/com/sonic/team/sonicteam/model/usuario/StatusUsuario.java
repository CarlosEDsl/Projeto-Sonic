package com.sonic.team.sonicteam.model.usuario;

import java.util.Set;

public enum StatusUsuario {
    ATIVO(Set.of("SUSPENSO", "INATIVO")),
    INATIVO(Set.of("ATIVO")),
    SUSPENSO(Set.of("ATIVO"));

    private final Set<String> transicoesPermitidas;

    StatusUsuario(Set<String> transicoesPermitidas) {
        this.transicoesPermitidas = transicoesPermitidas;
    }

    public boolean podeTransicionarPara(StatusUsuario destino) {
        return transicoesPermitidas.contains(destino.name());
    }

    public void validarTransicao(StatusUsuario destino) {
        if (!podeTransicionarPara(destino)) {
            throw new IllegalStateException(
                String.format("Transição inválida de %s para %s", this.name(), destino.name())
            );
        }
    }
}
