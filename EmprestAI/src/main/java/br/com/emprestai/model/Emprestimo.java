package br.com.emprestai.model;

import java.sql.Date;

public class Emprestimo {

    private int emprestimoId;
    private String cpfClienteId;
    private double valorTotal;
    private int quantidadeParcelas;
    private double juros;
    private Date dataInicio;
    private String statusEmprestimoId;
    private int tipoEmprestimoId;
    private double valorSeguro;
    private double valorIOF;
    private double outrosCustos;
    private Date dataContratacao;
    private int motivoEncerramentoId;
    private double jurosMora;
    private double taxaMulta;
    private int emprestimoOrigemId;

    public int getEmprestimoId() {
        return emprestimoId;
    }

    public void setEmprestimoId(int emprestimoId) {
        this.emprestimoId = emprestimoId;
    }

    public String getCpfClienteId() {
        return cpfClienteId;
    }

    public void setCpfClienteId(String cpfClienteId) {
        this.cpfClienteId = cpfClienteId;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(int quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public double getJuros() {
        return juros;
    }

    public void setJuros(double juros) {
        this.juros = juros;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getStatusEmprestimoId() {
        return statusEmprestimoId;
    }

    public void setStatusEmprestimoId(String statusEmprestimoId) {
        this.statusEmprestimoId = statusEmprestimoId;
    }

    public int getTipoEmprestimoId() {
        return tipoEmprestimoId;
    }

    public void setTipoEmprestimoId(int tipoEmprestimoId) {
        this.tipoEmprestimoId = tipoEmprestimoId;
    }

    public double getValorSeguro() {
        return valorSeguro;
    }

    public void setValorSeguro(double valorSeguro) {
        this.valorSeguro = valorSeguro;
    }

    public double getValorIOF() {
        return valorIOF;
    }

    public void setValorIOF(double valorIOF) {
        this.valorIOF = valorIOF;
    }

    public double getOutrosCustos() {
        return outrosCustos;
    }

    public void setOutrosCustos(double outrosCustos) {
        this.outrosCustos = outrosCustos;
    }

    public Date getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(Date dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public int getMotivoEncerramentoId() {
        return motivoEncerramentoId;
    }

    public void setMotivoEncerramentoId(int motivoEncerramentoId) {
        this.motivoEncerramentoId = motivoEncerramentoId;
    }

    public double getJurosMora() {
        return jurosMora;
    }

    public void setJurosMora(double jurosMora) {
        this.jurosMora = jurosMora;
    }

    public double getTaxaMulta() {
        return taxaMulta;
    }

    public void setTaxaMulta(double taxaMulta) {
        this.taxaMulta = taxaMulta;
    }

    public int getEmprestimoOrigemId() {
        return emprestimoOrigemId;
    }

    public void setEmprestimoOrigemId(int emprestimoOrigemId) {
        this.emprestimoOrigemId = emprestimoOrigemId;
    }

    @Override
    public String toString() {
        return "Emprestimo [emprestimoId=" + emprestimoId
                + ", cpfClienteId=" + cpfClienteId
                + ", valorTotal=" + valorTotal
                + ", quantidadeParcelas=" + quantidadeParcelas
                + ", juros=" + juros
                + ", dataInicio=" + dataInicio
                + ", statusEmprestimoId=" + statusEmprestimoId
                + ", tipoEmprestimoId=" + tipoEmprestimoId
                + ", valorSeguro=" + valorSeguro
                + ", valorIOF=" + valorIOF
                + ", outrosCustos=" + outrosCustos
                + ", dataContratacao=" + dataContratacao
                + ", motivoEncerramentoId=" + motivoEncerramentoId
                + ", jurosMora=" + jurosMora
                + ", taxaMulta=" + taxaMulta
                + ", emprestimoOrigemId=" + emprestimoOrigemId
                + "]";
    }

}
