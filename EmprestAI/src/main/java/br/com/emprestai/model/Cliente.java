package br.com.emprestai.model;

import br.com.emprestai.enums.VinculoEnum;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Cliente {

    // Atributos
    private Long id_cliente; // Autoincrementado no banco
    private String cpf_cliente; // Mantido como atributo secundário
    private String nome_cliente;
    private BigDecimal renda_mensal_liquida;
    private LocalDate data_nascimento;
    private BigDecimal renda_familiar_liquida;
    private int qtd_pessoas_na_casa;
    private VinculoEnum id_tipo_cliente;
    private int score;

    // Construtor vazio
    public Cliente() {
    }

    // Construtor com parâmetros
    public Cliente(String cpf_cliente, String nome_cliente, BigDecimal renda_mensal_liquida, LocalDate data_nascimento,
                   BigDecimal renda_familiar_liquida, int qtd_pessoas_na_casa, TipoVinculo id_tipo_cliente, int score) {
        this.cpf_cliente = cpf_cliente;
        this.nome_cliente = nome_cliente;
        this.renda_mensal_liquida = renda_mensal_liquida;
        this.data_nascimento = data_nascimento;
        this.renda_familiar_liquida = renda_familiar_liquida;
        this.qtd_pessoas_na_casa = qtd_pessoas_na_casa;
        this.id_tipo_cliente = id_tipo_cliente;
        this.score = score;
    }

    // Getters e Setters
    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getCpf_cliente() {
        return cpf_cliente;
    }

    public void setCpf_cliente(String cpf_cliente) {
        this.cpf_cliente = cpf_cliente;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }

    public BigDecimal getRenda_mensal_liquida() {
        return renda_mensal_liquida;
    }

    public void setRenda_mensal_liquida(BigDecimal renda_mensal_liquida) {
        this.renda_mensal_liquida = renda_mensal_liquida;
    }

    public LocalDate getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(LocalDate data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public BigDecimal getRenda_familiar_liquida() {
        return renda_familiar_liquida;
    }

    public void setRenda_familiar_liquida(BigDecimal renda_familiar_liquida) {
        this.renda_familiar_liquida = renda_familiar_liquida;
    }

    public int getQtd_pessoas_na_casa() {
        return qtd_pessoas_na_casa;
    }

    public void setQtd_pessoas_na_casa(int qtd_pessoas_na_casa) {
        this.qtd_pessoas_na_casa = qtd_pessoas_na_casa;
    }

    public TipoVinculo getId_tipo_cliente() {
        return id_tipo_cliente;
    }

    public void setId_tipo_cliente(TipoVinculo id_tipo_cliente) {
        this.id_tipo_cliente = id_tipo_cliente;
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