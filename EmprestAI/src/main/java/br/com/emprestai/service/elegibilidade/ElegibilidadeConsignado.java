package br.com.emprestai.service.elegibilidade;

import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.EmprestimoParams;

public class ElegibilidadeConsignado {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Taxa de Juros
    public static void validarTaxaJurosEmprestimoConsig(double juros) {
        if (juros < params.getJurosMinimoConsignado() || juros > params.getJurosMaximoConsignado()) {
            throw new ValidationException("Taxa de juros fora do intervalo permitido (" +
                    params.getJurosMinimoConsignado() + " a " + params.getJurosMaximoConsignado() + "%).");
        }
    }

    // Carência
    public static void validarCarenciaEmprestimoConsig(int carencia) {
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (carencia > params.getCarenciaMaximaConsignado()) {
            throw new ValidationException("Carência excede o limite máximo de " +
                    params.getCarenciaMaximaConsignado() + " dias.");
        }
    }

    // Valor Mínimo
    public static void validarValorMinimoEmprestimo(double valorSolicitado) {
        if (valorSolicitado < params.getValorMinimoConsignado()) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") é inferior ao mínimo de R$ " + params.getValorMinimoConsignado() + ".");
        }
    }

    // Limite de Crédito
    public static void validarLimiteCreditoConsignado(Cliente cliente, double valorSolicitado) {
        double limiteCredito = cliente.getLimiteCreditoConsignado();
        if (valorSolicitado > limiteCredito) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") excede o limite de crédito consignado disponível (R$ " + limiteCredito + ").");
        }
    }

    // Idade Máxima Final
    public static void validarIdadeClienteConsig(Cliente cliente, int parcelas) {
        int idade = cliente.getIdade();
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        if (idadeFinal > params.getIdadeMaximaConsignado()) {
            throw new ValidationException("Idade final (" + idadeFinal +
                    ") excede o limite máximo para consignado (" + params.getIdadeMaximaConsignado() + " anos).");
        }
    }

    // Prazo
    public static void validarPrazoConsignado(Cliente cliente, int parcelas) {
        int prazoMaximoConsignado = cliente.getPrazoMaximoConsignado();
        if (parcelas < params.getPrazoMinimoConsignado()) {
            throw new ValidationException("Quantidade de parcelas (" + parcelas +
                    ") inferior ao mínimo de " + params.getPrazoMinimoConsignado() + ".");
        }
        if (parcelas > prazoMaximoConsignado) {
            throw new ValidationException("Quantidade de parcelas (" + parcelas +
                    ") superior ao máximo permitido para o cliente (" + prazoMaximoConsignado + ").");
        }
    }


    // Margem por Parcela
    public static void validarMargemConsignavel(Cliente cliente, double valorParcela) {
        double margemDisponivel = cliente.getMargemConsignavelDisponivel();
        if (valorParcela > margemDisponivel) {
            throw new ValidationException("O valor da parcela (R$ " + valorParcela +
                    ") excede a margem consignável disponível (R$ " + margemDisponivel + ").");
        }
    }

    // Elegibilidade do Empréstimo
    public static void validarConsignado(Cliente cliente, Emprestimo emprestimo) {
        validarTaxaJurosEmprestimoConsig(emprestimo.getTaxaJuros());
        validarCarenciaEmprestimoConsig(emprestimo.getCarencia());
        validarValorMinimoEmprestimo(emprestimo.getValorEmprestimo());
        validarLimiteCreditoConsignado(cliente, emprestimo.getValorEmprestimo());
        validarIdadeClienteConsig(cliente, emprestimo.getQuantidadeParcelas());
        validarPrazoConsignado(cliente, emprestimo.getQuantidadeParcelas());
        validarMargemConsignavel(cliente, emprestimo.getValorParcela());
    }
}