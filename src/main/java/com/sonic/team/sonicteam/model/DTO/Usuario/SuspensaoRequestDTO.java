package com.sonic.team.sonicteam.model.DTO.Usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspensaoRequestDTO {
    @NotBlank(message = "Motivo é obrigatório")
    private String motivo;
}
