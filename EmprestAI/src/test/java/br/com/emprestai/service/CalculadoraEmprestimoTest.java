package br.com.emprestai.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

/*
Utilizado para validação dos valores esperado no teste o site do Investnews e calculadora financeira online
https://investnews.com.br/ferramentas/calculadoras/calculadora-price/
https://www.calculadoraonline.com.br/financeira
https://www.dinkytown.net/
 */

class CalculadoraEmprestimoTest {

    @Test
    void contratoPrice() {
        double valorEmprestimo = 5000.00;
        int qtdeParcelas = 24;
        double taxaJurosMensal = 0.0949;
        LocalDate dataContratacao = LocalDate.of(2025, 3, 7);// Sexta-feira
        LocalDate dataLiberacaoCred = LocalDate.of(2025, 3, 10);// Segunda-feira
        LocalDate dataFimContrato = LocalDate.of(2027, 3, 10);// Segunda-feira
        LocalDate dtNasc = LocalDate.of(1995, 1, 1);
        BigDecimal seguro = new BigDecimal("40.00");
        BigDecimal iof = new BigDecimal("170.00");
        boolean contratarSeguro = true;
        BigDecimal valorTotalFinanciado = new BigDecimal("5210.00");
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        BigDecimal saldoDevedorPresente = new BigDecimal("5210.00");
        BigDecimal taxaEfetivaMensal = new BigDecimal("0.1003");
        Map<String, Object> resultado = CalculadoraEmprestimo.contratoPrice(
                valorEmprestimo, qtdeParcelas, taxaJurosMensal, dataContratacao, dtNasc, contratarSeguro);

        assertEquals(valorEmprestimo,resultado.get("valorEmprestimo"));
        assertEquals(qtdeParcelas,resultado.get("qtdeParcelas"));
        assertEquals(dataContratacao,resultado.get("dataContratacao"));
        assertEquals(dataLiberacaoCred,resultado.get("dataLiberacaoCred"));
        assertEquals(dataFimContrato,resultado.get("dataFimContrato"));
        assertEquals(seguro,resultado.get("custoSeguro"));
        assertEquals(iof,resultado.get("valorTributos"));
        assertEquals(valorTotalFinanciado,resultado.get("valorTotalFinanciado"));
        assertEquals(parcelaMensal,resultado.get("parcelaMensal"));
        assertEquals(saldoDevedorPresente,resultado.get("saldoDevedorPresente"));
        assertEquals(taxaEfetivaMensal,resultado.get("taxaEfetivaMensal"));
    }

    @Test
    void calcParcela() {
        BigDecimal valorTotalFinanciado = new BigDecimal("5209.32");
        double taxaJurosMensal = 0.0949;
        int qtdeParcelas = 24;
        assertEquals(new BigDecimal("557.66"), CalculadoraEmprestimo.calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas).setScale(2, HALF_UP));
    }

    @Test
    void calcSeguro() {
        double valorEmprestimo = 5000.00;
        LocalDate dtNasc = LocalDate.of(1995, 1, 1);
        LocalDate dataContratacao = LocalDate.of(2025, 3, 7);
        int qtdeParcelas = 24;
        // assertEquals(); para testar calculo
        assertEquals(new BigDecimal("40.00"), CalculadoraEmprestimo.calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas).setScale(2, HALF_UP));
    }

    @Test
    void calcIOF() {
        double valorEmprestimo = 5000.00;
        BigDecimal seguro = new BigDecimal("40.00");
        int qtdeParcelas = 24;
        LocalDate dataLiberacaoCred = LocalDate.of(2025, 3, 10);// Segunda-feira
        LocalDate dataFimContrato = LocalDate.of(2026, 3, 10);// Segunda-feira
        assertEquals(new BigDecimal("170.00"), CalculadoraEmprestimo.calcIOF(valorEmprestimo, seguro, dataLiberacaoCred, dataFimContrato).setScale(2, HALF_UP));
    }

    @Test
    void calcTxEfetivaMes() {
        double valorEmprestimo = 5000;
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        double taxaJurosMensal = 0.0949;
        int qtdeParcelas = 24;
        assertEquals(new BigDecimal("0.1003"),CalculadoraEmprestimo.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas));
    }

    @Test
    void calcSaldoDevSemJuros() {
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        double taxaJurosMensal = 0.0949;
        int qtdeParcela = 24;
        assertEquals(new BigDecimal("5209.96"), CalculadoraEmprestimo.calcSaldoDevedorPresente(parcelaMensal, taxaJurosMensal, qtdeParcela).setScale(2, HALF_UP));
    }
}