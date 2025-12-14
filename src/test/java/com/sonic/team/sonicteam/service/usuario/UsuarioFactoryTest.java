package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.Bibliotecario;
import com.sonic.team.sonicteam.model.usuario.Professor;
import com.sonic.team.sonicteam.model.usuario.Usuario;
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
class UsuarioFactoryTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioFactory usuarioFactory;

    private UsuarioRequestDTO requestDTO;
    private Aluno aluno;
    private Professor professor;
    private Bibliotecario bibliotecario;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("João Silva");
        requestDTO.setCpf("12345678901");
        requestDTO.setEmail("joao@email.com");
        requestDTO.setCategoriaId(1L);
        requestDTO.setCursoId(1L);

        aluno = new Aluno();
        aluno.setNome("João Silva");

        professor = new Professor();
        professor.setNome("Maria Santos");

        bibliotecario = new Bibliotecario();
        bibliotecario.setNome("Pedro Oliveira");
    }

    @Test
    void criar_DeveRetornarAluno_QuandoTipoForAluno() {
        requestDTO.setTipo("ALUNO");
        when(modelMapper.map(requestDTO, Aluno.class)).thenReturn(aluno);

        Usuario resultado = usuarioFactory.criar(requestDTO);

        assertNotNull(resultado);
        assertInstanceOf(Aluno.class, resultado);
        verify(modelMapper).map(requestDTO, Aluno.class);
    }

    @Test
    void criar_DeveRetornarProfessor_QuandoTipoForProfessor() {
        requestDTO.setTipo("PROFESSOR");
        when(modelMapper.map(requestDTO, Professor.class)).thenReturn(professor);

        Usuario resultado = usuarioFactory.criar(requestDTO);

        assertNotNull(resultado);
        assertInstanceOf(Professor.class, resultado);
        verify(modelMapper).map(requestDTO, Professor.class);
    }

    @Test
    void criar_DeveRetornarBibliotecario_QuandoTipoForBibliotecario() {
        requestDTO.setTipo("BIBLIOTECARIO");
        when(modelMapper.map(requestDTO, Bibliotecario.class)).thenReturn(bibliotecario);

        Usuario resultado = usuarioFactory.criar(requestDTO);

        assertNotNull(resultado);
        assertInstanceOf(Bibliotecario.class, resultado);
        verify(modelMapper).map(requestDTO, Bibliotecario.class);
    }

    @Test
    void criar_DeveLancarExcecao_QuandoTipoInvalido() {
        requestDTO.setTipo("INVALIDO");

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> usuarioFactory.criar(requestDTO)
        );

        assertTrue(exception.getMessage().contains("INVALIDO"));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void criar_DeveLancarExcecao_QuandoTipoNull() {
        requestDTO.setTipo(null);

        assertThrows(
                Exception.class,
                () -> usuarioFactory.criar(requestDTO)
        );
    }

    @Test
    void criar_DeveAceitarTipoEmMinusculas() {
        requestDTO.setTipo("aluno");
        when(modelMapper.map(requestDTO, Aluno.class)).thenReturn(aluno);

        Usuario resultado = usuarioFactory.criar(requestDTO);

        assertNotNull(resultado);
        assertInstanceOf(Aluno.class, resultado);
    }

    @Test
    void criar_DeveAceitarTipoComLetrasVariadas() {
        requestDTO.setTipo("Professor");
        when(modelMapper.map(requestDTO, Professor.class)).thenReturn(professor);

        Usuario resultado = usuarioFactory.criar(requestDTO);

        assertNotNull(resultado);
        assertInstanceOf(Professor.class, resultado);
    }
}
