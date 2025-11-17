package com.sonic.team.sonicteam.service.estoque;

import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import java.util.List;

public interface IEstoqueService {
    EstoqueResponseDTO cadastrarNovoExemplar(EstoqueRequestDTO request);
    List<EstoqueResponseDTO> getExemplaresDisponiveis(String livroIsbn);
    EstoqueResponseDTO getExemplar(Long id);
    EstoqueResponseDTO atualizarDisponibilidadeExemplar(AtualizarEstoqueResquestDTO request);
    void deletarExemplar(Long id);
}
