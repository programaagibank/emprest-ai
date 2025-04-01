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
    private double valorParcelasMensaisConsignado; // Soma das parcelas mensais de consignado
    private double valorParcelasMensaisTotal;      // Soma das parcelas mensais de todos os empréstimos
    private int prazoMaximoPessoal;                // Prazo máximo em meses para empréstimo pessoal
    private int prazoMaximoConsignado;             // Prazo máximo em meses para empréstimo consignado
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
        atualizarPrazosMaximos(); // Atualiza os prazos máximos sempre que a data de nascimento mudar
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

    // Método auxiliar para calcular a idade
    private int getIdade() {
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

        // Prazo máximo pessoal: menor valor entre (idade máxima - idade atual) em meses e o limite de params
        int anosRestantesPessoal = (int) Math.max(0, params.getIdadeMaximaPessoal() - idade);
        int prazoCalculadoPessoal = anosRestantesPessoal * 12;
        this.prazoMaximoPessoal = Math.min(prazoCalculadoPessoal, params.getPrazoMaximoPessoal());

        // Prazo máximo consignado: menor valor entre (idade máxima - idade atual) em meses e o limite de params
        int anosRestantesConsignado = (int) Math.max(0, params.getIdadeMaximaConsignado() - idade);
        int prazoCalculadoConsignado = anosRestantesConsignado * 12;
        this.prazoMaximoConsignado = Math.min(prazoCalculadoConsignado, params.getPrazoMaximoConsignado());
    }

    // Métodos para cálculo das margens disponíveis
    public double getMargemConsignavelDisponivel() {
        if (vencimentoConsignavelTotal <= 0) {
            return 0; // Cliente não é elegível para consignado
        }
        double margemConsignavel = vencimentoConsignavelTotal * params.getMargemConsignavel() / 100; // 35%
        return Math.max(0, margemConsignavel - valorParcelasMensaisConsignado);
    }

    public double getMargemPessoalDisponivel() {
        double margemPessoal = vencimentoLiquidoTotal * params.getPercentualRendaPessoal() / 100; // 30%
        return Math.max(0, margemPessoal - valorParcelasMensaisTotal);
    }

    // Verifica elegibilidade geral para empréstimo consignado
    public boolean isElegivelConsignado() {
        int idade = getIdade();

        // Verificações básicas
        if (vencimentoConsignavelTotal <= 0 || idade <= 0) {
            return false;
        }

        // Verifica se há margem consignável disponível
        if (getMargemConsignavelDisponivel() <= 0) {
            return false;
        }

        // Verifica idade mínima (18) e máxima inicial (80)
        if (idade < 18 || idade > 80) {
            return false;
        }

        // Verifica se o prazo máximo atende ao mínimo exigido para consignado
        if (prazoMaximoConsignado < params.getPrazoMinimoConsignado()) {
            return false;
        }

        return true; // Cliente é elegível para consignado
    }

    // Verifica elegibilidade geral para empréstimo pessoal
    public boolean isElegivelPessoal() {
        int idade = getIdade();

        // Verificações básicas
        if (vencimentoLiquidoTotal <= 0 || score <= 0 || idade <= 0) {
            return false;
        }

        // Verifica se há margem pessoal disponível
        if (getMargemPessoalDisponivel() <= 0) {
            return false;
        }

        // Verifica renda mínima
        if (vencimentoLiquidoTotal < params.getRendaMinimaPessoal()) {
            return false;
        }

        // Verifica idade mínima (18) e máxima
        if (idade < 18 || idade > params.getIdadeMaximaPessoal()) {
            return false;
        }

        // Verifica se o prazo máximo atende ao mínimo exigido para pessoal
        if (prazoMaximoPessoal < params.getPrazoMinimoPessoal()) {
            return false;
        }

        // Verifica score mínimo
        if (score < params.getScoreMinimoPessoal()) {
            return false;
        }

        return true; // Cliente é elegível para pessoal
    }
}