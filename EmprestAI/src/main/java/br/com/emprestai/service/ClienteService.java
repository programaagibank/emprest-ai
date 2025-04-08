package br.com.emprestai.service;

import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.service.calculos.CalculadoraContrato;
import br.com.emprestai.service.calculos.CalculadoraCustosAdicionais;
import br.com.emprestai.service.calculos.CalculadoraParcela;
import br.com.emprestai.service.calculos.CalculoTaxaJuros;
import br.com.emprestai.util.EmprestimoParams;

import java.math.BigDecimal;
import java.rmi.server.ExportException;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmprestimoEnum.PESSOAL;

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
        if (cliente.getScore() < params.getScoreMinimoPessoal() || cliente.getPrazoMaximoConsignado() <= params.getPrazoMinimoConsignado()) {
            return 0.0; // Cliente não elegível
        }
        double margem = cliente.getVencimentoConsignavelTotal() * params.getMargemConsignavel() / 100;
        return Math.max(0, margem - cliente.getValorParcelasMensaisConsignado());
    }

    public static double calcularMargemPessoalDisponivel(Cliente cliente) {
        if (cliente.getScore() < params.getScoreMinimoPessoal() || cliente.getPrazoMaximoPessoal() <= params.getPrazoMinimoPessoal()) {
            return 0.0; // Cliente não elegível
        }
        double margem = cliente.getVencimentoLiquidoTotal() * params.getPercentualRendaPessoal() / 100;
        return Math.max(0, margem - cliente.getValorParcelasMensaisTotal())*getPercentualScore(cliente.getScore());
    }

    public static double calcularLimiteCreditoPessoal(Cliente cliente) {
        double valorTotalFinanciado = 0;
        try{
            double taxaJuros = CalculoTaxaJuros.calcularTaxaJurosMensal(cliente.getScore(),cliente.getPrazoMaximoPessoal(), PESSOAL);

            valorTotalFinanciado = CalculadoraContrato.calcularValorTotalFinanciado(
                    cliente.getMargemPessoalDisponivel(),
                    taxaJuros,
                    cliente.getPrazoMaximoPessoal());

            return CalculadoraCustosAdicionais.reverterValorEmprestimo(
                    valorTotalFinanciado,
                    cliente.getIdade(),
                    cliente.getPrazoMaximoPessoal(),
                    taxaJuros,
                    params.getCarenciaMaximaPessoal(),
                    true);

        } catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static double calcularLimiteCreditoConsignado(Cliente cliente) {
        System.out.println("Prazo Máximo: " + cliente.getPrazoMaximoConsignado());
        double valorTotalFinanciado = 0;
        double valorTotalRevertido = 0;
        try{
            double taxaJuros = CalculoTaxaJuros.calcularTaxaJurosMensal(cliente.getScore(),cliente.getPrazoMaximoConsignado(), CONSIGNADO);
            System.out.println("Taxa Juros: " + taxaJuros);
            valorTotalFinanciado = CalculadoraContrato.calcularValorTotalFinanciado(
                    cliente.getMargemConsignavelDisponivel(),
                    taxaJuros,
                    cliente.getPrazoMaximoConsignado());

            valorTotalRevertido = CalculadoraCustosAdicionais.reverterValorEmprestimo(
                    valorTotalFinanciado,
                    cliente.getIdade(),
                    cliente.getPrazoMaximoConsignado(),
                    taxaJuros,
                    params.getCarenciaMaximaConsignado(),
                    true);
            System.out.println("Margem Consignável: " + cliente.getMargemConsignavelDisponivel());
            System.out.println("Valor Total Financiado: " + valorTotalFinanciado);
            System.out.println("Valor Revertido: " + valorTotalRevertido);
            return valorTotalRevertido;

        } catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }

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

