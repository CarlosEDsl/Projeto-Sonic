# Projeto Sonic – API da Biblioteca

Este repositório contém uma API de biblioteca acadêmica com gerenciamento de usuários, livros, estoque de exemplares e empréstimos. Abaixo você encontra uma documentação estilo README do GitHub, com todos os “waypoints” (endpoints) do software, o que cada um espera e o que retorna, além de exemplos de uso.

Observação: a aplicação usa H2 em memória por padrão e já vem com scripts para criação do esquema e carga inicial de dados (cursos e categorias de usuário).

## Sumário
- Como executar
- SOLID aplicado no projeto
- Documentação interativa (Swagger)
- Convenções gerais da API
- Endpoints
  - Usuários
  - Livros
  - Estoque (Exemplares)
  - Empréstimos
  - Catálogos

---

## Como executar

Requisitos: Java 17+ e Maven.

1. Build e execução:
   - mvn spring-boot:run
   - A API sobe na porta 8090 (http://localhost:8090)

2. Configurações relevantes (src/main/resources/application.properties):
   - spring.datasource.url=jdbc:h2:mem:biblioteca
   - spring.jpa.hibernate.ddl-auto=create-drop
   - spring.sql.init.mode=always (executa schema.sql e data.sql)
   - server.port=8090

## SOLID aplicado no projeto

- SRP (Single Responsibility Principle): aqui a ideia é simples — cada classe faz uma coisa. Os controladores cuidam das rotas, os serviços da lógica, os repositórios do banco, os validators das checagens e os mappers das conversões. Isso ajuda a entender e arrumar partes do sistema sem bagunçar o resto.

- OCP (Open/Closed Principle): o código foi pensado pra crescer sem precisar reescrever tudo. Um exemplo prático é como as regras de empréstimo mudam conforme o tipo de usuário: cada comportamento fica em uma "estratégia" separada. Quer adicionar um tipo novo? Cria a estratégia e pronto — não precisa mexer na lógica que orquestra o empréstimo.

- LSP (Liskov Substitution Principle): na prática, isso significa que objetos especializados podem ser usados onde o genérico é esperado sem causar erros. No projeto, o serviço de empréstimo trata usuários de forma genérica porque cada tipo implementa a mesma interface/contrato (ou comportamento esperado).

- ISP (Interface Segregation Principle): preferimos interfaces pequenas e específicas. Em vez de uma classe gigante com vários métodos misturados, temos interfaces focadas — cada consumidor depende só do que precisa, sem métodos inúteis.

- DIP (Dependency Inversion Principle): o código depende mais de abstrações (interfaces) do que de implementações. Os controladores recebem serviços por injeção, e isso facilita trocar uma implementação por um mock nos testes ou por outra versão sem mudar quem usa esse serviço.

## Documentação interativa (Swagger)

- Swagger UI: http://localhost:8090/library
- OpenAPI JSON: http://localhost:8090/library

Observação: ambos os caminhos estão configurados para “/library” no application.properties.

## Convenções gerais da API

- Todas as rotas respondem e aceitam JSON (quando há corpo de requisição).
- Códigos de retorno comuns:
  - 200 OK: operação bem sucedida
  - 201 Created: recurso criado
  - 204 No Content: remoção/ação sem corpo de retorno
  - 400 Bad Request: erro de validação/parâmetro inválido
  - 404 Not Found: recurso não encontrado
  - 409 Conflict: conflitos de negócio (ex.: CPF duplicado) – quando aplicável

---

## Endpoints

### 1) Usuários

Base path: /usuarios

- POST /usuarios
  - Descrição: Cria um novo usuário (Aluno, Professor ou Bibliotecário).
  - Body (application/json):
    {
      "nome": "string (3..100)",
      "cpf": "somente dígitos (11) ou no formato XXX.XXX.XXX-XX",
      "email": "email válido",
      "categoriaId": number (>0),
      "cursoId": number (>0),
      "tipo": "ALUNO|PROFESSOR|BIBLIOTECARIO"
    }
  - Retorno 201 (application/json) – UsuarioResponseDTO:
    {
      "id": number,
      "nome": "string",
      "cpf": "string",
      "email": "string",
      "status": "ATIVO|INATIVO|SUSPENSO",
      "categoriaNome": "string",
      "cursoNome": "string",
      "tipo": "ALUNO|PROFESSOR|BIBLIOTECARIO"
    }
  - Exemplo cURL:
    curl -X POST http://localhost:8090/usuarios \
      -H "Content-Type: application/json" \
      -d '{
            "nome":"Maria Silva",
            "cpf":"12345678901",
            "email":"maria@exemplo.com",
            "categoriaId":1,
            "cursoId":1,
            "tipo":"ALUNO"
          }'

