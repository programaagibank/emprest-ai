package br.com.emprestai.model;

import br.com.emprestai.enums.VinculoEnum;

import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;

public class Cliente {

    // Atributos
    private Long idCliente; // Autoincrementado no banco
    private String nomecliente;
    private double rendaMensalLiquida;
    private LocalDate dataNascimento;
    private double rendaFamiliarLiquida;
    private int qtdePessoasNaCasa;
    private VinculoEnum tipoCliente;
    private int score;
    private String cpfCliente; // Usado como identificador de login
    private String senha;
    private List<Emprestimo> emprestimoList;
    private double parcelasAtivas;
    private double margemConsignavel;
    private double margemPessoal;

    // Construtor vazio
    public Cliente() {
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

    public void setTipoCliente(VinculoEnum tipoCliente) {
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

    public List<Emprestimo> getEmprestimoList() {
        return emprestimoList;
    }

    public void setEmprestimoList(List<Emprestimo> emprestimoList) {
        this.emprestimoList = emprestimoList;
    }

    public double getParcelasAtivas() {
        return parcelasAtivas;
    }

    public void setParcelasAtivas(double parcelasAtivas) {
        this.parcelasAtivas = parcelasAtivas;
    }

    public double getMargemConsignavel() {
        return margemConsignavel;
    }

    public void setMargemConsignavel(double margemConsignavel) {
        this.margemConsignavel = margemConsignavel;
    }

    public double getMargemPessoal() {
        return margemPessoal;
    }

    public void setMargemPessoal(double margemPessoal) {
        this.margemPessoal = margemPessoal;
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