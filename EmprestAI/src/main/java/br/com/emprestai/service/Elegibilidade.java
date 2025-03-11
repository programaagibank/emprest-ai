package br.com.emprestai.service;

public class Elegibilidade {
    //Empréstimo Consignado - Elegibilidade
    private static final double MARGEM_CONSIGNAVEL = 0.35;
    private static final int IDADE_MAXIMA_CONSIGNADO = 80;

    // 11.1.1 Margem Consignável
    public static boolean verificacaoDaMargemDoConsignavel(double parcela, double remuneracaoLiquida, double somaParcelasAtivas) {
        double margemDisponivel = (remuneracaoLiquida * MARGEM_CONSIGNAVEL) - somaParcelasAtivas;
        return parcela <= margemDisponivel;
    }

    // 11.1.2 Idade Máxima
    public static boolean verificacaoDaIdadeMaxima(int idadeAtual, int quantidadeParcelas) {
        int anosContrato = quantidadeParcelas / 12;
        int idadeFinal = idadeAtual + anosContrato;
        return idadeFinal <= IDADE_MAXIMA_CONSIGNADO;
    }

    // 11.1.3 Quantidade de Parcelas

    // 11.1.4 Taxa de Juros


    // 11.1.5 Tipo de Vínculo

    // 11.1.6 Carência
}

