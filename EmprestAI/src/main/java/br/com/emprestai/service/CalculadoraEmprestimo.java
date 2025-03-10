package br.com.emprestai.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.emprestai.util.DateUtils.primeiroDiaUtil;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class CalculadoraEmprestimo {

    private static final BigDecimal PERCENTUAL_FIXO_IOF = new BigDecimal("0.0038");
    private static final BigDecimal PERCENTUAL_VAR_IOF = new BigDecimal("0.000082");
    private static final BigDecimal PERCENTUAL_FIXO_SEG = new BigDecimal("0.0025");
    private static final BigDecimal PERCENTUAL_IDADE = new BigDecimal("0.00005");
    private static final BigDecimal DOZE = new BigDecimal("12");

    public static Map<String, Object> contratoPrice(double valorEmprestimo, int qtdeParcelas, double taxaJurosMensal, LocalDate dataContratacao, LocalDate dtNasc, boolean contratarSeguro) {
        if (valorEmprestimo <= 0 || qtdeParcelas <= 1 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }

        Map<String, Object> dadosContrato = new HashMap<>();
        LocalDate dataLiberacaoCred = primeiroDiaUtil(dataContratacao);
        LocalDate dataFimContrato = dataLiberacaoCred.plusMonths(qtdeParcelas);
        BigDecimal valorEmprestimoBD = BigDecimal.valueOf(valorEmprestimo);
        BigDecimal seguro = (contratarSeguro ? calcSeguro(valorEmprestimo, dtNasc, dataContratacao, qtdeParcelas) : ZERO);
        BigDecimal iof = calcIOF(valorEmprestimo, seguro, dataLiberacaoCred, dataFimContrato);
        BigDecimal valorTotalFinanciado = (valorEmprestimoBD.add(iof).add(seguro));
        BigDecimal parcelaMensal = calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        BigDecimal taxaEfetivaMensal = calcTxEfetivaMes(valorEmprestimo, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        BigDecimal saldoDevedorPresente = calcSaldoDevedorPresente(parcelaMensal, taxaJurosMensal, qtdeParcelas);
        dadosContrato.put("valorEmprestimo", valorEmprestimo);
        dadosContrato.put("qtdeParcelas", qtdeParcelas);
        dadosContrato.put("dataContratacao", dataContratacao);
        dadosContrato.put("dataLiberacaoCred", dataLiberacaoCred);
        dadosContrato.put("dataFimContrato", dataFimContrato);
        dadosContrato.put("taxaJurosMensal", taxaJurosMensal);
        dadosContrato.put("custoSeguro", seguro.setScale(2, HALF_UP));
        dadosContrato.put("valorTributos", iof.setScale(2, HALF_UP));
        dadosContrato.put("valorTotalFinanciado", valorTotalFinanciado.setScale(2, HALF_UP));
        dadosContrato.put("parcelaMensal", parcelaMensal.setScale(2, HALF_UP));
        dadosContrato.put("saldoDevedorPresente", saldoDevedorPresente.setScale(2, HALF_UP));
        dadosContrato.put("taxaEfetivaMensal", taxaEfetivaMensal.setScale(4, HALF_UP));
        return dadosContrato;
    }

    public static BigDecimal calcParcela(BigDecimal valorTotalFinanciado, double taxaJurosMensal, int qtdeParcelas) {
        if (valorTotalFinanciado.doubleValue() <= 0 || qtdeParcelas <= 1 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal));
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas, DECIMAL128));
        return valorTotalFinanciado.multiply(BigDecimal.valueOf(taxaJurosMensal)).divide(denominador, DECIMAL128);
    }

    public static BigDecimal calcSeguro(double valorEmprestimo, LocalDate dtNasc, LocalDate dataContratacao, int qtdeParcelas) {
        if (valorEmprestimo <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long idade = YEARS.between(dtNasc, dataContratacao);
        BigDecimal fatorParcelas = BigDecimal.valueOf(qtdeParcelas).divide(DOZE, DECIMAL128);
        return (new BigDecimal(valorEmprestimo).multiply(PERCENTUAL_FIXO_SEG.add(PERCENTUAL_IDADE.multiply(BigDecimal.valueOf(idade))).multiply(fatorParcelas)));
    }

    public static BigDecimal calcIOF(double valorEmprestimo, BigDecimal seguro, LocalDate dataLiberacaoCred, LocalDate dataFimContrato) {
        if (valorEmprestimo <= 0 || seguro.doubleValue() < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        long diasDeContrato = DAYS.between(dataLiberacaoCred, dataFimContrato);
        long diasIOF = Long.min(diasDeContrato, 365);
        BigDecimal percentualVarIOF = PERCENTUAL_VAR_IOF.multiply(BigDecimal.valueOf(diasIOF));
        BigDecimal valorEmprestimoSeg = new BigDecimal(valorEmprestimo).add(seguro);
        return (valorEmprestimoSeg.multiply((PERCENTUAL_FIXO_IOF.add(percentualVarIOF))));
    }

    //Utilizando metodo de Newton-Raphson
    public static BigDecimal calcTxEfetivaMes(double valorEmprestimo, BigDecimal parcelaMensal, double taxaNominal, int qtdeParcelas) {
        // Tolerância para convergência (ex.: 0,000001)
        BigDecimal TOLERANCIA = new BigDecimal("0.000001");
        // Número máximo de iterações
        int MAX_ITERACOES = 100;
        if (valorEmprestimo <= 0 || parcelaMensal.doubleValue() < 0 || taxaNominal <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal taxaEfetiva = BigDecimal.valueOf(taxaNominal);
        BigDecimal fr;
        BigDecimal frlin;
        for (int i = 0; i < MAX_ITERACOES; i++) {
            //Calculo da parcela
            fr = fr(valorEmprestimo, parcelaMensal, taxaEfetiva, qtdeParcelas);
            frlin = frlin(parcelaMensal, taxaEfetiva, qtdeParcelas);
            // Verifica convergência: se |f(r)| < tolerância, para
            if (fr.abs().compareTo(TOLERANCIA) < 0) {
                System.out.println("teste");
                return taxaEfetiva.setScale(6, HALF_UP);
            }
            taxaEfetiva = taxaEfetiva.subtract(fr.divide(frlin, DECIMAL128));
        }
        return taxaEfetiva.setScale(4, HALF_UP);
    }

    private static BigDecimal fr(double valorEmprestimo, BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        // Passo 1: Calcular (1 + r)
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);

        // Passo 2: Calcular (1 + r)^-n
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);

        // Passo 3: Calcular 1 - (1 + r)^-n
        BigDecimal numerador = ONE.subtract(umMaisTaxaPowNeg);

        // Passo 4: Definir o denominador r
        BigDecimal denominador = taxaNominal;

        // Passo 5: Calcular (1 - (1 + r)^-n) / r
        BigDecimal resultado = numerador.divide(denominador, DECIMAL128);

        // Passo 6: Calcular f(r) = PMT * [(1 - (1 + r)^-n) / r] - PV
        return (parcelaMensal.multiply(resultado)).subtract(BigDecimal.valueOf(valorEmprestimo));
    }

    private static BigDecimal frlin(BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        // Passo 1: Calcular (1 + r)
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);

        // Passo 2: Calcular (1 + r)^-n
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);

        // Passo 3: Calcular (1 + r)^-(n+1)
        BigDecimal umMaisTaxaPowNegMaisUm = umMaisTaxa.pow(-qtdeParcelas - 1, DECIMAL128);

        // Passo 4: Calcular (1 + r)^-n - 1
        BigDecimal numerador1 = umMaisTaxaPowNeg.subtract(ONE);

        // Passo 5: Calcular n * (1 + r)^-(n+1)
        BigDecimal numerador2 = BigDecimal.valueOf(qtdeParcelas).multiply(umMaisTaxaPowNegMaisUm);

        // Passo 6: Calcular r^2
        BigDecimal denominador1 = taxaNominal.pow(2, DECIMAL128);

        // Passo 7: Definir o denominador r
        BigDecimal denominador2 = taxaNominal;

        // Passo 8: Calcular [(1 + r)^-n - 1] / r^2
        BigDecimal resultado1 = numerador1.divide(denominador1, DECIMAL128);

        // Passo 9: Calcular [n * (1 + r)^-(n+1)] / r
        BigDecimal resultado2 = numerador2.divide(denominador2, DECIMAL128);

        // Passo 10: Somar os resultados e multiplicar por PMT
        return (resultado1.add(resultado2)).multiply(parcelaMensal);
    }

    public static BigDecimal calcSaldoDevedorPresente(BigDecimal parcelaMensal, double taxaJurosMensal, int qtdeParcelas) {
        if (parcelaMensal.doubleValue() <= 0 || taxaJurosMensal <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal saldoDevedorAtualizado = ZERO;

        List<BigDecimal> parcelasVP = calcParcelaVP(parcelaMensal, taxaJurosMensal, qtdeParcelas);
        for (int i = 0; i < parcelasVP.size(); i++) {

            // Acumula o valor presente no saldo devedor
            saldoDevedorAtualizado = saldoDevedorAtualizado.add(parcelasVP.get(i));
        }
        return saldoDevedorAtualizado;
    }

    public static List<BigDecimal> calcParcelaVP(BigDecimal parcelaMensal, double taxaJurosMensal, int qtdeParcelas) {
        List<BigDecimal> parcelasVP = new ArrayList<>();
        if (parcelaMensal.doubleValue() <= 0 || taxaJurosMensal <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal));
        for (int i = 1; i <= qtdeParcelas; i++) {
            // Calcula o valor presente da parcela i: PMT / (1 + r)^i
            parcelasVP.add(parcelaMensal.divide(umMaisTaxa.pow(i, DECIMAL128), DECIMAL128));
        }
        return parcelasVP;
    }
}
