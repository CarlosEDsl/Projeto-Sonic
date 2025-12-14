package com.sonic.team.sonicteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para verificar os prazos de empréstimo
 * 
 * Requisitos testados (Seção 4.4):
 * - Professores: 5 livros por 40 dias
 * - Alunos: 3 livros por 15 dias (30 dias se for livro da área do curso)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class PrazosEmprestimoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CategoriaUsuarioRepository categoriaUsuarioRepository;

    private CategoriaUsuario categoriaAluno;
    private CategoriaUsuario categoriaProfessor;
    private Curso cursoComputacao;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        estoqueRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();

        categoriaAluno = categoriaUsuarioRepository.findById(1L)
                .orElseGet(() -> categoriaUsuarioRepository.save(new CategoriaUsuario(null, "ALUNO")));
        categoriaProfessor = categoriaUsuarioRepository.findById(2L)
                .orElseGet(() -> categoriaUsuarioRepository.save(new CategoriaUsuario(null, "PROFESSOR")));
        
        // Criar curso COMPUTACAO para testar área do curso
        cursoComputacao = cursoRepository.save(new Curso(null, "COMPUTACAO"));
    }

    @Test
    @Order(1)
    void prazoAluno_15DiasParaLivroForaDaArea() throws Exception {
        // Requisito: Alunos: 15 dias (livro fora da área do curso)
        
        // Criar aluno com curso COMPUTACAO
        criarUsuarioNoBanco("Aluno Computação", "12345678909", "aluno@email.com",
                categoriaAluno.getId(), cursoComputacao.getId(), "ALUNO");
        
        // Criar livro de ROMANCE (fora da área COMPUTACAO)
        criarLivroNoBanco("9788500100011", "Romance Novel", "Autor", "Editora", "1", CategoriaLivro.ROMANCE);
        criarExemplarNoBanco("9788500100011");
        
        // Fazer empréstimo
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("12345678909", "9788500100011");
        MvcResult result = mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // Verificar que o prazo está correto (aproximadamente 15 dias)
        Long emprestimoId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId).orElseThrow();
        
        long diasDePrazo = ChronoUnit.DAYS.between(
                emprestimo.getDataEmprestimo().toLocalDate(),
                emprestimo.getDataDevolucao().toLocalDate()
        );
        
        assertEquals(15, diasDePrazo, "Prazo de empréstimo para aluno fora da área deve ser 15 dias");
    }

    @Test
    @Order(2)
    void prazoAluno_30DiasParaLivroDaArea() throws Exception {
        // Requisito: Alunos: 30 dias se for livro da área do curso
        
        // Criar aluno com curso COMPUTACAO
        criarUsuarioNoBanco("Aluno Computação 2", "52998224725", "aluno2@email.com",
                categoriaAluno.getId(), cursoComputacao.getId(), "ALUNO");
        
        // Criar livro de COMPUTACAO (mesma área do curso)
        criarLivroNoBanco("9788500200011", "Java Book", "Autor", "Editora", "1", CategoriaLivro.COMPUTACAO);
        criarExemplarNoBanco("9788500200011");
        
        // Fazer empréstimo
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("52998224725", "9788500200011");
        MvcResult result = mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // Verificar que o prazo está correto (aproximadamente 30 dias)
        Long emprestimoId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId).orElseThrow();
        
        long diasDePrazo = ChronoUnit.DAYS.between(
                emprestimo.getDataEmprestimo().toLocalDate(),
                emprestimo.getDataDevolucao().toLocalDate()
        );
        
        assertEquals(30, diasDePrazo, "Prazo de empréstimo para aluno na área do curso deve ser 30 dias");
    }

    @Test
    @Order(3)
    void prazoProfessor_40Dias() throws Exception {
        // Requisito: Professores: 40 dias
        
        // Criar professor
        criarUsuarioNoBanco("Professor Teste", "98765432100", "prof@email.com",
                categoriaProfessor.getId(), cursoComputacao.getId(), "PROFESSOR");
        
        // Criar livro qualquer
        criarLivroNoBanco("9788500300011", "Teaching Book", "Autor", "Editora", "1", CategoriaLivro.LETRAS);
        criarExemplarNoBanco("9788500300011");
        
        // Fazer empréstimo
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("98765432100", "9788500300011");
        MvcResult result = mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // Verificar que o prazo está correto (aproximadamente 40 dias)
        Long emprestimoId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId).orElseThrow();
        
        long diasDePrazo = ChronoUnit.DAYS.between(
                emprestimo.getDataEmprestimo().toLocalDate(),
                emprestimo.getDataDevolucao().toLocalDate()
        );
        
        assertEquals(40, diasDePrazo, "Prazo de empréstimo para professor deve ser 40 dias");
    }

    @Test
    @Order(4)
    void dataDevolucaoRegistradaAutomaticamente() throws Exception {
        // Requisito: Data de devolução registrada automaticamente
        
        criarUsuarioNoBanco("Teste Devolução", "12345678909", "dev@email.com",
                categoriaAluno.getId(), cursoComputacao.getId(), "ALUNO");
        
        criarLivroNoBanco("9788500400011", "Test Book", "Autor", "Editora", "1", CategoriaLivro.ROMANCE);
        criarExemplarNoBanco("9788500400011");
        
        // Fazer empréstimo
        EmprestimoRequestDTO request = new EmprestimoRequestDTO("12345678909", "9788500400011");
        MvcResult result = mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataEmprestimo").exists())
                .andExpect(jsonPath("$.dataDevolucao").exists())
                .andReturn();
        
        // Verificar que a data de devolução foi calculada
        Long emprestimoId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId).orElseThrow();
        
        assertNotNull(emprestimo.getDataEmprestimo(), "Data de empréstimo deve ser registrada");
        assertNotNull(emprestimo.getDataDevolucao(), "Data de devolução deve ser calculada automaticamente");
        assertTrue(emprestimo.getDataDevolucao().isAfter(emprestimo.getDataEmprestimo()), 
                "Data de devolução deve ser após a data de empréstimo");
    }

    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================

    private UsuarioRequestDTO criarUsuarioRequestDTO(String nome, String cpf, String email,
                                                      Long categoriaId, Long cursoId, String tipo) {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNome(nome);
        dto.setCpf(cpf);
        dto.setEmail(email);
        dto.setCategoriaId(categoriaId);
        dto.setCursoId(cursoId);
        dto.setTipo(tipo);
        return dto;
    }

    private void criarLivroNoBanco(String isbn, String titulo, String autor,
                                    String editora, String edicao, CategoriaLivro categoria) {
        Livro livro = new Livro();
        livro.setIsbn(isbn);
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setEditora(editora);
        livro.setEdicao(edicao);
        livro.setCategoriaLivro(categoria);
        livro.setDisponivel(true);
        livroRepository.save(livro);
    }

    private void criarExemplarNoBanco(String livroIsbn) throws Exception {
        EstoqueRequestDTO request = new EstoqueRequestDTO(livroIsbn);
        mockMvc.perform(post("/estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void criarUsuarioNoBanco(String nome, String cpf, String email,
                                      Long categoriaId, Long cursoId, String tipo) throws Exception {
        UsuarioRequestDTO request = criarUsuarioRequestDTO(nome, cpf, email, categoriaId, cursoId, tipo);
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
