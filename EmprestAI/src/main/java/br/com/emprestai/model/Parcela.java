package br.com.emprestai.model;

import br.com.emprestai.enums.StatusEmpParcela;

import java.time.LocalDate;

public class Parcela {

    // Atributos
    private long idParcela; // Identificador único da parcela (autoincrementado no banco)
    private long idEmprestimo; // Referência ao empréstimo associado
    private int numeroParcela; // Número da parcela (ex.: 1, 2, 3...)
    private LocalDate dataVencimento; // Data de vencimento da parcela
    private double valorPago; // Valor pago
    private StatusEmpParcela idStatus; // Status da parcela (paga ou pendente)
    private LocalDate dataPagamento; // Data em que foi paga

    // Construtor vazio
    public Parcela() {
    }

    // Construtor com parâmetros principais
    public Parcela(Long idEmprestimo, int numeroParcela, LocalDate dataVencimento, double valorPago) {
        this.idEmprestimo = idEmprestimo;
        this.numeroParcela = numeroParcela;
        this.dataVencimento = dataVencimento;
        this.valorPago = valorPago;
    }

    // Getters e Setters
    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public Long getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Long idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public int getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(int numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public StatusEmpParcela getIdStatus() {
        return idStatus;
    }

    public void setStatusParcela(StatusEmpParcela idStatus) {
        this.idStatus = idStatus;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    @Override
    public String toString() {
        return "Parcela{" +
                "idParcela=" + idParcela +
                ", idEmprestimo=" + idEmprestimo +
                ", numeroParcela=" + numeroParcela +
                ", dataVencimento=" + dataVencimento +
                ", valorPago=" + valorPago +
                ", statusParcela=" + idStatus +
                ", dataPagamento=" + dataPagamento +
                '}';
    }
}