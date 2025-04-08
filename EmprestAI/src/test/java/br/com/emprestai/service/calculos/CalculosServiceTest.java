package br.com.emprestai.service.calculos;

import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.ConversorFinanceiro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

class CalculosServiceTest {

    @Test
    void contratoPrice() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorEmprestimo(5000.00);
        emprestimo.setQuantidadeParcelas(24);
        emprestimo.setTaxaJuros(9.49);
        emprestimo.setDataContratacao(LocalDate.of(2025, 3, 7));
        emprestimo.setDataInicio(LocalDate.of(2025, 4, 7));
        emprestimo.setContratarSeguro(true);
        emprestimo.setTaxaMulta(0.02);
        emprestimo.setTaxaJurosMora(0.033);

        int idade = 30;

        Emprestimo resultado = CalculadoraContrato.contratoPrice(emprestimo, idade);

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
        BigDecimal resultado = CalculadoraParcela.calcularParcelaPrice(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        assertEquals(557.66, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void calcSeguro() {
        BigDecimal valorEmprestimo = new BigDecimal("5000.00");
        int idade = 35;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraCustosAdicionais.calcSeguro(valorEmprestimo, idade, qtdeParcelas);
        assertEquals(42.50, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void calcIOF() {
        BigDecimal valorEmprestimoComSeguro = new BigDecimal("5040.00");
        LocalDate dataLiberacaoCred = LocalDate.of(2025, 3, 10);
        LocalDate dataFimContrato = LocalDate.of(2027, 3, 10);
        BigDecimal resultado = CalculadoraCustosAdicionais.calcIOF(valorEmprestimoComSeguro, dataLiberacaoCred, dataFimContrato);
        assertEquals(170.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void calcTxEfetivaMes() {
        BigDecimal valorEmprestimo = new BigDecimal("5000");
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculoTaxaJuros.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        assertEquals(10.03, resultado.doubleValue(), 0.01);
    }

    @Test
    void testProcessarValoresParcelaInvalidos() {
        Emprestimo emprestimo1 = new Emprestimo();
        emprestimo1.setValorParcela(0.0);
        emprestimo1.setTaxaJuros(9.49);
        emprestimo1.setQuantidadeParcelas(24);
        emprestimo1.setParcelaList(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraParcela.processarValoresParcela(emprestimo1);
        });

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setValorParcela(571.99);
        emprestimo2.setTaxaJuros(0.0);
        emprestimo2.setQuantidadeParcelas(24);
        emprestimo2.setParcelaList(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraParcela.processarValoresParcela(emprestimo2);
        });
    }

    @Test
    void testCalcularCarencia() {
        BigDecimal valorTotalFinanciado = new BigDecimal("5343.24");
        double taxaDeJurosMensal = 9.49;
        int diasCarencia = 30;
        BigDecimal resultado = CalculadoraCustosAdicionais.calcularCarencia(valorTotalFinanciado, taxaDeJurosMensal, diasCarencia);
        assertNotNull(resultado);
        assertTrue(resultado.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(resultado.compareTo(valorTotalFinanciado) < 0);
    }

    @Test
    void testConversorTaxaDeJurosDiaria() {
        double taxaMensal = 1.0;
        double taxaDiaria = ConversorFinanceiro.conversorTaxaDeJurosDiaria(taxaMensal);
        assertEquals(0.0328, taxaDiaria, 0.01);
    }

    @Test
    void testMultaAtraso() {
        BigDecimal valorParcela = new BigDecimal("571.99");
        double taxaMulta = 2.0;
        BigDecimal resultado = CalculadoraMultaJurosMora.multaAtraso(valorParcela, taxaMulta);
        assertEquals(11.44, resultado.setScale(2, HALF_UP).doubleValue(), 0.01);
    }

    @Test
    void testValorJurosMora() {
        double valorParcela = 571.99;
        double taxaJurosMora = 1.0;
        LocalDate dataVencimento = LocalDate.now().minusDays(10);
        BigDecimal resultado = CalculadoraMultaJurosMora.valorJurosMora(valorParcela, taxaJurosMora, dataVencimento);
        assertEquals(571.99 * 0.01 * 10, resultado.doubleValue(), 0.01); // ≈ 1.91
    }
}