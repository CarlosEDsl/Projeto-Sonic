package com.sonic.team.sonicteam.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Component
public class CpfUtil {
    
    // ==================== NORMALIZAÇÃO ====================
    

    public String normalize(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }
    
    // ==================== FORMATAÇÃO ====================
    

    public String format(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return null;
        }
        return String.format("%s.%s.%s-%s", 
            cpf.substring(0, 3),
            cpf.substring(3, 6),
            cpf.substring(6, 9),
            cpf.substring(9, 11)
        );
    }
    
    // ==================== VALIDAÇÃO ====================
    

    public boolean isValid(String cpf) {
        if (cpf == null) return false;
        
        String cpfNumerico = normalize(cpf);
        
        if (cpfNumerico.length() != 11) return false;
        if (ehSequenciaRepetida(cpfNumerico)) return false;
        
        int digitoVerificador1 = calcularDigito(cpfNumerico.substring(0, 9), 10);
        int digitoVerificador2 = calcularDigito(cpfNumerico.substring(0, 10), 11);
        
        return cpfNumerico.charAt(9) - '0' == digitoVerificador1 
            && cpfNumerico.charAt(10) - '0' == digitoVerificador2;
    }
    
    private boolean ehSequenciaRepetida(String sequencia) {
        char primeiroCaractere = sequencia.charAt(0);
        for (int i = 1; i < sequencia.length(); i++) {
            if (sequencia.charAt(i) != primeiroCaractere) return false;
        }
        return true;
    }
    
    private int calcularDigito(String baseNumerica, int pesoInicial) {
        int somaPonderada = 0;
        int pesoAtual = pesoInicial;
        for (int i = 0; i < baseNumerica.length(); i++) {
            somaPonderada += (baseNumerica.charAt(i) - '0') * pesoAtual--;
        }
        int restoDivisao = somaPonderada % 11;
        return restoDivisao < 2 ? 0 : 11 - restoDivisao;
    }
    
    // ==================== ANOTAÇÃO CUSTOMIZADA ====================
    

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = CpfValidator.class)
    @Documented
    public @interface Valid {
        String message() default "CPF inválido";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
    

    @Component
    public static class CpfValidator implements ConstraintValidator<Valid, String> {
        
        private final CpfUtil cpfUtil;

        public CpfValidator(CpfUtil cpfUtil) {
            this.cpfUtil = cpfUtil;
        }

        @Override
        public boolean isValid(String cpf, ConstraintValidatorContext context) {
            if (cpf == null || cpf.isBlank()) {
                return true; 
            }
            return cpfUtil.isValid(cpf);
        }
    }
}
