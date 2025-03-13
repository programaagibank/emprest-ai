package br.com.emprestai.service;

import br.com.emprestai.enums.VinculoEnum;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElegibilidadeTest {

    @Test
    void verificarMargemEmprestimoConsigTrue() {
        double valorParcela = 875.00;
        double renda_liquida = 2500;
        double parcelasAtivas = 0;
        assertTrue(Elegibilidade.verificarMargemEmprestimoConsig(valorParcela, renda_liquida, parcelasAtivas));
    }
    @Test
    void verificarMargemEmprestimoConsigFalse() {
        double valorparcela = 600;
        double renda_liquida = 1500;
        double parcelasAtivas = 0;
        assertFalse(Elegibilidade.verificarMargemEmprestimoConsig(valorparcela, renda_liquida, parcelasAtivas));
    }

    @Test
    void verificarIdadeClienteConsigTrue() {
        int idade = 18;
        int qtdeParcelas = 24;
        assertTrue(Elegibilidade.verificarIdadeClienteConsig(idade, qtdeParcelas));
    }
    @Test
    void verificarIdadeClienteConsigFalse() {
        int idade = 79;
        int qtdeparcelas = 24;
        assertFalse(Elegibilidade.verificarIdadeClienteConsig(idade, qtdeparcelas));
    }

    @Test
    void verificarQtdeParcelasConsigTrue() {
        int parcelas = 30;
        assertTrue(Elegibilidade.verificarQtdeParcelasConsig(parcelas));
    }
    @Test
    void verificarQtdeParcelasConsigFalse() {
        int parcelas = 0;
        assertFalse(Elegibilidade.verificarQtdeParcelasConsig(parcelas));
    }

    @Test
    void verificarTaxaJurosEmprestimoConsigTrue() {
        double juros = 0.0180;
        assertTrue(Elegibilidade.verificarTaxaJurosEmprestimoConsig(juros));
    }
    @Test
    void verificarTaxaJurosEmprestimoConsigFalse() {
        double juros = 0.03;
        assertFalse(Elegibilidade.verificarTaxaJurosEmprestimoConsig(juros));
    }

    @Test
    void verificarVinculoEmprestimoConsigTrue() {
        VinculoEnum vinculo = VinculoEnum.APOSENTADO;
        assertTrue(Elegibilidade.verificarVinculoEmprestimoConsig(vinculo));
    }
    @Test
    void verificarVinculoEmprestimoConsigFalse() {
        VinculoEnum vinculo = null;
        assertFalse(Elegibilidade.verificarVinculoEmprestimoConsig(vinculo));
    }

    @Test
    void verificacarCarenciaEmprestimoConsigTrue() {
        int dias = 60;
        assertTrue(Elegibilidade.verificacarCarenciaEmprestimoConsig(dias));
    }
    @Test
    void verificacarCarenciaEmprestimoConsigFalse() {
        int dias = 61;
        assertFalse(Elegibilidade.verificacarCarenciaEmprestimoConsig(dias));
    }

    @Test
    void verificarRendaMinimaPessoalTrue() {
        double rendaLiquida = 2000;
        assertTrue(Elegibilidade.verificarRendaMinimaPessoal(rendaLiquida));
    }
    @Test
    void verificarRendaMinimaPessoalFalse() {
        double rendaLiquida = 0;
        assertFalse(Elegibilidade.verificarRendaMinimaPessoal(rendaLiquida));
    }

    @Test
    void verificarComprometimentoPessoalTrue() {
        double parcela = 24;
        double rendaLiquida = 1500;
        assertTrue(Elegibilidade.verificarComprometimentoPessoal(parcela, rendaLiquida));
    }
    @Test
    void verificarComprometimentoPessoalFalse() {
        double parcela = 12;
        double rendaLiquida = 0;
        assertFalse(Elegibilidade.verificarComprometimentoPessoal(parcela, rendaLiquida));
    }

    @Test
    void verificarIdadePessoalTrue() {
        int idade = 35;
        int parcelas = 24;
        assertTrue(Elegibilidade.verificarIdadePessoal(idade, parcelas));
    }
    @Test
    void verificarIdadePessoalFalse() {
        int idade = 74;
        int parcelas = 24;
        assertFalse(Elegibilidade.verificarIdadePessoal(idade, parcelas));
    }

    @Test
    void verificarScorePessoalTrue() {
        int score = 350;
        assertTrue(Elegibilidade.verificarScorePessoal(score));
    }
    @Test
    void verificarScorePessoalFalse() {
        int score = 199;
        assertFalse(Elegibilidade.verificarScorePessoal(score));
    }

    @Test
    void verificarParcelasPessoalTrue() {
        int parcelas = 18;
        assertTrue(Elegibilidade.verificarParcelasPessoal(parcelas));
    }
    @Test
    void verificarParcelasPessoalFalse() {
        int parcelas = -10;
        assertFalse(Elegibilidade.verificarParcelasPessoal(parcelas));
    }

    @Test
    void verificarElegibilidadePessoalTrue() {
        double rendaLiquida = 3700;
        double parcela = 30;
        int idade = 55;
        int parcelas = 12;
        int score = 900;
        assertTrue(Elegibilidade.verificarElegibilidadePessoal(rendaLiquida, parcela, idade, parcelas, score));
    }
    @Test
    void verificarElegibilidadePessoalFalse() {
        double rendaLiquida = 1000;
        double parcela = 180;
        int idade = 18;
        int parcelas = 3;
        int score = 150;
        assertFalse(Elegibilidade.verificarElegibilidadePessoal(rendaLiquida, parcela, idade, parcelas, score));
    }
}