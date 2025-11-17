package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    @GetMapping("/categorias-livro")
    public ResponseEntity<List<String>> listarCategoriasLivro() {
        List<String> categorias = Arrays.stream(CategoriaLivro.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(categorias);
    }
}
