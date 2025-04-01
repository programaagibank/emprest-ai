package br.com.emprestai.service.elegibilidade;

import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.util.EmprestimoParams;


public class ElegibilidadeConsignado {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // 11.1.4 Taxa de Juros
    public static void verificarTaxaJurosEmprestimoConsig(double juros) {
        if (juros < params.getJurosMinimoConsignado() || juros > params.getJurosMaximoConsignado()) {
            throw new ValidationException("Taxa de juros fora do intervalo permitido pelo sistema.");
        }
    }

    // 11.1.6 Carência
    public static void verificarCarenciaEmprestimoConsig(int dias) {
        if (dias < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (dias > params.getCarenciaMaximaConsignado()) {
            throw new ValidationException("Carência excede o limite máximo permitido.");
        }
    }

    // Valor Mínimo para empréstimo consignado
    public static void verificarValorMinimoEmprestimo(double valorSolicitado) {
        double valorMinimoConsignado = params.getValorMinimoConsignado();
        if (valorSolicitado < valorMinimoConsignado) {
            throw new ValidationException("O valor solicitado (R$ " + valorSolicitado + ") é inferior ao mínimo de R$ " + valorMinimoConsignado + " para empréstimo consignado.");
        }
    }

    // 11.1.2 Idade Máxima Final (mantida pois verifica idade final com base em parcelas)
    public static void verificarIdadeClienteConsig(int idade, int parcelas) {
        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        if (idadeFinal > params.getIdadeMaximaConsignado()) {
            throw new ValidationException("Idade final excede o limite máximo para consignado.");
        }
    }

    // Verificação da elegibilidade para empréstimo consignado (ajustada)
    public static void verificarElegibilidadeConsignado(double valorParcela, int idade, int parcelas, double taxaJuros, int carencia, double valorSolicitado) {
        if (valorParcela <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (idade <= 0) throw new ValidationException("Idade deve ser maior que zero.");
        if (parcelas <= 0) throw new ValidationException("Quantidade de parcelas deve ser maior que zero.");
        if (taxaJuros <= 0) throw new ValidationException("Taxa de juros deve ser maior que zero.");
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (valorSolicitado <= 0) throw new ValidationException("Valor solicitado deve ser maior que zero.");

        verificarIdadeClienteConsig(idade, parcelas);
        verificarTaxaJurosEmprestimoConsig(taxaJuros);
        verificarCarenciaEmprestimoConsig(carencia);
        verificarValorMinimoEmprestimo(valorSolicitado);
    }
}