package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.DTO.Estoque.AtualizarEstoqueResquestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Estoque.EstoqueResponseDTO;
import com.sonic.team.sonicteam.service.estoque.IEstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {
    private final IEstoqueService estoqueService;

    public EstoqueController(IEstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("")
    public ResponseEntity<EstoqueResponseDTO> cadastrarExemplar(@RequestBody EstoqueRequestDTO request) {
        var novoExemplar = estoqueService.cadastrarNovoExemplar(request);
        return ResponseEntity.ok(novoExemplar);
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<EstoqueResponseDTO>> getExemplaresDisponiveis(@RequestParam(required = false) String livroIsbn) {
        var exemplaresDisponiveis = estoqueService.getExemplaresDisponiveis(livroIsbn);
        return ResponseEntity.ok(exemplaresDisponiveis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> getExemplar(@PathVariable Long id) {
        var exemplar = estoqueService.getExemplar(id);
        return ResponseEntity.ok(exemplar);
    }

    @PutMapping("")
    public ResponseEntity<EstoqueResponseDTO> atualizarDisponobilidadeExemplar(@RequestBody AtualizarEstoqueResquestDTO request) {
        var exemplarAtualizado = estoqueService.atualizarDisponibilidadeExemplar(request);
        return ResponseEntity.ok(exemplarAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarExemplar(@PathVariable Long id) {
        estoqueService.deletarExemplar(id);
        return ResponseEntity.noContent().build();
    }
}
