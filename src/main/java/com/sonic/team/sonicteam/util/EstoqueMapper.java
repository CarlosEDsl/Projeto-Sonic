package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;

import java.util.ArrayList;
import java.util.List;

public class EstoqueMapper {
    public static Estoque ToEntity(EstoqueResponseDTO estoqueResponseDTO, Livro livro) {
        return new Estoque(estoqueResponseDTO.id(), livro, estoqueResponseDTO.disponivel());
    }

    public static EstoqueResponseDTO ToResponseDTO(Estoque estoque) {
        return new EstoqueResponseDTO(estoque.getId(), estoque.getLivro().getIsbn(), estoque.getDisponivel());
    }

    public static List<EstoqueResponseDTO> ToListResponseDTO(List<Estoque> estoques) {
        List<EstoqueResponseDTO> list = new ArrayList<>();
        for(Estoque estoque : estoques) {
            var estoqueResponseDTO = ToResponseDTO(estoque);
            list.add(estoqueResponseDTO);
        }
        return list;
    }
}
