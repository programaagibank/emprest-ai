package br.com.emprestai.service;

public class CalculoConsignado {

    private static final double MARGEM_CONSIGNAVEL = 0.35;
    private static final double JUROS_MINIMO_CONSIGNADO = 0.018;
    private static final double JUROS_MAXIMO_CONSIGNADO = 0.0214;
    private static final double PRAZO_MINIMO_CONSIGNADO = 24;
    private static final double PERCENTUAL_MULTA_ATRASO = 0.02;
    private static final double PERCENTUAL_JUROS_MORA = 0.00033;

    // 12.2 Margem Consignável
    public static double calcularMargemEmprestimoConsig(double rendaLiquida, double parcelasAtivas) {
        if (rendaLiquida <= 0) {
            throw new IllegalArgumentException("A renda líquida não pode ser igual ou inferior a zero");
        }
        if (parcelasAtivas < 0) {
            throw new IllegalArgumentException("As parcelas ativas não podem ser negativas");
        }
        double margemMaxima = (rendaLiquida * MARGEM_CONSIGNAVEL) - parcelasAtivas;
        if (margemMaxima < 0) {
            throw new IllegalStateException("A margem consignável não pode ser negativa");
        }
        return margemMaxima;
    }

    // 12.3 Taxa de Juros Mensal
    public static double calcularTaxaJurosMensal(double quantidadeParcelas) {
        if (quantidadeParcelas < PRAZO_MINIMO_CONSIGNADO) {
            throw new IllegalArgumentException("A quantidade de parcelas não pode ser inferior ao prazo mínimo de " + PRAZO_MINIMO_CONSIGNADO);
        }
        if (quantidadeParcelas <= 0) {
            throw new IllegalArgumentException("A quantidade de parcelas deve ser maior que zero");
        }
        double taxaJurosMensal = JUROS_MINIMO_CONSIGNADO + 0.00005 * (quantidadeParcelas - PRAZO_MINIMO_CONSIGNADO);
        if (taxaJurosMensal > JUROS_MAXIMO_CONSIGNADO) {
            return JUROS_MAXIMO_CONSIGNADO;
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
        double multa = valorParcela * PERCENTUAL_MULTA_ATRASO;
        double jurosMora = valorParcela * PERCENTUAL_JUROS_MORA * diasAtraso;
        return multa + jurosMora;
    }
}