package br.com.emprestai.service;

import static br.com.emprestai.service.CalculoConsignado.calculoDeJurosMoraEMulta;

public class CalculoPessoal {


    private static final double PERCENTUAL_RENDA_PESSOAL = 0.30;
    private static final double PERCENTUAL_MULTA_ATRASO = 0.033;
    private static final double PERCENTUAL_JUROS_MORA = 0.002;


    // 12.1 Capacidade de Pagamento
    public static double calculoDeCapacidadeDePagamento(double rendaLiquida) {
        if (rendaLiquida <= 0) {
            throw new IllegalArgumentException("O Valor da renda líquida não pode ser inferior a zero.");
        }
        double capacidadeMaxima = rendaLiquida * PERCENTUAL_RENDA_PESSOAL;
        return capacidadeMaxima;
    }


    // 12.3 Taxa de Juros Mensal
    public static double calculoTaxaDeJurosMensal(double score) {
        if (score <= 0 || score > 1000) {
            throw new IllegalArgumentException("Score fora da tolerância");
        }

        double taxaMaxima = 0.0999;
        double taxaMinima = 0.0849;


        if (score == 1000) {
            return taxaMinima;
        } else if (score >= 801 && score < 1000) {
            return interpoLinear(score, 801, 1000, 8.49, 8.99);
        } else if (score >= 601 && score <= 800) {
            return interpoLinear(score, 601, 800, 8.99, 9.49);
        } else if (score >= 401 && score <= 600) {
            return interpoLinear(score, 401, 600, 9.49, 9.99);
        }
        return taxaMaxima;
    }

    private static double interpoLinear(double score, double scoreMin, double scoreMax, double taxaMin, double taxaMax) {
        return taxaMax - ((taxaMax - taxaMin) * (score - scoreMin)) / (scoreMax - scoreMin);
    }
    // 12.9 Juros Mora e Multa por Atraso

    public static double calculoDeJurosMoraEMulta(double valorParcela,double diasAtraso) {

        if(valorParcela <= 0){
            throw new IllegalArgumentException("O valor da parcela não pode ser igual a zero");
        }
        if (diasAtraso < 0){
            throw new IllegalArgumentException("Dias de atraso precisam ser maior do que zero");
        }
        double multa = valorParcela * PERCENTUAL_MULTA_ATRASO;
        double jurosMora = valorParcela * PERCENTUAL_JUROS_MORA * diasAtraso;
        return multa + jurosMora;
    }


    }






