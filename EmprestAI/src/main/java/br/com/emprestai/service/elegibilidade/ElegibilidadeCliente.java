package br.com.emprestai.service.elegibilidade;

import br.com.emprestai.exception.ElegibilidadeException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.EmprestimoParams;

public class ElegibilidadeCliente {
    private final Cliente cliente;
    private final EmprestimoParams params = EmprestimoParams.getInstance();

    public ElegibilidadeCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isElegivelConsignado() throws ElegibilidadeException {
        // Verificação inicial de dados básicos
        if (cliente == null) {
            throw new ElegibilidadeException("Cliente não informado.");
        }

        int idade = cliente.getIdade();
        if (idade <= 0) {
            throw new ElegibilidadeException("Idade inválida (data de nascimento não informada ou inválida).");
        }

        // Verifica idade mínima e máxima
        if (idade < params.getIdadeMinimaConsignado()) {
            throw new ElegibilidadeException(String.format(
                    "Idade inferior ao mínimo exigido para consignado (%s anos).", params.getIdadeMinimaConsignado()));
        }
        if (idade > params.getIdadeMaximaConsignado()) {
            throw new ElegibilidadeException(String.format(
                    "Idade superior ao máximo permitido para consignado (%s anos).", params.getIdadeMaximaConsignado()));
        }

        // Verifica renda consignável
        if (cliente.getVencimentoConsignavelTotal() <= 0) {
            throw new ElegibilidadeException("Vencimento consignável total é zero ou negativo.");
        }

        // Verifica margem consignável disponível
        double margemConsignavel = cliente.getMargemConsignavelDisponivel();
        if (margemConsignavel <= 0) {
            throw new ElegibilidadeException("Margem consignável disponível insuficiente.");
        }

        // Verifica limite de crédito consignado
        double limiteCreditoConsignado = cliente.getLimiteCreditoConsignado();
        if (limiteCreditoConsignado < params.getValorMinimoConsignado()) {
            throw new ElegibilidadeException(String.format(
                    "Limite de crédito consignado (%s) inferior ao valor mínimo exigido (%s).",
                    limiteCreditoConsignado, params.getValorMinimoConsignado()));
        }

        // Verifica score mínimo
        if (cliente.getScore() < params.getScoreMinimoConsignado()) {
            throw new ElegibilidadeException(String.format(
                    "Score (%s) inferior ao mínimo exigido para consignado (%s).",
                    cliente.getScore(), params.getScoreMinimoConsignado()));
        }

        // Verifica prazo máximo
        int prazoMaximoConsignado = cliente.getPrazoMaximoConsignado();
        if (prazoMaximoConsignado < params.getPrazoMinimoConsignado()) {
            throw new ElegibilidadeException(String.format(
                    "Prazo máximo consignado (%s meses) inferior ao mínimo exigido (%s meses).",
                    prazoMaximoConsignado, params.getPrazoMinimoConsignado()));
        }

        return true;
    }

    public boolean isElegivelPessoal() throws ElegibilidadeException {
        // Verificação inicial de dados básicos
        if (cliente == null) {
            throw new ElegibilidadeException("Cliente não informado.");
        }

        int idade = cliente.getIdade();
        if (idade <= 0) {
            throw new ElegibilidadeException("Idade inválida (data de nascimento não informada ou inválida).");
        }

        // Verifica idade mínima e máxima
        if (idade < params.getIdadeMinimaPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Idade inferior ao mínimo exigido para pessoal (%s anos).", params.getIdadeMinimaPessoal()));
        }
        if (idade > params.getIdadeMaximaPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Idade superior ao máximo permitido para pessoal (%s anos).", params.getIdadeMaximaPessoal()));
        }

        // Verifica renda líquida
        if (cliente.getVencimentoLiquidoTotal() <= 0) {
            throw new ElegibilidadeException("Vencimento líquido total é zero ou negativo.");
        }
        if (cliente.getVencimentoLiquidoTotal() < params.getRendaMinimaPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Renda líquida (%s) inferior ao mínimo exigido (%s).",
                    cliente.getVencimentoLiquidoTotal(), params.getRendaMinimaPessoal()));
        }

        // Verifica margem pessoal disponível
        double margemPessoal = cliente.getMargemPessoalDisponivel();
        if (margemPessoal <= 0) {
            throw new ElegibilidadeException("Margem pessoal disponível insuficiente.");
        }

        // Verifica limite de crédito pessoal
        double limiteCreditoPessoal = cliente.getLimiteCreditoPessoal();
        if (limiteCreditoPessoal < params.getValorMinimoPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Limite de crédito pessoal (%s) inferior ao valor mínimo exigido (%s).",
                    limiteCreditoPessoal, params.getValorMinimoPessoal()));
        }

        // Verifica score mínimo
        if (cliente.getScore() <= 0) {
            throw new ElegibilidadeException("Score inválido (zero ou negativo).");
        }
        if (cliente.getScore() < params.getScoreMinimoPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Score (%s) inferior ao mínimo exigido para pessoal (%s).",
                    cliente.getScore(), params.getScoreMinimoPessoal()));
        }

        // Verifica prazo máximo
        int prazoMaximoPessoal = cliente.getPrazoMaximoPessoal();
        if (prazoMaximoPessoal < params.getPrazoMinimoPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Prazo máximo pessoal (%s meses) inferior ao mínimo exigido (%s meses).",
                    prazoMaximoPessoal, params.getPrazoMinimoPessoal()));
        }

        return true;
    }
}