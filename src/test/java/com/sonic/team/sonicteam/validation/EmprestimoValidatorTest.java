package com.sonic.team.sonicteam.validation;

import com.sonic.team.sonicteam.exception.EmprestimoInvalido;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.strategies.PoliticaEmprestimo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoValidatorTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private PoliticaEmprestimo politicaEmprestimo;

    @InjectMocks
    private EmprestimoValidator emprestimoValidator;

    private Aluno usuario;

    @BeforeEach
    void setUp() {
        usuario = new Aluno();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setStatus(StatusUsuario.ATIVO);
    }

    // ==================== TESTES validarPoliticaEmprestimo ====================

    @Test
    void validarPoliticaEmprestimo_DevePassar_QuandoPodeRealizarEmprestimo() {
        when(politicaEmprestimo.podeRealizarEmprestimo()).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidator.validarPoliticaEmprestimo(politicaEmprestimo));
    }

    @Test
    void validarPoliticaEmprestimo_DeveLancarExcecao_QuandoNaoPodeRealizarEmprestimo() {
        when(politicaEmprestimo.podeRealizarEmprestimo()).thenReturn(false);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarPoliticaEmprestimo(politicaEmprestimo)
        );

        assertEquals("Este tipo de usuário não pode realizar empréstimos", exception.getMessage());
    }

    // ==================== TESTES validarStatusUsuario ====================

    @Test
    void validarStatusUsuario_DevePassar_QuandoUsuarioAtivo() {
        usuario.setStatus(StatusUsuario.ATIVO);

        assertDoesNotThrow(() -> emprestimoValidator.validarStatusUsuario(usuario));
    }

    @Test
    void validarStatusUsuario_DeveLancarExcecao_QuandoUsuarioInativo() {
        usuario.setStatus(StatusUsuario.INATIVO);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarStatusUsuario(usuario)
        );

        assertEquals("O usuário está inativo", exception.getMessage());
    }

    @Test
    void validarStatusUsuario_DeveLancarExcecao_QuandoUsuarioSuspenso() {
        usuario.setStatus(StatusUsuario.SUSPENSO);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarStatusUsuario(usuario)
        );

        assertEquals("O usuário está suspenso", exception.getMessage());
    }

    // ==================== TESTES validarLimiteEmprestimos ====================

    @Test
    void validarLimiteEmprestimos_DevePassar_QuandoAbaixoDoLimite() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(2L);

        assertDoesNotThrow(() -> emprestimoValidator.validarLimiteEmprestimos(usuario, 3));
    }

    @Test
    void validarLimiteEmprestimos_DeveLancarExcecao_QuandoNoLimite() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(3L);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarLimiteEmprestimos(usuario, 3)
        );

        assertEquals("O usuário já atingiu o limite de 3 empréstimos ativos", exception.getMessage());
    }

    @Test
    void validarLimiteEmprestimos_DeveLancarExcecao_QuandoAcimaDoLimite() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(5L);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarLimiteEmprestimos(usuario, 3)
        );

        assertTrue(exception.getMessage().contains("3 empréstimos ativos"));
    }

    @Test
    void validarLimiteEmprestimos_DevePassar_QuandoSemEmprestimos() {
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(0L);

        assertDoesNotThrow(() -> emprestimoValidator.validarLimiteEmprestimos(usuario, 5));
    }

    // ==================== TESTES validarEmprestimo ====================

    @Test
    void validarEmprestimo_DevePassar_QuandoTudoValido() {
        when(politicaEmprestimo.podeRealizarEmprestimo()).thenReturn(true);
        when(politicaEmprestimo.pegarLimiteEmprestimos()).thenReturn(3);
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(0L);

        assertDoesNotThrow(() -> emprestimoValidator.validarEmprestimo(usuario, politicaEmprestimo));
    }

    @Test
    void validarEmprestimo_DeveLancarExcecao_QuandoPoliticaNaoPermite() {
        when(politicaEmprestimo.podeRealizarEmprestimo()).thenReturn(false);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarEmprestimo(usuario, politicaEmprestimo)
        );

        assertEquals("Este tipo de usuário não pode realizar empréstimos", exception.getMessage());
    }

    @Test
    void validarEmprestimo_DeveLancarExcecao_QuandoUsuarioInativo() {
        usuario.setStatus(StatusUsuario.INATIVO);
        when(politicaEmprestimo.podeRealizarEmprestimo()).thenReturn(true);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarEmprestimo(usuario, politicaEmprestimo)
        );

        assertEquals("O usuário está inativo", exception.getMessage());
    }

    @Test
    void validarEmprestimo_DeveLancarExcecao_QuandoLimiteAtingido() {
        when(politicaEmprestimo.podeRealizarEmprestimo()).thenReturn(true);
        when(politicaEmprestimo.pegarLimiteEmprestimos()).thenReturn(3);
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(3L);

        EmprestimoInvalido exception = assertThrows(
            EmprestimoInvalido.class,
            () -> emprestimoValidator.validarEmprestimo(usuario, politicaEmprestimo)
        );

        assertTrue(exception.getMessage().contains("limite"));
    }
}
