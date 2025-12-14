package com.sonic.team.sonicteam.service.curso;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.repository.CursoRepository;
import com.sonic.team.sonicteam.util.ConstantesUsuario;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso cursoEngenharia;
    private Curso cursoMedicina;
    private Curso cursoDireito;

    @BeforeEach
    void setUp() {
        cursoEngenharia = new Curso(1L, "Engenharia de Software");
        cursoMedicina = new Curso(2L, "Medicina");
        cursoDireito = new Curso(3L, "Direito");
    }

    // ==================== TESTES buscarPorId ====================

    @Test
    void buscarPorId_DeveRetornarCurso_QuandoIdExistir() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoEngenharia));

        Curso resultado = cursoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Engenharia de Software", resultado.getNome());
        verify(cursoRepository).findById(1L);
    }

    @Test
    void buscarPorId_DeveRetornarCursoCorreto_QuandoBuscarDiferentesIds() {
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(cursoMedicina));
        when(cursoRepository.findById(3L)).thenReturn(Optional.of(cursoDireito));

        Curso resultadoMedicina = cursoService.buscarPorId(2L);
        Curso resultadoDireito = cursoService.buscarPorId(3L);

        assertEquals("Medicina", resultadoMedicina.getNome());
        assertEquals("Direito", resultadoDireito.getNome());
        assertNotEquals(resultadoMedicina.getId(), resultadoDireito.getId());
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoIdNaoExistir() {
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> cursoService.buscarPorId(999L)
        );

        assertEquals(ConstantesUsuario.CURSO_INEXISTENTE, exception.getMessage());
        verify(cursoRepository).findById(999L);
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoIdForZero() {
        when(cursoRepository.findById(0L)).thenReturn(Optional.empty());

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> cursoService.buscarPorId(0L)
        );

        assertEquals(ConstantesUsuario.CURSO_INEXISTENTE, exception.getMessage());
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoIdForNegativo() {
        when(cursoRepository.findById(-1L)).thenReturn(Optional.empty());

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> cursoService.buscarPorId(-1L)
        );

        assertEquals(ConstantesUsuario.CURSO_INEXISTENTE, exception.getMessage());
    }

    @Test
    void buscarPorId_DeveVerificarChamadaRepository() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoEngenharia));

        cursoService.buscarPorId(1L);

        verify(cursoRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(cursoRepository);
    }

    // ==================== TESTES listar ====================

    @Test
    void listar_DeveRetornarLista_QuandoHouverCursos() {
        List<Curso> cursos = List.of(cursoEngenharia, cursoMedicina, cursoDireito);
        when(cursoRepository.findAll()).thenReturn(cursos);

        List<Curso> resultado = cursoService.listar();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    void listar_DeveRetornarListaComUmCurso_QuandoHouverApenas1() {
        when(cursoRepository.findAll()).thenReturn(List.of(cursoEngenharia));

        List<Curso> resultado = cursoService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Engenharia de Software", resultado.get(0).getNome());
    }

    @Test
    void listar_DeveConterTodosOsCursos() {
        List<Curso> cursos = List.of(cursoEngenharia, cursoMedicina);
        when(cursoRepository.findAll()).thenReturn(cursos);

        List<Curso> resultado = cursoService.listar();

        assertTrue(resultado.contains(cursoEngenharia));
        assertTrue(resultado.contains(cursoMedicina));
    }

    @Test
    void listar_DeveManterOrdemDosCursos() {
        List<Curso> cursos = List.of(cursoEngenharia, cursoMedicina, cursoDireito);
        when(cursoRepository.findAll()).thenReturn(cursos);

        List<Curso> resultado = cursoService.listar();

        assertEquals(cursoEngenharia, resultado.get(0));
        assertEquals(cursoMedicina, resultado.get(1));
        assertEquals(cursoDireito, resultado.get(2));
    }

    @Test
    void listar_DeveLancarExcecao_QuandoListaVazia() {
        when(cursoRepository.findAll()).thenReturn(Collections.emptyList());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> cursoService.listar()
        );

        assertEquals("Não há nenhuma categoria cadastrada.", exception.getMessage());
    }

    @Test
    void listar_DeveChamarRepositoryDuasVezes() {
        // Nota: O método listar() chama findAll() duas vezes (uma para verificar e outra para retornar)
        List<Curso> cursos = List.of(cursoEngenharia);
        when(cursoRepository.findAll()).thenReturn(cursos);

        cursoService.listar();

        verify(cursoRepository, times(2)).findAll();
    }
}
