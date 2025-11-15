package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.DTO.Livro.LivroRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Livro.LivroResponseDTO;
import com.sonic.team.sonicteam.service.livro.ILivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/livros")
public class LivroController {
    private ILivroService livroService;

    public LivroController(ILivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping
    public ResponseEntity<LivroResponseDTO> criar(@Valid @RequestBody LivroRequestDTO request) {
        LivroResponseDTO criado = livroService.criarLivro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> listar() {
        return ResponseEntity.ok(livroService.listarLivros());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<LivroResponseDTO> buscar(@PathVariable String isbn) {
        return ResponseEntity.ok(livroService.buscarLivroPorISBN(isbn));
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<LivroResponseDTO> atualizar(
            @PathVariable String isbn,
            @Valid @RequestBody LivroRequestDTO request
    ) {
        return ResponseEntity.ok(livroService.atualizarLivro(isbn, request));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deletar(@PathVariable String isbn) {
        livroService.excluirLivro(isbn);
        return ResponseEntity.noContent().build();
    }
}


