package com.sonic.team.sonicteam.service.categorias;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import com.sonic.team.sonicteam.model.DTO.Categorias.CategoriaLivroRequestDto;

public interface ILivroCategoriaService {
    void criarCategorias(CategoriaLivroRequestDto categoria);
    CategoriaLivro buscarCategoriaPorId(Long id);
    CategoriaLivro atualizarCategoria(Long id, String novoNome);
    void excluirCategoria(Long id);


}
