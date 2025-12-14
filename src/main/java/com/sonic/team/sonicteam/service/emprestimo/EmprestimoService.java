package com.sonic.team.sonicteam.service.emprestimo;

import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.service.estoque.IEstoqueEmprestimoService;
import com.sonic.team.sonicteam.service.usuario.IUsuarioEmprestimoService;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import com.sonic.team.sonicteam.validation.EmprestimoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmprestimoService implements IEmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final IUsuarioEmprestimoService usuarioService;
    private final IEstoqueEmprestimoService estoqueService;
    private final EmprestimoValidator emprestimoValidator;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, 
                            IUsuarioEmprestimoService usuarioService, 
                            IEstoqueEmprestimoService estoqueService,
                            EmprestimoValidator emprestimoValidator) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioService = usuarioService;
        this.estoqueService = estoqueService;
        this.emprestimoValidator = emprestimoValidator;
    }

    @Transactional
    public Emprestimo criarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO) {
        Usuario usuario = this.usuarioService.pegarUsuarioPorCPF(emprestimoRequestDTO.cpfUsuario());
        PoliticaEmprestimo politica = usuario.getPoliticaEmprestimo();
        
        emprestimoValidator.validarEmprestimo(usuario, politica);
        
        EmprestimoStrategy strategy = (EmprestimoStrategy) politica;
        Estoque estoque = this.estoqueService.pegarUmExemplarDisponivel(emprestimoRequestDTO.livroISBN());
        Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);

        return this.emprestimoRepository.save(emprestimo);
    }

    @Transactional(readOnly = true)
    public Emprestimo buscarEmprestimoPorId(Long id) {
        return this.emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Emprestimo não encontrado com o id " + id));
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    @Transactional
    public Emprestimo devolverEmprestimo(Long id) {
        Emprestimo emprestimo = this.buscarEmprestimoPorId(id);
        emprestimo.setDataEntrega(LocalDateTime.now());

        this.estoqueService.atualizarDisponibilidadeExemplar(
                new AtualizarEstoqueResquestDTO(emprestimo.getEstoque().getId(), true)
        );

        return this.emprestimoRepository.save(emprestimo);
    }

    @Transactional(readOnly = true)
    public long contarEmprestimosAtivosPorUsuario(Long usuarioId) {
        return emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(usuarioId);
    }
}
