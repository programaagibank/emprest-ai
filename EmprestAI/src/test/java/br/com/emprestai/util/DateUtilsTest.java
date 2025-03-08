package br.com.emprestai.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void primeiroDiaUtil() {
        LocalDate dataReferencia = LocalDate.of(2025, 3, 7);// Sexta-feira
        LocalDate primeiroDiaUtilSeguinte = LocalDate.of(2025, 3, 10);// Segunda-feira
        assertEquals(primeiroDiaUtilSeguinte, DateUtils.primeiroDiaUtil(dataReferencia));
    }
}