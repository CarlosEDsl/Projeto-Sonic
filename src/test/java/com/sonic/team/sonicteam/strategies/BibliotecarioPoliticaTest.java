package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.usuario.Bibliotecario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BibliotecarioPoliticaTest {

    private Bibliotecario bibliotecario;
    private BibliotecarioPolitica politica;

    @BeforeEach
    void setUp() {
        bibliotecario = new Bibliotecario();
        bibliotecario.setId(1L);
        bibliotecario.setNome("Carlos Bibliotecário");
        bibliotecario.setCpf("11122233344");

        politica = new BibliotecarioPolitica(bibliotecario);
    }

    // ==================== TESTES pegarLimiteEmprestimos ====================

    @Test
    void pegarLimiteEmprestimos_DeveRetornar0() {
        int limite = politica.pegarLimiteEmprestimos();

        assertEquals(0, limite);
    }

    // ==================== TESTES podeRealizarEmprestimo ====================

    @Test
    void podeRealizarEmprestimo_DeveRetornarFalse() {
        boolean pode = politica.podeRealizarEmprestimo();

        assertFalse(pode);
    }

    // ==================== TESTES getEntity ====================

    @Test
    void getEntity_DeveRetornarBibliotecario() {
        Bibliotecario entity = politica.getEntity();

        assertNotNull(entity);
        assertEquals("Carlos Bibliotecário", entity.getNome());
        assertEquals("11122233344", entity.getCpf());
    }

    @Test
    void getEntity_DeveRetornarMesmoObjetoPassadoNoConstrutor() {
        Bibliotecario entity = politica.getEntity();

        assertSame(bibliotecario, entity);
    }

    // ==================== TESTES construtor ====================

    @Test
    void construtor_DeveArmazenarEntidade() {
        BibliotecarioPolitica novaPolitica = new BibliotecarioPolitica(bibliotecario);

        assertNotNull(novaPolitica.getEntity());
        assertEquals(bibliotecario.getId(), novaPolitica.getEntity().getId());
    }
}
