package br.com.emprestai.service.calculos;

import br.com.emprestai.util.ConversorFinanceiro;
import br.com.emprestai.util.DateUtils;
import br.com.emprestai.util.EmprestimoParams;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static br.com.emprestai.util.DateUtils.primeiroDiaUtil;
import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;

public class CalculadoraCustosAdicionais {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();
    private static final BigDecimal DOZE = new BigDecimal("12");

    public static BigDecimal calcSeguro(BigDecimal valorEmprestimo, int idade, int qtdeParcelas) {
        BigDecimal segFixo = new BigDecimal(Double.toString(params.getPercentualSegFixo() / 100));
        BigDecimal segVar = new BigDecimal(Double.toString(params.getPercentualSegVar() / 100));
        if (valorEmprestimo.doubleValue() <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal fatorParcelas = BigDecimal.valueOf(qtdeParcelas).divide(DOZE, DECIMAL128);
        return valorEmprestimo.multiply(segFixo.add(segVar.multiply(BigDecimal.valueOf(idade))).multiply(fatorParcelas));
    }

    public static BigDecimal calcIOF(BigDecimal valorEmprestimoComSeguro, LocalDate dataLiberacaoCred, LocalDate dataFimContrato) {
        BigDecimal percentualFixoIof = new BigDecimal(Double.toString(params.getPercentualIofFixo() / 100));
        BigDecimal percentualVarIof = new BigDecimal(Double.toString(params.getPercentualIofVar() / 100));
        if (valorEmprestimoComSeguro.doubleValue() <= 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long diasDeContrato = ChronoUnit.DAYS.between(dataLiberacaoCred, dataFimContrato);
        long diasIOF = Long.min(diasDeContrato, 365);
        BigDecimal fixoIof = percentualFixoIof.multiply(valorEmprestimoComSeguro);
        BigDecimal varIof = percentualVarIof.multiply(BigDecimal.valueOf(diasIOF)).multiply(valorEmprestimoComSeguro);
        return fixoIof.add(varIof);
    }

    public static BigDecimal calcularCarencia(BigDecimal valorTotalFinanciado, double taxaDeJurosMensal, int diasCarencia) {
        if (valorTotalFinanciado == null || valorTotalFinanciado.doubleValue() <= 0) {
            throw new IllegalArgumentException("O Total da carência não pode ser menor ou igual a zero");
        }
        BigDecimal taxaDiaria = new BigDecimal(ConversorFinanceiro.conversorTaxaDeJurosDiaria(taxaDeJurosMensal) / 100);
        return valorTotalFinanciado.multiply((ONE.add(taxaDiaria).pow(diasCarencia)).subtract(ONE));
    }

    public static double reverterValorEmprestimo(double valorTotalFinanciado,
                                                     int idade,
                                                     int qtdeParcelas,
                                                     double taxaJurosMensal,
                                                     int diasCarencia,
                                                     boolean contratarSeguro) {
        // Passo 1: Remover Carência
        BigDecimal valorTotalFinanciadoBD = new BigDecimal(valorTotalFinanciado);
        BigDecimal taxaDiaria = new BigDecimal(ConversorFinanceiro.conversorTaxaDeJurosDiaria(taxaJurosMensal) / 100);
        BigDecimal fatorCarencia = ONE.add(taxaDiaria).pow(diasCarencia);
        BigDecimal valorSemCarencia = valorTotalFinanciadoBD.divide(fatorCarencia, DECIMAL128);
        LocalDate dataLiberacaoCred = primeiroDiaUtil(LocalDate.now());
        LocalDate dataFimContrato = dataLiberacaoCred.plusMonths(qtdeParcelas);

        // Passo 2: Remover IOF
        BigDecimal percentualFixoIof = new BigDecimal(Double.toString(params.getPercentualIofFixo() / 100));
        BigDecimal percentualVarIof = new BigDecimal(Double.toString(params.getPercentualIofVar() / 100));
        long diasDeContrato = ChronoUnit.DAYS.between(dataLiberacaoCred, dataFimContrato);
        long diasIOF = Long.min(diasDeContrato, 365);
        BigDecimal fatorIOF = ONE.add(percentualFixoIof).add(percentualVarIof.multiply(BigDecimal.valueOf(diasIOF)));
        BigDecimal valorSemIOF = valorSemCarencia.divide(fatorIOF, DECIMAL128);

        // Passo 3: Remover Seguro (se aplicável)
        if (contratarSeguro) {
            BigDecimal segFixo = new BigDecimal(Double.toString(params.getPercentualSegFixo() / 100));
            BigDecimal segVar = new BigDecimal(Double.toString(params.getPercentualSegVar() / 100));
            BigDecimal fatorParcelas = BigDecimal.valueOf(qtdeParcelas).divide(DOZE, DECIMAL128);
            BigDecimal fatorSeguro = ONE.add(segFixo.add(segVar.multiply(BigDecimal.valueOf(idade))).multiply(fatorParcelas));
            return valorSemIOF.divide(fatorSeguro, DECIMAL128).doubleValue();
        }
        return valorSemIOF.doubleValue(); // Se não houver seguro, o valor sem IOF é o valorEmprestimo
    }
}