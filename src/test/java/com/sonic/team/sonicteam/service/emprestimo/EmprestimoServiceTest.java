package com.sonic.team.sonicteam.service.emprestimo;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.service.estoque.IEstoqueEmprestimoService;
import com.sonic.team.sonicteam.service.usuario.IUsuarioEmprestimoService;
import com.sonic.team.sonicteam.strategies.EmprestimoStrategy;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import com.sonic.team.sonicteam.validation.EmprestimoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private IUsuarioEmprestimoService usuarioService;

    @Mock
    private IEstoqueEmprestimoService estoqueService;

    @Mock
    private EmprestimoValidator emprestimoValidator;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Mock
    private Usuario usuario;

    @Mock
    private Estoque estoque;

    @Mock
    private PoliticaEmprestimo politicaEmprestimo;

    @Mock
    private EmprestimoStrategy emprestimoStrategy;

    private Emprestimo emprestimo;

    @BeforeEach
    void setUp() {
        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataDevolucao(LocalDateTime.now().plusDays(7));
    }

    // ==================== TESTES criarEmprestimo ====================

    @Test
    void criarEmprestimo_DeveRetornarEmprestimo_QuandoDadosValidos() {
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("12345678901", "978-3-16-148410-0");
        
        when(usuarioService.pegarUsuarioPorCPF("12345678901")).thenReturn(usuario);
        when(usuario.getPoliticaEmprestimo()).thenReturn(emprestimoStrategy);
        when(estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0")).thenReturn(estoque);
        when(emprestimoStrategy.pegarEmprestimo(estoque)).thenReturn(emprestimo);
        when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoService.criarEmprestimo(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(emprestimoValidator).validarEmprestimo(usuario, emprestimoStrategy);
        verify(emprestimoRepository).save(emprestimo);
    }

    @Test
    void criarEmprestimo_DeveChamarValidador() {
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("12345678901", "978-3-16-148410-0");
        
        when(usuarioService.pegarUsuarioPorCPF("12345678901")).thenReturn(usuario);
        when(usuario.getPoliticaEmprestimo()).thenReturn(emprestimoStrategy);
        when(estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0")).thenReturn(estoque);
        when(emprestimoStrategy.pegarEmprestimo(estoque)).thenReturn(emprestimo);
        when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);

        emprestimoService.criarEmprestimo(request);

        verify(emprestimoValidator, times(1)).validarEmprestimo(usuario, emprestimoStrategy);
    }

    @Test
    void criarEmprestimo_DeveBuscarUsuarioPorCPF() {
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("12345678901", "978-3-16-148410-0");
        
        when(usuarioService.pegarUsuarioPorCPF("12345678901")).thenReturn(usuario);
        when(usuario.getPoliticaEmprestimo()).thenReturn(emprestimoStrategy);
        when(estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0")).thenReturn(estoque);
        when(emprestimoStrategy.pegarEmprestimo(estoque)).thenReturn(emprestimo);
        when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);

        emprestimoService.criarEmprestimo(request);

        verify(usuarioService).pegarUsuarioPorCPF("12345678901");
    }

    @Test
    void criarEmprestimo_DeveBuscarExemplarDisponivel() {
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("12345678901", "978-3-16-148410-0");
        
        when(usuarioService.pegarUsuarioPorCPF("12345678901")).thenReturn(usuario);
        when(usuario.getPoliticaEmprestimo()).thenReturn(emprestimoStrategy);
        when(estoqueService.pegarUmExemplarDisponivel("978-3-16-148410-0")).thenReturn(estoque);
        when(emprestimoStrategy.pegarEmprestimo(estoque)).thenReturn(emprestimo);
        when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);

        emprestimoService.criarEmprestimo(request);

        verify(estoqueService).pegarUmExemplarDisponivel("978-3-16-148410-0");
    }

    // ==================== TESTES buscarEmprestimoPorId ====================

    @Test
    void buscarEmprestimoPorId_DeveRetornarEmprestimo_QuandoIdExistir() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        Emprestimo resultado = emprestimoService.buscarEmprestimoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(emprestimoRepository).findById(1L);
    }

    @Test
    void buscarEmprestimoPorId_DeveRetornarNull_QuandoIdNaoExistir() {
        when(emprestimoRepository.findById(999L)).thenReturn(Optional.empty());

        Emprestimo resultado = emprestimoService.buscarEmprestimoPorId(999L);

        assertNull(resultado);
        verify(emprestimoRepository).findById(999L);
    }

    @Test
    void buscarEmprestimoPorId_DeveChamarRepository() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        emprestimoService.buscarEmprestimoPorId(1L);

        verify(emprestimoRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(emprestimoRepository);
    }

    // ==================== TESTES listarEmprestimos ====================

    @Test
    void listarEmprestimos_DeveRetornarLista_QuandoHouverEmprestimos() {
        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);
        List<Emprestimo> emprestimos = List.of(emprestimo, emprestimo2);
        
        when(emprestimoRepository.findAll()).thenReturn(emprestimos);

        List<Emprestimo> resultado = emprestimoService.listarEmprestimos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(emprestimoRepository).findAll();
    }

    @Test
    void listarEmprestimos_DeveRetornarListaVazia_QuandoNaoHouverEmprestimos() {
        when(emprestimoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Emprestimo> resultado = emprestimoService.listarEmprestimos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(emprestimoRepository).findAll();
    }

    @Test
    void listarEmprestimos_DeveRetornarListaComUmElemento() {
        when(emprestimoRepository.findAll()).thenReturn(List.of(emprestimo));

        List<Emprestimo> resultado = emprestimoService.listarEmprestimos();

        assertEquals(1, resultado.size());
        assertEquals(emprestimo, resultado.get(0));
    }

    // ==================== TESTES devolverEmprestimo ====================

    @Test
    void devolverEmprestimo_DeveDefinirDataEntrega() {
        emprestimo.setEstoque(estoque);
        when(estoque.getId()).thenReturn(1L);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoService.devolverEmprestimo(1L);

        assertNotNull(resultado.getDataEntrega());
        verify(emprestimoRepository).save(emprestimo);
    }

    @Test
    void devolverEmprestimo_DeveAtualizarDisponibilidadeEstoque() {
        emprestimo.setEstoque(estoque);
        when(estoque.getId()).thenReturn(1L);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoService.devolverEmprestimo(1L);

        ArgumentCaptor<AtualizarEstoqueResquestDTO> captor = ArgumentCaptor.forClass(AtualizarEstoqueResquestDTO.class);
        verify(estoqueService).atualizarDisponibilidadeExemplar(captor.capture());
        
        AtualizarEstoqueResquestDTO dtoCapturado = captor.getValue();
        assertEquals(1L, dtoCapturado.id());
        assertTrue(dtoCapturado.disponivel());
    }

    @Test
    void devolverEmprestimo_DeveSalvarEmprestimo() {
        emprestimo.setEstoque(estoque);
        when(estoque.getId()).thenReturn(1L);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoService.devolverEmprestimo(1L);

        verify(emprestimoRepository).save(emprestimo);
    }

    // ==================== TESTES contarEmprestimosAtivosPorUsuario ====================

    @Test
    void contarEmprestimosAtivosPorUsuario_DeveRetornarQuantidade() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(3L);

        long resultado = emprestimoService.contarEmprestimosAtivosPorUsuario(1L);

        assertEquals(3L, resultado);
        verify(emprestimoRepository).countByUsuarioIdAndDataEntregaIsNull(1L);
    }

    @Test
    void contarEmprestimosAtivosPorUsuario_DeveRetornarZero_QuandoNaoHouverEmprestimosAtivos() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(0L);

        long resultado = emprestimoService.contarEmprestimosAtivosPorUsuario(1L);

        assertEquals(0L, resultado);
    }

    @Test
    void contarEmprestimosAtivosPorUsuario_DeveChamarRepositoryComIdCorreto() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(5L)).thenReturn(2L);

        emprestimoService.contarEmprestimosAtivosPorUsuario(5L);

        verify(emprestimoRepository).countByUsuarioIdAndDataEntregaIsNull(5L);
        verify(emprestimoRepository, never()).countByUsuarioIdAndDataEntregaIsNull(1L);
    }
}
