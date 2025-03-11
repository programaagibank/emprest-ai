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
    private static final int CARENCIA_MAXIMA = 60;

    // Empréstimo Pessoal
    private static final double RENDA_MINIMA_PESSOAL = 1000.00;
    private static final double COMPROMETIMENTO_MAXIMO_PESSOAL = 0.30;
    private static final int IDADE_MINIMA_PESSOAL = 18;
    private static final int IDADE_MAXIMA_PESSOAL = 75;
    private static final int SCORE_MINIMO_PESSOAL = 201;
    private static final int PARCELAS_MINIMAS_PESSOAL = 6;
    private static final int PARCELAS_MAXIMAS_PESSOAL = 30;

    // Empréstimo Consignado
    // 11.1.1 Margem Consignável
    public static boolean verificacaoDaMargemEmprestimoConsignado(double parcela, double rendaLiquida, double parcelasAtivas) {
        double margemDisponivel = (rendaLiquida * MARGEM_CONSIGNAVEL) - parcelasAtivas;
        return parcela <= margemDisponivel;
    }

    // 11.1.2 Idade Máxima
    public static boolean verificarIdadeClienteConsignado(int idade, int parcelas) {
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        return idadeFinal <= IDADE_MAXIMA_CONSIGNADO;
    }

    // 11.1.3 Quantidade de Parcelas
    public static boolean verificacaoQuantidadeDeParcelasDoConsignado(int parcelas) {
        return parcelas >= PARCELAS_MINIMAS_CONSIGNADO && parcelas <= PARCELAS_MAXIMAS_CONSIGNADO;
    }

    // 11.1.4 Taxa de Juros
    public static boolean verificacaoDaTaxaDeJurosEmprestimoConsignado(double juros) {
        return juros >= JUROS_MINIMO && juros <= JUROS_MAXIMO;
    }

    // 11.1.5 Tipo de Vínculo
    public static boolean verificarTipoDeVinculoParaEmprestimoConsignado(VinculoEnum vinculo) {
        return vinculo == VinculoEnum.APOSENTADO ||
                vinculo == VinculoEnum.SERVIDOR ||
                vinculo == VinculoEnum.PENSIONISTA;
    }

    // 11.1.6 Carência
    public static boolean verificacaoCarenciaEmprestimoConsignado(int dias) {
        return dias >= 0 && dias <= CARENCIA_MAXIMA;
    }

    // Empréstimo Pessoal

    // Verificar a renda do cliente para ver se está apto
    public static boolean verificarRendaMinimaPessoal(double rendaLiquida) {
        return rendaLiquida >= RENDA_MINIMA_PESSOAL;
    }

    // Análise da renda, parcela não pode ser maior que 30% da renda
    public static boolean verificarComprometimentoPessoal(double parcela, double rendaLiquida) {
        double limiteComprometimento = rendaLiquida * COMPROMETIMENTO_MAXIMO_PESSOAL;
        return parcela <= limiteComprometimento;
    }

    // Verifica a idade do cliente ao final do empréstimo pessoal para atender aos critérios
    public static boolean verificarIdadePessoal(int idade, int parcelas) {
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        return idade >= IDADE_MINIMA_PESSOAL && idadeFinal <= IDADE_MAXIMA_PESSOAL;
    }

    // Verificação do Score
    public static boolean verificarScorePessoal(int score) {
        return score >= SCORE_MINIMO_PESSOAL;
    }

    // Verificação da quantidade de parcelas para o empréstimo pessoal
    public static boolean verificarParcelasPessoal(int parcelas) {
        return parcelas >= PARCELAS_MINIMAS_PESSOAL && parcelas <= PARCELAS_MAXIMAS_PESSOAL;
    }

    // Verificação da elegibilidade para empréstimo pessoal
    public static boolean verificarElegibilidadePessoal(double rendaLiquida, double parcela, int idade, int parcelas, int score) {
        return verificarRendaMinimaPessoal(rendaLiquida) &&
                verificarComprometimentoPessoal(parcela, rendaLiquida) &&
                verificarIdadePessoal(idade, parcelas) &&
                verificarScorePessoal(score) &&
                verificarParcelasPessoal(parcelas);
    }
}