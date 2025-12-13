package com.sonic.team.sonicteam.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErroResponse {
    
    private final int status;
    private final String erro;
    private final String mensagem;
    private final LocalDateTime timestamp;
    private final List<Map<String, String>> erros;

    private ErroResponse(Builder builder) {
        this.status = builder.status;
        this.erro = builder.erro;
        this.mensagem = builder.mensagem;
        this.timestamp = LocalDateTime.now();
        this.erros = builder.erros;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<Map<String, String>> getErros() {
        return erros;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int status;
        private String erro;
        private String mensagem;
        private List<Map<String, String>> erros;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder erro(String erro) {
            this.erro = erro;
            return this;
        }

        public Builder mensagem(String mensagem) {
            this.mensagem = mensagem;
            return this;
        }

        public Builder erros(List<Map<String, String>> erros) {
            this.erros = erros;
            return this;
        }

        public ErroResponse build() {
            return new ErroResponse(this);
        }
    }
}
