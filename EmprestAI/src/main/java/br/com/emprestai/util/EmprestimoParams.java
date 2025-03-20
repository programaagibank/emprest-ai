package br.com.emprestai.util;

public class EmprestimoParams {
    // Singleton
    private static final EmprestimoParams INSTANCE = new EmprestimoParams();

    private EmprestimoParams() {
    }

    public static EmprestimoParams getInstance() {
        return INSTANCE;
    }

    // Taxas de juros
    private double jurosMinimoPessoal = 8.49;
    private double jurosMaximoPessoal = 9.99;
    private double jurosMinimoConsignado = 1.80;
    private double jurosMaximoConsignado = 2.14;

    // Valores limites
    private double valorMinimoPessoal = 100.00;
    private double valorMaximoPessoal = 20000.00;
    private double valorMinimoConsignado = 1000.00;

    // Prazos
    private int prazoMinimoPessoal = 6;
    private int prazoMaximoPessoal = 30;
    private int prazoMinimoConsignado = 24;
    private int prazoMaximoConsignado = 94;

    // Carências
    private double carenciaMaximaPessoal = 30;
    private double carenciaMaximaConsignado = 60;

    //Score

    public int getScoreMinimoPessoal() {
        return scoreMinimoPessoal;
    }

    public void setScoreMinimoPessoal(int scoreMinimoPessoal) {
        this.scoreMinimoPessoal = scoreMinimoPessoal;
    }

    private  int  scoreMinimoPessoal = 201;

    // Idades máximas
    private double idadeMaximaConsignado = 80;
    private double idadeMaximaPessoal = 75;
    private double idadeMinimaPessoal = 18;

    public double getIdadeMinimaPessoal() {
        return idadeMinimaPessoal;
    }

    public void setIdadeMinimaPessoal(double idadeMinimaPessoal) {
        this.idadeMinimaPessoal = idadeMinimaPessoal;
    }

    public double getIdadeMinimaConsignado() {
        return idadeMinimaConsignado;
    }

    public void setIdadeMinimaConsignado(double idadeMinimaConsignado) {
        this.idadeMinimaConsignado = idadeMinimaConsignado;
    }

    private double idadeMinimaConsignado = 18;

    // Percentuais
    private double margemConsignavel = 35;
    private double percentualIofFixo = 0.38;
    private double percentualIofVar = 0.0082;
    private double percentualSegFixo = 0.25;
    private double percentualSegVar = 0.005;
    private double percentualRendaPessoal = 30;
    private double percentualMinimoRefinanciamento = 20;
    private double percentualJurosMora = 0.033;
    private double percentualMultaAtraso = 2;

    //Renda minima
    private  double rendaMinimaPessoal = 1000;



    // Getters e Setters
    public double getJurosMinimoPessoal() {
        return jurosMinimoPessoal;
    }

    public void setJurosMinimoPessoal(double jurosMinimoPessoal) {
        this.jurosMinimoPessoal = jurosMinimoPessoal;
    }

    public double getJurosMaximoPessoal() {
        return jurosMaximoPessoal;
    }

    public void setJurosMaximoPessoal(double jurosMaximoPessoal) {
        this.jurosMaximoPessoal = jurosMaximoPessoal;
    }

    public double getJurosMinimoConsignado() {
        return jurosMinimoConsignado;
    }

    public void setJurosMinimoConsignado(double jurosMinimoConsignado) {
        this.jurosMinimoConsignado = jurosMinimoConsignado;
    }

    public double getJurosMaximoConsignado() {
        return jurosMaximoConsignado;
    }

    public void setJurosMaximoConsignado(double jurosMaximoConsignado) {
        this.jurosMaximoConsignado = jurosMaximoConsignado;
    }

    public double getValorMinimoPessoal() {
        return valorMinimoPessoal;
    }

    public void setValorMinimoPessoal(double valorMinimoPessoal) {
        this.valorMinimoPessoal = valorMinimoPessoal;
    }

    public double getValorMaximoPessoal() {
        return valorMaximoPessoal;
    }

    public void setValorMaximoPessoal(double valorMaximoPessoal) {
        this.valorMaximoPessoal = valorMaximoPessoal;
    }

    public double getValorMinimoConsignado() {
        return valorMinimoConsignado;
    }

