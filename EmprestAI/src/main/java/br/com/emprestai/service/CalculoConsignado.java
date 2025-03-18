package br.com.emprestai.service;

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
        double margemMaxima = (rendaLiquida * params.getMargemConsignavel()) - parcelasAtivas;
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
        double taxaJurosMensal = params.getJurosMinimoConsignado() + 0.00005 * (quantidadeParcelas - params.getPrazoMinimoConsignado());
        if (taxaJurosMensal > params.getJurosMaximoConsignado()) {
            return params.getJurosMaximoConsignado();
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
        double multa = valorParcela * params.getPercentualMultaAtraso();
        double jurosMora = valorParcela *  params.getPercentualJurosMora()* diasAtraso;
        return multa + jurosMora;
    }
}