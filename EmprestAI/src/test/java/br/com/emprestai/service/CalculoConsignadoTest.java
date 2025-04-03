package br.com.emprestai.service;

import br.com.emprestai.service.calculos.CalculoTaxaJuros;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculoConsignadoTest {

    @Test
    void calcularMargemEmprestimoConsig() {
        double rendaLiquida = 1000;
        double parcelasAtivas = 30;
        double valorEsperado = 320;
        double resultado = CalculoTaxaJuros.calcularMargemEmprestimoConsig(rendaLiquida, parcelasAtivas);
        assertEquals(valorEsperado, resultado, 0.0001);
    }

    @Test
    void calcularTaxaJurosMensal() {
        double quantidadeParcelas = 36;
        double valorEsperado = 0.0186;
        double resultado = CalculoTaxaJuros.calcularTaxaJurosMensal(quantidadeParcelas);
        assertEquals(valorEsperado, resultado, 0.0001);
    }
}