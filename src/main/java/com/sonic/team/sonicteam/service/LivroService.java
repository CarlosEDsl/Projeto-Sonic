package com.sonic.team.sonicteam.service;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.LivroRepository;

public class LivroService {
    private LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public void criarLivro(Livro livro){

    }
}
