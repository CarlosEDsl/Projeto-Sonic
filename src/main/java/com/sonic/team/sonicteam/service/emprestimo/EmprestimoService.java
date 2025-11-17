package com.sonic.team.sonicteam.service.emprestimo;

import com.sonic.team.sonicteam.exception.EmprestimoInvalido;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.service.estoque.IEstoqueEmprestimoService;
import com.sonic.team.sonicteam.service.estoque.IEstoqueService;
import com.sonic.team.sonicteam.service.usuario.IUsuarioEmprestimoService;
import com.sonic.team.sonicteam.strategies.AlunoEmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.BaseStrategy;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmprestimoService implements IEmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final IUsuarioEmprestimoService usuarioService;
    private final IEstoqueEmprestimoService estoqueService;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, IUsuarioEmprestimoService usuarioService, IEstoqueEmprestimoService estoqueService) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioService = usuarioService;
        this.estoqueService = estoqueService;
    }

    @Transactional
    public Emprestimo criarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO) {
        try{
            Usuario usuario = this.usuarioService.pegarUsuarioPorCPF(emprestimoRequestDTO.cpfUsuario());
            EmprestimoStrategy strategy = usuario.getEmprestimoStrategy();
            Estoque estoque = this.estoqueService.pegarUmExemplarDisponivel(emprestimoRequestDTO.livroISBN());
            Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);



            if(strategy.pegarLimiteEmprestimos() < 3){}

            return this.emprestimoRepository.save(emprestimo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Tratar melhor os resultados negativos
    @Transactional(readOnly = true)
    public Emprestimo buscarEmprestimoPorId(Long id) {
        return this.emprestimoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    @Transactional
    public Emprestimo atualizarEmprestimo(Long id, Emprestimo dadosAtualizados) {
        return emprestimoRepository.findById(id)
                .map(existing -> {
                    existing.setUsuario(dadosAtualizados.getUsuario());
                    existing.setEstoque(dadosAtualizados.getEstoque());
                    existing.setDataEmprestimo(dadosAtualizados.getDataEmprestimo());
                    existing.setDataDevolucao(dadosAtualizados.getDataDevolucao());
                    return emprestimoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
    }

    @Transactional
    public boolean excluirEmprestimo(Long id) {
        if (emprestimoRepository.existsById(id)) {
            emprestimoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void verifyEmprestimo(Emprestimo emprestimo) {
        if(emprestimo.getUsuario().getStatus() == Status.Inactive) {
            throw new EmprestimoInvalido("O usuário está inativado");
        }
    }
}
