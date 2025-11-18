package com.sonic.team.sonicteam.service.emprestimo;

import com.sonic.team.sonicteam.exception.EmprestimoInvalido;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.service.estoque.IEstoqueEmprestimoService;
import com.sonic.team.sonicteam.service.usuario.IUsuarioEmprestimoService;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//Princípios: SRP (explicar responsabilidade), DIP (depende de interfaces IUsuarioEmprestimoService e IEstoqueEmprestimoService) e OCP (usa estratégias de empréstimo).
//Esta classe orquestra o fluxo de empréstimo — delega verificação de usuário/exemplar para serviços abstratos e usa a estratégia do usuário para criar o empréstimo. Mantemos aqui a coordenação (não a lógica detalhada), o que facilita testar e trocar implementações.
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

            this.verifyEmprestimo(emprestimo, strategy.pegarLimiteEmprestimos());

            return this.emprestimoRepository.save(emprestimo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public Emprestimo buscarEmprestimoPorId(Long id) {
        return this.emprestimoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    @Transactional
    public Emprestimo devolverEmprestimo(Long id) {
        try{
            Emprestimo emprestimo = this.buscarEmprestimoPorId(id);
            emprestimo.setDataEntrega(LocalDateTime.now());

            this.estoqueService.atualizarDisponibilidadeExemplar(
                    new AtualizarEstoqueResquestDTO(emprestimo.getEstoque().getId(), true)
            );

            return this.emprestimoRepository.save(emprestimo);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public long contarEmprestimosAtivosPorUsuario(Long usuarioId) {
        return emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(usuarioId);
    }

    private void verifyEmprestimo(Emprestimo emprestimo, int limiteEmprestimos) {
        long quantidadeEmprestimosAtivos = this.emprestimoRepository
                .countByUsuarioIdAndDataEntregaIsNull(emprestimo.getUsuario().getId());

        if(emprestimo.getUsuario().getStatus() == StatusUsuario.INATIVO
                || emprestimo.getUsuario().getStatus() == StatusUsuario.SUSPENSO) {
            throw new EmprestimoInvalido("O usuário está inativado");
        }
        if(quantidadeEmprestimosAtivos + 1 > limiteEmprestimos) {
            throw new EmprestimoInvalido("O usuário está tentando pegar mais livros do que é permitido");
        }
    }
}
