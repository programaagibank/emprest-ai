package br.com.emprestai.service;

import br.com.emprestai.enums.VinculoEnum;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ElegibilidadeTest {

    // 11.1.1 Margem Consignável
    @Test
    void verificarMargemEmprestimoConsigTrue() {
        double valorParcela = 875.00;
        double rendaLiquida = 2500.0;
        double parcelasAtivas = 0.0;
        assertTrue(Elegibilidade.verificarMargemEmprestimoConsig(valorParcela, rendaLiquida, parcelasAtivas));
    }

    @Test
    void verificarMargemEmprestimoConsigFalse() {
        double valorParcela = 600.0;
        double rendaLiquida = 1500.0;
        double parcelasAtivas = 0.0;
        assertFalse(Elegibilidade.verificarMargemEmprestimoConsig(valorParcela, rendaLiquida, parcelasAtivas));
    }

    // 11.1.2 Idade Máxima
    @Test
    void verificarIdadeClienteConsigTrue() {
        int idade = 18;
        int qtdeParcelas = 24;
        assertTrue(Elegibilidade.verificarIdadeClienteConsig(idade, qtdeParcelas));
    }

    @Test
    void verificarIdadeClienteConsigFalse() {
        int idade = 79;
        int qtdeParcelas = 24;
        assertFalse(Elegibilidade.verificarIdadeClienteConsig(idade, qtdeParcelas));
    }

    // 11.1.3 Quantidade de Parcelas
    @Test
    void verificarQtdeParcelasConsigTrue() {
        int parcelas = 30;
        assertTrue(Elegibilidade.verificarQtdeParcelasConsig(parcelas));
    }

    @Test
    void verificarQtdeParcelasConsigFalse() {
        int parcelas = 93;
        assertFalse(Elegibilidade.verificarQtdeParcelasConsig(parcelas));
    }

    // 11.1.4 Taxa de Juros
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

    // 11.1.5 Tipo de Vínculo
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

    // 11.1.6 Carência
    @Test
    void verificarCarenciaEmprestimoConsigTrue() {
        int dias = 60;
        assertTrue(Elegibilidade.verificarCarenciaEmprestimoConsig(dias));
    }

    @Test
    void verificarCarenciaEmprestimoConsigFalse() {
        int dias = 61;
        assertFalse(Elegibilidade.verificarCarenciaEmprestimoConsig(dias));
    }

    // Empréstimo Pessoal

    // 11.2.1 Idade Máxima
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

    // 11.2.2 Valor do Empréstimo
    @Test
    void verificarValorEmprestimoPessoalTrue() {
        double valor = 5000.0;
        int score = 600;
        assertTrue(Elegibilidade.verificarValorEmprestimoPessoal(valor, score));
    }

    @Test
    void verificarValorEmprestimoPessoalFalse() {
        double valor = 6000.0;
        int score = 500;
        assertFalse(Elegibilidade.verificarValorEmprestimoPessoal(valor, score));
    }

    // 11.2.3 Quantidade de Parcelas
    @Test
    void verificarParcelasPessoalTrue() {
        int parcelas = 18;
        int score = 600;
        assertTrue(Elegibilidade.verificarParcelasPessoal(parcelas, score));
    }

    @Test
    void verificarParcelasPessoalFalse() {
        int parcelas = 19;
        int score = 500;
        assertFalse(Elegibilidade.verificarParcelasPessoal(parcelas, score));
    }

    // 11.2.4 Taxa de Juros
    @Test
    void verificarTaxaJurosPessoalTrue() {
        double taxa = 0.0949;
        int score = 600;
        assertTrue(Elegibilidade.verificarTaxaJurosPessoal(taxa, score));
    }

    @Test
    void verificarTaxaJurosPessoalFalse() {
        double taxa = 0.08;
        int score = 600;
        assertFalse(Elegibilidade.verificarTaxaJurosPessoal(taxa, score));
    }

    // 11.2.5 Score
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

    // 11.2.6 Capacidade de Pagamento
    @Test
    void verificarComprometimentoPessoalTrue() {
        double parcela = 300.0;
        double rendaLiquida = 1500.0;
        assertTrue(Elegibilidade.verificarComprometimentoPessoal(parcela, rendaLiquida));
    }

    @Test
    void verificarComprometimentoPessoalFalse() {
        double parcela = 500.0;
        double rendaLiquida = 1000.0;
        assertFalse(Elegibilidade.verificarComprometimentoPessoal(parcela, rendaLiquida));
    }

    // 11.2.7 Carência
    @Test
    void verificarCarenciaPessoalTrue() {
        int dias = 30;
        assertTrue(Elegibilidade.verificarCarenciaPessoal(dias));
    }

    @Test
    void verificarCarenciaPessoalFalse() {
        int dias = 31;
        assertFalse(Elegibilidade.verificarCarenciaPessoal(dias));
    }

    // 11.3.1 Percentual Mínimo Pago (Refinanciamento)
    @Test
    void verificarPercentualMinimoPagoTrue() {
        int parcelasPagas = 6;
        int totalParcelas = 24;
        assertTrue(Elegibilidade.verificarPercentualMinimoPago(parcelasPagas, totalParcelas));
    }

    @Test
    void verificarPercentualMinimoPagoFalse() {
        int parcelasPagas = 4;
        int totalParcelas = 24;
        assertFalse(Elegibilidade.verificarPercentualMinimoPago(parcelasPagas, totalParcelas));
    }

    // 11.4.1 Portabilidade
    @Test
    void verificarContratoSemAtrasosTrue() {
        boolean ativo = true;
        boolean temAtrasos = false;
        assertTrue(Elegibilidade.verificarContratoSemAtrasos(ativo, temAtrasos));
    }

    @Test
    void verificarContratoSemAtrasosFalse() {
        boolean ativo = true;
        boolean temAtrasos = true;
        assertFalse(Elegibilidade.verificarContratoSemAtrasos(ativo, temAtrasos));
    }

    // Calculo de mínimo de renda
    @Test
    void verificarRendaMinimaPessoalTrue() {
        double rendaLiquida = 2000.0;
        assertTrue(Elegibilidade.verificarRendaMinimaPessoal(rendaLiquida));
    }

    @Test
    void verificarRendaMinimaPessoalFalse() {
        double rendaLiquida = 999.0;
        assertFalse(Elegibilidade.verificarRendaMinimaPessoal(rendaLiquida));
    }

    // Verificação de Elegibilidade de Empréstimo Consignado
    @Test
    void verificarElegibilidadeConsignadoTrue() {
        double rendaLiquida = 2500.0;
        double valorParcela = 875.0;
        double parcelasAtivas = 0.0;
        int idade = 30;
        int parcelas = 24;
        double taxaJuros = 0.018;
        VinculoEnum vinculo = VinculoEnum.APOSENTADO;
        int carencia = 0;
        assertTrue(Elegibilidade.verificarElegibilidadeConsignado(rendaLiquida, valorParcela, parcelasAtivas, idade, parcelas, taxaJuros, vinculo, carencia));
    }

    @Test
    void verificarElegibilidadeConsignadoFalse() {
        double rendaLiquida = 1500.0;
        double valorParcela = 600.0;
        double parcelasAtivas = 0.0;
        int idade = 79;
        int parcelas = 24;
        double taxaJuros = 0.03;
        VinculoEnum vinculo = null;
        int carencia = 61;
        assertFalse(Elegibilidade.verificarElegibilidadeConsignado(rendaLiquida, valorParcela, parcelasAtivas, idade, parcelas, taxaJuros, vinculo, carencia));
    }

    // Verificação Elegibilidade do Empréstimo Pessoal
    @Test
    void verificarElegibilidadePessoalTrue() {
        double rendaLiquida = 3700.0;
        double parcela = 900.0;
        int idade = 55;
        int parcelas = 12;
        int score = 900;
        assertTrue(Elegibilidade.verificarElegibilidadePessoal(rendaLiquida, parcela, idade, parcelas, score));
    }

    @Test
    void verificarElegibilidadePessoalFalse() {
        double rendaLiquida = 1000.0;
        double parcela = 400.0;
        int idade = 74;
        int parcelas = 24;
        int score = 150;
        assertFalse(Elegibilidade.verificarElegibilidadePessoal(rendaLiquida, parcela, idade, parcelas, score));
    }
}