package br.com.emprestai.model;

import br.com.emprestai.enums.StatusParcelaEnum;

import java.time.LocalDate;

public class Parcela {

    // Atributos
    private long idParcela; // Identificador único da parcela (autoincrementado no banco)
    private long idEmprestimo; // Referência ao empréstimo associado
    private int numeroParcela; // Número da parcela (ex.: 1, 2, 3...)
    private LocalDate dataVencimento; // Data de vencimento da parcela
    private double valorPresenteParcela; // Valor da parcela (fixo no sistema Price)
    private double valorPago; // Valor pago
    private double juros; // Parte dos juros na parcela
    private double amortizacao; // Parte da amortização do principal
    private double saldoDevedor; // Parte da amortização do principal
    private StatusParcelaEnum status; // Status da parcela (paga ou pendente)
    private LocalDate dataPagamento; // Data em que foi paga
    private double multa; // Multa por atraso
    private double jurosMora; // Juros de mora por atraso

    // Construtor vazio
    public Parcela() {
    }

    public Parcela(Double valorPresenteParcela, Double jurosMora, Double multa, LocalDate dataVencimento) {
        this.valorPresenteParcela = valorPresenteParcela;
        this.jurosMora = jurosMora;
        this.multa = multa;
        this.dataVencimento = dataVencimento;
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

    public double getValorPresenteParcela() {
        return valorPresenteParcela;
    }

    public void setValorPresenteParcela(double valorPresenteParcela) {
        this.valorPresenteParcela = valorPresenteParcela;
    }

    public double getValorPago() { return valorPago; }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public double getJuros() {
        return juros;
    }

    public void setJuros(double juros) {
        this.juros = juros;
    }

    public double getAmortizacao() {
        return amortizacao;
    }

    public void setAmortizacao(double amortizacao) {
        this.amortizacao = amortizacao;
    }

    public StatusParcelaEnum getStatus() {
        return status;
    }

    public void setStatusParcela(StatusParcelaEnum status) {
        this.status = status;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public double getJurosMora() {
        return jurosMora;
    }

    public void setJurosMora(double jurosMora) {
        this.jurosMora = jurosMora;
    }

    public void setIdParcela(long idParcela) {
        this.idParcela = idParcela;
    }

    public void setIdEmprestimo(long idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public double getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(double saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public void setStatus(StatusParcelaEnum status) {
        this.status = status;
    }
}