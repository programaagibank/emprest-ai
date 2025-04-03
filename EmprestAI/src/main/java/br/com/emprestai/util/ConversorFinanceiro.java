package br.com.emprestai.util;

public class ConversorFinanceiro {

    public static double conversorTaxaDeJurosDiaria(double taxaDeJurosMensal) {
        return Math.pow((1 + taxaDeJurosMensal), (double) 1 / 30) - 1;
    }
}
