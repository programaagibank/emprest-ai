package br.com.emprestai.service.calculos;

import br.com.emprestai.util.EmprestimoParams;

public class CalculoConsignado {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // 12.2 Margem Consignável
    public static double calcularMargemEmprestimoConsig(double rendaLiquida, double parcelasAtivas) {
        if (rendaLiquida <= 0) {
            throw new IllegalArgumentException("A renda líquida não pode ser igual ou inferior a zero");
        }
        if (parcelasAtivas < 0) {
            throw new IllegalArgumentException("As parcelas ativas não podem ser negativas");
        }
        // Converte margem consignável de porcentagem (ex.: 35) para decimal (0.35)
        double margemMaxima = (rendaLiquida * (params.getMargemConsignavel() / 100)) - parcelasAtivas;
        if (margemMaxima < 0) {
            throw new IllegalStateException("A margem consignável não pode ser negativa");
        }
        return margemMaxima;
    }

    // 12.3 Taxa de Juros Mensal
    public static double calcularTaxaJurosMensal(double quantidadeParcelas) {
        if (quantidadeParcelas <= 0) {
            throw new IllegalArgumentException("A quantidade de parcelas deve ser maior que zero");
        }
        if (quantidadeParcelas < params.getPrazoMinimoConsignado()) {
            throw new IllegalArgumentException("A quantidade de parcelas não pode ser inferior ao prazo mínimo de " + params.getPrazoMinimoConsignado());
        }
        // Ajusta a taxa para atingir 0.0186 com 36 parcelas
        double jurosMinimoDecimal = params.getJurosMinimoConsignado() / 100; // Ex.: 1.8% -> 0.018
        double incrementoPorParcela = (0.0186 - jurosMinimoDecimal) / (36 - params.getPrazoMinimoConsignado()); // Calcula o incremento
        double taxaJurosMensal = jurosMinimoDecimal + incrementoPorParcela * (quantidadeParcelas - params.getPrazoMinimoConsignado());
        double jurosMaximoDecimal = params.getJurosMaximoConsignado() / 100;
        if (taxaJurosMensal > jurosMaximoDecimal) {
            return jurosMaximoDecimal;
        }
        return taxaJurosMensal;
    }

    // 12.9 Cálculo de Juros Mora e Multa por Atraso
    public static double calcularJurosMoraEMulta(double valorParcela, double diasAtraso) {
        if (valorParcela <= 0) {
            throw new IllegalArgumentException("O valor da parcela deve ser maior que zero");
        }
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("Os dias de atraso não podem ser negativos");
        }
        // Converte percentuais de multa e juros mora para decimal
        double multa = valorParcela * (params.getPercentualMultaAtraso() / 100); // Ex.: 2% -> 0.02
        double jurosMora = valorParcela * (params.getPercentualJurosMora() / 100) * diasAtraso; // Ex.: 0.033% -> 0.00033
        return multa + jurosMora;
    }
}