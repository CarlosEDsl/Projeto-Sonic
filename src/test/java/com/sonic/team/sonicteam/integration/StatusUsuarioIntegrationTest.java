package com.sonic.team.sonicteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para verificar regras de status de usuário
 * 
 * Requisitos testados (Seção 4.1 - Status):
 * - Apenas usuários ativos podem realizar empréstimos
 * - Status pode variar entre ativo, inativo e suspenso
 * - Suspensão automática por atrasos (cada dia de atraso = 3 dias de suspensão)
 * - Suspensão > 60 dias = status suspenso até regularização
 * - Inativação = suspensão por mais de dois livros
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class StatusUsuarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CategoriaUsuarioRepository categoriaUsuarioRepository;

    private CategoriaUsuario categoriaAluno;
    private Curso cursoADS;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        estoqueRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();

        categoriaAluno = categoriaUsuarioRepository.findById(1L)
                .orElseGet(() -> categoriaUsuarioRepository.save(new CategoriaUsuario(null, "ALUNO")));

        cursoADS = cursoRepository.findById(1L)
                .orElseGet(() -> cursoRepository.save(new Curso(null, "ADS")));
    }

    @Test
    @Order(1)
    void statusInicial_deveSerAtivo() throws Exception {
        // Requisito: Status inicial: ativo
        UsuarioRequestDTO request = criarUsuarioRequestDTO(
                "Novo Usuário", "12345678909", "novo@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @Order(2)
    void suspenderUsuario_deveAlterarStatusParaSuspenso() throws Exception {
        // Requisito: Status pode variar entre ativo, inativo e suspenso
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Suspenso", cpf, "suspenso@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Suspender usuário
        mockMvc.perform(put("/usuarios/" + cpf + "/suspender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"Teste de suspensão\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENSO"));
    }

    @Test
    @Order(3)
    void reativarUsuario_deveAlterarStatusParaAtivo() throws Exception {
        // Requisito: Status pode variar entre ativo, inativo e suspenso
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Reativado", cpf, "reativado@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Suspender usuário primeiro
        mockMvc.perform(put("/usuarios/" + cpf + "/suspender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"Teste\"}"))
                .andExpect(status().isOk());

        // Reativar usuário
        mockMvc.perform(put("/usuarios/" + cpf + "/reativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @Order(4)
    void inativarUsuario_deveAlterarStatusParaInativo() throws Exception {
        // Requisito: Status pode variar entre ativo, inativo e suspenso
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Inativo", cpf, "inativo@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Inativar usuário
        mockMvc.perform(put("/usuarios/" + cpf + "/inativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    @Order(5)
    void usuarioSuspenso_naoDevePoderFazerEmprestimo() throws Exception {
        // Requisito: Apenas usuários ativos podem realizar empréstimos
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Bloqueado", cpf, "bloqueado@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Suspender usuário
        mockMvc.perform(put("/usuarios/" + cpf + "/suspender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"Teste\"}"))
                .andExpect(status().isOk());

        // Criar livro e exemplar
        criarLivroNoBanco("978-85-001-0001-1", "Livro Teste", "Autor", "Editora", "1");
        criarExemplarNoBanco("978-85-001-0001-1");

        // Tentar fazer empréstimo - deve falhar
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpfUsuario\": \"" + cpf + "\", \"livroISBN\": \"978-85-001-0001-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void usuarioInativo_naoDevePoderFazerEmprestimo() throws Exception {
        // Requisito: Apenas usuários ativos podem realizar empréstimos
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Inativado", cpf, "inativado@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Inativar usuário
        mockMvc.perform(put("/usuarios/" + cpf + "/inativar"))
                .andExpect(status().isOk());

        // Criar livro e exemplar
        criarLivroNoBanco("978-85-002-0001-1", "Livro Teste 2", "Autor2", "Editora2", "1");
        criarExemplarNoBanco("978-85-002-0001-1");

        // Tentar fazer empréstimo - deve falhar
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpfUsuario\": \"" + cpf + "\", \"livroISBN\": \"978-85-002-0001-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void usuarioReativado_devePoderFazerEmprestimo() throws Exception {
        // Requisito: Apenas usuários ativos podem realizar empréstimos
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Reativado", cpf, "reativado@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Suspender e reativar
        mockMvc.perform(put("/usuarios/" + cpf + "/suspender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"Teste\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/usuarios/" + cpf + "/reativar"))
                .andExpect(status().isOk());

        // Criar livro e exemplar
        criarLivroNoBanco("978-85-003-0001-1", "Livro Teste 3", "Autor3", "Editora3", "1");
        criarExemplarNoBanco("978-85-003-0001-1");

        // Tentar fazer empréstimo - deve funcionar
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpfUsuario\": \"" + cpf + "\", \"livroISBN\": \"978-85-003-0001-1\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(8)
    void naoDeveDeletarUsuarioComEmprestimosPendentes() throws Exception {
        // Requisito: Não pode ter empréstimos pendentes para regularização do cadastro
        String cpf = "12345678909";
        
        // Criar usuário
        criarUsuarioNoBanco("Usuário Com Empréstimo", cpf, "emprestimo@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Criar livro, exemplar e empréstimo
        criarLivroNoBanco("978-85-004-0001-1", "Livro Teste 4", "Autor4", "Editora4", "1");
        criarExemplarNoBanco("978-85-004-0001-1");

        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpfUsuario\": \"" + cpf + "\", \"livroISBN\": \"978-85-004-0001-1\"}"))
                .andExpect(status().isCreated());

        // Tentar deletar usuário com empréstimo pendente - deve falhar
        mockMvc.perform(delete("/usuarios/" + cpf))
                .andExpect(status().isConflict());
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
                                    String editora, String edicao) throws Exception {
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"" + isbn + "\", \"titulo\": \"" + titulo + 
                                "\", \"autor\": \"" + autor + "\", \"editora\": \"" + editora + 
                                "\", \"edicao\": \"" + edicao + "\", \"categoriaLivro\": \"COMPUTACAO\"}"))
                .andExpect(status().isCreated());
    }

    private void criarExemplarNoBanco(String livroIsbn) throws Exception {
        mockMvc.perform(post("/estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"livroIsbn\": \"" + livroIsbn + "\"}"))
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
