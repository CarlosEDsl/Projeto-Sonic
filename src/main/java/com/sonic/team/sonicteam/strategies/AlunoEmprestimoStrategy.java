package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.usuario.Aluno;

import java.time.LocalDateTime;

public class AlunoEmprestimoStrategy extends BaseStrategy<Aluno> {

    private static final int PRAZO_PADRAO_DIAS = 15;
    private static final int PRAZO_AREA_CURSO_DIAS = 30;
    private static final int LIMITE_EMPRESTIMOS = 3;

    public AlunoEmprestimoStrategy(Aluno entity) {
        super(entity, LIMITE_EMPRESTIMOS);
    }

    @Override
    public LocalDateTime calcularPrazo(Livro livro) {
        int dias = isLivroDaArea(livro) ? PRAZO_AREA_CURSO_DIAS : PRAZO_PADRAO_DIAS;
        return LocalDateTime.now().plusDays(dias);
    }

    private boolean isLivroDaArea(Livro livro) {
        // Compara o CURSO do aluno com a CATEGORIA do livro
        // Ex: Aluno de "COMPUTACAO" pegando livro de "COMPUTACAO" tem 30 dias
        return getEntity().getCurso().getNome().equalsIgnoreCase(livro.getCategoriaLivro().toString());
    }
}

