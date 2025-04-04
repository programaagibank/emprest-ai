package br.com.emprestai.service.validator;

import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.service.ClienteService;
import br.com.emprestai.util.EmprestimoParams;

public class ValidatorConsignado {
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Taxa de Juros
    public static void verificarTaxaJurosEmprestimoConsig(double juros) {
        if (juros < params.getJurosMinimoConsignado() || juros > params.getJurosMaximoConsignado()) {
            throw new ValidationException("Taxa de juros fora do intervalo permitido (" +
                    params.getJurosMinimoConsignado() + " a " + params.getJurosMaximoConsignado() + "%).");
        }
    }

    // Carência
    public static void verificarCarenciaEmprestimoConsig(int carencia) {
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (carencia > params.getCarenciaMaximaConsignado()) {
            throw new ValidationException("Carência excede o limite máximo de " +
                    params.getCarenciaMaximaConsignado() + " dias.");
        }
    }

    // Valor Mínimo
    public static void verificarValorMinimoEmprestimo(double valorSolicitado) {
        if (valorSolicitado < params.getValorMinimoConsignado()) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") é inferior ao mínimo de R$ " + params.getValorMinimoConsignado() + ".");
        }
    }

    // Limite de Crédito
    public static void verificarLimiteCreditoConsignado(Cliente cliente, double valorSolicitado) {
        double limiteCredito = ClienteService.calcularLimiteCreditoConsignado(cliente);
        if (valorSolicitado > limiteCredito) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado +
                    ") excede o limite de crédito consignado disponível (R$ " + limiteCredito + ").");
        }
    }

    // Idade Máxima Final
    public static void verificarIdadeClienteConsig(Cliente cliente, int parcelas) {
        int idade = cliente.getIdade();
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        if (idadeFinal > params.getIdadeMaximaConsignado()) {
            throw new ValidationException("Idade final (" + idadeFinal +
                    ") excede o limite máximo para consignado (" + params.getIdadeMaximaConsignado() + " anos).");
        }
    }

    // Prazo
    public static void verificarPrazoConsignado(Cliente cliente, int parcelas) {
        int prazoMaximoConsignado = ClienteService.calcularPrazoMaximoConsignado(cliente);
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
    public static void verificarMargemConsignavel(Cliente cliente, double valorParcela) {
        double margemDisponivel = ClienteService.calcularMargemConsignavelDisponivel(cliente);
        if (valorParcela > margemDisponivel) {
            throw new ValidationException("O valor da parcela (R$ " + valorParcela +
                    ") excede a margem consignável disponível (R$ " + margemDisponivel + ").");
        }
    }

    // Elegibilidade do Empréstimo
    public static void verificarElegibilidadeConsignado(Cliente cliente, Emprestimo emprestimo) {
        if (emprestimo.getValorParcela() <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (cliente.getIdade() <= 0) throw new ValidationException("Idade deve ser maior que zero.");
        if (emprestimo.getQuantidadeParcelas() <= 0) throw new ValidationException("Quantidade de parcelas deve ser maior que zero.");
        if (emprestimo.getTaxaJuros() <= 0) throw new ValidationException("Taxa de juros deve ser maior que zero.");
        if (emprestimo.getCarencia() < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (emprestimo.getValorEmprestimo() <= 0) throw new ValidationException("Valor solicitado deve ser maior que zero.");

        verificarTaxaJurosEmprestimoConsig(emprestimo.getTaxaJuros());
        verificarCarenciaEmprestimoConsig(emprestimo.getCarencia());
        verificarValorMinimoEmprestimo(emprestimo.getValorEmprestimo());
        verificarLimiteCreditoConsignado(cliente, emprestimo.getValorEmprestimo());
        verificarIdadeClienteConsig(cliente, emprestimo.getQuantidadeParcelas());
        verificarPrazoConsignado(cliente, emprestimo.getQuantidadeParcelas());
        verificarMargemConsignavel(cliente, emprestimo.getValorParcela());
    }
}