package br.com.emprestai;

import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.view.Menu;

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
                    Cliente cliente = menu.exibirLogin();
                    if (cliente != null) {
                        mostrarTipoEmprestimo(menu, scanner, cliente);
                    }
                }
                case 2 -> {
                    Cliente cliente = menu.exibirCriarUsuario();
                    if (cliente != null) {
                        mostrarTipoEmprestimo(menu, scanner, cliente);
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

    private static void mostrarTipoEmprestimo(Menu menu, Scanner scanner, Cliente cliente) {
        while (true) {
            menu.mostrarTelaTipoEmprestimo();
            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1 -> handleMenuEmprestimos(menu, scanner, CONSIGNADO, cliente);
                case 2 -> handleMenuEmprestimos(menu, scanner, PESSOAL, cliente);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void handleMenuEmprestimos(Menu menu, Scanner scanner, TipoEmpEnum tipoEmp, Cliente cliente) {
        while (true) {
            if (tipoEmp == CONSIGNADO) {
                menu.mostrarMenuConsignado();
            } else {
                menu.mostrarMenuPessoal();
            }
            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1 -> handleObterEmprestimo(menu, scanner, tipoEmp, cliente);
                case 2 -> menu.buscarEmprestimo();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void handleObterEmprestimo(Menu menu, Scanner scanner, TipoEmpEnum tipoEmp, Cliente cliente) {
        Emprestimo emprestimo = menu.simularEmprestimo(cliente, tipoEmp);
        menu.mostrarMenuContratar();
        while (true) {
            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1 -> menu.mostrarConfirmarContrato(emprestimo);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}