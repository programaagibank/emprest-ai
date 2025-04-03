package br.com.emprestai.service.calculos;

import br.com.emprestai.model.Emprestimo;
import java.math.BigDecimal;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;

import static br.com.emprestai.util.DateUtils.primeiroDiaUtil;

public class CalculadoraContrato {

    public static Emprestimo contratoPrice(Emprestimo emprestimo, LocalDate dtNasc) {
        BigDecimal valorEmprestimo = new BigDecimal(String.valueOf(emprestimo.getValorEmprestimo()));
        int qtdeParcelas = emprestimo.getQuantidadeParcelas();
        double taxaJurosMensal = emprestimo.getTaxaJuros();

        LocalDate dataContratacao = emprestimo.getDataContratacao();
        LocalDate dataLiberacaoCred = primeiroDiaUtil(dataContratacao);
        emprestimo.setDataLiberacaoCred(dataLiberacaoCred);
        LocalDate dataInicioPagamento = emprestimo.getDataInicio();
        LocalDate dataFimContrato = dataInicioPagamento.plusMonths(qtdeParcelas);

        BigDecimal seguro = emprestimo.getContratarSeguro()
                ? CalculadoraCustosAdicionais.calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas)
                : BigDecimal.ZERO;
        BigDecimal valorTotalComSeguro = valorEmprestimo.add(seguro);
        BigDecimal iof = CalculadoraCustosAdicionais.calcIOF(valorTotalComSeguro, dataLiberacaoCred, dataFimContrato);
        BigDecimal valorTotalSemCarencia = valorEmprestimo.add(iof).add(seguro);

        int diasCarencia = (int) DAYS.between(dataContratacao, dataInicioPagamento);
        BigDecimal resultadoCarencia = CalculadoraCustosAdicionais.calcularCarencia(valorTotalSemCarencia, taxaJurosMensal, diasCarencia);
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

}