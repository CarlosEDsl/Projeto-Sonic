package com.sonic.team.sonicteam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de Integração para verificar os Requisitos do Sistema de Biblioteca
 * 
 * Este arquivo contém testes que verificam se a aplicação atende todos os 
 * requerimentos especificados no documento docs/requerimentos.md
 * 
 * Requisitos testados:
 * - Seção 4: Regras de Negócio (Usuário, Livro, Estoque, Empréstimo)
 * - Seção 5: Documentação da API (Endpoints)
 * - Anexo I: Validação de CPF
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class RequerimentosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CategoriaUsuarioRepository categoriaUsuarioRepository;

    private CategoriaUsuario categoriaAluno;
    private CategoriaUsuario categoriaProfessor;
    private Curso cursoADS;
    private Curso cursoPedagogia;

    @BeforeEach
    void setUp() {
        // Limpar dados existentes
        emprestimoRepository.deleteAll();
        estoqueRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Configurar categorias de usuário se não existirem
        categoriaAluno = categoriaUsuarioRepository.findById(1L)
                .orElseGet(() -> categoriaUsuarioRepository.save(new CategoriaUsuario(null, "ALUNO")));
        categoriaProfessor = categoriaUsuarioRepository.findById(2L)
                .orElseGet(() -> categoriaUsuarioRepository.save(new CategoriaUsuario(null, "PROFESSOR")));

        // Configurar cursos se não existirem
        cursoADS = cursoRepository.findById(1L)
                .orElseGet(() -> cursoRepository.save(new Curso(null, "ADS")));
        cursoPedagogia = cursoRepository.findById(2L)
                .orElseGet(() -> cursoRepository.save(new Curso(null, "PEDAGOGIA")));
    }

    // ============================================================================
    // SEÇÃO 4.1 - REGRAS DE NEGÓCIO: USUÁRIO
    // ============================================================================

    @Test
    @Order(1)
    void RN_Usuario_01_cadastrarUsuarioComCpfValido() throws Exception {
        // Requisito: CPF único (validado conforme Anexo I)
        UsuarioRequestDTO request = criarUsuarioRequestDTO(
                "João Silva",
                "12345678909", // CPF válido conforme Anexo I
                "joao@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.status").value("ATIVO")); // Status inicial: ativo
    }

    @Test
    @Order(2)
    void RN_Usuario_02_rejeitarCpfInvalido() throws Exception {
        // Requisito: CPF deve ser validado conforme Anexo I
        UsuarioRequestDTO request = criarUsuarioRequestDTO(
                "Maria Teste",
                "11111111111", // CPF inválido (sequência repetida)
                "maria@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void RN_Usuario_03_rejeitarCpfDuplicado() throws Exception {
        // Requisito: Não pode haver CPF duplicado
        String cpfValido = "12345678909";

        UsuarioRequestDTO primeiroUsuario = criarUsuarioRequestDTO(
                "João Silva",
                cpfValido,
                "joao@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        // Primeiro cadastro deve funcionar
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primeiroUsuario)))
                .andExpect(status().isCreated());

        // Segundo cadastro com mesmo CPF deve falhar
        UsuarioRequestDTO segundoUsuario = criarUsuarioRequestDTO(
                "Pedro Santos",
                cpfValido,
                "pedro@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundoUsuario)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    void RN_Usuario_04_statusInicialAtivo() throws Exception {
        // Requisito: Status inicial: ativo
        UsuarioRequestDTO request = criarUsuarioRequestDTO(
                "Ana Maria",
                "98765432100",
                "ana@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @Order(5)
    void RN_Usuario_05_vincularCategoriaECurso() throws Exception {
        // Requisito: Deve estar vinculado a uma categoria e curso válidos
        UsuarioRequestDTO request = criarUsuarioRequestDTO(
                "Carlos Souza",
                "52998224725",
                "carlos@email.com",
                categoriaProfessor.getId(),
                cursoPedagogia.getId(),
                "PROFESSOR"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoria").exists())
                .andExpect(jsonPath("$.curso").exists());
    }

    // ============================================================================
    // SEÇÃO 4.2 - REGRAS DE NEGÓCIO: LIVRO
    // ============================================================================

    @Test
    @Order(10)
    void RN_Livro_01_cadastrarLivroComDadosValidos() throws Exception {
        // Requisito: Campos obrigatórios: título, ISBN, autor, editora, edição, categoria
        // ISBN deve ter 10 ou 13 dígitos numéricos
        LivroRequestDTO request = criarLivroRequestDTO(
                "9788533302273", // ISBN 13 dígitos
                "Clean Code",
                "Robert C. Martin",
                "Alta Books",
                "1",
                CategoriaLivro.COMPUTACAO
        );

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("9788533302273"))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.autor").value("Robert C. Martin"));
    }

    @Test
    @Order(11)
    void RN_Livro_02_rejeitarCombinacaoDuplicada() throws Exception {
        // Requisito: Combinação única de autor, editora e edição
        LivroRequestDTO primeiroLivro = criarLivroRequestDTO(
                "9788533302273",
                "Clean Code",
                "Robert C. Martin",
                "Alta Books",
                "1",
                CategoriaLivro.COMPUTACAO
        );

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primeiroLivro)))
                .andExpect(status().isCreated());

        // Tentar cadastrar outro livro com mesma combinação autor/editora/edição
        LivroRequestDTO segundoLivro = criarLivroRequestDTO(
                "9788533302280", // ISBN diferente
                "Outro Título",
                "Robert C. Martin", // Mesmo autor
                "Alta Books",       // Mesma editora
                "1",                // Mesma edição
                CategoriaLivro.COMPUTACAO
        );

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundoLivro)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(12)
    void RN_Livro_03_buscarLivroPorISBN() throws Exception {
        // Requisito: GET /:isbn - Mostra detalhes de um livro
        LivroRequestDTO request = criarLivroRequestDTO(
                "9788533302297",
                "Design Patterns",
                "Gang of Four",
                "Addison Wesley",
                "1",
                CategoriaLivro.COMPUTACAO
        );

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/livros/9788533302297"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Design Patterns"));
    }

    // ============================================================================
    // SEÇÃO 4.3 - REGRAS DE NEGÓCIO: ESTOQUE
    // ============================================================================

    @Test
    @Order(20)
    void RN_Estoque_01_cadastrarExemplar() throws Exception {
        // Requisito: Campos obrigatórios: ISBN livro, código exemplar
        criarLivroNoBanco("9788533302303", "Test Book", "Autor", "Editora", "1", CategoriaLivro.COMPUTACAO);

        EstoqueRequestDTO request = new EstoqueRequestDTO("9788533302303");

        mockMvc.perform(post("/estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.disponivel").value(true));
    }

    @Test
    @Order(21)
    void RN_Estoque_02_listarExemplaresDisponiveis() throws Exception {
        // Requisito: Lista exemplares com disponibilidade
        criarLivroNoBanco("9788533302310", "Test Book 2", "Autor", "Editor", "2", CategoriaLivro.COMPUTACAO);

        EstoqueRequestDTO request = new EstoqueRequestDTO("9788533302310");

        mockMvc.perform(post("/estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ============================================================================
    // SEÇÃO 4.4 - REGRAS DE NEGÓCIO: EMPRÉSTIMO
    // ============================================================================

    @Test
    @Order(30)
    void RN_Emprestimo_01_alunoLimite3Livros() throws Exception {
        // Requisito: Alunos: 3 livros por 15 dias
        String cpfAluno = "12345678909";

        // Criar aluno
        criarUsuarioNoBanco("Aluno Teste", cpfAluno, "aluno@email.com", 
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Criar 4 livros com exemplares (ISBNs de 13 dígitos)
        String[] isbns = {"9788500000011", "9788500000022", "9788500000033", "9788500000044"};
        for (int i = 0; i < 4; i++) {
            criarLivroNoBanco(isbns[i], "Livro " + (i+1), "Autor " + (i+1), "Editora " + (i+1), String.valueOf(i+1), CategoriaLivro.COMPUTACAO);
            criarExemplarNoBanco(isbns[i]);
        }

        // Realizar 3 empréstimos (deve funcionar)
        for (int i = 0; i < 3; i++) {
            EmprestimoRequestDTO request = new EmprestimoRequestDTO(cpfAluno, isbns[i]);
            mockMvc.perform(post("/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // O 4º empréstimo deve falhar (limite atingido)
        EmprestimoRequestDTO quartoEmprestimo = new EmprestimoRequestDTO(cpfAluno, isbns[3]);
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quartoEmprestimo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(31)
    void RN_Emprestimo_02_professorLimite5Livros() throws Exception {
        // Requisito: Professores: 5 livros por 40 dias
        String cpfProfessor = "52998224725";

        // Criar professor
        criarUsuarioNoBanco("Professor Teste", cpfProfessor, "prof@email.com",
                categoriaProfessor.getId(), cursoPedagogia.getId(), "PROFESSOR");

        // Criar 6 livros com exemplares
        String[] isbns = {"9788511100011", "9788511100022", "9788511100033", 
                          "9788511100044", "9788511100055", "9788511100066"};
        for (int i = 0; i < 6; i++) {
            criarLivroNoBanco(isbns[i], "Livro Prof " + (i+1), "Autor Prof " + (i+1), "Editora Prof " + (i+1), String.valueOf(i+1), CategoriaLivro.LETRAS);
            criarExemplarNoBanco(isbns[i]);
        }

        // Realizar 5 empréstimos (deve funcionar)
        for (int i = 0; i < 5; i++) {
            EmprestimoRequestDTO request = new EmprestimoRequestDTO(cpfProfessor, isbns[i]);
            mockMvc.perform(post("/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // O 6º empréstimo deve falhar (limite atingido)
        EmprestimoRequestDTO sextoEmprestimo = new EmprestimoRequestDTO(cpfProfessor, isbns[5]);
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sextoEmprestimo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(32)
    void RN_Emprestimo_03_usuarioDeveEstarAtivo() throws Exception {
        // Requisito: Usuário deve estar ativo e sem suspensões
        String cpfUsuario = "98765432100";

        // Criar usuário
        criarUsuarioNoBanco("Usuario Inativo", cpfUsuario, "inativo@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Suspender usuário
        mockMvc.perform(put("/usuarios/" + cpfUsuario + "/suspender")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\": \"Teste de suspensão\"}"))
                .andExpect(status().isOk());

        // Criar livro e exemplar
        criarLivroNoBanco("9788522200011", "Livro Teste", "Autor", "Editora", "1", CategoriaLivro.ROMANCE);
        criarExemplarNoBanco("9788522200011");

        // Tentar fazer empréstimo com usuário suspenso
        EmprestimoRequestDTO request = new EmprestimoRequestDTO(cpfUsuario, "9788522200011");
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(33)
    void RN_Emprestimo_04_livroDeveEstarDisponivel() throws Exception {
        // Requisito: Livro deve estar disponível
        String cpfAluno1 = "12345678909";
        String cpfAluno2 = "52998224725";

        // Criar dois alunos
        criarUsuarioNoBanco("Aluno 1", cpfAluno1, "aluno1@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");
        criarUsuarioNoBanco("Aluno 2", cpfAluno2, "aluno2@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        // Criar livro com apenas 1 exemplar
        criarLivroNoBanco("9788533300011", "Livro Único", "Autor", "Editora", "1", CategoriaLivro.COMPUTACAO);
        criarExemplarNoBanco("9788533300011");

        // Primeiro aluno faz empréstimo
        EmprestimoRequestDTO primeiroEmprestimo = new EmprestimoRequestDTO(cpfAluno1, "9788533300011");
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primeiroEmprestimo)))
                .andExpect(status().isCreated());

        // Segundo aluno tenta emprestar o mesmo livro (sem exemplar disponível)
        EmprestimoRequestDTO segundoEmprestimo = new EmprestimoRequestDTO(cpfAluno2, "9788533300011");
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundoEmprestimo)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(34)
    void RN_Emprestimo_05_registrarDevolucao() throws Exception {
        // Requisito: Data de devolução registrada automaticamente
        String cpfAluno = "12345678909";

        criarUsuarioNoBanco("Aluno Devolução", cpfAluno, "dev@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        criarLivroNoBanco("9788544400011", "Livro Devolução", "Autor", "Editora", "1", CategoriaLivro.ROMANCE);
        criarExemplarNoBanco("9788544400011");

        // Fazer empréstimo
        EmprestimoRequestDTO request = new EmprestimoRequestDTO(cpfAluno, "9788544400011");
        MvcResult result = mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extrair ID do empréstimo
        String response = result.getResponse().getContentAsString();
        Long emprestimoId = objectMapper.readTree(response).get("id").asLong();

        // Registrar devolução
        mockMvc.perform(put("/emprestimos/" + emprestimoId + "/devolucao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataEntrega").exists());
    }

    // ============================================================================
    // SEÇÃO 5 - ENDPOINTS DA API
    // ============================================================================

    @Test
    @Order(50)
    void API_Usuarios_listarTodos() throws Exception {
        // GET /usuarios - Lista todos os usuários
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(51)
    void API_Usuarios_buscarPorCpf() throws Exception {
        // GET /usuarios/:cpf - Retorna detalhes de um usuário específico
        String cpf = "12345678909";
        criarUsuarioNoBanco("Busca CPF", cpf, "busca@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        mockMvc.perform(get("/usuarios/" + cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Busca CPF"));
    }

    @Test
    @Order(52)
    void API_Usuarios_atualizar() throws Exception {
        // PUT /usuarios/:cpf - Atualiza dados do usuário
        String cpf = "12345678909";
        criarUsuarioNoBanco("Original", cpf, "original@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        UsuarioRequestDTO updateRequest = criarUsuarioRequestDTO(
                "Atualizado",
                cpf,
                "atualizado@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(put("/usuarios/" + cpf)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Atualizado"));
    }

    @Test
    @Order(53)
    void API_Usuarios_deletar() throws Exception {
        // DELETE /usuarios/:cpf - Remove usuário (se não tiver empréstimos)
        String cpf = "52998224725";
        criarUsuarioNoBanco("Para Deletar", cpf, "deletar@email.com",
                categoriaAluno.getId(), cursoADS.getId(), "ALUNO");

        mockMvc.perform(delete("/usuarios/" + cpf))
                .andExpect(status().isNoContent());

        // Verificar se foi deletado
        mockMvc.perform(get("/usuarios/" + cpf))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(54)
    void API_Livros_listarComFiltros() throws Exception {
        // GET /livros - Lista todos os livros (com filtros)
        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk());

        // Teste com filtro por categoria
        mockMvc.perform(get("/livros").param("categoria", "COMPUTACAO"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(55)
    void API_Livros_atualizarLivro() throws Exception {
        // PUT /livros/:isbn - Atualiza informações do livro
        criarLivroNoBanco("9788555500011", "Original", "Autor", "Editora", "1", CategoriaLivro.ROMANCE);

        LivroRequestDTO updateRequest = criarLivroRequestDTO(
                "9788555500011",
                "Atualizado",
                "Autor",
                "Editora",
                "1",
                CategoriaLivro.ROMANCE
        );

        mockMvc.perform(put("/livros/9788555500011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Atualizado"));
    }

    @Test
    @Order(56)
    void API_Livros_deletar() throws Exception {
        // DELETE /livros/:isbn - Remove livro (se não estiver emprestado)
        criarLivroNoBanco("9788566600011", "Para Deletar", "Autor", "Edit", "1", CategoriaLivro.GESTAO);

        mockMvc.perform(delete("/livros/9788566600011"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(57)
    void API_Emprestimos_listar() throws Exception {
        // GET /emprestimos - Lista todos os empréstimos
        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(58)
    void API_Catalogos_listarCategoriasUsuario() throws Exception {
        // GET /catalogos/categorias-usuario - Lista tipos de usuário
        mockMvc.perform(get("/catalogos/categorias-usuario"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(59)
    void API_Catalogos_listarCategoriasLivro() throws Exception {
        // GET /catalogos/categorias-livro - Lista categorias de livros
        mockMvc.perform(get("/catalogos/categorias-livro"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(60)
    void API_Catalogos_listarCursos() throws Exception {
        // GET /catalogos/cursos - Lista cursos disponíveis
        mockMvc.perform(get("/catalogos/cursos"))
                .andExpect(status().isOk());
    }

    // ============================================================================
    // ANEXO I - VALIDAÇÃO DE CPF
    // ============================================================================

    @Test
    @Order(70)
    void CPF_validarCpfComExatamente11Digitos() throws Exception {
        // Requisito: CPF válido possui 11 dígitos
        UsuarioRequestDTO requestComCpfCurto = criarUsuarioRequestDTO(
                "Teste CPF",
                "123456789", // Menos de 11 dígitos
                "teste@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestComCpfCurto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(71)
    void CPF_rejeitarSequenciaRepetida() throws Exception {
        // Requisito: Verificar se não é uma sequência repetida
        String[] cpfsInvalidos = {
                "00000000000",
                "11111111111",
                "22222222222",
                "33333333333",
                "44444444444",
                "55555555555",
                "66666666666",
                "77777777777",
                "88888888888",
                "99999999999"
        };

        for (String cpfInvalido : cpfsInvalidos) {
            UsuarioRequestDTO request = criarUsuarioRequestDTO(
                    "Teste Repetido",
                    cpfInvalido,
                    "teste@email.com",
                    categoriaAluno.getId(),
                    cursoADS.getId(),
                    "ALUNO"
            );

            mockMvc.perform(post("/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @Order(72)
    void CPF_validarExemplo12345678909() throws Exception {
        // Requisito: Exemplo prático do Anexo I - CPF 123.456.789-09 é válido
        UsuarioRequestDTO request = criarUsuarioRequestDTO(
                "Exemplo Anexo I",
                "12345678909", // CPF do exemplo
                "exemplo@email.com",
                categoriaAluno.getId(),
                cursoADS.getId(),
                "ALUNO"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
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

    private LivroRequestDTO criarLivroRequestDTO(String isbn, String titulo, String autor,
                                                  String editora, String edicao, CategoriaLivro categoriaLivro) {
        return new LivroRequestDTO(isbn, titulo, autor, editora, edicao, categoriaLivro);
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
