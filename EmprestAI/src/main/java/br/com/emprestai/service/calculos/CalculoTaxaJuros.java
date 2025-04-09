package br.com.emprestai.service.calculos;

import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.util.EmprestimoParams;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;

public class CalculoTaxaJuros {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // 12.3 Taxa de Juros Mensal
    public static double calcularTaxaJurosMensal(double score, int quantidadeParcelas, TipoEmprestimoEnum TipoEmprestimo) {
        EmprestimoParams params = EmprestimoParams.getInstance();

        if (score <= 0 || score > 1000) {
            throw new IllegalArgumentException("Score fora da tolerância");
        }

        // Adicionado a validação para score abaixo de 200
        if (score < 200) {
            throw new IllegalArgumentException("Score abaixo do limite mínimo permitido");
        }

        double jurosMinimo = 0, jurosMaximo = 0, prazoMinimo = 0, incrementoTaxa = 0;

        switch (TipoEmprestimo) {
            case CONSIGNADO -> {
                jurosMinimo = params.getJurosMinimoConsignado();
                jurosMaximo = params.getJurosMaximoConsignado();
                prazoMinimo = params.getPrazoMinimoConsignado();
                incrementoTaxa = params.getIncrementoTaxaConsig();
            }
            case PESSOAL -> {
                jurosMinimo = params.getJurosMinimoPessoal();
                jurosMaximo = params.getJurosMaximoPessoal();
                prazoMinimo = params.getPrazoMinimoPessoal();
                incrementoTaxa = 0.05; // Exemplo de incremento para pessoal
            }
        }

        if (quantidadeParcelas < prazoMinimo) {
            throw new IllegalArgumentException("A quantidade de parcelas não pode ser inferior ao prazo mínimo de " + prazoMinimo);
        }

        double taxaBase;
        if (score == 1000) {
            taxaBase = jurosMinimo;
        } else if (score >= 801 && score < 1000) {
            taxaBase = interpoLinear(score, 801, 1000, jurosMinimo, jurosMinimo + (jurosMaximo - jurosMinimo) * 0.25);
        } else if (score >= 601 && score <= 800) {
            taxaBase = interpoLinear(score, 601, 800, jurosMinimo + (jurosMaximo - jurosMinimo) * 0.25, jurosMinimo + (jurosMaximo - jurosMinimo) * 0.5);
        } else if (score >= 401 && score <= 600) {
            taxaBase = interpoLinear(score, 401, 600, jurosMinimo + (jurosMaximo - jurosMinimo) * 0.5, jurosMinimo + (jurosMaximo - jurosMinimo) * 0.75);
        } else {
            taxaBase = jurosMaximo;
        }

        double taxaJurosMensal = taxaBase + incrementoTaxa * (quantidadeParcelas - prazoMinimo);

        if (taxaJurosMensal > jurosMaximo) {
            return jurosMaximo;
        }
        return taxaJurosMensal;
    }

    private static double interpoLinear(double score, double scoreMin, double scoreMax, double taxaMin, double taxaMax) {
        return taxaMax - ((taxaMax - taxaMin) * (score - scoreMin)) / (scoreMax - scoreMin);
    }

    public static BigDecimal calcTxEfetivaMes(BigDecimal valorEmprestimo, BigDecimal parcelaMensal, double taxaNominal, int qtdeParcelas) {
        BigDecimal TOLERANCIA = new BigDecimal("0.000001");
        int MAX_ITERACOES = 100;
        if (valorEmprestimo.doubleValue() <= 0 || parcelaMensal.doubleValue() < 0 || taxaNominal <= 0 || qtdeParcelas <= 1) {
            throw new IllegalArgumentException("Valores inválidos");
        }
        BigDecimal taxaEfetiva = BigDecimal.valueOf(taxaNominal / 100);
        for (int i = 0; i < MAX_ITERACOES; i++) {
            BigDecimal fr = fr(valorEmprestimo, parcelaMensal, taxaEfetiva, qtdeParcelas);
            BigDecimal frlin = frlin(parcelaMensal, taxaEfetiva, qtdeParcelas);
            if (fr.abs().compareTo(TOLERANCIA) < 0) {
                return taxaEfetiva.multiply(BigDecimal.valueOf(100)).setScale(2, HALF_UP);
            }
            taxaEfetiva = taxaEfetiva.subtract(fr.divide(frlin, DECIMAL128));
        }
        return taxaEfetiva.multiply(BigDecimal.valueOf(100)).setScale(2, HALF_UP);
    }

    private static BigDecimal fr(BigDecimal valorEmprestimo, BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);
        BigDecimal numerador = ONE.subtract(umMaisTaxaPowNeg);
        BigDecimal denominador = taxaNominal;
        BigDecimal resultado = numerador.divide(denominador, DECIMAL128);
        return (parcelaMensal.multiply(resultado)).subtract(valorEmprestimo);
    }

    private static BigDecimal frlin(BigDecimal parcelaMensal, BigDecimal taxaNominal, int qtdeParcelas) {
        BigDecimal umMaisTaxa = ONE.add(taxaNominal);
        BigDecimal umMaisTaxaPowNeg = umMaisTaxa.pow(-qtdeParcelas, DECIMAL128);
        BigDecimal umMaisTaxaPowNegMaisUm = umMaisTaxa.pow(-qtdeParcelas - 1, DECIMAL128);
        BigDecimal numerador1 = umMaisTaxaPowNeg.subtract(ONE);
        BigDecimal numerador2 = BigDecimal.valueOf(qtdeParcelas).multiply(umMaisTaxaPowNegMaisUm);
        BigDecimal denominador1 = taxaNominal.pow(2, DECIMAL128);
        BigDecimal denominador2 = taxaNominal;
        BigDecimal resultado1 = numerador1.divide(denominador1, DECIMAL128);
        BigDecimal resultado2 = numerador2.divide(denominador2, DECIMAL128);
        return (resultado1.add(resultado2)).multiply(parcelaMensal);
    }

}