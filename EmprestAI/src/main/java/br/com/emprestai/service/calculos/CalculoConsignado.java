package br.com.emprestai.service.calculos;

import br.com.emprestai.util.EmprestimoParams;

public class CalculoConsignado {

    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // 12.2 Margem Consignável
    public static double calcularMargemEmprestimoConsig(double rendaLiquida, double parcelasAtivas) {
        if (rendaLiquida <= 0) {
            throw new IllegalArgumentException("A renda líquida não pode ser igual ou inferior a zero");
        }
        if (parcelasAtivas < 0) {
            throw new IllegalArgumentException("As parcelas ativas não podem ser negativas");
        }
        // Converte margem consignável de porcentagem (ex.: 35) para decimal (0.35)
        double margemMaxima = (rendaLiquida * (params.getMargemConsignavel() / 100)) - parcelasAtivas;
        if (margemMaxima < 0) {
            throw new IllegalStateException("A margem consignável não pode ser negativa");
        }
        return margemMaxima;
    }

    // 12.3 Taxa de Juros Mensal
    public static double calcularTaxaJurosMensal(double quantidadeParcelas) {
        if (quantidadeParcelas <= 0) {
            throw new IllegalArgumentException("A quantidade de parcelas deve ser maior que zero");
        }
        if (quantidadeParcelas < params.getPrazoMinimoConsignado()) {
            throw new IllegalArgumentException("A quantidade de parcelas não pode ser inferior ao prazo mínimo de " + params.getPrazoMinimoConsignado());
        }
        // Ajusta a taxa para atingir 0.0186 com 36 parcelas
        double taxaJurosMensal = params.getJurosMinimoConsignado() + params.getIncrementoTaxaConsig() * (quantidadeParcelas - params.getPrazoMinimoConsignado());
        double jurosMaximoDecimal = params.getJurosMaximoConsignado();
        if (taxaJurosMensal > jurosMaximoDecimal) {
            return jurosMaximoDecimal;
        }
        return taxaJurosMensal;
    }
}