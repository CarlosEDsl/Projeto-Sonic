package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.Livro;

import java.util.List;

public interface ILivroService {
   Livro criarLivro(LivroRequestDTO livroRequestDTO);
   Livro buscarLivroPorId(Long id);
   List<Livro> listarLivros();
   Livro atualizarLivro(Long id,Livro dadosAtualizados);
   boolean excluirLivro(Long id);
}
