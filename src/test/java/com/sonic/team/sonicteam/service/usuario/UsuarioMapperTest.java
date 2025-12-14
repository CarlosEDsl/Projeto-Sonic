package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.Professor;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioMapperTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioMapper usuarioMapper;

    private Aluno aluno;
    private Professor professor;
    private CategoriaUsuario categoria;
    private Curso curso;

    @BeforeEach
    void setUp() {
        categoria = new CategoriaUsuario(1L, "Graduação");
        curso = new Curso(1L, "Engenharia de Software");

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setCpf("12345678901");
        aluno.setEmail("joao@email.com");
        aluno.setStatus(StatusUsuario.ATIVO);
        aluno.setCategoria(categoria);
        aluno.setCurso(curso);

        professor = new Professor();
        professor.setId(2L);
        professor.setNome("Maria Santos");
        professor.setCpf("98765432109");
        professor.setEmail("maria@email.com");
        professor.setStatus(StatusUsuario.SUSPENSO);
        professor.setCategoria(categoria);
        professor.setCurso(curso);
    }

    @Test
    void toResponseDTO_DeveRetornarDTO_ComDadosCorretos() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(aluno, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(aluno);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("ATIVO", resultado.getStatus());
        assertEquals("Graduação", resultado.getCategoriaNome());
        assertEquals("Engenharia de Software", resultado.getCursoNome());
        assertEquals("ALUNO", resultado.getTipo());
    }

    @Test
    void toResponseDTO_DeveRetornarTipoProfessor() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(professor, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(professor);

        assertEquals("PROFESSOR", resultado.getTipo());
    }

    @Test
    void toResponseDTO_DeveRetornarStatusSuspenso() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(professor, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(professor);

        assertEquals("SUSPENSO", resultado.getStatus());
    }

    @Test
    void toResponseDTO_DeveRetornarStatusInativo() {
        aluno.setStatus(StatusUsuario.INATIVO);
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(aluno, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(aluno);

        assertEquals("INATIVO", resultado.getStatus());
    }

    @Test
    void toResponseDTO_DeveChamarModelMapper() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(aluno, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        usuarioMapper.toResponseDTO(aluno);

        verify(modelMapper).map(aluno, UsuarioResponseDTO.class);
    }

    @Test
    void toResponseDTO_DeveSetarIdCorretamente() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(aluno, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(aluno);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void toResponseDTO_DeveSetarCategoriaNomeCorretamente() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(aluno, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(aluno);

        assertEquals("Graduação", resultado.getCategoriaNome());
    }

    @Test
    void toResponseDTO_DeveSetarCursoNomeCorretamente() {
        UsuarioResponseDTO dtoMock = new UsuarioResponseDTO();
        when(modelMapper.map(aluno, UsuarioResponseDTO.class)).thenReturn(dtoMock);

        UsuarioResponseDTO resultado = usuarioMapper.toResponseDTO(aluno);

        assertEquals("Engenharia de Software", resultado.getCursoNome());
    }
}
