package br.com.emprestai.model;

import br.com.emprestai.util.EmprestimoParams;

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
    private int prazoMaximoPessoal;
    private int prazoMaximoConsignado;
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    // Getters e Setters
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        atualizarPrazosMaximos();
    }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public double getVencimentoLiquidoTotal() { return vencimentoLiquidoTotal; }
    public void setVencimentoLiquidoTotal(double vencimentoLiquidoTotal) { this.vencimentoLiquidoTotal = vencimentoLiquidoTotal; }

    public double getVencimentoConsignavelTotal() { return vencimentoConsignavelTotal; }
    public void setVencimentoConsignavelTotal(double vencimentoConsignavelTotal) { this.vencimentoConsignavelTotal = vencimentoConsignavelTotal; }

    public double getValorComprometido() { return valorComprometido; }
    public void setValorComprometido(double valorComprometido) { this.valorComprometido = valorComprometido; }

    public double getValorParcelasMensaisConsignado() { return valorParcelasMensaisConsignado; }
    public void setValorParcelasMensaisConsignado(double valorParcelasMensaisConsignado) {
        this.valorParcelasMensaisConsignado = valorParcelasMensaisConsignado;
    }

    public double getValorParcelasMensaisTotal() { return valorParcelasMensaisTotal; }
    public void setValorParcelasMensaisTotal(double valorParcelasMensaisTotal) {
        this.valorParcelasMensaisTotal = valorParcelasMensaisTotal;
    }

    public int getPrazoMaximoPessoal() { return prazoMaximoPessoal; }
    public void setPrazoMaximoPessoal(int prazoMaximoPessoal) { this.prazoMaximoPessoal = prazoMaximoPessoal; }

    public int getPrazoMaximoConsignado() { return prazoMaximoConsignado; }
    public void setPrazoMaximoConsignado(int prazoMaximoConsignado) { this.prazoMaximoConsignado = prazoMaximoConsignado; }

    // Método para calcular a idade (público para ser acessado por ElegibilidadeCliente)
    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(dataNascimento, LocalDate.now());
    }

    // Método para atualizar os prazos máximos com base na idade e limites de params
    private void atualizarPrazosMaximos() {
        int idade = getIdade();
        if (idade <= 0) {
            this.prazoMaximoPessoal = 0;
            this.prazoMaximoConsignado = 0;
            return;
        }

        int anosRestantesPessoal = (int) Math.max(0, params.getIdadeMaximaPessoal() - idade);
        int prazoCalculadoPessoal = anosRestantesPessoal * 12;
        this.prazoMaximoPessoal = Math.min(prazoCalculadoPessoal, params.getPrazoMaximoPessoal());

        int anosRestantesConsignado = (int) Math.max(0, params.getIdadeMaximaConsignado() - idade);
        int prazoCalculadoConsignado = anosRestantesConsignado * 12;
        this.prazoMaximoConsignado = Math.min(prazoCalculadoConsignado, params.getPrazoMaximoConsignado());
    }

    // Métodos para cálculo das margens disponíveis
    public double getMargemConsignavelDisponivel() {
        if (vencimentoConsignavelTotal <= 0) {
            return 0;
        }
        double margemConsignavel = vencimentoConsignavelTotal * params.getMargemConsignavel() / 100;
        return Math.max(0, margemConsignavel - valorParcelasMensaisConsignado);
    }

    public double getMargemPessoalDisponivel() {
        double margemPessoal = vencimentoLiquidoTotal * params.getPercentualRendaPessoal() / 100;
        return Math.max(0, margemPessoal - valorParcelasMensaisTotal);
    }
}