package com.sonic.team.sonicteam.service.estoque;

import com.sonic.team.sonicteam.exception.ExemplarNaoEstaDisponivelException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.repository.EstoqueRepository;
import com.sonic.team.sonicteam.repository.LivroRepository;
import com.sonic.team.sonicteam.util.EstoqueMapper;
import com.sonic.team.sonicteam.validation.EstoqueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EstoqueValidator estoqueValidator;

    @Mock
    private EstoqueMapper estoqueMapper;

    @InjectMocks
    private EstoqueService estoqueService;

    private Livro livro;
    private Estoque estoque;
    private EstoqueResponseDTO estoqueResponseDTO;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setIsbn("978-3-16-148410-0");
        livro.setTitulo("Clean Code");
        livro.setAutor("Robert C. Martin");

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setLivro(livro);
        estoque.setDisponivel(true);

        estoqueResponseDTO = new EstoqueResponseDTO(1L, "978-3-16-148410-0", true);
    }

    // ==================== TESTES cadastrarNovoExemplar ====================

    @Test
    void cadastrarNovoExemplar_DeveRetornarResponse_QuandoLivroExistir() {
        EstoqueRequestDTO request = new EstoqueRequestDTO("978-3-16-148410-0");
        
        when(livroRepository.findById("978-3-16-148410-0")).thenReturn(Optional.of(livro));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);
        when(estoqueMapper.paraResponse(estoque)).thenReturn(estoqueResponseDTO);

        EstoqueResponseDTO resultado = estoqueService.cadastrarNovoExemplar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("978-3-16-148410-0", resultado.livroIsbn());
        assertTrue(resultado.disponivel());
    }

    @Test
    void cadastrarNovoExemplar_DeveLancarExcecao_QuandoLivroNaoExistir() {
        EstoqueRequestDTO request = new EstoqueRequestDTO("isbn-inexistente");
        
        when(livroRepository.findById("isbn-inexistente")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> estoqueService.cadastrarNovoExemplar(request)
        );

        assertTrue(exception.getMessage().contains("Livro não encontrado"));
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void cadastrarNovoExemplar_DeveSalvarNovoEstoque() {
        EstoqueRequestDTO request = new EstoqueRequestDTO("978-3-16-148410-0");
        
        when(livroRepository.findById("978-3-16-148410-0")).thenReturn(Optional.of(livro));
        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);
        when(estoqueMapper.paraResponse(estoque)).thenReturn(estoqueResponseDTO);

        estoqueService.cadastrarNovoExemplar(request);

        verify(estoqueRepository).save(any(Estoque.class));
    }

    // ==================== TESTES getExemplaresDisponiveis ====================

    @Test
    void getExemplaresDisponiveis_DeveRetornarTodos_QuandoIsbnForNull() {
        List<Estoque> estoques = List.of(estoque);
        List<EstoqueResponseDTO> responseDTOs = List.of(estoqueResponseDTO);
        
        when(estoqueRepository.findAllByDisponivelIsTrue()).thenReturn(estoques);
        when(estoqueMapper.paraListaResponse(estoques)).thenReturn(responseDTOs);

        List<EstoqueResponseDTO> resultado = estoqueService.getExemplaresDisponiveis(null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(estoqueRepository).findAllByDisponivelIsTrue();
    }

    @Test
    void getExemplaresDisponiveis_DeveRetornarTodos_QuandoIsbnForVazio() {
        List<Estoque> estoques = List.of(estoque);
        List<EstoqueResponseDTO> responseDTOs = List.of(estoqueResponseDTO);
        
        when(estoqueRepository.findAllByDisponivelIsTrue()).thenReturn(estoques);
        when(estoqueMapper.paraListaResponse(estoques)).thenReturn(responseDTOs);

        List<EstoqueResponseDTO> resultado = estoqueService.getExemplaresDisponiveis("");

        assertEquals(1, resultado.size());
        verify(estoqueRepository).findAllByDisponivelIsTrue();
    }

    @Test
    void getExemplaresDisponiveis_DeveRetornarTodos_QuandoIsbnForBlank() {
        List<Estoque> estoques = List.of(estoque);
        List<EstoqueResponseDTO> responseDTOs = List.of(estoqueResponseDTO);
        
        when(estoqueRepository.findAllByDisponivelIsTrue()).thenReturn(estoques);
        when(estoqueMapper.paraListaResponse(estoques)).thenReturn(responseDTOs);

        List<EstoqueResponseDTO> resultado = estoqueService.getExemplaresDisponiveis("   ");

        assertEquals(1, resultado.size());
        verify(estoqueRepository).findAllByDisponivelIsTrue();
    }

    @Test
    void getExemplaresDisponiveis_DeveFiltrarPorIsbn_QuandoIsbnForValido() {
        List<Estoque> estoques = List.of(estoque);
        List<EstoqueResponseDTO> responseDTOs = List.of(estoqueResponseDTO);
        
        when(estoqueRepository.findAllByDisponivelIsTrueAndLivroIsbn("978-3-16-148410-0")).thenReturn(estoques);
        when(estoqueMapper.paraListaResponse(estoques)).thenReturn(responseDTOs);

        List<EstoqueResponseDTO> resultado = estoqueService.getExemplaresDisponiveis("978-3-16-148410-0");

        assertEquals(1, resultado.size());
        verify(estoqueRepository).findAllByDisponivelIsTrueAndLivroIsbn("978-3-16-148410-0");
    }

    @Test
    void getExemplaresDisponiveis_DeveRetornarListaVazia_QuandoNaoHouverExemplares() {
        when(estoqueRepository.findAllByDisponivelIsTrue()).thenReturn(Collections.emptyList());
        when(estoqueMapper.paraListaResponse(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<EstoqueResponseDTO> resultado = estoqueService.getExemplaresDisponiveis(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== TESTES getExemplar ====================

    @Test
    void getExemplar_DeveRetornarExemplar_QuandoIdExistir() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.paraResponse(estoque)).thenReturn(estoqueResponseDTO);

        EstoqueResponseDTO resultado = estoqueService.getExemplar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        verify(estoqueRepository).findById(1L);
    }

    @Test
    void getExemplar_DeveLancarExcecao_QuandoIdNaoExistir() {
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> estoqueService.getExemplar(999L)
        );

        assertTrue(exception.getMessage().contains("Exemplar não encontrado"));
    }

    // ==================== TESTES atualizarDisponibilidadeExemplar ====================

    @Test
    void atualizarDisponibilidadeExemplar_DeveAtualizarParaDisponivel() {
        estoque.setDisponivel(false);
        AtualizarEstoqueResquestDTO request = new AtualizarEstoqueResquestDTO(1L, true);
        EstoqueResponseDTO responseAtualizado = new EstoqueResponseDTO(1L, "978-3-16-148410-0", true);
        
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.paraResponse(estoque)).thenReturn(responseAtualizado);

        EstoqueResponseDTO resultado = estoqueService.atualizarDisponibilidadeExemplar(request);

        assertTrue(resultado.disponivel());
        verify(estoqueRepository).save(estoque);
    }

    @Test
    void atualizarDisponibilidadeExemplar_DeveAtualizarParaIndisponivel() {
        AtualizarEstoqueResquestDTO request = new AtualizarEstoqueResquestDTO(1L, false);
        EstoqueResponseDTO responseAtualizado = new EstoqueResponseDTO(1L, "978-3-16-148410-0", false);
        
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.paraResponse(estoque)).thenReturn(responseAtualizado);

        EstoqueResponseDTO resultado = estoqueService.atualizarDisponibilidadeExemplar(request);

        assertFalse(resultado.disponivel());
        verify(estoqueRepository).save(estoque);
    }

    @Test
    void atualizarDisponibilidadeExemplar_DeveLancarExcecao_QuandoIdNaoExistir() {
        AtualizarEstoqueResquestDTO request = new AtualizarEstoqueResquestDTO(999L, true);
        
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> estoqueService.atualizarDisponibilidadeExemplar(request)
        );

        assertTrue(exception.getMessage().contains("Exemplar não encontrado"));
        verify(estoqueRepository, never()).save(any());
    }

    // ==================== TESTES deletarExemplar ====================

    @Test
    void deletarExemplar_DeveDeletarExemplar_QuandoDisponivelEExistir() {
        when(estoqueValidator.validarDisponibilidadeDoExemplar(1L)).thenReturn(true);

        estoqueService.deletarExemplar(1L);

        verify(estoqueRepository).deleteById(1L);
    }

    @Test
    void deletarExemplar_DeveLancarExcecao_QuandoNaoDisponivel() {
        when(estoqueValidator.validarDisponibilidadeDoExemplar(1L)).thenReturn(false);

        ExemplarNaoEstaDisponivelException exception = assertThrows(
                ExemplarNaoEstaDisponivelException.class,
                () -> estoqueService.deletarExemplar(1L)
        );

        assertNotNull(exception);
        verify(estoqueRepository, never()).deleteById(any());
    }

    // ==================== TESTES pegarUmExemplarDisponivel ====================

    @Test
    void pegarUmExemplarDisponivel_DeveRetornarEstoque_QuandoDisponivelExistir() {
        when(estoqueRepository.getFirstByLivroIsbnAndDisponivelIsTrue("978-3-16-148410-0")).thenReturn(estoque);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.paraResponse(estoque)).thenReturn(new EstoqueResponseDTO(1L, "978-3-16-148410-0", false));

        Estoque resultado = estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0");

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void pegarUmExemplarDisponivel_DeveAtualizarDisponibilidadeParaFalse() {
        when(estoqueRepository.getFirstByLivroIsbnAndDisponivelIsTrue("978-3-16-148410-0")).thenReturn(estoque);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.paraResponse(estoque)).thenReturn(new EstoqueResponseDTO(1L, "978-3-16-148410-0", false));

        estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0");

        verify(estoqueRepository).save(estoque);
    }

    @Test
    void pegarUmExemplarDisponivel_DeveLancarExcecao_QuandoExemplarNaoDisponivel() {
        Estoque estoqueIndisponivel = new Estoque();
        estoqueIndisponivel.setId(2L);
        estoqueIndisponivel.setDisponivel(false);
        
        when(estoqueRepository.getFirstByLivroIsbnAndDisponivelIsTrue("978-3-16-148410-0")).thenReturn(estoqueIndisponivel);

        ExemplarNaoEstaDisponivelException exception = assertThrows(
                ExemplarNaoEstaDisponivelException.class,
                () -> estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0")
        );

        assertNotNull(exception);
    }
}
