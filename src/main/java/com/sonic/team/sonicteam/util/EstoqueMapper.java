package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstoqueMapper {
    
    public Estoque paraEntidade(EstoqueResponseDTO dto, Livro livro) {
        return new Estoque(dto.id(), livro, dto.disponivel());
    }

    public EstoqueResponseDTO paraResponse(Estoque estoque) {
        return new EstoqueResponseDTO(
            estoque.getId(), 
            estoque.getLivro().getIsbn(), 
            estoque.getDisponivel()
        );
    }

    public List<EstoqueResponseDTO> paraListaResponse(List<Estoque> estoques) {
        return estoques.stream()
                .map(this::paraResponse)
                .collect(Collectors.toList());
    }
}

