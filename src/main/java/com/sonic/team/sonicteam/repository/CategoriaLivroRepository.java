package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaLivroRepository extends JpaRepository<CategoriaLivro, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<CategoriaLivro> findByNomeIgnoreCase(String nome);
}
