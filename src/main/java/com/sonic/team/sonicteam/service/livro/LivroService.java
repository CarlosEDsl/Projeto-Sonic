package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.CategoriaRepository;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.validator.LivroValidator;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class LivroService implements ILivroService {
    private LivroRepository livroRepository;
    private CategoriaRepository categoriaRepository;
    private LivroValidator livroValidator;

    public LivroService(LivroRepository livroRepository, LivroValidator livroValidator) {
        this.livroRepository = livroRepository;
        this.livroValidator = livroValidator;
    }

    @Override
    @Transactional
    public Livro criarLivro(LivroRequestDTO dto) {

        Livro livro = dtoParaEntidade(dto);
        livroValidator.validarCadastro(livro);
        livroRepository.save(livro);
        return livro;
    }

    @Override
    @Transactional
    public Livro buscarLivroPorISBN(String id) {
        if(id == null || id.isBlank() || id.length() > 10 && id.length() < 13) {
            throw new IllegalArgumentException("ID inválido.");
        }
        return livroRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livro> listarLivros() {
        return livroRepository.findAll();
    }

    @Override
    @Transactional
    public Livro atualizarLivro(String id, LivroRequestDTO LivroRequestDTO) {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livroExiste = livroRepository.findById(id).orElse(null);

        if(livroExiste == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        Livro livroAtualizado = dtoParaEntidade(LivroRequestDTO);
        livroAtualizado.setIsbn(id);

        livroValidator.validarAtualizacao(livroAtualizado);

        return livroRepository.save(livroAtualizado);

    }

    @Override
    @Transactional
    public void excluirLivro(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livro = livroRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        livroValidator.validarRemocao(livro);
        livroRepository.delete(livro);
    }

    private Livro dtoParaEntidade(LivroRequestDTO dto) {
        CategoriaLivro categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        Livro livro = new Livro();
        livro.setIsbn(dto.isbn());
        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setEditora(dto.editora());
        livro.setEdicao(dto.edicao());
        livro.setCategoria(categoria);
        return livro;
    }
}
