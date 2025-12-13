package com.sonic.team.sonicteam.util;

public final class ConstantesUsuario {
    
    private ConstantesUsuario() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
    
    // === PAGINAÇÃO ===
    public static final int TAMANHO_PAGINA_PADRAO = 20;
    public static final String ORDENACAO_PADRAO = "nome";
    
    // === VALIDAÇÃO CPF ===
    public static final String REGEX_CPF = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
    public static final String MENSAGEM_CPF_FORMATO_INVALIDO = "CPF deve estar no formato válido";
    public static final String MENSAGEM_CPF_INVALIDO = "CPF inválido";
    
    // === VALIDAÇÃO TIPO ===
    public static final String REGEX_TIPO_USUARIO = "ALUNO|PROFESSOR|BIBLIOTECARIO";
    public static final String MENSAGEM_TIPO_INVALIDO = "Tipo deve ser ALUNO, PROFESSOR ou BIBLIOTECARIO";
    
    // === MENSAGENS DE ERRO - VALIDAÇÃO ===
    public static final String CPF_DUPLICADO = "CPF duplicado";
    public static final String CATEGORIA_INEXISTENTE = "Categoria inexistente";
    public static final String CURSO_INEXISTENTE = "Curso inexistente";
    public static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    public static final String CPF_NAO_PODE_SER_ALTERADO = "CPF não pode ser alterado";
    public static final String TIPO_USUARIO_NAO_PODE_SER_ALTERADO = "Tipo de usuário não pode ser alterado";
    public static final String TIPO_USUARIO_INVALIDO = "Tipo de usuário inválido: %s";
    
    // === MENSAGENS DE ERRO - STATUS ===
    public static final String USUARIO_JA_SUSPENSO = "Usuário já está suspenso";
    public static final String USUARIO_JA_ATIVO = "Usuário já está ativo";
    public static final String USUARIO_JA_INATIVO = "Usuário já está inativo";
    
    // === MENSAGENS DE ERRO - EMPRÉSTIMOS ===
    public static final String USUARIO_COM_EMPRESTIMOS_PENDENTES = "Não é possível deletar usuário com empréstimos pendentes";
}

