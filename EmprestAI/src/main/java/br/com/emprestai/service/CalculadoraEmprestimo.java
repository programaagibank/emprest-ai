package br.com.emprestai.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
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

    public static Map<String, Number> contratoPrice(double valorEmprestimo, int qtdeParcelas, double taxaJurosMensal, LocalDate dataContratacao, LocalDate dtNasc, boolean contratarSeguro){
        if (valorEmprestimo <= 0 || qtdeParcelas <= 0 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        if (dtNasc.isAfter(dataContratacao)) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }

        Map<String, Number> dadosContrato = new HashMap<>();

        BigDecimal valorEmprestimoBD = BigDecimal.valueOf(valorEmprestimo);
        BigDecimal iof = calcIOF(valorEmprestimoBD, qtdeParcelas, dataContratacao);
        BigDecimal seguro = (contratarSeguro? calcSeguro(valorEmprestimoBD, dtNasc, dataContratacao, qtdeParcelas): ZERO);
        BigDecimal valorTotalFinanciado = (valorEmprestimoBD.add(iof).add(seguro));
        BigDecimal parcelaMensal = calcParcela(valorTotalFinanciado, taxaJurosMensal, qtdeParcelas);
        BigDecimal taxaEfetivaMensal = calcTxEfetivaMes(valorTotalFinanciado, parcelaMensal, taxaJurosMensal, qtdeParcelas);
        dadosContrato.put("valorEmprestimo", valorEmprestimo);
        dadosContrato.put("qtdeParcelas", qtdeParcelas);
        dadosContrato.put("taxaJurosMensal", taxaJurosMensal);
        dadosContrato.put("custoSeguro", seguro.setScale(2, HALF_UP));
        dadosContrato.put("valorTributos", iof.setScale(2, HALF_UP));
        dadosContrato.put("valorTotalFinanciado", valorTotalFinanciado.setScale(2, HALF_UP));
        dadosContrato.put("parcelaMensal", parcelaMensal.setScale(2, HALF_UP));
        dadosContrato.put("saldoDevAtualizadoSJuros", calcSaldoDevSemJuros(parcelaMensal, taxaJurosMensal, qtdeParcelas).setScale(2, HALF_UP));
        dadosContrato.put("taxaEfetivaMensal", taxaEfetivaMensal.setScale(2, HALF_UP));
        return dadosContrato;
    }

    public static BigDecimal calcParcela(BigDecimal valorTotalFinanciado, double taxaJurosMensal, int qtdeParcelas){
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal));
        BigDecimal denominador = ONE.subtract(umMaisTaxa.pow(-qtdeParcelas));
        return valorTotalFinanciado.multiply(BigDecimal.valueOf(taxaJurosMensal)).divide(denominador, DECIMAL128);
    }

    public static BigDecimal calcSeguro(BigDecimal valorEmprestimo, LocalDate dtNasc, LocalDate dataContratacao, int qtdeParcelas){
        long idade = YEARS.between(dtNasc, dataContratacao);
        BigDecimal fatorParcelas = BigDecimal.valueOf(qtdeParcelas).divide(DOZE, 2, HALF_UP);
        return valorEmprestimo.multiply(PERCENTUAL_FIXO_SEG.add(PERCENTUAL_IDADE.multiply(BigDecimal.valueOf(idade))).multiply(fatorParcelas));
    }

    public static BigDecimal calcIOF(BigDecimal valorEmprestimo, int qtdeParcelas, LocalDate dataContratacao){
        LocalDate diaLibCred = primeiroDiaUtil(dataContratacao);
        LocalDate diaFimContrato = diaLibCred.plusMonths(qtdeParcelas);
        long diasDeContrato =  DAYS.between(diaLibCred, diaFimContrato);
        long diasIOF = Long.min(diasDeContrato, 365);
        BigDecimal percentualVarIOF = PERCENTUAL_VAR_IOF.multiply(BigDecimal.valueOf(diasIOF));
        return valorEmprestimo.multiply((PERCENTUAL_FIXO_IOF.add(percentualVarIOF)));
    }

    //Utilizando metodo de Newton-Raphson
    public static BigDecimal calcTxEfetivaMes(BigDecimal valorTotalFinanciado, BigDecimal parcelaMensal, double taxaNominal, int qtdeParcelas){
        BigDecimal taxaEfetiva = BigDecimal.valueOf(taxaNominal);
        BigDecimal fr;
        BigDecimal frlin;
        for (int i = 0; i < 100; i++) {
            //Calculo da parcela
            fr = fr(valorTotalFinanciado, parcelaMensal, taxaEfetiva, qtdeParcelas);
            frlin = frlin(valorTotalFinanciado, parcelaMensal, taxaEfetiva, qtdeParcelas);
            taxaEfetiva = taxaEfetiva.subtract(fr.divide(frlin, DECIMAL128));
        }
        return taxaEfetiva;
    };

    private static BigDecimal fr(BigDecimal valorTotalFinanciado, BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas){
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
        return (parcelaMensal.multiply(resultado)).subtract(valorTotalFinanciado);
    }

    private static BigDecimal frlin(BigDecimal valorTotalFinanciado, BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas){
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

    public static BigDecimal calcSaldoDevSemJuros(BigDecimal parcelaMensal, double taxaJurosMensal, int qtdeParcelas){
        BigDecimal saldoDevedorAtualizado = ZERO;
        BigDecimal umMaisTaxa = ONE.add(BigDecimal.valueOf(taxaJurosMensal));
        BigDecimal valorPresenteParcela;
        for (int i = 0; i < qtdeParcelas; i++) {
            // Calcula o valor presente da parcela i: PMT / (1 + r)^i
            valorPresenteParcela = parcelaMensal.divide(umMaisTaxa.pow(i, DECIMAL128), DECIMAL128);

            // Acumula o valor presente no saldo devedor
            saldoDevedorAtualizado = saldoDevedorAtualizado.add(valorPresenteParcela);
        }
        return saldoDevedorAtualizado;
    }
}
