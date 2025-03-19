package br.com.emprestai.view;

import java.util.Scanner;

public class TelaSimulacaoEntrada {
    private static final Scanner scanner = new Scanner(System.in);

    public static void mostrarTelaSimulacao(String tipoEmprestimo) {
        System.out.println("\n--- Tela de Simulação de Empréstimo (" + tipoEmprestimo + ") ---");


        ConfigEmprestimo config;
        if (tipoEmprestimo.equalsIgnoreCase("pessoal")) {
            config = new ConfigEmprestimo(100.0, 20000.0, 6, 30, 8.49, 9.99, 30);
        } else if (tipoEmprestimo.equalsIgnoreCase("consignado")) {
            config = new ConfigEmprestimo(1000.0, 50000.0, 24, 92, 1.80, 2.14, 60);
        } else {
            System.out.println("Tipo de empréstimo inválido!");
            return;
        }

        // Solicitação dos dados para empréstimo
        double valor = obterNumero("Digite o valor do empréstimo: R$ ", config.valorMinimo, config.valorMaximo);
        int parcelas = obterNumeroInteiro("Digite o número de parcelas: ", config.parcelasMin, config.parcelasMax);
        double taxa = obterNumero("Digite a taxa de juros mensal (%): ", config.taxaMin, config.taxaMax);

      
        exibirResultado(tipoEmprestimo, valor, parcelas, taxa, config.carencia);
    }

    private static double obterNumero(String mensagem, double minimo, double maximo) {
        while (true) {
            System.out.print(mensagem);
            try {
                double valor = Double.parseDouble(scanner.nextLine().replace(",", ".").trim());
                if (valor >= minimo && valor <= maximo) return valor;
                System.out.printf("Valor fora dos limites! Deve estar entre %.2f e %.2f.\n", minimo, maximo);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número válido.");
            }
        }
    }

    private static int obterNumeroInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= minimo && valor <= maximo) return valor;
                System.out.printf("Valor fora dos limites! Deve estar entre %d e %d.\n", minimo, maximo);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número inteiro válido.");
            }
        }
    }

    private static void exibirResultado(String tipoEmprestimo, double valor, int parcelas, double taxa, int carencia) {
        double valorParcela = calcularParcela(valor, parcelas, taxa);
        System.out.printf("\n--- Resultado da Simulação de Empréstimo %s ---\n", tipoEmprestimo);
        System.out.printf("Valor: R$ %.2f | Parcelas: %d | Taxa: %.2f%% | Carência: %d dias\n", valor, parcelas, taxa, carencia);
        System.out.printf("Parcela aproximada: R$ %.2f\n", valorParcela);
    }

    private static double calcularParcela(double valor, int parcelas, double taxa) {
        double jurosDecimal = taxa / 100;
        return valor * (jurosDecimal * Math.pow(1 + jurosDecimal, parcelas)) /
                (Math.pow(1 + jurosDecimal, parcelas) - 1);
    }

    // Classe auxiliar para configurar limites
    private static class ConfigEmprestimo {
        double valorMinimo, valorMaximo, taxaMin, taxaMax;
        int parcelasMin, parcelasMax, carencia;

        ConfigEmprestimo(double valorMinimo, double valorMaximo, int parcelaMinima, int parcelaMaxima, double taxaMinima, double taxaMaxima, int carencia) {
            this.valorMinimo = valorMinimo;
            this.valorMaximo = valorMaximo;
            this.parcelasMin = parcelaMinima;
            this.parcelasMax = parcelaMaxima;
            this.taxaMin = taxaMinima;
            this.taxaMax = taxaMaxima;
            this.carencia = carencia;
        }
    }


    public static void main(String[] args) {
        //mostrarTelaSimulacao("pessoal");
         mostrarTelaSimulacao("consignado");
    }
}