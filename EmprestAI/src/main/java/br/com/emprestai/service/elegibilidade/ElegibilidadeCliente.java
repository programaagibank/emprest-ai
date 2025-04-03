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
        int idade = cliente.getIdade();

        // Verificações básicas
        if (cliente.getVencimentoConsignavelTotal() <= 0) {
            throw new ElegibilidadeException("Vencimento consignável total é zero ou negativo.");
        }
        if (idade <= 0) {
            throw new ElegibilidadeException("Idade inválida (data de nascimento não informada ou inválida).");
        }

        // Verifica margem consignável
        if (cliente.getMargemConsignavelDisponivel() <= 0) {
            throw new ElegibilidadeException("Margem consignável disponível insuficiente.");
        }

        // Verifica idade mínima e máxima
        if (idade < 18) {
            throw new ElegibilidadeException("Idade inferior ao mínimo exigido (18 anos).");
        }
        if (idade > 80) {
            throw new ElegibilidadeException("Idade superior ao máximo permitido (80 anos).");
        }

        // Verifica prazo máximo
        if (cliente.getPrazoMaximoConsignado() < params.getPrazoMinimoConsignado()) {
            throw new ElegibilidadeException(String.format(
                    "Prazo máximo consignado possível de contratar inferior ao mínimo exigido, verifique se sua idade máxima com as parcelas não ultrapassa o limite máximo %s.",
                    params.getIdadeMaximaConsignado()));
        }

        return true;
    }

    public boolean isElegivelPessoal() throws ElegibilidadeException {
        int idade = cliente.getIdade();

        // Verificações básicas
        if (cliente.getVencimentoLiquidoTotal() <= 0) {
            throw new ElegibilidadeException("Vencimento líquido total é zero ou negativo.");
        }
        if (cliente.getScore() <= 0) {
            throw new ElegibilidadeException("Score inválido (zero ou negativo).");
        }
        if (idade <= 0) {
            throw new ElegibilidadeException("Idade inválida (data de nascimento não informada ou inválida).");
        }

        // Verifica margem pessoal
        if (cliente.getMargemPessoalDisponivel() <= 0) {
            throw new ElegibilidadeException("Margem pessoal disponível insuficiente.");
        }

        // Verifica renda mínima
        if (cliente.getVencimentoLiquidoTotal() < params.getRendaMinimaPessoal()) {
            throw new ElegibilidadeException("Renda líquida inferior ao mínimo exigido.");
        }

        // Verifica idade mínima e máxima
        if (idade < 18) {
            throw new ElegibilidadeException("Idade inferior ao mínimo exigido (18 anos).");
        }
        if (idade > params.getIdadeMaximaPessoal()) {
            throw new ElegibilidadeException("Idade superior ao máximo permitido para empréstimo pessoal.");
        }

        // Verifica prazo máximo
        if (cliente.getPrazoMaximoPessoal() < params.getPrazoMinimoPessoal()) {
            throw new ElegibilidadeException(String.format(
                    "Prazo máximo pessoal possível de contratar inferior ao mínimo exigido, verifique se sua idade máxima com as parcelas não ultrapassa o limite máximo %s.",
                    params.getIdadeMaximaPessoal()));
        }

        // Verifica score mínimo
        if (cliente.getScore() < params.getScoreMinimoPessoal()) {
            throw new ElegibilidadeException("Score inferior ao mínimo exigido.");
        }

        return true;
    }
}