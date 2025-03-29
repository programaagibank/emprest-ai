package br.com.emprestai.model;

import br.com.emprestai.enums.StatusEmpParcela;

import java.time.LocalDate;
import java.util.Date;

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
    private StatusEmpParcela status; // Status da parcela (paga ou pendente)
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
        // if (numeroParcela <= 0) {
        // throw new IllegalArgumentException("Número da parcela deve ser maior que
        // zero.");
        // }
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
        // if (valorPresenteParcela < 0) {
        // throw new IllegalArgumentException("Valor da parcela não pode ser
        // negativo.");
        // }
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
        // if (juros < 0) {
        // throw new IllegalArgumentException("Juros não podem ser negativos.");
        // }
        this.juros = juros;
    }

    public double getAmortizacao() {
        return amortizacao;
    }

    public void setAmortizacao(double amortizacao) {
        // if (amortizacao < 0) {
        // throw new IllegalArgumentException("Amortização não pode ser negativa.");
        // }
        this.amortizacao = amortizacao;
    }

    public StatusEmpParcela getStatus() {
        return status;
    }

    public void setStatusParcela(StatusEmpParcela status) {
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
        // if (multa < 0) {
        // throw new IllegalArgumentException("Multa não pode ser negativa.");
        // }
        this.multa = multa;
    }

    public double getJurosMora() {
        return jurosMora;
    }

    public void setJurosMora(double jurosMora) {
        this.jurosMora = jurosMora;
    }

    @Override
    public String toString() {
        return "Parcela{" +
                "idParcela=" + idParcela +
                ", idEmprestimo=" + idEmprestimo +
                ", numeroParcela=" + numeroParcela +
                ", dataVencimento=" + dataVencimento +
                ", valorPresenteParcela=" + valorPresenteParcela +
                ", valorPago=" + valorPago +
                ", juros=" + juros +
                ", amortizacao=" + amortizacao +
                ", statusParcela=" + status +
                ", dataPagamento=" + dataPagamento +
                ", multa=" + multa +
                ", jurosMora=" + jurosMora +
                '}';
    }
}