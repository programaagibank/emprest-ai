package br.com.emprestai.model;

import br.com.emprestai.enums.VinculoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cliente {

    // Atributos
    private Long idCliente; // Autoincrementado no banco
    private String cpfCliente; // Mantido como atributo secundário
    private String nomecliente;
    private BigDecimal rendaMensalLiquida;
    private LocalDate dataNascimento;
    private BigDecimal rendaFamiliarLiquida;
    private int qtdePessoasNaCasa;
    private VinculoEnum idTipoCliente;
    private int score;

    @Override
    public String toString() {
        return "Cliente{" +
                "id_cliente=" + idCliente +
                ", cpf_cliente='" + cpfCliente + '\'' +
                ", nome_cliente='" + nomecliente + '\'' +
                ", renda_mensal_liquida=" + rendaMensalLiquida +
                ", data_nascimento=" + dataNascimento +
                ", renda_familiar_liquida=" + rendaFamiliarLiquida +
                ", qtd_pessoas_na_casa=" + qtdePessoasNaCasa +
                ", id_tipo_cliente=" + idTipoCliente +
                ", score=" + score +
                '}';
    }

    // Construtor vazio
    public Cliente() {
    }

    // Construtor com parâmetros
    public Cliente(String cpf_cliente, String nome_cliente, BigDecimal renda_mensal_liquida, LocalDate data_nascimento,
                   BigDecimal renda_familiar_liquida, int qtd_pessoas_na_casa, VinculoEnum id_tipo_cliente, int score) {
        this.cpfCliente = cpf_cliente;
        this.nomecliente = nome_cliente;
        this.rendaMensalLiquida = renda_mensal_liquida;
        this.dataNascimento = data_nascimento;
        this.rendaFamiliarLiquida = renda_familiar_liquida;
        this.qtdePessoasNaCasa = qtd_pessoas_na_casa;
        this.idTipoCliente = id_tipo_cliente;
        this.score = score;
    }

    // Getters e Setters
    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getNomecliente() {
        return nomecliente;
    }

    public void setNomecliente(String nomecliente) {
        this.nomecliente = nomecliente;
    }

    public BigDecimal getRendaMensalLiquida() {
        return rendaMensalLiquida;
    }

    public void setRendaMensalLiquida(BigDecimal rendaMensalLiquida) {
        this.rendaMensalLiquida = rendaMensalLiquida;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public BigDecimal getRendaFamiliarLiquida() {
        return rendaFamiliarLiquida;
    }

    public void setRendaFamiliarLiquida(BigDecimal rendaFamiliarLiquida) {
        this.rendaFamiliarLiquida = rendaFamiliarLiquida;
    }

    public int getQtdePessoasNaCasa() {
        return qtdePessoasNaCasa;
    }

    public void setQtdePessoasNaCasa(int qtdePessoasNaCasa) {
        this.qtdePessoasNaCasa = qtdePessoasNaCasa;
    }

    public VinculoEnum getIdTipoCliente() {
        return idTipoCliente;
    }

    public void setIdTipoCliente(VinculoEnum idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score < 0 || score > 1000) {
            throw new IllegalArgumentException("Score deve estar entre 0 e 1000.");
        }
        this.score = score;
    }

}