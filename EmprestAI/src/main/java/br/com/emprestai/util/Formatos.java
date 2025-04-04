package br.com.emprestai.util;

public class Formatos {

    public static String formatCurrency(double value) {
        return String.format("R$ %.2f", value);
    }

    public static String formatCpf(String cpf) {
        if (cpf != null && cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf != null ? cpf : "";
    }
}
