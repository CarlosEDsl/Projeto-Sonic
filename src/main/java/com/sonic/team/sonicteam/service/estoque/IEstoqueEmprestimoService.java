package com.sonic.team.sonicteam.service.estoque;

import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.model.Estoque;

public interface IEstoqueEmprestimoService {
    Estoque pegarUmExemplarDisponivel(String livroISBN);
    EstoqueResponseDTO atualizarDisponibilidadeExemplar(AtualizarEstoqueResquestDTO request);
}
