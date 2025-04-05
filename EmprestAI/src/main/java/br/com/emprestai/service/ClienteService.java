package br.com.emprestai.service;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.EmprestimoParams;

public class ClienteService {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    public static int calcularPrazoMaximoPessoal(Cliente cliente) {
        int idade = cliente.getIdade();
        int score = cliente.getScore();

        if (idade <= 0 || score < params.getScoreMinimoPessoal()) {
            return 0;
        }

        int prazoPorIdade = (int) (Math.max(0, params.getIdadeMaximaPessoal() - idade) * 12);
        int prazoPorScore = getPrazoMaximoPessoalPorScore(score);

        return Math.min(Math.min(prazoPorIdade, params.getPrazoMaximoPessoal()), prazoPorScore);
    }

    public static int calcularPrazoMaximoConsignado(Cliente cliente) {
        int idade = cliente.getIdade();
        int score = cliente.getScore();

        if (idade <= 0 || score < params.getScoreMinimoPessoal()) {
            return 0;
        }

        int prazoPorIdade = (int) (Math.max(0, params.getIdadeMaximaConsignado() - idade) * 12);
        int prazoPorScore = getPrazoMaximoConsignadoPorScore(score);

        return Math.min(Math.min(prazoPorIdade, params.getPrazoMaximoConsignado()), prazoPorScore);
    }

    public static double calcularMargemConsignavelDisponivel(Cliente cliente) {
        double margem = cliente.getVencimentoConsignavelTotal() * params.getMargemConsignavel() / 100;
        return Math.max(0, margem - cliente.getValorParcelasMensaisConsignado());
    }

    public static double calcularMargemPessoalDisponivel(Cliente cliente) {
        double margem = cliente.getVencimentoLiquidoTotal() * params.getPercentualRendaPessoal() / 100;
        return Math.max(0, margem - cliente.getValorParcelasMensaisTotal());
    }

    public static double calcularLimiteCreditoPessoal(Cliente cliente) {
        return calcularMargemPessoalDisponivel(cliente) * getPercentualScore(cliente.getScore())*getPrazoMaximoPessoalPorScore(cliente.getScore());
    }

    public static double calcularLimiteCreditoConsignado(Cliente cliente) {
        return calcularMargemConsignavelDisponivel(cliente) *getPrazoMaximoConsignadoPorScore(cliente.getScore());
    }

    private static double getPercentualScore(int score) {
        if (score <= 200) return 0.0;   // 0% para scores muito baixos
        if (score <= 300) return 0.10;  // 10%
        if (score <= 400) return 0.25;  // 25%
        if (score <= 500) return 0.40;  // 40%
        if (score <= 600) return 0.55;  // 55%
        if (score <= 700) return 0.70;  // 70%
        if (score <= 800) return 0.85;  // 85%
        return 1.00;                    // 100% para scores acima de 900
    }

    private static int getPrazoMaximoPessoalPorScore(int score) {
        if (score < params.getScoreMinimoPessoal()) return 0;
        if (score <= 300) return 6;
        if (score <= 400) return 10;
        if (score <= 500) return 14;
        if (score <= 600) return 18;
        if (score <= 700) return 22;
        if (score <= 800) return 26;
        return params.getPrazoMaximoPessoal();
    }

    private static int getPrazoMaximoConsignadoPorScore(int score) {
        if (score < params.getScoreMinimoPessoal()) return 0;
        if (score <= 300) return 12;
        if (score <= 400) return 24;
        if (score <= 500) return 36;
        if (score <= 600) return 48;
        if (score <= 700) return 60;
        if (score <= 800) return 72;
        return params.getPrazoMaximoConsignado();
    }
}

