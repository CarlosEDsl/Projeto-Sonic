package com.sonic.team.sonicteam.service.livro;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.LivroRepository;

import java.util.List;

public class LivroService implements ILivroService {
    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Override
    public Livro criarLivro(LivroRequestDTO livroRequestDTO) {
        if(livroRequestDTO.autor().isEmpty() || livroRequestDTO.titulo().isEmpty() || livroRequestDTO.categoriaNome().isEmpty() || livroRequestDTO.edicao().isEmpty() || livroRequestDTO.editora().isEmpty() || livroRequestDTO.isbn().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos são obrigatórios.");
        }


        Livro livro = new Livro(
                livroRequestDTO.isbn(),
                livroRequestDTO.titulo(),
                livroRequestDTO.autor(),
                livroRequestDTO.editora(),
                livroRequestDTO.edicao(),
               new CategoriaLivro(livroRequestDTO.categoriaNome()),
                true);

        livroRepository.save(livro);

        return livro;
    }

    @Override
    public Livro buscarLivroPorISBN(String id) {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }
        Livro livro = livroRepository.findById(id).orElse(null);
        return livro;
    }

    @Override
    public List<Livro> listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        if(!livros.isEmpty()) {
            return livros;
        }
        throw new IllegalArgumentException("Nenhum livro encontrado.");
    }

    @Override
    public Livro atualizarLivro(String id, Livro dadosAtualizados) {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livroExiste = livroRepository.findById(id).orElse(null);

        if(livroExiste == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        } else{
            livroExiste.setTitulo(dadosAtualizados.getTitulo());
            livroExiste.setAutor(dadosAtualizados.getAutor());
            livroExiste.setEditora(dadosAtualizados.getEditora());
            livroExiste.setEdicao(dadosAtualizados.getEdicao());
            livroExiste.setCategoria(dadosAtualizados.getCategoria());
            livroExiste.setDisponivel(dadosAtualizados.isDisponivel());

            livroRepository.save(livroExiste);

            return livroExiste;
        }
    }

    @Override
    public boolean excluirLivro(String id) {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Livro livroExiste = livroRepository.findById(id).orElse(null);

        if(livroExiste == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }
        if(livroExiste.isDisponivel() == false) {
            throw new IllegalArgumentException("Livro não pode ser excluído pois está emprestado.");
        } else {
            livroRepository.deleteById(id);
            return true;
        }

    }
}
