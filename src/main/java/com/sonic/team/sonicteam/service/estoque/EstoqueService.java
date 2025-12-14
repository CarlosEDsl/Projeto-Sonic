package com.sonic.team.sonicteam.service.estoque;

import com.sonic.team.sonicteam.exception.ExemplarNaoEstaDisponivelException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;

import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.model.Estoque;

import com.sonic.team.sonicteam.repository.EstoqueRepository;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.util.EstoqueMapper;
import com.sonic.team.sonicteam.validation.EstoqueValidator;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService implements IEstoqueService, IEstoqueEmprestimoService {
    
    private final EstoqueRepository estoqueRepository;
    private final LivroRepository livroRepository;
    private final EstoqueValidator estoqueValidator;
    private final EstoqueMapper estoqueMapper;

    public EstoqueService(EstoqueRepository estoqueRepository, 
                         LivroRepository livroRepository, 
                         EstoqueValidator estoqueValidator,
                         EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.livroRepository = livroRepository;
        this.estoqueValidator = estoqueValidator;
        this.estoqueMapper = estoqueMapper;
    }

    @Override
    public EstoqueResponseDTO cadastrarNovoExemplar(EstoqueRequestDTO request) {
        var livroExiste = livroRepository.findById(request.livroIsbn());
        if (livroExiste.isEmpty()) {
            throw new RecursoNaoEncontradoException("Livro não encontrado com o isbn " + request.livroIsbn());
        }

        var novoEstoque = estoqueRepository.save(new Estoque(livroExiste.get()));
        return estoqueMapper.paraResponse(novoEstoque);
    }

    @Override
    public List<EstoqueResponseDTO> listarTodos() {
        return estoqueMapper.paraListaResponse(estoqueRepository.findAll());
    }

    @Override
    public List<EstoqueResponseDTO> getExemplaresDisponiveis(String livroIsbn) {
        if (livroIsbn == null || livroIsbn.isBlank()) {
            return estoqueMapper.paraListaResponse(
                    estoqueRepository.findAllByDisponivelIsTrue()
            );
        }

        return estoqueMapper.paraListaResponse(
                estoqueRepository.findAllByDisponivelIsTrueAndLivroIsbn(livroIsbn)
        );
    }

    @Override
    public EstoqueResponseDTO getExemplar(Long id) {
        var exemplarEstoque = estoqueRepository.findById(id);

        if (exemplarEstoque.isPresent()) {
            return estoqueMapper.paraResponse(exemplarEstoque.get());
        }

        throw new RecursoNaoEncontradoException("Exemplar não encontrado com o id " + id);
    }

    @Override
    public EstoqueResponseDTO atualizarDisponibilidadeExemplar(AtualizarEstoqueResquestDTO request) {
        var exemplarEstoque = estoqueRepository.findById(request.id());
        if (exemplarEstoque.isPresent()) {
            exemplarEstoque.get().setDisponivel(request.disponivel());
            estoqueRepository.save(exemplarEstoque.get());
            return estoqueMapper.paraResponse(exemplarEstoque.get());
        }

        throw new RecursoNaoEncontradoException("Exemplar não encontrado com o id " + request.id());
    }

    @Override
    public void deletarExemplar(Long id) {
        try {
            if (estoqueValidator.validarDisponibilidadeDoExemplar(id)) {
                estoqueRepository.deleteById(id);
            } else {
                throw new ExemplarNaoEstaDisponivelException(id);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new RecursoNaoEncontradoException("Exemplar não encontrado com o id " + id);
        }
    }

    @Override
    public Estoque pegarUmExemplarDisponivel(String livroISBN) {
        try {
            Estoque estoque = this.estoqueRepository.getFirstByLivroIsbnAndDisponivelIsTrue(livroISBN);
            if (estoque == null) {
                throw new ExemplarNaoEstaDisponivelException(0L);
            }
            if (estoque.getDisponivel()) {
                this.atualizarDisponibilidadeExemplar(new AtualizarEstoqueResquestDTO(estoque.getId(), false));
                return estoque;
            } else {
                throw new ExemplarNaoEstaDisponivelException(0L);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new RecursoNaoEncontradoException("Exemplar não encontrado com o ISBN " + livroISBN);
        }
    }
}

