package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.CategoriaLivroRepository;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.util.LivroMapper;
import com.sonic.team.sonicteam.validation.LivroValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService implements ILivroService {
    private LivroRepository livroRepository;
    private CategoriaLivroRepository categoriaRepository;
    private LivroMapper livroMapper;
    private LivroValidator livroValidator;

    public LivroService(LivroRepository livroRepository, LivroValidator livroValidator, CategoriaLivroRepository categoriaRepository, LivroMapper livroMapper) {
        this.livroRepository = livroRepository;
        this.livroValidator = livroValidator;
    }

    @Override
    @Transactional
    public LivroResponseDTO criarLivro(LivroRequestDTO dto) {
        Livro livro = LivroMapper.toEntity(dto, categoriaRepository.findById(dto.categoriaId()).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada.")));
        livroValidator.validarCadastro(livro);
        livroRepository.save(livro);
        return LivroMapper.toResponse(livro);
    }

    // TODO: O buscar deve ter filtros para buscar por título, autor, editora, categoria, etc.

    @Override
    @Transactional
    public LivroResponseDTO buscarLivroPorISBN(String id) {
        if(id == null || id.isBlank() || id.length() > 10 && id.length() < 13) {
            throw new IllegalArgumentException("ID inválido.");
        }
        return LivroMapper.toResponse(livroRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Livro não encontrado.")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarLivros() {
        return livroRepository.findAll().stream().map(LivroMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public LivroResponseDTO atualizarLivro(String id, LivroRequestDTO LivroRequestDTO) {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livroExiste = livroRepository.findById(id).orElse(null);

        if(livroExiste == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        Livro livroAtualizado = LivroMapper.toEntity(LivroRequestDTO, categoriaRepository.findById(LivroRequestDTO.categoriaId()).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada.")));

        livroAtualizado.setIsbn(id);
        livroValidator.validarAtualizacao(livroAtualizado);
        livroRepository.save(livroAtualizado);

        return LivroMapper.toResponse(livroAtualizado);

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
}


