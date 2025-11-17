package com.sonic.team.sonicteam.service.estoque;

import com.sonic.team.sonicteam.model.Estoque;

public interface IEstoqueEmprestimoService {
    Estoque pegarUmExemplarDisponivel(String livroISBN);
}
