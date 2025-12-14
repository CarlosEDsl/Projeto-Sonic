package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.model.usuario.Professor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorEmprestimoStrategyTest {

    private Professor professor;
    private ProfessorEmprestimoStrategy strategy;
    private Livro livro;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Maria Santos");
        professor.setCpf("98765432100");

        strategy = new ProfessorEmprestimoStrategy(professor);

        livro = new Livro();
        livro.setIsbn("987-654-321");
        livro.setTitulo("Algoritmos Avançados");
        livro.setAutor("Autor Professor");
        livro.setEditora("Editora Acadêmica");
        livro.setEdicao("2");
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setLivro(livro);
        estoque.setDisponivel(true);
    }

    // ==================== TESTES pegarLimiteEmprestimos ====================

    @Test
    void pegarLimiteEmprestimos_DeveRetornar5() {
        int limite = strategy.pegarLimiteEmprestimos();

        assertEquals(5, limite);
    }

    // ==================== TESTES podeRealizarEmprestimo ====================

    @Test
    void podeRealizarEmprestimo_DeveRetornarTrue() {
        boolean pode = strategy.podeRealizarEmprestimo();

        assertTrue(pode);
    }

    // ==================== TESTES getEntity ====================

    @Test
    void getEntity_DeveRetornarProfessor() {
        Professor entity = strategy.getEntity();

        assertNotNull(entity);
        assertEquals("Maria Santos", entity.getNome());
        assertEquals("98765432100", entity.getCpf());
    }

    // ==================== TESTES calcularPrazo ====================

    @Test
    void calcularPrazo_DeveRetornar40Dias() {
        LocalDateTime antes = LocalDateTime.now();
        LocalDateTime prazo = strategy.calcularPrazo(livro);
        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(prazo);
        assertTrue(prazo.isAfter(antes.plusDays(39)));
        assertTrue(prazo.isBefore(depois.plusDays(41)));
    }

    @Test
    void calcularPrazo_DeveRetornar40Dias_IndependenteDaCategoria() {
        livro.setCategoriaLivro(CategoriaLivro.ROMANCE);

        LocalDateTime antes = LocalDateTime.now();
        LocalDateTime prazo = strategy.calcularPrazo(livro);
        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(prazo);
        assertTrue(prazo.isAfter(antes.plusDays(39)));
        assertTrue(prazo.isBefore(depois.plusDays(41)));
    }

    @Test
    void calcularPrazo_DeveRetornar40Dias_ParaQualquerLivro() {
        livro.setCategoriaLivro(CategoriaLivro.LETRAS);
        LocalDateTime prazo1 = strategy.calcularPrazo(livro);

        livro.setCategoriaLivro(CategoriaLivro.GESTAO);
        LocalDateTime prazo2 = strategy.calcularPrazo(livro);

        assertNotNull(prazo1);
        assertNotNull(prazo2);
    }

    // ==================== TESTES pegarEmprestimo ====================

    @Test
    void pegarEmprestimo_DeveRetornarEmprestimoComDadosCorretos() {
        Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);

        assertNotNull(emprestimo);
        assertEquals(estoque, emprestimo.getEstoque());
        assertEquals(professor, emprestimo.getUsuario());
        assertNotNull(emprestimo.getDataEmprestimo());
        assertNotNull(emprestimo.getDataDevolucao());
    }

    @Test
    void pegarEmprestimo_DeveSetarDataEmprestimoComoAgora() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        
        Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);
        
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertTrue(emprestimo.getDataEmprestimo().isAfter(antes));
        assertTrue(emprestimo.getDataEmprestimo().isBefore(depois));
    }

    @Test
    void pegarEmprestimo_DeveSetarDataDevolucao40DiasNoFuturo() {
        Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);

        assertNotNull(emprestimo.getDataDevolucao());
        assertTrue(emprestimo.getDataDevolucao().isAfter(emprestimo.getDataEmprestimo().plusDays(39)));
    }
}