- GET /usuarios
  - Descrição: Lista usuários com paginação.
  - Query params padrão do Spring Data:
    - page (0..n), size (ex.: 20), sort (ex.: nome,asc)
  - Retorno 200 (application/json): Page<UsuarioResponseDTO>

- GET /usuarios/{cpf}
  - Descrição: Busca usuário pelo CPF.
  - Path param: cpf (formato validado)
  - Retorno 200 (application/json): UsuarioResponseDTO

- PUT /usuarios/{cpf}
  - Descrição: Atualiza dados do usuário pelo CPF.
  - Body: igual ao POST /usuarios
  - Retorno 200 (application/json): UsuarioResponseDTO atualizado

- DELETE /usuarios/{cpf}
  - Descrição: Remove usuário.
  - Retorno 204 (sem corpo)

- PATCH /usuarios/{cpf}/suspender
  - Descrição: Suspende um usuário.
  - Body:
    { "motivo": "string (obrigatório)" }
  - Retorno 200 (application/json): UsuarioResponseDTO

- PATCH /usuarios/{cpf}/reativar
  - Descrição: Reativa um usuário.
  - Retorno 200 (application/json): UsuarioResponseDTO

- PATCH /usuarios/{cpf}/inativar
  - Descrição: Inativa um usuário.
  - Retorno 200 (application/json): UsuarioResponseDTO

### 2) Livros

Base path: /livros

- POST /livros
  - Descrição: Cria um livro.
  - Body:
    {
      "isbn": "10 ou 13 dígitos",
      "titulo": "string (>=3)",
      "autor": "string",
      "editora": "string",
      "edicao": "apenas números",
      "categoriaLivro": "[ver /catalogos/categorias-livro]"
    }
  - Retorno 201 (application/json) – LivroResponseDTO:
    {
      "isbn": "string",
      "titulo": "string",
      "autor": "string",
      "editora": "string",
      "edicao": "string",
      "categoria": "string"
    }

- GET /livros
  - Descrição: Lista livros com filtros opcionais.
  - Query params: titulo, autor, editora, edicao, categoria
  - Retorno 200 (application/json): Lista de LivroResponseDTO

- GET /livros/{isbn}
  - Descrição: Busca livro por ISBN.
  - Retorno 200 (application/json): LivroResponseDTO

- PUT /livros/{isbn}
  - Descrição: Atualiza livro.
  - Body: igual ao POST /livros
  - Retorno 200 (application/json): LivroResponseDTO atualizado

- DELETE /livros/{isbn}
  - Descrição: Remove livro.
  - Retorno 204 (sem corpo)

### 3) Estoque (Exemplares)

Base path: /estoque

- POST /estoque
  - Descrição: Cadastra um novo exemplar atrelado a um livro.
  - Body:
    { "livroIsbn": "string" }
  - Retorno 200 (application/json) – EstoqueResponseDTO:
    { "id": number, "livroIsbn": "string", "disponivel": true|false }

