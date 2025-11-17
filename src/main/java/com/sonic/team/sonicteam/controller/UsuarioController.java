package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.DTO.Usuario.SuspensaoRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.service.usuario.UsuarioService;
import com.sonic.team.sonicteam.util.ConstantesUsuario;
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
            @PageableDefault(size = ConstantesUsuario.TAMANHO_PAGINA_PADRAO, sort = ConstantesUsuario.ORDENACAO_PADRAO) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorCpf(
            @PathVariable
            @Pattern(regexp = ConstantesUsuario.REGEX_CPF, message = ConstantesUsuario.MENSAGEM_CPF_FORMATO_INVALIDO)
            @CpfUtil.Valid(message = ConstantesUsuario.MENSAGEM_CPF_INVALIDO)
            String cpf) {
        return ResponseEntity.ok(usuarioService.buscarPorCpf(cpf));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable
            @Pattern(regexp = ConstantesUsuario.REGEX_CPF, message = ConstantesUsuario.MENSAGEM_CPF_FORMATO_INVALIDO)
            @CpfUtil.Valid(message = ConstantesUsuario.MENSAGEM_CPF_INVALIDO)
            String cpf,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(cpf, dto));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> removerUsuario(
            @PathVariable
            @Pattern(regexp = ConstantesUsuario.REGEX_CPF, message = ConstantesUsuario.MENSAGEM_CPF_FORMATO_INVALIDO)
            @CpfUtil.Valid(message = ConstantesUsuario.MENSAGEM_CPF_INVALIDO)
            String cpf) {
        usuarioService.deletar(cpf);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{cpf}/suspender")
    public ResponseEntity<UsuarioResponseDTO> suspenderUsuario(
            @PathVariable
            @Pattern(regexp = ConstantesUsuario.REGEX_CPF, message = ConstantesUsuario.MENSAGEM_CPF_FORMATO_INVALIDO)
            @CpfUtil.Valid(message = ConstantesUsuario.MENSAGEM_CPF_INVALIDO)
            String cpf,
            @Valid @RequestBody SuspensaoRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.suspender(cpf, dto.getMotivo()));
    }

    @PatchMapping("/{cpf}/reativar")
    public ResponseEntity<UsuarioResponseDTO> reativarUsuario(
            @PathVariable
            @Pattern(regexp = ConstantesUsuario.REGEX_CPF, message = ConstantesUsuario.MENSAGEM_CPF_FORMATO_INVALIDO)
            @CpfUtil.Valid(message = ConstantesUsuario.MENSAGEM_CPF_INVALIDO)
            String cpf) {
        return ResponseEntity.ok(usuarioService.reativar(cpf));
    }

    @PatchMapping("/{cpf}/inativar")
    public ResponseEntity<UsuarioResponseDTO> inativarUsuario(
            @PathVariable
            @Pattern(regexp = ConstantesUsuario.REGEX_CPF, message = ConstantesUsuario.MENSAGEM_CPF_FORMATO_INVALIDO)
            @CpfUtil.Valid(message = ConstantesUsuario.MENSAGEM_CPF_INVALIDO)
            String cpf) {
        return ResponseEntity.ok(usuarioService.inativar(cpf));
    }
}
