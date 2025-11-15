package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.Livro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro,String> {
    boolean existsByAutorAndEditoraAndEdicao(String autor, String editora, String edicao);
    Optional<Livro> findByAutorAndEditoraAndEdicao(String autor, String editora, String edicao);
}
