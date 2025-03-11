package br.com.emprestai.service;

public class CalculoConsignado {

    private static final double MARGEM_CONSIGNAVEL = 0.35;
    private static final double JUROS_MINIMO_CONSIGNADO = 0.018;
    private static final double JUROS_MAXIMO_CONSIGNADO = 0.0214;
    private static final double PRAZO_MINIMO_CONSIGNADO = 24;
    private static final double PERCENTUAL_MULTA_ATRASO = 0.02;
    private static final double PERCENTUAL_JUROS_MORA = 0.00033;


    // 12.2 Margem Consignável
    public static double calculoDeMargemEmprestimoConsignavel(double rendaLiquida, double parcelasAtivas) {
        double margemMaxima = (rendaLiquida * MARGEM_CONSIGNAVEL) - parcelasAtivas;
        return margemMaxima;
    }

    // 12.3 Taxa de Juros Mensal
    public static double calculoDeTaxaJurosMensal(double quantidadeParcelas) {
        double taxaJurosMensal = JUROS_MINIMO_CONSIGNADO + 0.00005 * (quantidadeParcelas - PRAZO_MINIMO_CONSIGNADO);
        if (taxaJurosMensal > JUROS_MAXIMO_CONSIGNADO) {
            return JUROS_MAXIMO_CONSIGNADO;
        }
        return taxaJurosMensal;
    }

    // 12.9 Cálculo de Juros Mora e Multa por Atraso
    public static double calculoDeJurosMoraEMulta(double valorParcela, double diasAtraso) {
        double multa = valorParcela * PERCENTUAL_MULTA_ATRASO;
        double jurosMora = valorParcela * PERCENTUAL_JUROS_MORA * diasAtraso;
        return multa + jurosMora;
    }
}