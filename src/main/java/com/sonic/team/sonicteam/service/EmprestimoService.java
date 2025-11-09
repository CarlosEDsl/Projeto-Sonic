package com.sonic.team.sonicteam.service;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional
    public Emprestimo criarEmprestimo(Emprestimo emprestimo) {
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional(readOnly = true)
    public Optional<Emprestimo> buscarEmprestimoPorId(Long id) {
        return emprestimoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    @Transactional
    public Optional<Emprestimo> atualizarEmprestimo(Long id, Emprestimo dadosAtualizados) {
        return emprestimoRepository.findById(id).map(existing -> {
            existing.setUsuario(dadosAtualizados.getUsuario());
            existing.setEstoque(dadosAtualizados.getEstoque());
            existing.setDataEmprestimo(dadosAtualizados.getDataEmprestimo());
            existing.setDataDevolucao(dadosAtualizados.getDataDevolucao());
            return emprestimoRepository.save(existing);
        });
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
