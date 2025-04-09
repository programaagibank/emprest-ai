package br.com.emprestai.service.calculos;

import br.com.emprestai.model.Emprestimo;

import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.emprestai.util.DateUtils.primeiroDiaUtil;
import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;

public class CalculadoraContrato {

    public static Emprestimo contratoPrice(Emprestimo emprestimo, int idade) {
        BigDecimal valorEmprestimo = new BigDecimal(String.valueOf(emprestimo.getValorEmprestimo()));
        int qtdeParcelas = emprestimo.getQuantidadeParcelas();
        double taxaJurosMensal = emprestimo.getTaxaJuros();

        LocalDate dataContratacao = emprestimo.getDataContratacao();
        LocalDate dataLiberacaoCred = primeiroDiaUtil(dataContratacao);
        emprestimo.setDataLiberacaoCred(dataLiberacaoCred);
        LocalDate dataInicioPagamento = emprestimo.getDataInicio();
        LocalDate dataFimContrato = dataInicioPagamento.plusMonths(qtdeParcelas);

        BigDecimal seguro = emprestimo.getContratarSeguro()
                ? CalculadoraCustosAdicionais.calcSeguro(valorEmprestimo, idade, qtdeParcelas)
                : BigDecimal.ZERO;
        BigDecimal valorTotalComSeguro = valorEmprestimo.add(seguro);
        BigDecimal iof = CalculadoraCustosAdicionais.calcIOF(valorTotalComSeguro, dataLiberacaoCred, dataFimContrato);
        BigDecimal valorTotalSemCarencia = valorEmprestimo.add(iof).add(seguro);

        BigDecimal resultadoCarencia = CalculadoraCustosAdicionais.calcularCarencia(valorTotalSemCarencia, taxaJurosMensal, emprestimo.getCarencia());
        BigDecimal valorTotalFinanciado = valorTotalSemCarencia.add(resultadoCarencia);

        emprestimo.setOutrosCustos(resultadoCarencia.doubleValue());
        emprestimo.setValorSeguro(seguro.doubleValue());
        emprestimo.setValorIOF(iof.doubleValue());
        emprestimo.setValorTotal(valorTotalFinanciado.doubleValue());

        BigDecimal parcelaMensal = CalculadoraParcela.calcularParcelaPrice(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        emprestimo.setValorParcela(parcelaMensal.doubleValue());

        BigDecimal taxaEfetivaMensal = CalculoTaxaJuros.calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        emprestimo.setTaxaEfetivaMensal(taxaEfetivaMensal.doubleValue());

        return emprestimo;
    }

    // metodo que calcula o valor total financiado a partir da parcela
    public static double calcularValorTotalFinanciado(double valorParcela, double taxaJurosMensal, int qtdeParcelas) {
        BigDecimal parcelaBD = BigDecimal.valueOf(valorParcela);
        if (parcelaBD.doubleValue() <= 0 || qtdeParcelas <= 1 || taxaJurosMensal <= 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }

        BigDecimal i = BigDecimal.valueOf(taxaJurosMensal / 100); // Taxa em decimal
        BigDecimal umMaisTaxa = ONE.add(i);
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas, DECIMAL128));
        BigDecimal valorTotalFinanciado = parcelaBD.multiply(denominador).divide(i, DECIMAL128);

        System.out.println("valorTotalFinanciado antes do double: " + valorTotalFinanciado);

        return valorTotalFinanciado.doubleValue();
    }
}