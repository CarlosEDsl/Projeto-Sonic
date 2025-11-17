package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;

import java.util.List;

public interface ILivroService {
   LivroResponseDTO criarLivro(LivroRequestDTO livroRequestDTO);
   LivroResponseDTO buscarLivroPorISBN(String id);
   List<LivroResponseDTO> listarLivros(String titulo, String autor, String editora, String edicao, String categoria);
   LivroResponseDTO atualizarLivro(String id, LivroRequestDTO livroRequestDTO);
   void excluirLivro(String id);
}
