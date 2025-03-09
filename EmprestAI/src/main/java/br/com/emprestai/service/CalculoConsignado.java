package br.com.emprestai.service;

public class CalculoConsignado {

    // 11.1.1 Margem Consignável
    public static void margemConsignavel(double renda, double valorEmprestimo, int parcelas) {
        double margem = renda * 0.35; 
        double parcela = valorEmprestimo / parcelas;
        if (parcela <= margem) {
            System.out.println("Parcela ok! Dentro da margem de 35% da renda.");
        } else {
            System.out.println("Parcela maior que 35% da renda!");
        }
    }

    // 11.1.2 Idade Máxima
    public static void idadeMaximaParaEmprestimoConsignado(int idade, int parcelas) {
        int idadeFinal = idade + (parcelas / 12);
        if (idadeFinal <= 80) {
            System.out.println("Idade ok! Máximo 80 anos ao final.");
        } else {
            System.out.println("Idade final ultrapassa 80 anos!");
        }
    }

    // 11.1.3 Quantidade de Parcelas
    public static void quantidadeDeParcelasParaEmprestimoConsignado(int parcelas) {
        if (parcelas >= 24 && parcelas <= 92) {
            System.out.println("Parcelas ok! Entre 24 e 92.");
        } else {
            System.out.println("Parcelas fora do limite (24 a 92)!");
        }
    }
}