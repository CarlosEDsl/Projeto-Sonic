package com.sonic.team.sonicteam.model.DTO.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroUsuarioDTO {
    private String status; // ATIVO, INATIVO, SUSPENSO
    private String tipo; // ALUNO, PROFESSOR
    private Long categoriaId;
    private Long cursoId;
    private String nome;
}
