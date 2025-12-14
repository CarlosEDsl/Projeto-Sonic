package com.sonic.team.sonicteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.team.sonicteam.model.DTO.Usuario.SuspensaoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.service.usuario.IUsuarioService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private IUsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioRequestDTO requestDTO;
    private UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("João Silva");
        requestDTO.setCpf("52998224725");
        requestDTO.setEmail("joao@email.com");
        requestDTO.setCategoriaId(1L);
        requestDTO.setCursoId(1L);
        requestDTO.setTipo("ALUNO");

        responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João Silva");
        responseDTO.setCpf("52998224725");
        responseDTO.setEmail("joao@email.com");
        responseDTO.setStatus("ATIVO");
        responseDTO.setCategoria("Graduação");
        responseDTO.setCurso("Engenharia");
        responseDTO.setTipo("ALUNO");
    }

    // ==================== TESTES criarUsuario ====================

    @Test
    void criarUsuario_DeveRetornarUsuarioCriado() {
        when(usuarioService.criarUsuario(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<UsuarioResponseDTO> response = usuarioController.criarUsuario(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getNome());
        assertEquals("52998224725", response.getBody().getCpf());
    }

    // ==================== TESTES listarUsuarios ====================

    @Test
    void listarUsuarios_DeveRetornarPaginaDeUsuarios() {
        Page<UsuarioResponseDTO> page = new PageImpl<>(List.of(responseDTO));
        when(usuarioService.listarTodos(any())).thenReturn(page);

        ResponseEntity<Page<UsuarioResponseDTO>> response = usuarioController.listarUsuarios(PageRequest.of(0, 20));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    // ==================== TESTES buscarUsuarioPorCpf ====================

    @Test
    void buscarUsuarioPorCpf_DeveRetornarUsuario() {
        when(usuarioService.buscarPorCpf("52998224725")).thenReturn(responseDTO);

        ResponseEntity<UsuarioResponseDTO> response = usuarioController.buscarUsuarioPorCpf("52998224725");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getNome());
    }

    // ==================== TESTES atualizarUsuario ====================

    @Test
    void atualizarUsuario_DeveRetornarUsuarioAtualizado() {
        when(usuarioService.atualizar(eq("52998224725"), any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<UsuarioResponseDTO> response = usuarioController.atualizarUsuario("52998224725", requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getNome());
    }

    // ==================== TESTES removerUsuario ====================

    @Test
    void removerUsuario_DeveRetornarNoContent() {
        doNothing().when(usuarioService).deletar("52998224725");

        ResponseEntity<Void> response = usuarioController.removerUsuario("52998224725");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService).deletar("52998224725");
    }

    // ==================== TESTES suspenderUsuario ====================

    @Test
    void suspenderUsuario_DeveRetornarUsuarioSuspenso() {
        SuspensaoRequestDTO suspensaoDTO = new SuspensaoRequestDTO("Motivo teste");
        responseDTO.setStatus("SUSPENSO");
        when(usuarioService.suspender(eq("52998224725"), eq("Motivo teste"))).thenReturn(responseDTO);

        ResponseEntity<UsuarioResponseDTO> response = usuarioController.suspenderUsuario("52998224725", suspensaoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SUSPENSO", response.getBody().getStatus());
    }

    // ==================== TESTES reativarUsuario ====================

    @Test
    void reativarUsuario_DeveRetornarUsuarioAtivo() {
        when(usuarioService.reativar("52998224725")).thenReturn(responseDTO);

        ResponseEntity<UsuarioResponseDTO> response = usuarioController.reativarUsuario("52998224725");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ATIVO", response.getBody().getStatus());
    }

    // ==================== TESTES inativarUsuario ====================

    @Test
    void inativarUsuario_DeveRetornarUsuarioInativo() {
        responseDTO.setStatus("INATIVO");
        when(usuarioService.inativar("52998224725")).thenReturn(responseDTO);

        ResponseEntity<UsuarioResponseDTO> response = usuarioController.inativarUsuario("52998224725");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INATIVO", response.getBody().getStatus());
    }
}