- GET /estoque/disponiveis
  - Descrição: Lista exemplares disponíveis, opcionalmente filtrando por ISBN do livro.
  - Query params: livroIsbn (opcional)
  - Retorno 200 (application/json): Lista de EstoqueResponseDTO

- GET /estoque/{id}
  - Descrição: Busca um exemplar pelo ID.
  - Retorno 200 (application/json): EstoqueResponseDTO

- PUT /estoque
  - Descrição: Atualiza disponibilidade de um exemplar.
  - Body:
    { "id": number, "disponivel": true|false }
  - Retorno 200 (application/json): EstoqueResponseDTO atualizado

- DELETE /estoque/{id}
  - Descrição: Deleta um exemplar.
  - Retorno 204 (sem corpo)

### 4) Empréstimos

Base path: /emprestimos

- POST /emprestimos
  - Descrição: Cria um empréstimo para um usuário e um exemplar disponível.
  - Body:
    { "cpfUsuario": "string", "livroISBN": "string" }
  - Retorno 201 (application/json): Emprestimo (entidade) com campos principais:
    {
      "id": number,
      "usuario": { ...usuario... },
      "estoque": { ...exemplar... },
      "dataEmprestimo": "yyyy-MM-dd'T'HH:mm:ss",
      "dataDevolucao": "yyyy-MM-dd'T'HH:mm:ss|null",
      "dataEntrega": "yyyy-MM-dd'T'HH:mm:ss|null"
    }

- GET /emprestimos
  - Descrição: Lista todos os empréstimos.
  - Retorno 200 (application/json): Lista de Emprestimo

- GET /emprestimos/{id}
  - Descrição: Busca um empréstimo por ID.
  - Retorno 200 (application/json): Emprestimo

- PUT /emprestimos/{id}/devolucao
  - Descrição: Registra a devolução (dataEntrega = agora).
  - Retorno 200 (application/json): Emprestimo atualizado

Regras de negócio (alto nível):
- Usuário precisa estar ATIVO e dentro do limite de empréstimos do seu tipo.
- Exemplar precisa estar disponível.

### 5) Catálogos

Base path: /catalogos

- GET /catalogos/categorias-livro
  - Descrição: Lista categorias de livro (enums).
  - Retorno 200 (application/json): ["CATEGORIA1", "CATEGORIA2", ...]

- GET /catalogos/categorias-usuario
  - Descrição: Lista categorias de usuário existentes.
  - Retorno 200 (application/json):
    [ { "id": number, "nome": "string" }, ... ]

- GET /catalogos/cursos
  - Descrição: Lista cursos existentes.
  - Retorno 200 (application/json):
    [ { "id": number, "nome": "string" }, ... ]

---

## Exemplos rápidos de cURL

- Criar usuário:
  curl -X POST http://localhost:8090/usuarios \
    -H "Content-Type: application/json" \
    -d '{"nome":"João","cpf":"12345678901","email":"joao@ex.com","categoriaId":1,"cursoId":1,"tipo":"ALUNO"}'

- Criar livro:
  curl -X POST http://localhost:8090/livros \
    -H "Content-Type: application/json" \
    -d '{"isbn":"1234567890123","titulo":"Engenharia de Software","autor":"Sommerville","editora":"ABC","edicao":"10","categoriaLivro":"CIENCIA"}'

- Cadastrar exemplar:
  curl -X POST http://localhost:8090/estoque \
    -H "Content-Type: application/json" \
    -d '{"livroIsbn":"1234567890123"}'

- Criar empréstimo:
  curl -X POST http://localhost:8090/emprestimos \
    -H "Content-Type: application/json" \
    -d '{"cpfUsuario":"12345678901","livroISBN":"1234567890123"}'

---

## Notas finais

- Para schemas detalhados e testes interativos, utilize o Swagger UI em http://localhost:8090/library após subir a aplicação.
- Em caso de dúvidas ou para novas rotas, consulte os controladores no pacote com.sonic.team.sonicteam.controller.