package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.LivroRepository;

import java.util.List;

public class LivroService implements ILivroService {
    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Override
    public Livro criarLivro(LivroRequestDTO livroRequestDTO) {
        return null;
    }

    @Override
    public Livro buscarLivroPorId(Long id) {
        return null;
    }

    @Override
    public List<Livro> listarLivros() {
        return List.of();
    }

    @Override
    public Livro atualizarLivro(Long id, Livro dadosAtualizados) {
        return null;
    }

    @Override
    public boolean excluirLivro(Long id) {
        return false;
    }
}
