package br.com.emprestai.service.elegibilidade;

import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.util.EmprestimoParams;

public class ElegibilidadePessoal {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Verificar Idade Máxima Final
    public static void verificarIdadePessoal(int idade, int parcelas) {
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        if (idadeFinal > params.getIdadeMaximaPessoal()) {
            throw new ValidationException("Idade final excede o limite máximo para empréstimo pessoal.");
        }
    }

    // Verificar Quantidade de Parcelas com Base no Score
    public static void verificarParcelasPessoal(int parcelas, int score) {
        if (parcelas < params.getPrazoMinimoPessoal()) {
            throw new ValidationException("Quantidade mínima de parcelas é " + params.getPrazoMinimoPessoal() + ".");
        }
        if (parcelas > params.getPrazoMaximoPessoal()) {
            throw new ValidationException("Quantidade máxima de parcelas é " + params.getPrazoMaximoPessoal() + ".");
        }
        if (score < params.getScoreMinimoPessoal()) {
            throw new ValidationException("Score mínimo para parcelamento é " + params.getScoreMinimoPessoal() + ".");
        }
        if (score <= 400 && parcelas > 12) {
            throw new ValidationException("Para score até 400, o máximo de parcelas é 12.");
        }
        if (score <= 600 && parcelas > 18) {
            throw new ValidationException("Para score até 600, o máximo de parcelas é 18.");
        }
        if (score <= 800 && parcelas > 24) {
            throw new ValidationException("Para score até 800, o máximo de parcelas é 24.");
        }
    }

    // Verificar Taxa de Juros
    public static void verificarTaxaJurosPessoal(double juros) {
        if (juros < params.getJurosMinimoPessoal() || juros > params.getJurosMaximoPessoal()) {
            throw new ValidationException("Taxa de juros fora do intervalo permitido para empréstimo pessoal.");
        }
    }

    // Verificar Valor Solicitado
    public static void verificarValorPessoal(double valorSolicitado) {
        if (valorSolicitado < params.getValorMinimoPessoal()) {
            throw new ValidationException("O valor solicitado é inferior ao mínimo de R$ " + params.getValorMinimoPessoal() + " para empréstimo pessoal.");
        }
        if (valorSolicitado > params.getValorMaximoPessoal()) {
            throw new ValidationException("O valor solicitado excede o máximo de R$ " + params.getValorMaximoPessoal() + " para empréstimo pessoal.");
        }
    }

    // Verificar Carência
    public static void verificarCarenciaPessoal(int carencia) {
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (carencia > params.getCarenciaMaximaPessoal()) {
            throw new ValidationException("Carência excede o limite máximo de " + params.getCarenciaMaximaPessoal() + " dias para empréstimo pessoal.");
        }
    }

    // Verificação da elegibilidade para empréstimo pessoal
    public static void verificarElegibilidadePessoal(double valorParcela, int idade, int parcelas, int score, double taxaJuros, int carencia, double valorSolicitado) {
        if (valorParcela <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (idade <= 0) throw new ValidationException("Idade deve ser maior que zero.");
        if (parcelas <= 0) throw new ValidationException("Quantidade de parcelas deve ser maior que zero.");
        if (score <= 0) throw new ValidationException("Score deve ser maior que zero.");
        if (taxaJuros <= 0) throw new ValidationException("Taxa de juros deve ser maior que zero.");
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (valorSolicitado <= 0) throw new ValidationException("Valor solicitado deve ser maior que zero.");

        verificarIdadePessoal(idade, parcelas);
        verificarParcelasPessoal(parcelas, score);
        verificarTaxaJurosPessoal(taxaJuros);
        verificarValorPessoal(valorSolicitado);
        verificarCarenciaPessoal(carencia);
    }
}