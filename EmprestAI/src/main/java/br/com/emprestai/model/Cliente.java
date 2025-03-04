package br.com.emprestai.model;

import java.time.LocalDate;

public class Cliente {
    private int clienteId;
    private String nome;
    private double RemuneracaoLiqMes;
    private LocalDate dtNasc;
    private double rendaPessFamilia;
    private int qtdePessFamilia;
    private String tipoVinculo;
    private String scoreCredito;

    public Cliente(int clienteId, String nome, double remuneracaoLiqMes, LocalDate dtNasc) {
        this.clienteId = clienteId;
        this.nome = nome;
        this.RemuneracaoLiqMes = remuneracaoLiqMes;
        this.dtNasc = dtNasc;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getRemuneracaoLiqMes() {
        return RemuneracaoLiqMes;
    }

    public void setRemuneracaoLiqMes(double remuneracaoLiqMes) {
        RemuneracaoLiqMes = remuneracaoLiqMes;
    }

    public LocalDate getDtNasc() {
        return dtNasc;
    }

    public void setIdade(LocalDate dtNasc) {
        this.dtNasc = dtNasc;
    }

    public double getRendaPessFamilia() {
        return rendaPessFamilia;
    }

    public void setRendaPessFamilia(double rendaPessFamilia) {
        this.rendaPessFamilia = rendaPessFamilia;
    }

    public int getQtdePessFamilia() {
        return qtdePessFamilia;
    }

    public void setQtdePessFamilia(int qtdePessFamilia) {
        this.qtdePessFamilia = qtdePessFamilia;
    }

    public String getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(String tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public String getScoreCredito() {
        return scoreCredito;
    }

    public void setScoreCredito(String scoreCredito) {
        this.scoreCredito = scoreCredito;
    }

    @Override
    public String toString() {
        return "{" +
                "\"clienteId\": " + clienteId +
                ", \"nome\": \"" + nome + "\"" +
                ", \"RemuneracaoLiqMes\": " + RemuneracaoLiqMes +
                ", \"dtNasc\": \"" + dtNasc + "\"" +
                ", \"rendaPessFamilia\": " + rendaPessFamilia +
                ", \"qtdePessFamilia\": " + qtdePessFamilia +
                ", \"tipoVinculo\": \"" + tipoVinculo + "\"" +
                ", \"scoreCredito\": \"" + scoreCredito + "\"" +
                "}";
    }
}
