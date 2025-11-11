package com.sonic.team.sonicteam.model.DTO.Usuario;

import com.sonic.team.sonicteam.util.CpfUtil;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato válido (11 dígitos ou XXX.XXX.XXX-XX)")
    @CpfUtil.Valid(message = "CPF inválido")
    private String cpf;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotNull(message = "Categoria é obrigatória")
    @Positive(message = "ID da categoria deve ser positivo")
    private Long categoriaId;
    
    @NotNull(message = "Curso é obrigatório")
    @Positive(message = "ID do curso deve ser positivo")
    private Long cursoId;
}
