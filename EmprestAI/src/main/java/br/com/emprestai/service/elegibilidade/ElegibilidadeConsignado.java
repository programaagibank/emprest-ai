package br.com.emprestai.service.elegibilidade;

import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.exception.ValidationException;
import br.com.emprestai.util.EmprestimoParams;

import static br.com.emprestai.enums.VinculoEnum.*;

public class ElegibilidadeConsignado {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // 11.1.1 Margem Consignável
    public static void verificarMargemEmprestimoConsig(double valorParcela, double rendaLiquida, double parcelasAtivas) {
        if (valorParcela <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (rendaLiquida <= 0) throw new ValidationException("Renda líquida deve ser maior que zero.");
        if (parcelasAtivas < 0) throw new ValidationException("Parcelas ativas não podem ser negativas.");

        double margemDisponivel = (rendaLiquida * params.getMargemConsignavel()) - parcelasAtivas;
        if (valorParcela > margemDisponivel) {
            throw new ValidationException("Valor da parcela excede a margem consignável disponível.");
        }
    }

    // 11.1.2 Idade Máxima Para Empréstimo Consignado
    public static void verificarIdadeClienteConsig(int idade, int parcelas) {
        if (idade < 18) throw new ValidationException("Idade mínima para consignado é 18 anos.");
        if (idade > 80) throw new ValidationException("Idade máxima inicial para consignado é 80 anos.");

        int anos = parcelas / 12;
        int idadeFinal = idade + anos;
        if (idadeFinal > params.getIdadeMaximaConsignado()) {
            throw new ValidationException("Idade final excede o limite máximo para consignado.");
        }
    }

    // 11.1.4 Taxa de Juros
    public static void verificarTaxaJurosEmprestimoConsig(double juros) {
        if (juros < params.getJurosMinimoConsignado() || juros > params.getJurosMaximoConsignado()) {
            throw new ValidationException("Taxa de juros fora do intervalo permitido pelo sistema.");
        }
    }

    // 11.1.5 Tipo de Vínculo
    public static void verificarVinculoEmprestimoConsig(VinculoEnum vinculo) {
        if (vinculo == null) throw new ValidationException("Vínculo não pode ser nulo.");
        if (vinculo != APOSENTADO && vinculo != SERVIDOR && vinculo != PENSIONISTA) {
            throw new ValidationException("Vínculo inválido para empréstimo consignado.");
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


    // Verificação da elegibilidade para empréstimo consignado
    public static void verificarElegibilidadeConsignado(double rendaLiquida, double valorParcela, double parcelasAtivas,
                                                        int idade, int parcelas, double taxaJuros, VinculoEnum vinculo, int carencia,double valorSolicitado) {
        if (rendaLiquida <= 0) throw new ValidationException("Renda líquida deve ser maior que zero.");
        if (valorParcela <= 0) throw new ValidationException("Valor da parcela deve ser maior que zero.");
        if (parcelasAtivas < 0) throw new ValidationException("Parcelas ativas não podem ser negativas.");
        if (idade <= 0) throw new ValidationException("Idade deve ser maior que zero.");
        if (parcelas <= 0) throw new ValidationException("Quantidade de parcelas deve ser maior que zero.");
        if (taxaJuros <= 0) throw new ValidationException("Taxa de juros deve ser maior que zero.");
        if (carencia < 0) throw new ValidationException("Carência não pode ser negativa.");
        if (valorSolicitado <= 0) throw new ValidationException("Valor solicitado deve ser maior que zero.");


        verificarMargemEmprestimoConsig(valorParcela, rendaLiquida, parcelasAtivas);
        verificarIdadeClienteConsig(idade, parcelas);
        verificarTaxaJurosEmprestimoConsig(taxaJuros);
        verificarVinculoEmprestimoConsig(vinculo);
        verificarCarenciaEmprestimoConsig(carencia);
        verificarValorMinimoEmprestimo(valorSolicitado);
    }

}