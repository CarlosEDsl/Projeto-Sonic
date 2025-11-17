package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.model.DTO.Usuario.FiltroUsuarioDTO;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.Bibliotecario;
import com.sonic.team.sonicteam.model.usuario.Professor;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.TipoUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UsuarioQueryBuilder {

    public static Specification<Usuario> comFiltros(FiltroUsuarioDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getStatus() != null && !filtro.getStatus().isBlank()) {
                try {
                    StatusUsuario status = StatusUsuario.valueOf(filtro.getStatus().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                }
            }

            if (filtro.getTipo() != null && !filtro.getTipo().isBlank()) {
                try {
                    TipoUsuario tipo = TipoUsuario.fromString(filtro.getTipo());
                    Class<? extends Usuario> tipoClasse = switch (tipo) {
                        case ALUNO -> Aluno.class;
                        case PROFESSOR -> Professor.class;
                        case BIBLIOTECARIO -> Bibliotecario.class;
                    };
                    predicates.add(criteriaBuilder.equal(root.type(), tipoClasse));
                } catch (IllegalArgumentException e) {
                }
            }

            if (filtro.getCategoriaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("id"), filtro.getCategoriaId()));
            }

            if (filtro.getCursoId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("curso").get("id"), filtro.getCursoId()));
            }

            if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")),
                    "%" + filtro.getNome().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
