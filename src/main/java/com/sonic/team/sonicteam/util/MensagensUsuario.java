package com.sonic.team.sonicteam.util;

public final class MensagensUsuario {
    
    private MensagensUsuario() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
    
    // Mensagens de erro - Validação
    public static final String CPF_DUPLICADO = "CPF duplicado";
    public static final String CATEGORIA_INEXISTENTE = "Categoria inexistente";
    public static final String CURSO_INEXISTENTE = "Curso inexistente";
    public static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    public static final String CPF_NAO_PODE_SER_ALTERADO = "CPF não pode ser alterado";
    public static final String TIPO_USUARIO_NAO_PODE_SER_ALTERADO = "Tipo de usuário não pode ser alterado";
    public static final String TIPO_USUARIO_INVALIDO = "Tipo de usuário inválido: %s";
    
    // Mensagens de erro - Status
    public static final String USUARIO_JA_SUSPENSO = "Usuário já está suspenso";
    public static final String USUARIO_JA_ATIVO = "Usuário já está ativo";
    public static final String USUARIO_JA_INATIVO = "Usuário já está inativo";
    
    // Mensagens de erro - Empréstimos
    public static final String USUARIO_COM_EMPRESTIMOS_PENDENTES = "Não é possível deletar usuário com empréstimos pendentes";
}
