package br.com.emprestai.model;

import br.com.emprestai.enums.VinculoEnum;

import java.time.LocalDate;

public class Cliente {

    // Atributos
    private Long idCliente; // Autoincrementado no banco
    private String cpfCliente; // Mantido como atributo secundário
    private String nomecliente;
    private double rendaMensalLiquida;
    private LocalDate dataNascimento;
    private double rendaFamiliarLiquida;
    private int qtdePessoasNaCasa;
    private VinculoEnum tipoCliente;
    private int score;
    private String senha;

    // Construtor vazio
    public Cliente() {
    }

    // Construtor com parâmetros
    public Cliente(String cpfCliente, String nomecliente, double rendaMensalLiquida,
            LocalDate dataNascimento, double rendaFamiliarLiquida, int qtdePessoasNaCasa,
            VinculoEnum tipoCliente, int score, String senha) {

        this.cpfCliente = cpfCliente;
        this.nomecliente = nomecliente;
        this.rendaMensalLiquida = rendaMensalLiquida;
        this.dataNascimento = dataNascimento;
        this.rendaFamiliarLiquida = rendaFamiliarLiquida;
        this.qtdePessoasNaCasa = qtdePessoasNaCasa;
        this.tipoCliente = tipoCliente;
        this.score = score;
        this.senha = senha;
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

    public double getRendaMensalLiquida() {
        return rendaMensalLiquida;
    }

    public void setRendaMensalLiquida(double rendaMensalLiquida) {
        this.rendaMensalLiquida = rendaMensalLiquida;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public double getRendaFamiliarLiquida() {
        return rendaFamiliarLiquida;
    }

    public void setRendaFamiliarLiquida(double rendaFamiliarLiquida) {
        this.rendaFamiliarLiquida = rendaFamiliarLiquida;
    }

    public int getQtdePessoasNaCasa() {
        return qtdePessoasNaCasa;
    }

    public void setQtdePessoasNaCasa(int qtdePessoasNaCasa) {
        this.qtdePessoasNaCasa = qtdePessoasNaCasa;
    }

    public VinculoEnum getTipoCliente() {
        return tipoCliente;
    }

    public void setipoCliente(VinculoEnum tipoCliente) {
        this.tipoCliente = tipoCliente;
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
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", cpfCliente='" + cpfCliente + '\'' +
                ", nomecliente='" + nomecliente + '\'' +
                ", rendaMensalLiquida=" + rendaMensalLiquida +
                ", dataNascimento=" + dataNascimento +
                ", rendaFamiliarLiquida=" + rendaFamiliarLiquida +
                ", qtdePessoasNaCasa=" + qtdePessoasNaCasa +
                ", tipoCliente=" + tipoCliente +
                ", score=" + score +
                '}';
    }
}