    public void setValorMinimoConsignado(double valorMinimoConsignado) {
        this.valorMinimoConsignado = valorMinimoConsignado;
    }

    public int getPrazoMinimoPessoal() {
        return prazoMinimoPessoal;
    }

    public void setPrazoMinimoPessoal(int prazoMinimoPessoal) {
        this.prazoMinimoPessoal = prazoMinimoPessoal;
    }

    public int getPrazoMaximoPessoal() {
        return prazoMaximoPessoal;
    }

    public void setPrazoMaximoPessoal(int prazoMaximoPessoal) {
        this.prazoMaximoPessoal = prazoMaximoPessoal;
    }

    public int getPrazoMinimoConsignado() {
        return prazoMinimoConsignado;
    }

    public void setPrazoMinimoConsignado(int prazoMinimoConsignado) {
        this.prazoMinimoConsignado = prazoMinimoConsignado;
    }

    public int getPrazoMaximoConsignado() {
        return prazoMaximoConsignado;
    }

    public void setPrazoMaximoConsignado(int prazoMaximoConsignado) {
        this.prazoMaximoConsignado = prazoMaximoConsignado;
    }

    public double getCarenciaMaximaPessoal() {
        return carenciaMaximaPessoal;
    }

    public void setCarenciaMaximaPessoal(double carenciaMaximaPessoal) {
        this.carenciaMaximaPessoal = carenciaMaximaPessoal;
    }

    public double getCarenciaMaximaConsignado() {
        return carenciaMaximaConsignado;
    }

    public void setCarenciaMaximaConsignado(double carenciaMaximaConsignado) {
        this.carenciaMaximaConsignado = carenciaMaximaConsignado;
    }

    public double getIdadeMaximaConsignado() {
        return idadeMaximaConsignado;
    }

    public void setIdadeMaximaConsignado(double idadeMaximaConsignado) {
        this.idadeMaximaConsignado = idadeMaximaConsignado;
    }

    public double getIdadeMaximaPessoal() {
        return idadeMaximaPessoal;
    }

    public void setIdadeMaximaPessoal(double idadeMaximaPessoal) {
        this.idadeMaximaPessoal = idadeMaximaPessoal;
    }

    public double getMargemConsignavel() {
        return margemConsignavel;
    }

    public void setMargemConsignavel(double margemConsignavel) {
        this.margemConsignavel = margemConsignavel;
    }

    public double getPercentualIofFixo() {
        return percentualIofFixo;
    }

    public void setPercentualIofFixo(double percentualIofFixo) {
        this.percentualIofFixo = percentualIofFixo;
    }

    public double getPercentualIofVar() {
        return percentualIofVar;
    }

    public void setPercentualIofVar(double percentualIofVar) {
        this.percentualIofVar = percentualIofVar;
    }

    public double getPercentualSegFixo() {
        return percentualSegFixo;
    }

    public void setPercentualSegFixo(double percentualSegFixo) {
        this.percentualSegFixo = percentualSegFixo;
    }

    public double getPercentualSegVar() {
        return percentualSegVar;
    }

    public void setPercentualSegVar(double percentualSegVar) {
        this.percentualSegVar = percentualSegVar;
    }

    public double getPercentualRendaPessoal() {
        return percentualRendaPessoal;
    }

    public void setPercentualRendaPessoal(double percentualRendaPessoal) {
        this.percentualRendaPessoal = percentualRendaPessoal;
    }

    public double getPercentualMinimoRefinanciamento() {
        return percentualMinimoRefinanciamento;
    }

    public void setPercentualMinimoRefinanciamento(double percentualMinimoRefinanciamento) {
        this.percentualMinimoRefinanciamento = percentualMinimoRefinanciamento;
    }

    public double getPercentualJurosMora() {
        return percentualJurosMora;
    }

    public void setPercentualJurosMora(double percentualJurosMora) {
        this.percentualJurosMora = percentualJurosMora;
    }

    public double getPercentualMultaAtraso() {
        return percentualMultaAtraso;
    }

    public void setPercentualMultaAtraso(double percentualMultaAtraso) {
        this.percentualMultaAtraso = percentualMultaAtraso;

    }
    public double getRendaMinimaPessoal() {
        return rendaMinimaPessoal;
    }

    public void setRendaMinimaPessoal(double rendaMinimaPessoal) {
        this.rendaMinimaPessoal = rendaMinimaPessoal;
    }
}
