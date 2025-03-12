package br.com.emprestai.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Parcela {

    // Atributos
    private Long idParcela; // Identificador único da parcela (autoincrementado no banco)
    private Long idEmprestimo; // Referência ao empréstimo associado
    private int numeroParcela; // Número da parcela (ex.: 1, 2, 3...)
    private LocalDate dataVencimento; // Data de vencimento da parcela
    private BigDecimal valorPresenteParcela; // Valor da parcela (fixo no sistema Price)
    private BigDecimal juros; // Parte dos juros na parcela
    private BigDecimal amortizacao; // Parte da amortização do principal
    private StatusParcela status; // Status da parcela (paga ou pendente)
    private LocalDate dataPagamento; // Data em que foi paga (null se pendente)
    private BigDecimal multa; // Multa por atraso (null se não aplicável)
    private BigDecimal jurosMora; // Juros de mora por atraso (null se não aplicável)

    // Enum para status da parcela
    public enum StatusParcela {
        PAGA, PENDENTE
    }

    // Construtor vazio
    public Parcela() {
    }

    // Construtor com parâmetros principais
    public Parcela(Long idEmprestimo, int numeroParcela, LocalDate dataVencimento,
                   BigDecimal valorPresenteParcela, BigDecimal juros, BigDecimal amortizacao) {
        this.idEmprestimo = idEmprestimo;
        this.numeroParcela = numeroParcela;
        this.dataVencimento = dataVencimento;
        this.valorPresenteParcela = valorPresenteParcela;
        this.juros = juros;
        this.amortizacao = amortizacao;
        this.status = StatusParcela.PENDENTE; // Status inicial padrão
        this.multa = BigDecimal.ZERO; // Inicializa como zero
        this.jurosMora = BigDecimal.ZERO; // Inicializa como zero
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
        if (numeroParcela <= 0) {
            throw new IllegalArgumentException("Número da parcela deve ser maior que zero.");
        }
        this.numeroParcela = numeroParcela;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValorPresenteParcela() {
        return valorPresenteParcela;
    }

    public void setValorPresenteParcela(BigDecimal valorPresenteParcela) {
        if (valorPresenteParcela.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor da parcela não pode ser negativo.");
        }
        this.valorPresenteParcela = valorPresenteParcela;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        if (juros.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Juros não podem ser negativos.");
        }
        this.juros = juros;
    }

    public BigDecimal getAmortizacao() {
        return amortizacao;
    }

    public void setAmortizacao(BigDecimal amortizacao) {
        if (amortizacao.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amortização não pode ser negativa.");
        }
        this.amortizacao = amortizacao;
    }

    public StatusParcela getStatus() {
        return status;
    }

    public void setStatus(StatusParcela status) {
        this.status = status;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        if (multa != null && multa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Multa não pode ser negativa.");
        }
        this.multa = multa;
    }

    public BigDecimal getJurosMora() {
        return jurosMora;
    }

    public void setJurosMora(BigDecimal jurosMora) {
        if (jurosMora != null && jurosMora.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Juros de mora não podem ser negativos.");
        }
        this.jurosMora = jurosMora;
    }

    // Método auxiliar para calcular o valor total a pagar (com multa e juros mora, se aplicável)
    public BigDecimal getValorTotalAPagar() {
        BigDecimal total = valorPresenteParcela;
        if (multa != null) total = total.add(multa);
        if (jurosMora != null) total = total.add(jurosMora);
        return total;
    }
}