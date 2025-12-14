package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.exception.ConflitoNegocioException;
import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.exception.RecursoNaoEncontradoException;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.FiltroUsuarioDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.repository.UsuarioRepository;
import com.sonic.team.sonicteam.service.catalogo.ICategoriaUsuarioService;
import com.sonic.team.sonicteam.service.curso.ICursoService;
import com.sonic.team.sonicteam.util.ConstantesUsuario;
import com.sonic.team.sonicteam.util.CpfUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ICategoriaUsuarioService categoriaUsuarioService;

    @Mock
    private ICursoService cursoService;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private CpfUtil cpfUtil;

    @Mock
    private UsuarioFactory usuarioFactory;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO requestDTO;
    private UsuarioResponseDTO responseDTO;
    private Aluno usuario;
    private CategoriaUsuario categoria;
    private Curso curso;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("João Silva");
        requestDTO.setCpf("12345678901");
        requestDTO.setEmail("joao@email.com");
        requestDTO.setCategoriaId(1L);
        requestDTO.setCursoId(1L);
        requestDTO.setTipo("ALUNO");

        responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João Silva");
        responseDTO.setCpf("12345678901");
        responseDTO.setEmail("joao@email.com");
        responseDTO.setStatus("ATIVO");
        responseDTO.setCategoria("Graduação");
        responseDTO.setCurso("Engenharia");
        responseDTO.setTipo("ALUNO");

        categoria = new CategoriaUsuario(1L, "Graduação");
        curso = new Curso(1L, "Engenharia");

        usuario = new Aluno();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setCategoria(categoria);
        usuario.setCurso(curso);
    }

    // ==================== TESTES criarUsuario ====================

    @Test
    void criarUsuario_DeveRetornarResponse_QuandoDadosValidos() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.existsByCpf("12345678901")).thenReturn(false);
        when(categoriaUsuarioService.buscarPorId(1L)).thenReturn(categoria);
        when(cursoService.buscarPorId(1L)).thenReturn(curso);
        when(usuarioFactory.criar(requestDTO)).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.criarUsuario(requestDTO);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("12345678901", resultado.getCpf());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void criarUsuario_DeveLancarExcecao_QuandoCpfDuplicado() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.existsByCpf("12345678901")).thenReturn(true);

        ConflitoNegocioException exception = assertThrows(
                ConflitoNegocioException.class,
                () -> usuarioService.criarUsuario(requestDTO)
        );

        assertEquals(ConstantesUsuario.CPF_DUPLICADO, exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarUsuario_DeveNormalizarCpf() {
        when(cpfUtil.normalize("123.456.789-01")).thenReturn("12345678901");
        when(usuarioRepository.existsByCpf("12345678901")).thenReturn(false);
        when(categoriaUsuarioService.buscarPorId(1L)).thenReturn(categoria);
        when(cursoService.buscarPorId(1L)).thenReturn(curso);
        when(usuarioFactory.criar(requestDTO)).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        requestDTO.setCpf("123.456.789-01");
        usuarioService.criarUsuario(requestDTO);

        verify(cpfUtil).normalize("123.456.789-01");
    }

    // ==================== TESTES listarTodos (Pageable) ====================

    @Test
    void listarTodosPaginado_DeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(pageable)).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(usuarioRepository).findAll(pageable);
    }

    @Test
    void listarTodosPaginado_DeveRetornarPaginaVazia() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageVazia = new PageImpl<>(Collections.emptyList());
        
        when(usuarioRepository.findAll(pageable)).thenReturn(pageVazia);

        Page<UsuarioResponseDTO> resultado = usuarioService.listarTodos(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== TESTES listarTodos (List) ====================

    @Test
    void listarTodos_DeveRetornarLista() {
        when(usuarioRepository.findAllWithRelationships()).thenReturn(List.of(usuario));
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() {
        when(usuarioRepository.findAllWithRelationships()).thenReturn(Collections.emptyList());

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== TESTES buscarPorCpf ====================

    @Test
    void buscarPorCpf_DeveRetornarUsuario_QuandoCpfExistir() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.buscarPorCpf("12345678901");

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
    }

    @Test
    void buscarPorCpf_DeveLancarExcecao_QuandoCpfNaoExistir() {
        when(cpfUtil.normalize("99999999999")).thenReturn("99999999999");
        when(usuarioRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> usuarioService.buscarPorCpf("99999999999")
        );

        assertEquals(ConstantesUsuario.USUARIO_NAO_ENCONTRADO, exception.getMessage());
    }

    // ==================== TESTES atualizar ====================

    @Test
    void atualizar_DeveRetornarResponse_QuandoDadosValidos() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(categoriaUsuarioService.buscarPorId(1L)).thenReturn(categoria);
        when(cursoService.buscarPorId(1L)).thenReturn(curso);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.atualizar("12345678901", requestDTO);

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void atualizar_DeveLancarExcecao_QuandoCpfMudou() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(cpfUtil.normalize("99999999999")).thenReturn("99999999999");
        requestDTO.setCpf("99999999999");

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> usuarioService.atualizar("12345678901", requestDTO)
        );

        assertEquals(ConstantesUsuario.CPF_NAO_PODE_SER_ALTERADO, exception.getMessage());
    }

    @Test
    void atualizar_DeveLancarExcecao_QuandoTipoMudou() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        requestDTO.setTipo("PROFESSOR");

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> usuarioService.atualizar("12345678901", requestDTO)
        );

        assertEquals(ConstantesUsuario.TIPO_USUARIO_NAO_PODE_SER_ALTERADO, exception.getMessage());
    }

    @Test
    void atualizar_DeveLancarExcecao_QuandoUsuarioNaoExistir() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> usuarioService.atualizar("12345678901", requestDTO)
        );

        assertEquals(ConstantesUsuario.USUARIO_NAO_ENCONTRADO, exception.getMessage());
    }

    // ==================== TESTES deletar ====================

    @Test
    void deletar_DeveDeletarUsuario_QuandoSemEmprestimosPendentes() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(0L);

        usuarioService.deletar("12345678901");

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deletar_DeveLancarExcecao_QuandoComEmprestimosPendentes() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(emprestimoRepository.countByUsuarioIdAndDataEntregaIsNull(1L)).thenReturn(2L);

        ConflitoNegocioException exception = assertThrows(
                ConflitoNegocioException.class,
                () -> usuarioService.deletar("12345678901")
        );

        assertEquals(ConstantesUsuario.USUARIO_COM_EMPRESTIMOS_PENDENTES, exception.getMessage());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    // ==================== TESTES pegarUsuarioPorCPF ====================

    @Test
    void pegarUsuarioPorCPF_DeveRetornarUsuario() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.pegarUsuarioPorCPF("12345678901");

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
    }

    // ==================== TESTES suspender ====================

    @Test
    void suspender_DeveRetornarUsuarioSuspenso() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.suspender("12345678901", "Motivo teste");

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void suspender_DeveLancarExcecao_QuandoJaSuspenso() {
        usuario.setStatus(StatusUsuario.SUSPENSO);
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> usuarioService.suspender("12345678901", "Motivo")
        );

        assertEquals(ConstantesUsuario.USUARIO_JA_SUSPENSO, exception.getMessage());
    }

    // ==================== TESTES reativar ====================

    @Test
    void reativar_DeveRetornarUsuarioAtivo() {
        usuario.setStatus(StatusUsuario.SUSPENSO);
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.reativar("12345678901");

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void reativar_DeveLancarExcecao_QuandoJaAtivo() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> usuarioService.reativar("12345678901")
        );

        assertEquals(ConstantesUsuario.USUARIO_JA_ATIVO, exception.getMessage());
    }

    // ==================== TESTES inativar ====================

    @Test
    void inativar_DeveRetornarUsuarioInativo() {
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.inativar("12345678901");

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void inativar_DeveLancarExcecao_QuandoJaInativo() {
        usuario.setStatus(StatusUsuario.INATIVO);
        when(cpfUtil.normalize("12345678901")).thenReturn("12345678901");
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));

        DadoInvalidoException exception = assertThrows(
                DadoInvalidoException.class,
                () -> usuarioService.inativar("12345678901")
        );

        assertEquals(ConstantesUsuario.USUARIO_JA_INATIVO, exception.getMessage());
    }

    // ==================== TESTES buscarComFiltros ====================

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltroVazio() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(usuarioRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltrarPorNome() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setNome("João");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("João Silva", resultado.getContent().get(0).getNome());
    }

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltrarPorStatus() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setStatus("ATIVO");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("ATIVO", resultado.getContent().get(0).getStatus());
    }

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltrarPorTipo() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setTipo("ALUNO");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("ALUNO", resultado.getContent().get(0).getTipo());
    }

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltrarPorCategoriaId() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setCategoriaId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Graduação", resultado.getContent().get(0).getCategoria());
    }

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltrarPorCursoId() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setCursoId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Engenharia", resultado.getContent().get(0).getCurso());
    }

    @Test
    void buscarComFiltros_DeveRetornarUsuarios_QuandoFiltrarComTodosCampos() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setNome("João");
        filtro.setStatus("ATIVO");
        filtro.setTipo("ALUNO");
        filtro.setCategoriaId(1L);
        filtro.setCursoId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        UsuarioResponseDTO dto = resultado.getContent().get(0);
        assertEquals("João Silva", dto.getNome());
        assertEquals("ATIVO", dto.getStatus());
        assertEquals("ALUNO", dto.getTipo());
    }

    @Test
    void buscarComFiltros_DeveRetornarPaginaVazia_QuandoNaoEncontrar() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setNome("Usuário que não existe");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> paginaVazia = new PageImpl<>(Collections.emptyList());
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paginaVazia);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());
    }

    @Test
    void buscarComFiltros_DeveIgnorar_QuandoStatusInvalido() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setStatus("STATUS_INVALIDO");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void buscarComFiltros_DeveIgnorar_QuandoTipoInvalido() {
        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO();
        filtro.setTipo("TIPO_INVALIDO");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageUsuarios);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscarComFiltros(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }
}
