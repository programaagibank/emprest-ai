package br.com.emprestai.service;

import br.com.emprestai.service.calculos.CalculoConsignado;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculoConsignadoTest {

    @Test
    void calcularMargemEmprestimoConsig() {
        double rendaLiquida = 1000;
        double parcelasAtivas = 30;
        double valorEsperado = 320;
        double resultado = CalculoConsignado.calcularMargemEmprestimoConsig(rendaLiquida, parcelasAtivas);
        assertEquals(valorEsperado, resultado, 0.0001);
    }

    @Test
    void calcularTaxaJurosMensal() {
        double quantidadeParcelas = 36;
        double valorEsperado = 0.0186;
        double resultado = CalculoConsignado.calcularTaxaJurosMensal(quantidadeParcelas);
        assertEquals(valorEsperado, resultado, 0.0001);
    }

    @Test
    void calcularJurosMoraEMulta() {
        double valorParcela = 600.00;
        double diasAtraso = 10;
        double valorEsperado = 13.98;
        double resultado = CalculoConsignado.calcularJurosMoraEMulta(valorParcela, diasAtraso);
        assertEquals(valorEsperado, resultado, 0.0001);
    }
}