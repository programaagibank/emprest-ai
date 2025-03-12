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
    public static <parcelasAtivas> boolean verificarMargemEmprestimoConsig(double valorParcela, double rendaLiquida, double parcelasAtivas) {
        if(valorParcela <= 0){
            throw new IllegalArgumentException("Parcela não pode ser igual a zero ou negativo");
        }
        else if (rendaLiquida <= 0){
            throw new IllegalArgumentException("Renda Líquida não pode ser zero ou negativo");
        }
        else if (parcelasAtivas <= 0){
            throw new IllegalArgumentException("Parcelas ativas não podem ser menor que zero");
        }

        double margemDisponivel = (rendaLiquida * MARGEM_CONSIGNAVEL) - parcelasAtivas;
        return valorParcela <= margemDisponivel;
    }

    // 11.1.2 Idade Máxima Para Empréstimo Consignado
    public static boolean verificarIdadeClienteConsig(int idade, int parcelas) {
        if(idade < 18 || idade > 80){
            throw new IllegalArgumentException("Idade não pode ser menor que 18 anos e idade não pode ser superior a 80 anos");
        }
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        return idadeFinal <= IDADE_MAXIMA_CONSIGNADO;
    }

    // 11.1.3 Quantidade de Parcelas
    public static boolean verificarQtdeParcelasConsig(int parcelas) {
        if(parcelas < 24 || parcelas > 92){
            throw new IllegalArgumentException("Quantidade de parcelas não pode ser inferior a 24 e superior a 92 parcelas");
        }
        return parcelas >= PARCELAS_MINIMAS_CONSIGNADO && parcelas <= PARCELAS_MAXIMAS_CONSIGNADO;
    }

    // 11.1.4 Taxa de Juros
    public static boolean verificarTaxaJurosEmprestimoConsig(double juros) {
        if(juros < 0.0180 || juros > 0.0214){
            throw new IllegalArgumentException("A taxa de Juros não pode ser diferente de 0.0180 ou maior que 0.0214");
        }
        return juros >= JUROS_MINIMO && juros <= JUROS_MAXIMO;
    }

    // 11.1.5 Tipo de Vínculo
    public static boolean verificarVinculoEmprestimoConsig(VinculoEnum vinculo) {
        if(vinculo == null){
            throw new IllegalArgumentException("Tipo de vinculo não pode ser nulo ");
        }
        return vinculo == VinculoEnum.APOSENTADO ||
                vinculo == VinculoEnum.SERVIDOR ||
                vinculo == VinculoEnum.PENSIONISTA;
    }

    // 11.1.6 Carência
    public static boolean verificacarCarenciaEmprestimoConsig(int dias) {
        if(dias <= 0 || dias > 60) {
            throw new IllegalArgumentException("Carência não pode ser superior a 60 dias ou igual a zero");
        }
        return dias >= 0 && dias <= CARENCIA_MAXIMA;
    }

    // Empréstimo Pessoal

    // Verificar a renda do cliente para ver se está apto
    public static boolean verificarRendaMinimaPessoal(double rendaLiquida) {
        if(rendaLiquida < 1000){
            throw new IllegalArgumentException("A capacidade miníma de renda é de R$1000");
        }
        return rendaLiquida >= RENDA_MINIMA_PESSOAL;
    }

    // Análise da renda, parcela não pode ser maior que 30% da renda
    public static boolean verificarComprometimentoPessoal(double parcela, double rendaLiquida) {
        if(parcela < 6 || parcela > 30){
            throw new IllegalArgumentException("Quantidade de parcelas não pode ser menor que 6 ou maior que 30 meses");
        } else if (rendaLiquida <= 0.30) {
            throw  new IllegalArgumentException("O comprometimento da renda não pode ser superior a 30 % da renda");
        }

        double limiteComprometimento = rendaLiquida * COMPROMETIMENTO_MAXIMO_PESSOAL;
        return parcela <= limiteComprometimento;
    }

    // Verifica a idade do cliente ao final do empréstimo pessoal para atender aos critérios
    public static boolean verificarIdadePessoal(int idade, int parcelas) {
        if(idade < 18 || idade > 75){
            throw new IllegalArgumentException("Idade não pode ser menor que 18 anos e maior que 75 anos durante o empréstimo");
        } else if (parcelas < 6 || parcelas > 30) {
            throw new IllegalArgumentException("Parcelas não podem ser menor que 6 ou maior que 30");
        }
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        return idade >= IDADE_MINIMA_PESSOAL && idadeFinal <= IDADE_MAXIMA_PESSOAL;
    }

    // Verificação do Score
    public static boolean verificarScorePessoal(int score) {
        if(score <= 200){
            throw new IllegalArgumentException("Score não pode ser menor que 201");
        }
        return score >= SCORE_MINIMO_PESSOAL;
    }

    // Verificação da quantidade de parcelas para o empréstimo pessoal
    public static boolean verificarParcelasPessoal(int parcelas) {
        if(parcelas < 6 || parcelas > 30){
            throw new IllegalArgumentException("A quantidade de parcelas está inferior a 6 ou excede a 30 parcelas");
        }
        return parcelas >= PARCELAS_MINIMAS_PESSOAL && parcelas <= PARCELAS_MAXIMAS_PESSOAL;
    }

    // Verificação da elegibilidade para empréstimo pessoal
    public static boolean verificarElegibilidadePessoal(double rendaLiquida, double parcela, int idade, int parcelas, int score) {
        if(rendaLiquida <= 0.30){
            throw new IllegalArgumentException("O comprometimento da renda não pode exceder 30 % do valor do empréstimo");
        } else if (parcela >= rendaLiquida) {
            throw new IllegalArgumentException("O valor da Parcela não pode exceder os 30 % da renda familiar");
        } else if (idade < 18 || idade > 75) {
            throw new IllegalArgumentException("A idade não pode ser inferior a 18 ou maior que 75 anos");
        } else if (parcelas < 6 || parcelas > 30) {
            throw new IllegalArgumentException("A quantidade de parcelas não pode ser inferior a 6 ou superior a 30");
        }if( score <= 200){
            throw new IllegalArgumentException("O Score não pode ser inferior a 201");
        }
        return verificarRendaMinimaPessoal(rendaLiquida) &&
                verificarComprometimentoPessoal(parcela, rendaLiquida) &&
                verificarIdadePessoal(idade, parcelas) &&
                verificarScorePessoal(score) &&
                verificarParcelasPessoal(parcelas);
    }
}