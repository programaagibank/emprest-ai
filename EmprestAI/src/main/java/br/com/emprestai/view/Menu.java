package br.com.emprestai.view;

import br.com.emprestai.service.LoginService;
import java.util.Scanner;

public class Menu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LoginService loginService = new LoginService();
    private static boolean isLoggedIn = false;

    public static void main(String[] args) {
        mostrarMenu();
    }

    public static void mostrarMenu() {
        while (true) {
            exibirOpcoes();

            if (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida! Digite um número correspondente às opções.");
                scanner.next(); // Limpa a entrada inválida
                continue;
            }

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consome a quebra de linha

            if (!processarOpcao(opcao)) {
                break;
            }
        }
    }

    private static void exibirOpcoes() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Logar na conta");
        if (isLoggedIn) {
            System.out.println("2. Simular Empréstimo Consignado");
            System.out.println("3. Simular Empréstimo Pessoal");
            System.out.println("4. Fazer Empréstimo Consignado");
            System.out.println("5. Fazer Empréstimo Pessoal");
            System.out.println("6. Buscar Empréstimo");
        }
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static boolean processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                isLoggedIn = loginService.exibirLogin(); // Deve retornar boolean
                break;
            case 2:
                executarSeLogado(() -> loginService.simularEmprestimoConsignado());
                break;
            case 3:
                executarSeLogado(() -> loginService.simularEmprestimoPessoal());
                break;
            case 4:
                executarSeLogado(() -> loginService.fazerEmprestimoConsignado());
                break;
            case 5:
                executarSeLogado(() -> loginService.fazerEmprestimoPessoal());
                break;
            case 6:
                executarSeLogado(() -> loginService.buscarEmprestimo());
                break;
            case 0:
                System.out.println("Saindo...");
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static void executarSeLogado(Runnable acao) {
        if (isLoggedIn) {
            acao.run();
        } else {
            System.out.println("Você precisa estar logado para acessar esta opção.");
        }
    }
}
