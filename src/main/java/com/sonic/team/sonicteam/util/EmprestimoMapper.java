package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoResponseDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EmprestimoMapper {

    public EmprestimoResponseDTO paraResponse(Emprestimo emprestimo) {
        Usuario usuario = emprestimo.getUsuario();
        Estoque estoque = emprestimo.getEstoque();
        Livro livro = estoque != null ? estoque.getLivro() : null;

        return new EmprestimoResponseDTO(
            emprestimo.getId(),
            extrair(usuario, Usuario::getId),
            extrair(usuario, Usuario::getNome),
            usuario != null ? extrair(usuario.getCategoria(), c -> c.getNome()) : null,
            extrair(livro, Livro::getTitulo),
            extrair(livro, Livro::getIsbn),
            extrair(estoque, Estoque::getId),
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

    private <T, R> R extrair(T objeto, Function<T, R> extrator) {
        return objeto != null ? extrator.apply(objeto) : null;
    }
}

