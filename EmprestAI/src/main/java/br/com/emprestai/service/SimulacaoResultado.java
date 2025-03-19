package br.com.emprestai.service;

public class SimulacaoResultado {
    public double valorTotalFinanciado;
    public double parcelaMensal;
    public double iof;
    public double custoSeguro;

    public SimulacaoResultado(double valorTotalFinanciado, double parcelaMensal, double iof, double custoSeguro) {
        this.valorTotalFinanciado = valorTotalFinanciado;
        this.parcelaMensal = parcelaMensal;
        this.iof = iof;
        this.custoSeguro = custoSeguro;
    }
}