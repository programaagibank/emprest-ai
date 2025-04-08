package br.com.emprestai.util;


import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(LOCALE_BR);

    /**
     * Formata um valor para moeda brasileira (R$)
     * @param valor Valor a ser formatado
     * @return String formatada no padrão de moeda brasileira
     */
    public static String formatarMoeda(double valor) {
        return CURRENCY_FORMAT.format(valor);
    }

    /**
     * Formata um CPF com máscara (XXX.XXX.XXX-XX)
     * @param cpf CPF sem formatação
     * @return CPF formatado
     */
    public static String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9);
    }

    /**
     * Formata um número com duas casas decimais
     * @param valor Valor a ser formatado
     * @return String com o valor formatado com duas casas decimais
     */
    public static String formatarDecimal(double valor) {
        NumberFormat format = NumberFormat.getNumberInstance(LOCALE_BR);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(valor);
    }

    /**
     * Remove formatação de um CPF
     * @param cpf CPF formatado
     * @return CPF sem formatação
     */
    public static String removerFormatacaoCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }
}
