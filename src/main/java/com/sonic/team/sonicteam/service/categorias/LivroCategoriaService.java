package com.sonic.team.sonicteam.service.categorias;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import com.sonic.team.sonicteam.model.DTO.Categorias.CategoriaLivroRequestDto;
import com.sonic.team.sonicteam.repository.CategoriaLivroRepository;
import com.sonic.team.sonicteam.validation.CategoriaLivroValidator;
import org.springframework.stereotype.Service;

@Service
public class LivroCategoriaService implements ILivroCategoriaService {
    private CategoriaLivroRepository categoriaRepository;
    private CategoriaLivroValidator categoriaLivroValidator;

    public LivroCategoriaService(CategoriaLivroRepository categoriaRepository, CategoriaLivroValidator categoriaLivroValidator) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaLivroValidator = categoriaLivroValidator;
    }


    @Override
    public void criarCategorias(CategoriaLivroRequestDto categoria) {
        CategoriaLivro categoriaLivro = new CategoriaLivro(categoria.nome());
        categoriaLivroValidator.validarCadastro(categoriaLivro);
        categoriaRepository.save(categoriaLivro);
    }

    @Override
    public CategoriaLivro buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));
    }

    @Override
    public CategoriaLivro atualizarCategoria(Long id, String novoNome) {
        if(novoNome == null || novoNome.isBlank() || novoNome.length() < 3) {
            throw new IllegalArgumentException("Nome inválido.");
        }

        CategoriaLivro categoriaExiste = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));
        categoriaLivroValidator.validarCadastro(categoriaExiste);
        categoriaExiste.setNome(novoNome);
        return categoriaRepository.save(categoriaExiste);
    }

    @Override
    public void excluirCategoria(Long id) {
        CategoriaLivro categoriaExiste = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));
        categoriaRepository.delete(categoriaExiste);
    }
}
