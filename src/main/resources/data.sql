-- Script de inicialização do banco de dados
-- Adiciona dados padrão para testes

-- ==================== CURSOS ====================
INSERT INTO curso (nome) VALUES 
    ('Ciência da Computação'),
    ('Engenharia de Software'),
    ('Sistemas de Informação');

-- ==================== CATEGORIAS DE USUÁRIO ====================
INSERT INTO categoria_usuario (nome) VALUES 
    ('Aluno'),
    ('Professor'),
    ('Bibliotecário');
