package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.service.usuario.UsuarioService;
import com.sonic.team.sonicteam.util.CpfUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO res = usuarioService.criarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorCpf(
            @PathVariable
            @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato válido")
            @CpfUtil.Valid(message = "CPF inválido")
            String cpf) {
        return ResponseEntity.ok(usuarioService.buscarPorCpf(cpf));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable
            @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato válido")
            @CpfUtil.Valid(message = "CPF inválido")
            String cpf,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(cpf, dto));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> removerUsuario(
            @PathVariable
            @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato válido")
            @CpfUtil.Valid(message = "CPF inválido")
            String cpf) {
        usuarioService.deletar(cpf);
        return ResponseEntity.noContent().build();
    }
}
