package com.sonic.team.sonicteam.util;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.CategoriaLivroRepository;

public class LivroMapper {

        public static Livro toEntity(LivroRequestDTO dto, CategoriaLivro categoria) {

            Livro livro = new Livro();
            livro.setIsbn(dto.isbn());
            livro.setTitulo(dto.titulo());
            livro.setAutor(dto.autor());
            livro.setEditora(dto.editora());
            livro.setEdicao(dto.edicao());
            livro.setCategoria(categoria);

            return livro;
        }

    public static LivroResponseDTO toResponse(Livro livro) {
        return new LivroResponseDTO(
                livro.getIsbn(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getEdicao(),
                livro.getCategoria().getNome()
        );
    }
    }

