package br.com.emprestai.config;

public class Config {
    // Pessoal
    public static final double PESS_PERCENT_RENDA = 30.0;
    public static final double PESS_JUROS_MIN = 8.49;
    public static final double PESS_JUROS_MAX = 9.99;
    public static final int PESS_VALOR_MIN = 100;
    public static final int PESS_VALOR_MAX = 20000;
    public static final int PESS_PRAZO_MIN = 6;
    public static final int PESS_PRAZO_MAX = 30;
    public static final int PESS_IDADE_MAX = 75;
    public static final int PESS_CARENCIA_MAX = 30;

    // Consignado
    public static final double CONS_MARGEM = 35.0;
    public static final double CONS_JUROS_MIN = 1.80;
    public static final double CONS_JUROS_MAX = 2.14;
    public static final int CONS_VALOR_MIN = 1000;
    public static final int CONS_PRAZO_MIN = 24;
    public static final int CONS_PRAZO_MAX = 92;
    public static final int CONS_IDADE_MAX = 80;
    public static final int CONS_CARENCIA_MAX = 60;

    // Gerais
    public static final double IOF = 0.38;
    public static final double PERCENTUAL_REFINANCIAMENTO = 20.0;
    public static final double PERCENTUAL_JUROS_MORA = 0.033;
    public static final double PERCENTUAL_MULTA_ATRASO = 2.0;
}