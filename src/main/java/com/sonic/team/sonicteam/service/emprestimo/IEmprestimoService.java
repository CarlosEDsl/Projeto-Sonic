package com.sonic.team.sonicteam.service.emprestimo;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;

import java.util.List;

public interface IEmprestimoService {
    Emprestimo criarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO);
    Emprestimo buscarEmprestimoPorId(Long id);
    List<Emprestimo> listarEmprestimos();
    Emprestimo devolverEmprestimo(Long id);
    long contarEmprestimosAtivosPorUsuario(Long usuarioId);
}
