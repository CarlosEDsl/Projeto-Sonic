package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.Livro;

import java.util.List;

public interface ILivroService {
   Livro criarLivro(LivroRequestDTO livroRequestDTO);
   Livro buscarLivroPorISBN(String id);
   List<Livro> listarLivros();
   Livro atualizarLivro(String id,LivroRequestDTO livroRequestDTO);
   void excluirLivro(String id);
}
