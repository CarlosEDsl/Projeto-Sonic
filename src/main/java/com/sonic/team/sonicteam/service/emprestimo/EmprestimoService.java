package com.sonic.team.sonicteam.service.emprestimo;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmprestimoService implements IEmprestimoService {

    private final EmprestimoRepository emprestimoRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    // TODO: Implementar os serviços para buscar os usuários e exemplares para testar validade e salvar.
    @Transactional
    public Emprestimo criarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO) {

        Usuario usuario = new Usuario();
        Estoque estoque = new Estoque();

        LocalDateTime dataDevolucao = LocalDateTime.now();

        Emprestimo emprestimo = new Emprestimo(usuario, estoque, dataDevolucao);

        return this.emprestimoRepository.save(emprestimo);
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
}
