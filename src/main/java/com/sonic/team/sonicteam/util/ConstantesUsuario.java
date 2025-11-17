package com.sonic.team.sonicteam.util;

public final class ConstantesUsuario {
    
    private ConstantesUsuario() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
    
    // Paginação
    public static final int TAMANHO_PAGINA_PADRAO = 20;
    public static final String ORDENACAO_PADRAO = "nome";
    
    // Validação CPF
    public static final String REGEX_CPF = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
    public static final String MENSAGEM_CPF_FORMATO_INVALIDO = "CPF deve estar no formato válido";
    public static final String MENSAGEM_CPF_INVALIDO = "CPF inválido";
    
    // Validação Tipo
    public static final String REGEX_TIPO_USUARIO = "ALUNO|PROFESSOR|BIBLIOTECARIO";
    public static final String MENSAGEM_TIPO_INVALIDO = "Tipo deve ser ALUNO, PROFESSOR ou BIBLIOTECARIO";
}
