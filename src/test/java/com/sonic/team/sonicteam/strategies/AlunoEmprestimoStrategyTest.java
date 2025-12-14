package com.sonic.team.sonicteam.strategies;

import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Estoque;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AlunoEmprestimoStrategyTest {

    private Aluno aluno;
    private AlunoEmprestimoStrategy strategy;
    private Livro livro;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setCpf("12345678901");
        aluno.setCategoria(new CategoriaUsuario(1L, "COMPUTACAO"));

        strategy = new AlunoEmprestimoStrategy(aluno);

        livro = new Livro();
        livro.setIsbn("123-456-789");
        livro.setTitulo("Java para Iniciantes");
        livro.setAutor("Autor Teste");
        livro.setEditora("Editora Teste");
        livro.setEdicao("1");
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setLivro(livro);
        estoque.setDisponivel(true);
    }

    // ==================== TESTES pegarLimiteEmprestimos ====================

    @Test
    void pegarLimiteEmprestimos_DeveRetornar3() {
        int limite = strategy.pegarLimiteEmprestimos();

        assertEquals(3, limite);
    }

    // ==================== TESTES podeRealizarEmprestimo ====================

    @Test
    void podeRealizarEmprestimo_DeveRetornarTrue() {
        boolean pode = strategy.podeRealizarEmprestimo();

        assertTrue(pode);
    }

    // ==================== TESTES getEntity ====================

    @Test
    void getEntity_DeveRetornarAluno() {
        Aluno entity = strategy.getEntity();

        assertNotNull(entity);
        assertEquals("João Silva", entity.getNome());
        assertEquals("12345678901", entity.getCpf());
    }

    // ==================== TESTES calcularPrazo ====================

    @Test
    void calcularPrazo_DeveRetornar30Dias_QuandoLivroDaAreaDoCurso() {
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);
        aluno.setCategoria(new CategoriaUsuario(1L, "COMPUTACAO"));

        LocalDateTime antes = LocalDateTime.now();
        LocalDateTime prazo = strategy.calcularPrazo(livro);
        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(prazo);
        assertTrue(prazo.isAfter(antes.plusDays(29)));
        assertTrue(prazo.isBefore(depois.plusDays(31)));
    }

    @Test
    void calcularPrazo_DeveRetornar15Dias_QuandoLivroForaDeArea() {
        livro.setCategoriaLivro(CategoriaLivro.ROMANCE);
        aluno.setCategoria(new CategoriaUsuario(1L, "COMPUTACAO"));

        LocalDateTime antes = LocalDateTime.now();
        LocalDateTime prazo = strategy.calcularPrazo(livro);
        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(prazo);
        assertTrue(prazo.isAfter(antes.plusDays(14)));
        assertTrue(prazo.isBefore(depois.plusDays(16)));
    }

    @Test
    void calcularPrazo_DeveRetornar15Dias_QuandoCategoriaDiferente() {
        livro.setCategoriaLivro(CategoriaLivro.LETRAS);
        aluno.setCategoria(new CategoriaUsuario(1L, "GESTAO"));

        LocalDateTime prazo = strategy.calcularPrazo(livro);

        assertNotNull(prazo);
    }

    // ==================== TESTES pegarEmprestimo ====================

    @Test
    void pegarEmprestimo_DeveRetornarEmprestimoComDadosCorretos() {
        Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);

        assertNotNull(emprestimo);
        assertEquals(estoque, emprestimo.getEstoque());
        assertEquals(aluno, emprestimo.getUsuario());
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
    void pegarEmprestimo_DeveSetarDataDevolucaoCorreta() {
        livro.setCategoriaLivro(CategoriaLivro.COMPUTACAO);
        aluno.setCategoria(new CategoriaUsuario(1L, "COMPUTACAO"));

        Emprestimo emprestimo = strategy.pegarEmprestimo(estoque);

        assertNotNull(emprestimo.getDataDevolucao());
        assertTrue(emprestimo.getDataDevolucao().isAfter(emprestimo.getDataEmprestimo()));
    }
}
