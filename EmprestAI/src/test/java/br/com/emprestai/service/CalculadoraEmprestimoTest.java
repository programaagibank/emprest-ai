package br.com.emprestai.service;

import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    void contratoPrice() {
        // Configuração do objeto Emprestimo
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValorEmprestimo(5000.00); // Agora double
        emprestimo.setQuantidadeParcelas(24);
        emprestimo.setJuros(9.49);
        emprestimo.setDataContratacao(LocalDate.of(2025, 3, 7)); // Sexta-feira
        emprestimo.setDataInicio(LocalDate.of(2025, 4, 7)); // Início do pagamento (1 mês após contratação)
        emprestimo.setContratarSeguro(true);
        emprestimo.setTaxaMulta(0.02); // 2% (exemplo)
        emprestimo.setTaxaJurosMora(0.033); // 1% ao mês / 30 dias = 0.033% ao dia (exemplo)

        LocalDate dtNasc = LocalDate.of(1995, 1, 1);

        // Executa o método
        Emprestimo resultado = CalculadoraEmprestimo.contratoPrice(emprestimo, dtNasc);

        // Validações
        assertEquals(5000.00, resultado.getValorEmprestimo(), 0.01); // Valor inicial do empréstimo como double
        assertEquals(24, resultado.getQuantidadeParcelas());
        assertEquals(LocalDate.of(2025, 3, 7), resultado.getDataContratacao());
        assertEquals(LocalDate.of(2025, 3, 10), resultado.getDataLiberacaoCred()); // Primeiro dia útil após 07/03/2025
        assertEquals(LocalDate.of(2027, 4, 7), resultado.getDataInicio().plusMonths(24)); // Fim do contrato
        assertEquals(40.00, resultado.getValorSeguro(), 0.01); // Seguro aproximado como double
        assertEquals(170.00, resultado.getValorIOF(), 0.01); // IOF como double
        assertEquals(5210.00, resultado.getValorTotal(), 0.01); // Valor total financiado como double
        assertEquals(557.73, resultado.getValorParcela(), 0.01); // Parcela mensal como double
        assertEquals(10.03, resultado.getTaxaEfetivaMensal(), 0.01); // Taxa efetiva como double
    }

    @Test
    void calcParcela() {
        BigDecimal valorTotalFinanciado = new BigDecimal("5209.32");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        assertEquals(557.66, resultado.setScale(2, HALF_UP).doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcSeguro() {
        BigDecimal valorEmprestimo = new BigDecimal("5000.00");
        LocalDate dtNasc = LocalDate.of(1995, 1, 1);
        LocalDate dataContratacao = LocalDate.of(2025, 3, 7);
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas);
        assertEquals(40.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcIOF() {
        BigDecimal valorEmprestimoComSeguro = new BigDecimal("5040.00"); // 5000 + 40 (seguro)
        LocalDate dataLiberacaoCred = LocalDate.of(2025, 3, 10); // Segunda-feira
        LocalDate dataFimContrato = LocalDate.of(2027, 3, 10); // Segunda-feira (24 meses após liberação)
        BigDecimal resultado = CalculadoraEmprestimo.calcIOF(valorEmprestimoComSeguro, dataLiberacaoCred, dataFimContrato);
        assertEquals(170.00, resultado.setScale(2, HALF_UP).doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcTxEfetivaMes() {
        BigDecimal valorEmprestimo = new BigDecimal("5000");
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        BigDecimal resultado = CalculadoraEmprestimo.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        assertEquals(10.03, resultado.doubleValue(), 0.01); // Convertido para double
    }

    @Test
    void calcParcelaVP() {
        BigDecimal parcelaMensal = new BigDecimal("557.73");
        BigDecimal valorEmprestimo = new BigDecimal("5210.00");
        double taxaJurosMensal = 9.49;
        int qtdeParcelas = 24;
        LocalDate dataInicioPagamento = LocalDate.of(2025, 4, 7);
        double taxaMulta = 0.02; // 2%
        double taxaJurosMora = 0.033; // 1% ao mês / 30 dias
        List<Parcela> parcelas = CalculadoraEmprestimo.calcParcelaVP(parcelaMensal, valorEmprestimo, taxaJurosMensal, qtdeParcelas, dataInicioPagamento, taxaMulta, taxaJurosMora);

        assertEquals(24, parcelas.size());
        assertEquals(LocalDate.of(2025, 4, 7), parcelas.get(0).getDataVencimento());
        assertEquals(11.15, parcelas.get(0).getMulta(), 0.01); // 557.73 * 0.02 como double
        assertEquals(0.00, parcelas.get(0).getJurosMora(), 0.01); // Sem atraso em 18/03/2025
        assertTrue(parcelas.get(0).getJuros() > 0); // Juros deve ser positivo
        assertTrue(parcelas.get(0).getAmortizacao() > 0); // Amortização deve ser positiva
    }
}