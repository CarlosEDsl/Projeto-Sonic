package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.CategoriaLivro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaLivro, Long> {
}
