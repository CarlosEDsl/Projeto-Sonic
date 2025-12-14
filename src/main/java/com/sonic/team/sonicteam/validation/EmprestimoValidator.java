package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.EmprestimoInvalido;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoValidator {

    private final EmprestimoRepository emprestimoRepository;

    public EmprestimoValidator(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    public void validarPoliticaEmprestimo(PoliticaEmprestimo politica) {
        if (!politica.podeRealizarEmprestimo()) {
            throw new EmprestimoInvalido("Este tipo de usuário não pode realizar empréstimos");
        }
    }

    public void validarStatusUsuario(Usuario usuario) {
        if (usuario.getStatus() == StatusUsuario.INATIVO) {
            throw new EmprestimoInvalido("O usuário está inativo");
        }
        
        if (usuario.getStatus() == StatusUsuario.SUSPENSO) {
            throw new EmprestimoInvalido("O usuário está suspenso");
        }
    }

    public void validarLimiteEmprestimos(Usuario usuario, int limiteEmprestimos) {
        long quantidadeAtivos = emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(usuario.getId());
        
        if (quantidadeAtivos >= limiteEmprestimos) {
            throw new EmprestimoInvalido(
                String.format("O usuário já atingiu o limite de %d empréstimos ativos", limiteEmprestimos)
            );
        }
    }

    public void validarEmprestimo(Usuario usuario, PoliticaEmprestimo politica) {
        validarPoliticaEmprestimo(politica);
        validarStatusUsuario(usuario);
        validarLimiteEmprestimos(usuario, politica.pegarLimiteEmprestimos());
    }
}
