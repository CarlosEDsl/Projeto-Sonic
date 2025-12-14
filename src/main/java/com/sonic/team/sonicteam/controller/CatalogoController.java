package com.sonic.team.sonicteam.controller;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;
import com.sonic.team.sonicteam.service.curso.ICursoService;
import com.sonic.team.sonicteam.service.catalogo.ICategoriaUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {
    
    private final ICategoriaUsuarioService categoriaUsuarioService;
    private final ICursoService cursoService;

    public CatalogoController(ICategoriaUsuarioService categoriaUsuarioService, ICursoService cursoService) {
        this.categoriaUsuarioService = categoriaUsuarioService;
        this.cursoService = cursoService;
    }

    @GetMapping("/categorias-livro")
    public ResponseEntity<List<String>> listarCategoriasLivro() {
        List<String> categorias = Arrays.stream(CategoriaLivro.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categorias-usuario")
    public ResponseEntity<List<CategoriaUsuario>> listarCategoriaUsuario(){
        List< CategoriaUsuario> categoriaUsuario = categoriaUsuarioService.listar();
        return ResponseEntity.ok(categoriaUsuario);
    }

    @GetMapping("/cursos")
    public ResponseEntity<List<Curso>> listarCursos(){
        List<Curso> cursos = cursoService.listar();
        return ResponseEntity.ok(cursos);
    }
}
