package br.com.emprestai.service.validator;

import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.ClienteService;
import br.com.emprestai.util.EmprestimoParams;

public class ValidatorPessoal {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Idade Máxima Final
    public static void validarIdadePessoal(Cliente cliente, int parcelas) {
        int idade = cliente.getIdade();
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        if (idadeFinal > params.getIdadeMaximaPessoal()) {
            throw new ValidationException("Idade final (" + idadeFinal +
                    ") excede o limite máximo para pessoal (" + params.getIdadeMaximaPessoal() + " anos).");
        }
    }

    // Parcelas
    public static void validarParcelasPessoal(Cliente cliente, int parcelas) {
        int prazoMaximoPessoal = ClienteService.calcularPrazoMaximoPessoal(cliente);
        if (parcelas < params.getPrazoMinimoPessoal()) {
            throw new ValidationException("Quantidade de parcelas (" + parcelas +
                    ") inferior ao mínimo de " + params.getPrazoMinimoPessoal() + ".");
        }
        if (parcelas > prazoMaximoPessoal) {
            throw new ValidationException("Quantidade de parcelas (" + parcelas +
                    ") superior ao máximo permitido para o cliente (" + prazoMaximoPessoal + ").");
        }
    }

    // Taxa de Juros
    public static void validarTaxaJurosPessoal(double juros) {
        if (juros < params.getJurosMinimoPessoal() || juros > params.getJurosMaximoPessoal()) {
            throw new ValidationException("Taxa de juros fora do intervalo permitido (" +
                    params.getJurosMinimoPessoal() + " a " + params.getJurosMaximoPessoal() + "%).");
        }
    }

    // Valor Solicitado
    public static void validarValorPessoal(double valorSolicitado) {
        if (valorSolicitado < params.getValorMinimoPessoal()) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") é inferior ao mínimo de R$ " + params.getValorMinimoPessoal() + ".");
        }
        if (valorSolicitado > params.getValorMaximoPessoal()) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") excede o máximo global de R$ " + params.getValorMaximoPessoal() + ".");
        }
    }

    // Limite de Crédito
    public static void validarLimiteCreditoPessoal(Cliente cliente, double valorSolicitado) {
        double limiteCredito = ClienteService.calcularLimiteCreditoPessoal(cliente);
        if (valorSolicitado > limiteCredito) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") excede o limite de crédito pessoal disponível (R$ " + limiteCredito + ").");
        }
    }

    // Carência
    public static void validarCarenciaPessoal(int carencia) {
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (carencia > params.getCarenciaMaximaPessoal()) {
            throw new ValidationException("Carência excede o limite máximo de " +
                    params.getCarenciaMaximaPessoal() + " dias.");
        }
    }

    // Margem por Parcela
    public static void validarMargemPessoal(Cliente cliente, double valorParcela) {
        double margemDisponivel = ClienteService.calcularMargemPessoalDisponivel(cliente);
        if (valorParcela > margemDisponivel) {
            throw new ValidationException("O valor da parcela (R$ " + valorParcela +
                    ") excede a margem pessoal disponível (R$ " + margemDisponivel + ").");
        }
    }


    // Elegibilidade do Empréstimo
    public static void validarPessoal(Cliente cliente, Emprestimo emprestimo) {
        if (emprestimo.getValorParcela() <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (cliente.getIdade() <= 0) throw new ValidationException("Idade deve ser maior que zero.");
        if (emprestimo.getQuantidadeParcelas() <= 0) throw new ValidationException("Quantidade de parcelas deve ser maior que zero.");
        if (emprestimo.getTaxaJuros() <= 0) throw new ValidationException("Taxa de juros deve ser maior que zero.");
        if (emprestimo.getCarencia() < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (emprestimo.getValorEmprestimo() <= 0) throw new ValidationException("Valor solicitado deve ser maior que zero.");

        validarIdadePessoal(cliente, emprestimo.getQuantidadeParcelas());
        validarParcelasPessoal(cliente, emprestimo.getQuantidadeParcelas());
        validarTaxaJurosPessoal(emprestimo.getTaxaJuros());
        validarValorPessoal(emprestimo.getValorEmprestimo());
        validarCarenciaPessoal(emprestimo.getCarencia());
        validarLimiteCreditoPessoal(cliente, emprestimo.getValorEmprestimo());
        validarMargemPessoal(cliente, emprestimo.getValorParcela());
    }
}