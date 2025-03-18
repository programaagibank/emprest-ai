package br.com.emprestai.service;

public class SimulacaoEmprestimos {

    public static SimulacaoResultado simularEmprestimoPessoal(double valor, int parcelas, double score, boolean contratarSeguro, double rendaLiquida, int idade) {
        double capacidade = CalculoPessoal.calculoDeCapacidadeDePagamento(rendaLiquida);
        double taxaJuros = CalculoPessoal.calculoTaxaDeJurosMensal(score) / 100;
        double iof = valor * 0.0038 + valor * 0.000082 * Math.min(parcelas * 30, 365);
        double custoSeguro = contratarSeguro ? valor * (0.0025 + 0.00005 * idade) * (parcelas / 12.0) : 0;
        double valorTotalFinanciado = valor + iof + custoSeguro;
        double parcelaMensal = (valorTotalFinanciado * taxaJuros) / (1 - Math.pow(1 + taxaJuros, -parcelas));

        if (parcelaMensal > capacidade) {
            throw new IllegalArgumentException("Parcela excede a capacidade de pagamento");
        }

        return new SimulacaoResultado(valorTotalFinanciado, parcelaMensal, iof, custoSeguro);
    }

    public static SimulacaoResultado simularEmprestimoConsignado(double valor, int parcelas, double rendaLiquida) {
        double margem = CalculoConsignado.calcularMargemEmprestimoConsig(rendaLiquida, 0);
        double taxaJuros = CalculoConsignado.calcularTaxaJurosMensal(parcelas);
        double iof = valor * 0.0038 + valor * 0.000082 * Math.min(parcelas * 30, 365);
        double valorTotalFinanciado = valor + iof;
        double parcelaMensal = (valorTotalFinanciado * taxaJuros) / (1 - Math.pow(1 + taxaJuros, -parcelas));

        if (parcelaMensal > margem) {
            throw new IllegalArgumentException("Parcela excede margem consignável");
        }

        return new SimulacaoResultado(valorTotalFinanciado, parcelaMensal, iof, 0);
    }
}

class SimulacaoResultado {
    public double valorTotalFinanciado;
    public double parcelaMensal;
    public double iof;
    public double custoSeguro;

    SimulacaoResultado(double valorTotalFinanciado, double parcelaMensal, double iof, double custoSeguro) {
        this.valorTotalFinanciado = valorTotalFinanciado;
        this.parcelaMensal = parcelaMensal;
        this.iof = iof;
        this.custoSeguro = custoSeguro;
    }
}