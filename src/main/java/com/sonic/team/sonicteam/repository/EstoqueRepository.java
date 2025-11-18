package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    List<Estoque> findAllByDisponivelIsTrue();
    List<Estoque> findAllByDisponivelIsTrueAndLivroIsbn(String livroIsbn);
    Estoque getFirstByLivroIsbnAndDisponivelIsTrue(String livroIsbn);
}
