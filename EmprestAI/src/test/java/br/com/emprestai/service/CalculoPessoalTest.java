package br.com.emprestai.service;

import org.junit.jupiter.api.Test;

class CalculoPessoalTest {

    @Test
    void calculoDeCapacidadeDePagamento() {
        double rendaLiquida = 600;
        assertEquals(CalculoPessoal.calculoDeCapacidadeDePagamento(rendaLiquida));
    }

    private void assertEquals(double v) {
    }


    @Test
    void calculoTaxaDeJurosMensal() {
        double score = 0;
        assertEquals(CalculoPessoal.calculoTaxaDeJurosMensal(score));
    }

    @Test
    void calculoDeJurosMoraEMulta() {
        double valorParcela = 500;
        double diasAtraso = 21;
        assertEquals(CalculoPessoal.calculoDeJurosMoraEMulta(valorParcela,diasAtraso));
    }


}

