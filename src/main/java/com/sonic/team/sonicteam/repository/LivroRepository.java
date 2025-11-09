package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro,String> {
}
