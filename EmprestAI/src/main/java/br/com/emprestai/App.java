package br.com.emprestai;



import br.com.emprestai.service.LoginService;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        LoginService loginService = new LoginService();
        Scanner scanner = new Scanner(System.in);

        loginService.carregarPropriedadesBanco(); // Novo método a ser adicionado

        while (true) {
            loginService.mostrarMenuPrincipal(); // Novo método a ser adicionado
            int opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> {
                    if (loginService.exibirLogin()) {
                        loginService.mostrarMenu();
                        handleMenuEmprestimos(loginService, scanner);
                    }
                }
                case 0 -> {
                    System.out.println("Saindo do sistema...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void handleMenuEmprestimos(LoginService loginService, Scanner scanner) {
        while (true) {
            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1 -> loginService.exibirLogin();
                case 2 -> loginService.simularEmprestimoConsignado();
                case 3 -> loginService.simularEmprestimoPessoal();
                case 4 -> loginService.fazerEmprestimoConsignado();
                case 5 -> loginService.fazerEmprestimoPessoal();
                case 6 -> loginService.buscarEmprestimo();
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}