package br.com.emprestai.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculoPessoalTest {

    @Test
    void calculoDeCapacidadeDePagamento() {
        double rendaLiquida = 1000;
        double valorLimite = 300;
        assertEquals(valorLimite, CalculoPessoal.calculoDeCapacidadeDePagamento(rendaLiquida));
    }

    @Test
    void calculoTaxaDeJurosMensal() {
        double score = 500;
        double taxaJurosArred = Math.round(CalculoPessoal.calculoTaxaDeJurosMensal(score) * 100.0) / 100.0;
        assertEquals(9.74, taxaJurosArred);
    }

    @Test
    void calculoDeJurosMoraEMulta() {
        double valorParcela = 305.96;
        double diasAtraso = 10;
        assertEquals(7.13, CalculoPessoal.calculoDeJurosMoraEMulta(valorParcela, diasAtraso), 0.01);
    }


    }
