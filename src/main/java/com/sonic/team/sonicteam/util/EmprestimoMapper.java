package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoResponseDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmprestimoMapper {

    public EmprestimoResponseDTO paraResponse(Emprestimo emprestimo) {
        Long usuarioId = emprestimo.getUsuario() != null 
            ? emprestimo.getUsuario().getId() 
            : null;
        String usuarioNome = emprestimo.getUsuario() != null 
            ? emprestimo.getUsuario().getNome() 
            : null;
        String usuarioCategoria = (emprestimo.getUsuario() != null && emprestimo.getUsuario().getCategoria() != null)
            ? emprestimo.getUsuario().getCategoria().getNome()
            : null;

        String livroTitulo = (emprestimo.getEstoque() != null && emprestimo.getEstoque().getLivro() != null)
            ? emprestimo.getEstoque().getLivro().getTitulo()
            : null;
        String livroIsbn = (emprestimo.getEstoque() != null && emprestimo.getEstoque().getLivro() != null)
            ? emprestimo.getEstoque().getLivro().getIsbn()
            : null;

        Long estoqueId = emprestimo.getEstoque() != null 
            ? emprestimo.getEstoque().getId() 
            : null;

        return new EmprestimoResponseDTO(
            emprestimo.getId(),
            usuarioId,
            usuarioNome,
            usuarioCategoria,
            livroTitulo,
            livroIsbn,
            estoqueId,
            emprestimo.getDataEmprestimo(),
            emprestimo.getDataDevolucao(),
            emprestimo.getDataEntrega()
        );
    }

    public List<EmprestimoResponseDTO> paraListaResponse(List<Emprestimo> emprestimos) {
        return emprestimos.stream()
            .map(this::paraResponse)
            .collect(Collectors.toList());
    }
}
