package com.sonic.team.sonicteam.repository;

import com.sonic.team.sonicteam.model.Livro;
import com.sonic.team.sonicteam.model.catalogos.CategoriaLivro;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro,String> {
    boolean existsByAutorAndEditoraAndEdicao(String autor, String editora, String edicao);
    Optional<Livro> findByAutorAndEditoraAndEdicao(String autor, String editora, String edicao);

    @Query("""
       SELECT l FROM Livro l
       WHERE (:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
       AND (:autor IS NULL OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%')))
       AND (:editora IS NULL OR LOWER(l.editora) LIKE LOWER(CONCAT('%', :editora, '%')))
       AND (:edicao IS NULL OR l.edicao = :edicao)
       AND (:categoriaLivro IS NULL OR l.categoriaLivro = :categoriaLivro)
       """)
    List<Livro> buscarComFiltros(
            String titulo,
            String autor,
            String editora,
            Integer edicao,
            CategoriaLivro categoriaLivro
    );

}
