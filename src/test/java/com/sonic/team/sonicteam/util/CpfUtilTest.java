package com.sonic.team.sonicteam.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfUtilTest {

    private CpfUtil cpfUtil;

    @BeforeEach
    void setUp() {
        cpfUtil = new CpfUtil();
    }

    // ==================== TESTES normalize ====================

    @Test
    void normalize_DeveRemoverPontosETracos() {
        String cpf = "123.456.789-01";

        String resultado = cpfUtil.normalize(cpf);

        assertEquals("12345678901", resultado);
    }

    @Test
    void normalize_DeveManterApenasNumeros() {
        String cpf = "12345678901";

        String resultado = cpfUtil.normalize(cpf);

        assertEquals("12345678901", resultado);
    }

    @Test
    void normalize_DeveRetornarNull_QuandoCpfNull() {
        String resultado = cpfUtil.normalize(null);

        assertNull(resultado);
    }

    @Test
    void normalize_DeveRemoverEspacos() {
        String cpf = "123 456 789 01";

        String resultado = cpfUtil.normalize(cpf);

        assertEquals("12345678901", resultado);
    }

    // ==================== TESTES format ====================

    @Test
    void format_DeveFormatarCpf() {
        String cpf = "12345678901";

        String resultado = cpfUtil.format(cpf);

        assertEquals("123.456.789-01", resultado);
    }

    @Test
    void format_DeveRetornarNull_QuandoCpfNull() {
        String resultado = cpfUtil.format(null);

        assertNull(resultado);
    }

    @Test
    void format_DeveRetornarNull_QuandoCpfMenorQue11Digitos() {
        String resultado = cpfUtil.format("1234567890");

        assertNull(resultado);
    }

    @Test
    void format_DeveRetornarNull_QuandoCpfMaiorQue11Digitos() {
        String resultado = cpfUtil.format("123456789012");

        assertNull(resultado);
    }

    // ==================== TESTES isValid ====================

    @Test
    void isValid_DeveRetornarTrue_QuandoCpfValido() {
        String cpfValido = "52998224725";

        boolean resultado = cpfUtil.isValid(cpfValido);

        assertTrue(resultado);
    }

    @Test
    void isValid_DeveRetornarTrue_QuandoCpfValidoFormatado() {
        String cpfValido = "529.982.247-25";

        boolean resultado = cpfUtil.isValid(cpfValido);

        assertTrue(resultado);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfNull() {
        boolean resultado = cpfUtil.isValid(null);

        assertFalse(resultado);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfMenorQue11Digitos() {
        boolean resultado = cpfUtil.isValid("1234567890");

        assertFalse(resultado);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfMaiorQue11Digitos() {
        boolean resultado = cpfUtil.isValid("123456789012");

        assertFalse(resultado);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoSequenciaRepetida() {
        assertFalse(cpfUtil.isValid("11111111111"));
        assertFalse(cpfUtil.isValid("22222222222"));
        assertFalse(cpfUtil.isValid("33333333333"));
        assertFalse(cpfUtil.isValid("00000000000"));
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoDigitosVerificadoresIncorretos() {
        String cpfInvalido = "12345678901";

        boolean resultado = cpfUtil.isValid(cpfInvalido);

        assertFalse(resultado);
    }

    @Test
    void isValid_DeveRetornarTrue_ParaOutroCpfValido() {
        String cpfValido = "11144477735";

        boolean resultado = cpfUtil.isValid(cpfValido);

        assertTrue(resultado);
    }
}
