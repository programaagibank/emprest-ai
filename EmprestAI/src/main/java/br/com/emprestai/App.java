package br.com.emprestai;

import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.service.Menu;

import java.util.Scanner;

import static br.com.emprestai.enums.TipoEmpEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmpEnum.PESSOAL;

public class App {
    public static void main(String[] args) {
        Menu menu = new Menu();
        Scanner scanner = new Scanner(System.in);

        menu.carregarPropriedadesBanco(); // Novo mwtodo a ser adicionado

        while (true) {
            menu.mostrarMenuPrincipal(); // Novo metodo a ser adicionado
            int opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> {
                    if (menu.exibirLogin()) {
                        mostrarTipoEmprestimo(menu, scanner);
                    }
                }
                case 2 -> {
                    if (menu.exibirCriarUsuario()) {
                        mostrarTipoEmprestimo(menu, scanner);
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

    private static void mostrarTipoEmprestimo(Menu menu, Scanner scanner) {
        while (true) {
            menu.mostrarTelaTipoEmprestimo();
            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1 -> handleMenuEmprestimos(menu, scanner, CONSIGNADO);
                case 2 -> handleMenuEmprestimos(menu, scanner, PESSOAL);
                case 0 -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void handleMenuEmprestimos(Menu menu, Scanner scanner, TipoEmpEnum tipoEmp) {
        while (true) {
            if(tipoEmp == CONSIGNADO){
                menu.mostrarMenuConsignado();
                int opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1 -> menu.exibirLogin();
                    case 2 -> menu.simularEmprestimoConsignado();
                    case 3 -> menu.buscarEmprestimo();
                    case 0 -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } else if(tipoEmp == PESSOAL) {
                menu.mostrarMenuPessoal();
                int opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1 -> menu.exibirLogin();
                    case 2 -> menu.simularEmprestimoPessoal();
                    case 3 -> menu.buscarEmprestimo();
                    case 0 -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } else {
                System.out.println("Opção inválida.");
            }

        }
    }
}