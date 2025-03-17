package br.com.emprestai.view;

import br.com.emprestai.model.Login;

import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private Login login;

    public Menu(Login login) {
        this.scanner = new Scanner(System.in);
        this.login = login;
    }

    public void mostrarMenuPrincipal() {
        boolean sair = false;

        while (!sair) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Simular Empréstimo");
            System.out.println("2. Consultar Empréstimo");
            System.out.println("3. Pagar Parcela");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    mostrarTelaTipoEmp();
                    break;
                case 2:
                    mostrarTelaBuscaEmp();
                    break;
                case 3:
                    // Implementar pagamento de parcela
                    System.out.println("Funcionalidade de pagamento em desenvolvimento");
                    break;
                case 4:
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void mostrarTelaTipoEmp() {
        System.out.println("\n=== TIPO DE EMPRÉSTIMO ===");
        System.out.println("1. Pessoal");
        System.out.println("2. Consignado");
        System.out.print("Escolha o tipo de empréstimo: ");

        int tipo = scanner.nextInt();
        scanner.nextLine();

        if (tipo == 1 || tipo == 2) {
            telaSimulacaoEntrada(tipo);
        } else {
            System.out.println("Tipo inválido!");
        }
    }

    private void telaSimulacaoEntrada(int tipo) {
        System.out.println("\n=== SIMULAÇÃO DE EMPRÉSTIMO ===");
        System.out.print("Valor desejado: ");
        double valor = scanner.nextDouble();
        System.out.print("Número de parcelas: ");
        int parcelas = scanner.nextInt();
        scanner.nextLine();

        // Aqui você adicionaria mais dados necessários conforme o tipo
        telaSimulacaoSaida(valor, parcelas, tipo);
    }

    private void telaSimulacaoSaida(double valor, int parcelas, int tipo) {
        // Simulação de cálculo - substitua pela sua lógica real
        System.out.println("\n=== RESULTADO DA SIMULAÇÃO ===");
        System.out.println("Valor: R$" + valor);
        System.out.println("Parcelas: " + parcelas);
        System.out.println("Tipo: " + (tipo == 1 ? "Pessoal" : "Consignado"));

        // Simulação de aprovação
        boolean aprovado = true; // Substitua pela lógica real
        if (aprovado) {
            System.out.print("Deseja efetuar o empréstimo? (S/N): ");
            String resposta = scanner.nextLine();
            if (resposta.equalsIgnoreCase("S")) {
                System.out.println("Empréstimo efetuado com sucesso!");
            }
        } else {
            System.out.println("Empréstimo não aprovado.");
        }
    }

    private void mostrarTelaBuscaEmp() {
        System.out.println("\n=== CONSULTA DE EMPRÉSTIMO ===");
        System.out.print("Digite o número do contrato: ");
        String numeroContrato = scanner.nextLine();

        // Simulação de busca - substitua pela lógica com banco
        boolean encontrado = simularBuscaContrato(numeroContrato);
        mostrarRetornoEmp(numeroContrato, encontrado);
    }

    private void mostrarRetornoEmp(String numeroContrato, boolean encontrado) {
        if (encontrado) {
            System.out.println("\n=== DADOS DO EMPRÉSTIMO ===");
            System.out.println("Contrato: " + numeroContrato);
            // Mostrar outros dados do empréstimo
        } else {
            System.out.println("Número de contrato não existe");
            mostrarTelaBuscaEmp(); // Mantém na tela de busca
        }
    }

    private boolean simularBuscaContrato(String numero) {
        // Simulação - substitua pela busca real no banco
        return numero.length() > 0; // Exemplo simples
    }
}
