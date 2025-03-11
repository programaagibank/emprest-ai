package br.com.emprestai.util;

public class ParametrosEmprestimo {

    //Padrão Singleton

    ParametrosEmprestimo INSTANCE = new ParametrosEmprestimo();

    private ParametrosEmprestimo() {
    }

    public ParametrosEmprestimo getINSTANCE() {
        return INSTANCE;
    }

    private double jurosMinimoPessoal = 8.49;
    private double jurosMaximoPessoal = 9.99;
    private double jurosMinimoConsignado = 1.80;
    private double jurosMaximoConsignado = 2.14;
    private double valorMinimoPessoal = 100.00;
    private double valorMaximoPessoal = 20000;
    private double valorMinimoConsignado = 1000;
    private double prazoMinimoPessoal = 6;
    private double prazoMaximoPessoal = 30;
    private double prazoMinimoConsignado = 24;
    private double prazoMaximoConsignado = 94;
    private double carenciaMaximaPessoal = 30;
    private double carenciaMaximaConsignado = 60;
    private double idadeMaximaConsignado = 80;
    private double idadeMaximaPessoal = 75;
    private double margemConsignavel = 35;
    private double iof = 0.38;
    private double percentualRendaPessoal = 30;
    private double percentualMinimoRefinanciamento = 20;
    private double percentualJurosMora = 0.033;
    private double percentualMultaAtraso = 2;

    public double getJurosMaximoPessoal() {
        return jurosMaximoPessoal;
    }

    public void setJurosMaximoPessoal(double jurosMaximoPessoal) {
        this.jurosMaximoPessoal = jurosMaximoPessoal;
    }

    public double getJurosMinimoPessoal() {
        return jurosMinimoPessoal;
    }

    public void setJurosMinimoPessoal(double jurosMinimoPessoal) {
        this.jurosMinimoPessoal = jurosMinimoPessoal;
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

    public double getPrazoMinimoPessoal() {
        return prazoMinimoPessoal;
    }

    public void setPrazoMinimoPessoal(double prazoMinimoPessoal) {
        this.prazoMinimoPessoal = prazoMinimoPessoal;
    }

    public double getPrazoMaximoPessoal() {
        return prazoMaximoPessoal;
    }

    public void setPrazoMaximoPessoal(double prazoMaximoPessoal) {
        this.prazoMaximoPessoal = prazoMaximoPessoal;
    }

    public double getPrazoMinimoConsignado() {
        return prazoMinimoConsignado;
    }

    public void setPrazoMinimoConsignado(double prazoMinimoConsignado) {
        this.prazoMinimoConsignado = prazoMinimoConsignado;
    }

    public double getPrazoMaximoConsignado() {
        return prazoMaximoConsignado;
    }

    public void setPrazoMaximoConsignado(double prazoMaximoConsignado) {
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

    public double getIof() {
        return iof;
    }

    public void setIof(double iof) {
        this.iof = iof;
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
}
