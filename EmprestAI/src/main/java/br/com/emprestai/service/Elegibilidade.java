package br.com.emprestai.service;

import br.com.emprestai.enums.VinculoEnum;

public class Elegibilidade {
    // Empréstimo Consignado
    private static final double MARGEM_CONSIGNAVEL = 0.35;
    private static final int IDADE_MAXIMA_CONSIGNADO = 80;
    private static final int PARCELAS_MINIMAS_CONSIGNADO = 24;
    private static final int PARCELAS_MAXIMAS_CONSIGNADO = 92;
    private static final double JUROS_MINIMO = 0.0180;
    private static final double JUROS_MAXIMO = 0.0214;

    // Empréstimo Pessoal
    private static final double RENDA_MINIMA_PESSOAL = 1000.00;
    private static final double COMPROMETIMENTO_MAXIMO_PESSOAL = 0.30;
    private static final int IDADE_MINIMA_PESSOAL = 18;
    private static final int IDADE_MAXIMA_PESSOAL = 75;
    private static final int SCORE_MINIMO_PESSOAL = 201;


    // 11.1.1 Margem Consignável
    public static boolean verificarMargemEmprestimoConsig(double valorParcela, double rendaLiquida, double parcelasAtivas) {
        if (valorParcela <= 0 || rendaLiquida <= 0 || parcelasAtivas < 0) return false;
        double margemDisponivel = (rendaLiquida * MARGEM_CONSIGNAVEL) - parcelasAtivas;
        return valorParcela <= margemDisponivel;
    }

    // 11.1.2 Idade Máxima Para Empréstimo Consignado
    public static boolean verificarIdadeClienteConsig(int idade, int parcelas) {
        if (idade < 18 || idade > 80) return false;
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        return idadeFinal <= IDADE_MAXIMA_CONSIGNADO;
    }

    // 11.1.3 Quantidade de Parcelas
    public static boolean verificarQtdeParcelasConsig(int qtdeparcelas) {
        if (qtdeparcelas < 24 || qtdeparcelas > 92) return false;
        return qtdeparcelas >= PARCELAS_MINIMAS_CONSIGNADO && qtdeparcelas <= PARCELAS_MAXIMAS_CONSIGNADO;
    }

    // 11.1.4 Taxa de Juros
    public static boolean verificarTaxaJurosEmprestimoConsig(double juros) {
        if (juros < 0.0180 || juros > 0.0214) return false;
        return juros >= JUROS_MINIMO && juros <= JUROS_MAXIMO;
    }

    // 11.1.5 Tipo de Vínculo
    public static boolean verificarVinculoEmprestimoConsig(VinculoEnum vinculo) {
        if (vinculo == null) return false;
        return vinculo == VinculoEnum.APOSENTADO || vinculo == VinculoEnum.SERVIDOR || vinculo == VinculoEnum.PENSIONISTA;
    }

    // 11.1.6 Carência
    public static boolean verificarCarenciaEmprestimoConsig(int dias) {
        if (dias < 0 || dias > 60) return false;
        return true;
    }

    // 11.2.1 Idade Máxima (Pessoal)
    public static boolean verificarIdadePessoal(int idade, int parcelas) {
        if (idade < 18 || idade > 75) return false;
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        return idade >= IDADE_MINIMA_PESSOAL && idadeFinal <= IDADE_MAXIMA_PESSOAL;
    }

    // 11.2.2 Valor do Empréstimo
    public static boolean verificarValorEmprestimoPessoal(double valor, int score) {
        if (valor < 100 || score < 201) return false;
        if (score <= 400) return valor <= 1000;
        if (score <= 600) return valor <= 5000;
        if (score <= 800) return valor <= 15000;
        return valor <= 20000;
    }

    // 11.2.3 Quantidade de Parcelas
    public static boolean verificarParcelasPessoal(int parcelas, int score) {
        if (parcelas < 6 || score < 201) return false;
        if (score <= 400) return parcelas <= 12;
        if (score <= 600) return parcelas <= 18;
        if (score <= 800) return parcelas <= 24;
        return parcelas <= 30;
    }

    // 11.2.4 Taxa de Juros
    public static boolean verificarTaxaJurosPessoal(double taxa, int score) {
        if (taxa < 0.0849 || taxa > 0.0999 || score < 201) return false;
        if (score <= 400) return taxa == 0.0999;
        if (score <= 600) return taxa >= 0.0949 && taxa <= 0.0999;
        if (score <= 800) return taxa >= 0.0899 && taxa <= 0.0949;
        return taxa >= 0.0849 && taxa <= 0.0899;
    }

    // 11.2.5 Score
    public static boolean verificarScorePessoal(int score) {
        if (score < 201) return false;
        return score >= SCORE_MINIMO_PESSOAL;
    }

    // 11.2.6 Capacidade de Pagamento
    public static boolean verificarComprometimentoPessoal(double parcela, double rendaLiquida) {
        if (parcela <= 0 || rendaLiquida <= 0) return false;
        double limiteComprometimento = rendaLiquida * COMPROMETIMENTO_MAXIMO_PESSOAL;
        return parcela <= limiteComprometimento;
    }

    // 11.2.7 Carência
    public static boolean verificarCarenciaPessoal(int dias) {
        if (dias < 0 || dias > 30) return false;
        return true;
    }

    // 11.3.1 Percentual Mínimo Pago
    public static boolean verificarPercentualMinimoPago(int parcelasPagas, int totalParcelas) {
        if (parcelasPagas < 0 || totalParcelas <= 0 || parcelasPagas > totalParcelas) return false;
        return (double) parcelasPagas / totalParcelas >= 0.20;
    }

    // 11.4.1/11.4.2 Portabilidade
    public static boolean verificarContratoSemAtrasos(boolean ativo, boolean temAtrasos) {
        return ativo && !temAtrasos;
    }

    // Calculo mínimo de renda
    public static boolean verificarRendaMinimaPessoal(double rendaLiquida) {
        if (rendaLiquida < 1000) return false;
        return rendaLiquida >= RENDA_MINIMA_PESSOAL;
    }

    // Verificação da elegibilidade para empréstimo pessoal
    public static boolean verificarElegibilidadePessoal(double rendaLiquida, double parcela, int idade, int parcelas, int score) {
        if (rendaLiquida <= 0 || parcela <= 0) return false;
        return verificarRendaMinimaPessoal(rendaLiquida) &&
                verificarComprometimentoPessoal(parcela, rendaLiquida) &&
                verificarIdadePessoal(idade, parcelas) &&
                verificarScorePessoal(score) &&
                verificarParcelasPessoal(parcelas, score);
    }

    // Verificação da elegibilidade para empréstimo consignado
    public static boolean verificarElegibilidadeConsignado(double rendaLiquida, double valorParcela, double parcelasAtivas,
                                                           int idade, int parcelas, double taxaJuros, VinculoEnum vinculo, int carencia) {
        if (rendaLiquida <= 0 || valorParcela <= 0 || parcelasAtivas < 0 || idade <= 0 || parcelas <= 0 || taxaJuros <= 0 || carencia < 0) return false;
        return verificarMargemEmprestimoConsig(valorParcela, rendaLiquida, parcelasAtivas) &&
                verificarIdadeClienteConsig(idade, parcelas) &&
                verificarQtdeParcelasConsig(parcelas) &&
                verificarTaxaJurosEmprestimoConsig(taxaJuros) &&
                verificarVinculoEmprestimoConsig(vinculo) &&
                verificarCarenciaEmprestimoConsig(carencia);
    }
}