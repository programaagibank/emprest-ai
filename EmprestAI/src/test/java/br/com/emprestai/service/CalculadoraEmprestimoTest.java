package br.com.emprestai.service;

import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraEmprestimo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraEmprestimoTest {

    @Test
    void contratoPrice() {
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
        Emprestimo resultado = CalculadoraEmprestimo.contratoPrice(emprestimo, dtNasc);

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
    void calcIOF() {
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

//    @Test
//    void testProcessarValoresParcelaAtrasada() {
//        Emprestimo emprestimo = new Emprestimo();
//        emprestimo.setValorParcela(571.99);
//        emprestimo.setJuros(9.49);
//        emprestimo.setQuantidadeParcelas(3);
//        emprestimo.setValorTotal(5343.24);
//        emprestimo.setTaxaMulta(2.0); // 2%
//        emprestimo.setTaxaJurosMora(1.0); // 1% ao mês
//
//        List<Parcela> parcelas = new ArrayList<>();
//        Parcela parcelaAtrasada = new Parcela(1L, 1, LocalDate.now().minusDays(10), 0.0);
//        parcelaAtrasada.setStatusParcela(StatusEmpParcela.PENDENTE);
//        parcelas.add(parcelaAtrasada);
//        parcelas.add(new Parcela(1L, 2, LocalDate.now().plusDays(10), 0.0));
//        Parcela parcelaPaga = new Parcela(1L, 3, LocalDate.now().minusDays(5), 571.99);
//        parcelaPaga.setDataPagamento(LocalDate.now().minusDays(2));
//        parcelas.add(parcelaPaga);
//
//        emprestimo.setParcelaList(parcelas);
//
//        Emprestimo resultado = CalculadoraEmprestimo.processarValoresParcela(emprestimo);
//        List<Map<String, Object>> detalhes = resultado.getDetalhesParcelas();
//
//        // Parcela atrasada
//        Map<String, Object> parcela1 = detalhes.get(0);
//        assertEquals(StatusEmpParcela.ATRASADA, resultado.getParcelaList().get(0).getIdStatus());
//        assertEquals(571.99, (Double) parcela1.get("valorPresenteParcela"), 0.01);
//        assertEquals(571.99 * 0.02, (Double) parcela1.get("multa"), 0.01); // 11.44
//        assertEquals(571.99 * (0.01 / 30) * 10, (Double) parcela1.get("jurosMora"), 0.01); // ≈ 1.91
//
//        // Parcela futura
//        Map<String, Object> parcela2 = detalhes.get(1);
//        double taxaDiaria = CalculadoraEmprestimo.conversorTaxaDeJurosDiaria(9.49) / 100;
//        double valorPresenteEsperado = 571.99 / Math.pow(1 + taxaDiaria, 10);
//        assertEquals(valorPresenteEsperado, (Double) parcela2.get("valorPresenteParcela"), 0.01);
//        assertFalse(parcela2.containsKey("multa"));
//        assertFalse(parcela2.containsKey("jurosMora"));
//
//        // Parcela paga
//        Map<String, Object> parcela3 = detalhes.get(2);
//        assertEquals(0.0, (Double) parcela3.get("valorPresenteParcela"), 0.01);
//        assertFalse(parcela3.containsKey("multa"));
//        assertFalse(parcela3.containsKey("jurosMora"));
//
//        // Saldo devedor
//        double saldoEsperado = 571.99 + valorPresenteEsperado;
//        assertEquals(saldoEsperado, resultado.getSaldoDevedorAtualizado(), 0.01);
//    }

    @Test
    void testProcessarValoresParcelaInvalidos() {
        Emprestimo emprestimo1 = new Emprestimo();
        emprestimo1.setValorParcela(0.0);
        emprestimo1.setJuros(9.49);
        emprestimo1.setQuantidadeParcelas(24);
        emprestimo1.setParcelaList(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraEmprestimo.processarValoresParcela(emprestimo1);
        });

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setValorParcela(571.99);
        emprestimo2.setJuros(0.0);
        emprestimo2.setQuantidadeParcelas(24);
        emprestimo2.setParcelaList(new ArrayList<>());

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
        assertEquals(0.0328, taxaDiaria, 0.01);
    }

    @Test
    void testMultaAtraso() {
        BigDecimal valorParcela = new BigDecimal("571.99");
        double taxaMulta = 2.0;
        BigDecimal resultado = CalculadoraEmprestimo.multaAtraso(valorParcela, taxaMulta);
        assertEquals(11.44, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void testValorJurosMora() {
        double valorParcela = 571.99;
        double taxaJurosMora = 1.0;
        LocalDate dataVencimento = LocalDate.now().minusDays(10);
        BigDecimal resultado = CalculadoraEmprestimo.valorJurosMora(valorParcela, taxaJurosMora, dataVencimento);
        assertEquals(571.99 * (0.01 / 30) * 10, resultado.doubleValue(), 0.01); // ≈ 1.91
    }
}