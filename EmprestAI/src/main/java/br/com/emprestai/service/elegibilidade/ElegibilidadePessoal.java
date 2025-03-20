package br.com.emprestai.service.elegibilidade;

import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.util.EmprestimoParams;

public class ElegibilidadePessoal {
        private static final EmprestimoParams params = EmprestimoParams.getInstance();

        // Métodos auxiliares ajustados (exemplos)
        public static void verificarRendaMinimaPessoal(double rendaLiquida) {
            if (rendaLiquida < 1000) throw new ValidationException("Renda líquida abaixo do mínimo de R$ 1000.");
            if (rendaLiquida < params.getRendaMinimaPessoal()) {
                throw new ValidationException("Renda líquida abaixo do mínimo definido pelo sistema.");
            }
        }

        public static void verificarComprometimentoPessoal(double parcela, double rendaLiquida) {
            if (parcela <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
            if (rendaLiquida <= 0) throw new ValidationException("Renda líquida deve ser maior que zero.");

            double limiteComprometimento = rendaLiquida * params.getPercentualRendaPessoal();
            if (parcela > limiteComprometimento) {
                throw new ValidationException("Parcela excede o limite de comprometimento da renda.");
            }
        }

        public static void verificarIdadePessoal(int idade, int parcelas) {
            if (idade < 18) throw new ValidationException("Idade mínima para empréstimo pessoal é 18 anos.");
            if (idade > params.getIdadeMaximaPessoal()) {
                throw new ValidationException("Idade excede o limite máximo para empréstimo pessoal.");
            }

            int anos = parcelas / 12;
            int idadeFinal = idade + anos;
            if (idadeFinal > params.getIdadeMaximaPessoal()) {
                throw new ValidationException("Idade final excede o limite máximo para empréstimo pessoal.");
            }
        }

        public static void verificarScorePessoal(int score) {
            if (score < 201) throw new ValidationException("Score mínimo para empréstimo pessoal é 201.");
            if (score < params.getScoreMinimoPessoal()) {
                throw new ValidationException("Score abaixo do mínimo definido pelo sistema.");
            }
        }

        public static void verificarParcelasPessoal(int parcelas, int score) {
            if (parcelas < 6) throw new ValidationException("Quantidade mínima de parcelas é 6.");
            if (score < 201) throw new ValidationException("Score mínimo para parcelamento é 201.");

            if (score <= 400 && parcelas > 12) {
                throw new ValidationException("Para score até 400, o máximo de parcelas é 12.");
            }
            if (score <= 600 && parcelas > 18) {
                throw new ValidationException("Para score até 600, o máximo de parcelas é 18.");
            }
            if (score <= 800 && parcelas > 24) {
                throw new ValidationException("Para score até 800, o máximo de parcelas é 24.");
            }
            if (parcelas > 30) {
                throw new ValidationException("Quantidade máxima de parcelas é 30.");
            }
        }

    // Verificação da elegibilidade para empréstimo pessoal
    public static void verificarElegibilidadePessoal(double rendaLiquida, double valorParcela, int idade, int parcelas, int score) {
        if (rendaLiquida <= 0) throw new ValidationException("Renda líquida deve ser maior que zero.");
        if (valorParcela <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (idade <= 0) throw new ValidationException("Idade deve ser maior que zero.");
        if (parcelas <= 0) throw new ValidationException("Quantidade de parcelas deve ser maior que zero.");
        if (score <= 0) throw new ValidationException("Score deve ser maior que zero.");

        verificarRendaMinimaPessoal(rendaLiquida);
        verificarComprometimentoPessoal(valorParcela, rendaLiquida);
        verificarIdadePessoal(idade, parcelas);
        verificarScorePessoal(score);
        verificarParcelasPessoal(parcelas, score);
    }

}
