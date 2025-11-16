package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;

public class LivroMapper {

        public static Livro toEntity(LivroRequestDTO dto) {

            Livro livro = new Livro();
            livro.setIsbn(dto.isbn());
            livro.setTitulo(dto.titulo());
            livro.setAutor(dto.autor());
            livro.setEditora(dto.editora());
            livro.setEdicao(dto.edicao());
            livro.setCategoriaLivro(dto.categoriaLivro());

            return livro;
        }

    public static LivroResponseDTO toResponse(Livro livro) {
        return new LivroResponseDTO(
                livro.getIsbn(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getEdicao(),
                livro.getCategoriaLivro().name()
        );
    }
    }

