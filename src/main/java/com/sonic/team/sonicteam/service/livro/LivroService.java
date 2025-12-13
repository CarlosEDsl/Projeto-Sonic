package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.util.LivroMapper;
import com.sonic.team.sonicteam.validation.LivroValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService implements ILivroService {
    
    private final LivroRepository livroRepository;
    private final LivroValidator livroValidator;
    private final LivroMapper livroMapper;

    public LivroService(LivroRepository livroRepository, 
                       LivroValidator livroValidator,
                       LivroMapper livroMapper) {
        this.livroRepository = livroRepository;
        this.livroValidator = livroValidator;
        this.livroMapper = livroMapper;
    }

    @Override
    @Transactional
    public LivroResponseDTO criarLivro(LivroRequestDTO dto) {
        Livro livro = livroMapper.paraEntidade(dto);
        livroValidator.validarCadastro(livro);
        livroRepository.save(livro);
        return livroMapper.paraResponse(livro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarLivros(String titulo, String autor, String editora, String edicao, String categoria) {
        if (todosFiltrosVazios(titulo, autor, editora, edicao, categoria)) {
            return buscarTodosLivros();
        }

        return buscarLivrosComFiltros(titulo, autor, editora, edicao, categoria);
    }

    @Override
    @Transactional
    public LivroResponseDTO atualizarLivro(String id, LivroRequestDTO dto) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livroExiste = livroRepository.findById(id).orElse(null);
        if (livroExiste == null) {
            throw new RecursoNaoEncontradoException("Livro não encontrado.");
        }

        Livro livroAtualizado = livroMapper.paraEntidade(dto);
        livroAtualizado.setIsbn(id);

        livroValidator.validarAtualizacao(livroAtualizado);
        livroRepository.save(livroAtualizado);

        return livroMapper.paraResponse(livroAtualizado);
    }

    @Override
    @Transactional
    public void excluirLivro(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livro = livroRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado."));

        livroValidator.validarRemocao(livro);
        livroRepository.delete(livro);
    }

    @Override
    @Transactional(readOnly = true)
    public LivroResponseDTO buscarLivroPorISBN(String id) {
        if (id == null || id.isBlank() || id.length() > 10 && id.length() < 13) {
            throw new IllegalArgumentException("ID inválido.");
        }
        Livro livro = livroRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado."));
        return livroMapper.paraResponse(livro);
    }

    private boolean todosFiltrosVazios(String titulo, String autor, String editora, String edicao, String categoria) {
        return (titulo == null || titulo.isBlank()) &&
                (autor == null || autor.isBlank()) &&
                (editora == null || editora.isBlank()) &&
                (edicao == null || edicao.isBlank()) &&
                (categoria == null || categoria.isBlank());
    }

    private List<LivroResponseDTO> buscarTodosLivros() {
        return livroRepository.findAll()
                .stream()
                .map(livroMapper::paraResponse)
                .toList();
    }

    private List<LivroResponseDTO> buscarLivrosComFiltros(String titulo, String autor, String editora, String edicao, String categoria) {
        Integer edicaoInt = converterEdicao(edicao);
        CategoriaLivro categoriaEnum = converterCategoria(categoria);

        List<Livro> livros = livroRepository.buscarComFiltros(titulo, autor, editora, edicaoInt, categoriaEnum);

        return livros.stream()
                .map(livroMapper::paraResponse)
                .toList();
    }

    private Integer converterEdicao(String edicao) {
        if (edicao == null || edicao.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(edicao.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Edição inválida.");
        }
    }

    private CategoriaLivro converterCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return null;
        }

        try {
            return CategoriaLivro.valueOf(categoria.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new RecursoNaoEncontradoException("Categoria inválida.");
        }
    }
}

