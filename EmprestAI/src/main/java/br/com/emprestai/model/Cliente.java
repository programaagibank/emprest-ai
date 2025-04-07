package br.com.emprestai.model;

import br.com.emprestai.service.ClienteService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Cliente {
    private Long idCliente;
    private String cpfCliente;
    private String nomeCliente;
    private int score;
    private LocalDate dataNascimento;
    private String senha;
    private double vencimentoLiquidoTotal;
    private double vencimentoConsignavelTotal;
    private double valorComprometido;
    private double valorParcelasMensaisConsignado;
    private double valorParcelasMensaisTotal;

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

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public double getVencimentoLiquidoTotal() {
        return vencimentoLiquidoTotal;
    }

    public void setVencimentoLiquidoTotal(double vencimentoLiquidoTotal) {
        this.vencimentoLiquidoTotal = vencimentoLiquidoTotal;
    }

    public double getVencimentoConsignavelTotal() {
        return vencimentoConsignavelTotal;
    }

    public void setVencimentoConsignavelTotal(double vencimentoConsignavelTotal) {
        this.vencimentoConsignavelTotal = vencimentoConsignavelTotal;
    }

    public double getValorComprometido() {
        return valorComprometido;
    }

    public void setValorComprometido(double valorComprometido) {
        this.valorComprometido = valorComprometido;
    }

    public double getValorParcelasMensaisConsignado() {
        return valorParcelasMensaisConsignado;
    }

    public void setValorParcelasMensaisConsignado(double valorParcelasMensaisConsignado) {
        this.valorParcelasMensaisConsignado = valorParcelasMensaisConsignado;
    }

    public double getValorParcelasMensaisTotal() {
        return valorParcelasMensaisTotal;
    }

    public void setValorParcelasMensaisTotal(double valorParcelasMensaisTotal) {
        this.valorParcelasMensaisTotal = valorParcelasMensaisTotal;
    }

    // Método para calcular a idade
    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(dataNascimento, LocalDate.now());
    }

    // Métodos que delegam os cálculos para o EmprestimoService
    public int getPrazoMaximoPessoal() {
        return ClienteService.calcularPrazoMaximoPessoal(this);
    }

    public int getPrazoMaximoConsignado() {
        return ClienteService.calcularPrazoMaximoConsignado(this);
    }

    public double getMargemConsignavelDisponivel() {
        return ClienteService.calcularMargemConsignavelDisponivel(this);
    }

    public double getLimiteCreditoConsignado() {
        System.out.println(ClienteService.calcularLimiteCreditoConsignado(this));
        return ClienteService.calcularLimiteCreditoConsignado(this);
    }

    public double getMargemPessoalDisponivel() {
        return ClienteService.calcularMargemPessoalDisponivel(this);
    }

    public double getLimiteCreditoPessoal() {
        return ClienteService.calcularLimiteCreditoPessoal(this);
    }


}
