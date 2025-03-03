package br.com.emprestai.model;

import java.util.Date;

public class Parcela {

    private int parcelaId;
    private int emprestimoId;
    private int numeroParcela;
    private double valorPago;
    private Date dataNascimento;
    private Date dataPagamento;
    private String status;

    public Parcela(int numeroParcela, double valorPago, Date dataNascimento, Date dataPagamento, String status) {
        this.numeroParcela = numeroParcela;
        this.valorPago = valorPago;
        this.dataNascimento = dataNascimento;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }

    public int getParcelaId() {
        return parcelaId;
    }

    public int getEmprestimoId() {
        return emprestimoId;
    }

    public int getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(int numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Parcela{" +
                "idParcela=" + parcelaId +
                ", idEmprestimo=" + emprestimoId +
                ", numeroParcela=" + numeroParcela +
                ", valorPago=" + valorPago +
                ", dataNascimento=" + dataNascimento +
                ", dataPagamento=" + dataPagamento +
                ", status=" + status +
                '}';
    }
}
