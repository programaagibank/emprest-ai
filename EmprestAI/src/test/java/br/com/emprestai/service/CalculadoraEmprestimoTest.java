package br.com.emprestai.service;


import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
    void contratoPrice() { // Ok

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorEmprestimo(5000.00);
        emprestimo.setQuantidadeParcelas(24);
        emprestimo.setJuros(9.49);
        emprestimo.setDataContratacao(LocalDate.of(2025, 3, 7));
        emprestimo.setDataInicio(LocalDate.of(2025, 4, 7));
        emprestimo.setContratarSeguro(true);
        emprestimo.setTaxaMulta(0.02);
        emprestimo.setTaxaJurosMora(0.033);

        LocalDate dtNasc = LocalDate.of(1995, 1, 1);

        // Executa o método
        Emprestimo resultado = CalculadoraEmprestimo.contratoPrice(emprestimo, dtNasc);

        // Validações
        assertEquals(5000.00, resultado.getValorEmprestimo(), 0.01);
        assertEquals(24, resultado.getQuantidadeParcelas());
        assertEquals(LocalDate.of(2025, 3, 7), resultado.getDataContratacao());
        assertEquals(LocalDate.of(2025, 3, 10), resultado.getDataLiberacaoCred());
        assertEquals(LocalDate.of(2027, 4, 7), resultado.getDataInicio().plusMonths(24));
        assertEquals(40.00, resultado.getValorSeguro(), 0.01);
        assertEquals(170.00, resultado.getValorIOF(), 0.01);
        assertEquals(5343.24, resultado.getValorTotal(), 0.01);
        assertEquals(571.99, resultado.getValorParcela(), 0.01);
        assertEquals(10.37, resultado.getTaxaEfetivaMensal(), 0.01);
    }

    @Test
    void calcParcela() {
        BigDecimal valorTotalFinanciado = new BigDecimal("5209.32");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        assertEquals(557.66, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void calcSeguro() {
        BigDecimal valorEmprestimo = new BigDecimal("5000.00");
        LocalDate dtNasc = LocalDate.of(1995, 1, 1);
        LocalDate dataContratacao = LocalDate.of(2025, 3, 7);
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas);
        assertEquals(40.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void calcIOF() { //OK
        BigDecimal valorEmprestimoComSeguro = new BigDecimal("5040.00");
        LocalDate dataLiberacaoCred = LocalDate.of(2025, 3, 10);
        LocalDate dataFimContrato = LocalDate.of(2027, 3, 10);
        BigDecimal resultado = CalculadoraEmprestimo.calcIOF(valorEmprestimoComSeguro, dataLiberacaoCred, dataFimContrato);
        assertEquals(170.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void calcTxEfetivaMes() {
        BigDecimal valorEmprestimo = new BigDecimal("5000");
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        assertEquals(10.03, resultado.doubleValue(), 0.01);
    }
    @Test
    void testProcessarValoresParcela() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorParcela(571.99);
        emprestimo.setJuros(9.49);
        emprestimo.setQuantidadeParcelas(24);
        emprestimo.setValorTotal(5343.24);
        emprestimo.setTaxaMulta(0.02);
        emprestimo.setTaxaJurosMora(0.033);

        List<Parcela> parcelas = new ArrayList<>();
        LocalDate dataBase = LocalDate.now();

        for (int i = 0; i < emprestimo.getQuantidadeParcelas(); i++) {
            Parcela parcela = new Parcela();
            parcela.setDataVencimento(dataBase.plusMonths(i + 1));
            parcelas.add(parcela);
        }

        emprestimo.setParcelaList(parcelas);

        Emprestimo result = CalculadoraEmprestimo.processarValoresParcela(emprestimo);

        assertNotNull(result);
        assertEquals(emprestimo.getQuantidadeParcelas(), result.getParcelaList().size());
        assertTrue(result.getSaldoDevedorAtualizado() > 0);

        for (Parcela parcela : result.getParcelaList()) {
            assertNotNull(parcela.getJuros());
            assertNotNull(parcela.getAmortizacao());
            assertNotNull(parcela.getValorPresenteParcela());
        }
    }

    @Test
    void testProcessarValoresParcelaInvalidos() {
        Emprestimo emprestimo1 = new Emprestimo();
        emprestimo1.setValorParcela(0.0);
        emprestimo1.setJuros(9.49);
        emprestimo1.setQuantidadeParcelas(24);

        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraEmprestimo.processarValoresParcela(emprestimo1);
        });

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setValorParcela(571.99);
        emprestimo2.setJuros(0.0);
        emprestimo2.setQuantidadeParcelas(24);

        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraEmprestimo.processarValoresParcela(emprestimo2);
        });
    }

    @Test
    void testCalcularCarencia() {
        BigDecimal valorTotalFinanciado = new BigDecimal("5343.24");
        double taxaDeJurosMensal = 9.49;
        int diasCarencia = 30;

        BigDecimal resultado = CalculadoraEmprestimo.calcularCarencia(valorTotalFinanciado, taxaDeJurosMensal, diasCarencia);

        assertNotNull(resultado);
        assertTrue(resultado.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(resultado.compareTo(valorTotalFinanciado) < 0);
    }


    @Test
    void testConversorTaxaDeJurosDiaria() {
        double taxaMensal = 1.0;

        double taxaDiaria = CalculadoraEmprestimo.conversorTaxaDeJurosDiaria(taxaMensal);

        assertEquals(0.023373891996774976, taxaDiaria, 0.0000000001);
    }



    @Test
    void testMultaAtraso() {
        BigDecimal valorParcela = new BigDecimal("571.99");
        double taxaMulta = 0.02;

        BigDecimal resultado = CalculadoraEmprestimo.multaAtraso(valorParcela, taxaMulta);

        BigDecimal esperado = new BigDecimal("11.44");
        assertEquals(0, esperado.setScale(2, HALF_UP).compareTo(resultado.setScale(2, HALF_UP)));
    }


    @Test
    void testValorJurosMora() {
        double valorParcela = 571.99;
        double taxaJurosMora = 0.033;
        LocalDate dataVencimento = LocalDate.now().minusDays(10);

        BigDecimal resultado = CalculadoraEmprestimo.valorJurosMora(valorParcela, taxaJurosMora, dataVencimento);

        assertNotEquals(0, BigDecimal.ZERO.compareTo(resultado));
        assertTrue(resultado.compareTo(BigDecimal.ZERO) > 0);
    }
}
