package com.sonic.team.sonicteam.integration;

import com.sonic.team.sonicteam.util.CpfUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes Unitários para validação de CPF conforme Anexo I
 * 
 * Requisitos testados:
 * - 5.1: CPF válido possui 11 dígitos
 * - 5.2.1: Verificar se possui exatamente 11 dígitos
 * - 5.2.1: Verificar se não é uma sequência repetida
 * - 5.2.2: Cálculo do primeiro dígito verificador
 * - 5.2.3: Cálculo do segundo dígito verificador
 * - 5.3: Exemplo prático 123.456.789-09
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CpfValidacaoTest {

    @Autowired
    private CpfUtil cpfUtil;

    // ============================================================================
    // SEÇÃO 5.1 - ESTRUTURA DO CPF
    // ============================================================================

    @Test
    @Order(1)
    void cpfValido_deveTer11Digitos() {
        // Requisito: Um CPF válido possui 11 dígitos no formato XXXXXXXXXXX
        assertTrue(cpfUtil.isValid("12345678909")); // 11 dígitos
        assertFalse(cpfUtil.isValid("123456789")); // 9 dígitos - inválido
        assertFalse(cpfUtil.isValid("1234567890")); // 10 dígitos - inválido
        assertFalse(cpfUtil.isValid("123456789012")); // 12 dígitos - inválido
    }

    @Test
    @Order(2)
    void cpfNulo_deveSerInvalido() {
        assertFalse(cpfUtil.isValid(null));
    }

    // ============================================================================
    // SEÇÃO 5.2.1 - PRÉ-REQUISITOS
    // ============================================================================

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555",
            "66666666666",
            "77777777777",
            "88888888888",
            "99999999999"
    })
    @Order(3)
    void sequenciasRepetidas_devemSerInvalidas(String cpf) {
        // Requisito: Verifique se não é uma sequência repetida (e.g., 00000000000)
        assertFalse(cpfUtil.isValid(cpf), 
                "CPF com sequência repetida deve ser inválido: " + cpf);
    }

    // ============================================================================
    // SEÇÃO 5.2.2 e 5.2.3 - CÁLCULO DOS DÍGITOS VERIFICADORES
    // ============================================================================

    @Test
    @Order(4)
    void cpfComDigitosVerificadoresIncorretos_deveSerInvalido() {
        // Alterar os dígitos verificadores deve invalidar o CPF
        assertFalse(cpfUtil.isValid("12345678900")); // Último dígito errado
        assertFalse(cpfUtil.isValid("12345678919")); // Penúltimo dígito errado
        assertFalse(cpfUtil.isValid("12345678999")); // Ambos errados
    }

    // ============================================================================
    // SEÇÃO 5.3 - EXEMPLO PRÁTICO
    // ============================================================================

    @Test
    @Order(5)
    void exemploAnexoI_cpf12345678909_deveSerValido() {
        // Requisito 5.3: Vamos validar o CPF 123.456.789-09 (apenas números: 12345678909)
        
        // Verificação conforme Anexo I:
        // 5.3.1 Primeiro Dígito Verificador:
        // - Cálculo: (1×10) + (2×9) + (3×8) + (4×7) + (5×6) + (6×5) + (7×4) + (8×3) + (9×2) = 210
        // - 210 × 10 = 2100 -> 2100 mod 11 = 10 -> Dígito = 0
        // - 10º dígito do CPF: 0 -> válido
        
        // 5.3.2 Segundo Dígito Verificador:
        // - Cálculo com 10 dígitos (incluindo o primeiro verificador)
        // - 11º dígito do CPF: 9 -> válido
        
        assertTrue(cpfUtil.isValid("12345678909"), 
                "CPF 12345678909 deve ser válido conforme exemplo do Anexo I");
    }

    @Test
    @Order(6)
    void cpfComFormatacao_deveSerValidadoCorretamente() {
        // O CPF pode vir com formatação e deve ser normalizado
        assertTrue(cpfUtil.isValid("123.456.789-09"));
        assertTrue(cpfUtil.isValid("12345678909"));
    }

    @Test
    @Order(7)
    void normalizeCpf_deveRemoverCaracteresNaoNumericos() {
        assertEquals("12345678909", cpfUtil.normalize("123.456.789-09"));
        assertEquals("12345678909", cpfUtil.normalize("12345678909"));
        assertEquals("12345678909", cpfUtil.normalize("123 456 789 09"));
    }

    @Test
    @Order(8)
    void formatCpf_deveFormatarCorretamente() {
        assertEquals("123.456.789-09", cpfUtil.format("12345678909"));
    }

    @Test
    @Order(9)
    void formatCpf_comCpfInvalido_deveRetornarNull() {
        assertNull(cpfUtil.format(null));
        assertNull(cpfUtil.format("123456789")); // Menos de 11 dígitos
    }

    // ============================================================================
    // TESTES ADICIONAIS COM CPFs VÁLIDOS CONHECIDOS
    // ============================================================================

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678909", // Exemplo do Anexo I
            "52998224725", // CPF válido alternativo
            "98765432100"  // CPF válido alternativo
    })
    @Order(10)
    void cpfsValidosConhecidos_devemSerValidados(String cpf) {
        assertTrue(cpfUtil.isValid(cpf), 
                "CPF válido deve passar na validação: " + cpf);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901", // Dígitos verificadores incorretos
            "52998224726", // Último dígito errado
            "98765432101", // Último dígito errado
            "11111111112", // Quase sequência repetida mas com digito final diferente
            "12345678910"  // Dígitos verificadores incorretos
    })
    @Order(11)
    void cpfsInvalidosConhecidos_devemSerRejeitados(String cpf) {
        assertFalse(cpfUtil.isValid(cpf), 
                "CPF inválido deve ser rejeitado: " + cpf);
    }

    // ============================================================================
    // TESTES DE ALGORITMO (PASSO A PASSO CONFORME ANEXO I)
    // ============================================================================

    @Test
    @Order(12)
    void algoritmoCpf_verificarPrimeiroDigito() {
        // Conforme Seção 5.2.2 do Anexo I
        // Para CPF 123456789XX:
        // (1×10) + (2×9) + (3×8) + (4×7) + (5×6) + (6×5) + (7×4) + (8×3) + (9×2)
        // = 10 + 18 + 24 + 28 + 30 + 30 + 28 + 24 + 18 = 210
        // 210 mod 11 = 1 (resto)
        // Como resto < 2, então... espera, vamos recalcular
        // 210 mod 11 = 210 - (11*19) = 210 - 209 = 1
        // Se resto < 2, dígito = 0
        // Se resto >= 2, dígito = 11 - resto = 11 - 1 = 10... isso daria problema
        // Vamos verificar novamente: 210 / 11 = 19.09, então 11 * 19 = 209, 210 - 209 = 1
        // Resto = 1 < 2, então primeiro dígito verificador = 0
        
        String baseNove = "123456789";
        // O primeiro dígito verificador deveria ser 0 (conforme exemplo)
        assertTrue(cpfUtil.isValid("12345678909"), 
                "Com primeiro dígito 0 e segundo 9, CPF deve ser válido");
    }

    @Test
    @Order(13)
    void algoritmoCpf_verificarSegundoDigito() {
        // Conforme Seção 5.2.3 do Anexo I
        // Para CPF 1234567890X (já com primeiro dígito verificador):
        // (1×11) + (2×10) + (3×9) + (4×8) + (5×7) + (6×6) + (7×5) + (8×4) + (9×3) + (0×2)
        // = 11 + 20 + 27 + 32 + 35 + 36 + 35 + 32 + 27 + 0 = 255
        // 255 mod 11 = 255 - (11*23) = 255 - 253 = 2
        // Se resto >= 2, dígito = 11 - resto = 11 - 2 = 9
        // Segundo dígito verificador = 9
        
        assertTrue(cpfUtil.isValid("12345678909"), 
                "Com segundo dígito 9, CPF deve ser válido");
    }

    @Test
    @Order(14)
    void cpfVazio_deveSerInvalido() {
        assertFalse(cpfUtil.isValid(""));
        assertFalse(cpfUtil.isValid("   "));
    }

    @Test
    @Order(15)
    void cpfComLetras_deveSerInvalido() {
        // Após normalização, deve ter menos de 11 dígitos ou dígitos verificadores inválidos
        assertFalse(cpfUtil.isValid("1234567890A"));
        assertFalse(cpfUtil.isValid("ABCDEFGHIJK"));
    }
}
