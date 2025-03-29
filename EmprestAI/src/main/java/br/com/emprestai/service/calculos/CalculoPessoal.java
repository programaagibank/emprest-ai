package br.com.emprestai.service.calculos;

import br.com.emprestai.util.EmprestimoParams;

public class CalculoPessoal {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    public static double calculoDeCapacidadeDePagamento(double rendaLiquida, double parcelasAtivas) {
        if (rendaLiquida <= 0) {
            throw new IllegalArgumentException("O Valor da renda líquida não pode ser inferior a zero.");
        }

        double capacidadeMaxima = rendaLiquida * (params.getPercentualRendaPessoal() / 100) - parcelasAtivas;
        return capacidadeMaxima;
    }

    public static double calculoTaxaDeJurosMensal(double score) {
        if (score <= 0 || score > 1000) {
            throw new IllegalArgumentException("Score fora da tolerância");
        }
        double taxaMaxima = 9.99;
        double taxaMinima = 8.49;
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
}