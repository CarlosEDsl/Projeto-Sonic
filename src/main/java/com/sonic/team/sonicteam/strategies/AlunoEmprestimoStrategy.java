package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.Usuario;

import java.time.LocalDateTime;

public class AlunoEmprestimoStrategy extends BaseStrategy<Aluno> {

    private static final int PRAZO_PADRAO_DIAS = 15;
    private static final int PRAZO_AREA_CURSO_DIAS = 30;
    private static final int LIMITE_EMPRESTIMOS = 3;

    public AlunoEmprestimoStrategy(Aluno entity) {
        super(entity);
    }

    @Override
    public Emprestimo pegarEmprestimo(Estoque estoque) {
        Livro livro = estoque.getLivro();

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setEstoque(estoque);
        emprestimo.setUsuario(this.getEntity());
        emprestimo.setDataEmprestimo(LocalDateTime.now());

        LocalDateTime prazo = calcularPrazo(livro);
        emprestimo.setDataDevolucao(prazo);

        return emprestimo;
    }

    @Override
    public LocalDateTime calcularPrazo(Livro livro) {
        int dias;
        if (isLivroDaArea(livro)) {
            dias = PRAZO_AREA_CURSO_DIAS;
        } else {
            dias = PRAZO_PADRAO_DIAS;
        }
        return LocalDateTime.now().plusDays(dias);
    }

    public int pegarLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }

    private boolean isLivroDaArea(Livro livro) {
        Usuario usuario = this.getEntity();

        return usuario.getCategoria().equals(livro.getCategoriaLivro().toString());
    }
}
