package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoResponseDTO;
import com.sonic.team.sonicteam.model.Emprestimo;
import com.sonic.team.sonicteam.model.DTO.Emprestimo.EmprestimoRequestDTO;
import com.sonic.team.sonicteam.service.emprestimo.IEmprestimoService;
import com.sonic.team.sonicteam.util.EmprestimoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final IEmprestimoService emprestimoService;
    private final EmprestimoMapper emprestimoMapper;

    public EmprestimoController(IEmprestimoService emprestimoService, 
                               EmprestimoMapper emprestimoMapper) {
        this.emprestimoService = emprestimoService;
        this.emprestimoMapper = emprestimoMapper;
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> criar(@Valid @RequestBody EmprestimoRequestDTO request) {
        Emprestimo criado = emprestimoService.criarEmprestimo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoMapper.paraResponse(criado));
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listar() {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimos();
        return ResponseEntity.ok(emprestimoMapper.paraListaResponse(emprestimos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscar(@PathVariable Long id) {
        Emprestimo encontrado = emprestimoService.buscarEmprestimoPorId(id);
        if (encontrado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(emprestimoMapper.paraResponse(encontrado));
    }

    @PutMapping("/{id}/devolucao")
    public ResponseEntity<EmprestimoResponseDTO> atualizar(@PathVariable Long id) {
        Emprestimo atualizado = emprestimoService.devolverEmprestimo(id);
        return ResponseEntity.ok(emprestimoMapper.paraResponse(atualizado));
    }
}

