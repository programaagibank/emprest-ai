package br.com.emprestai.view;

import br.com.emprestai.App;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import static br.com.emprestai.enums.TipoEmpEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmpEnum.PESSOAL;

public class MenuPrincipalView {
    private static final Scanner scanner = new Scanner(System.in);
    private final Properties properties = new Properties();
    private ClienteView clienteView;
    private EmprestimoView emprestimoView;
    private PagamentoView pagamentoView;
    private ConsultaView consultaView;
    private Cliente cliente;
    private Emprestimo emprestimo;

    public MenuPrincipalView(ClienteView clienteView, EmprestimoView emprestimoView,
                             PagamentoView pagamentoView, ConsultaView consultaView) {
        this.clienteView = clienteView;
        this.emprestimoView = emprestimoView;
        this.pagamentoView = pagamentoView;
        this.consultaView = consultaView;
    }

    // Configuração do Banco
    public void carregarPropriedadesBanco() {
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("\033[1;31mErro: Arquivo database.properties não encontrado!\033[0m");
            }
            properties.load(input);
            System.out.println("\033[1;32m✓ Conectado ao banco em: " + properties.getProperty("db.url") + "\033[0m");
        } catch (Exception e) {
            System.out.println("\033[1;31m✗ Erro ao carregar configurações: " + e.getMessage() + "\033[0m");
        }
    }

    // Boas-vindas Estilizado
    public static void printBemVindo() {
        System.out.println("\n\033[1;34m" + "=".repeat(80) + "\033[0m");
        System.out.println("\033[1;34m███████ ███    ███ ██████  ██████  ███████ ███████ ████████            █████  ██\033[0m");
        System.out.println("\033[1;34m██      ████  ████ ██   ██ ██   ██ ██      ██         ██              ██   ██ ██\033[0m");
        System.out.println("\033[1;34m█████   ██ ████ ██ ██████  ██████  █████   ███████    ██              ███████ ██\033[0m");
        System.out.println("\033[1;34m██      ██  ██  ██ ██      ██   ██ ██           ██    ██              ██   ██ ██\033[0m");
        System.out.println("\033[1;34m███████ ██      ██ ██      ██   ██ ███████ ███████    ██       ██     ██   ██ ██\033[0m");
        System.out.println("\033[1;34m" + "=".repeat(80) + "\033[0m");
        System.out.println("\033[1;36m| " + " ".repeat(25) + "OLÁ, SEJA BEM-VINDO AO EMPRÉSTIMO!" + " ".repeat(25) + " |\033[0m");
        System.out.println("\033[1;34m" + "=".repeat(80) + "\033[0m");
    }

    // Menu Principal
    public void mostrarMenuPrincipal() {
        printBemVindo();
        System.out.println("\n\033[1;33m=== MENU PRINCIPAL ===\033[0m");
        System.out.println("1 - Logar");
        System.out.println("2 - Criar Usuário");
        System.out.println("0 - Sair");
        System.out.print("\n\033[1;32m➜ Escolha uma opção: \033[0m");
    }

    public void inicio() {
        while (true) {
            mostrarMenuPrincipal();
            int opcao = lerOpcaoUsuario(scanner);

            switch (opcao) {
                case 1 -> {
                    try {
                        cliente = clienteView.exibirLogin();
                        System.out.println("\033[1;32m✓ Login realizado com sucesso!\033[0m");
                    } catch (ApiException e) {
                        System.out.println("\033[1;31m✗ Erro no login: " + e.getMessage() + "\033[0m");
                    }
                    if (cliente == null) return;
                    tipoEmprestimo();
                }
                case 2 -> {
                    try {
                        cliente = clienteView.exibirCriarUsuario();
                        System.out.println("\033[1;32m✓ Usuário criado com sucesso!\033[0m");
                    } catch (ApiException e) {
                        System.out.println("\033[1;31m✗ Erro ao criar usuário: " + e.getMessage() + "\033[0m");
                    }
                    if (cliente == null) return;
                    tipoEmprestimo();
                }
                case 0 -> {
                    System.out.println("\n\033[1;33m Até logo! \033[0m");
                    return;
                }
                default -> System.out.println("\033[1;31m⚠ Opção inválida! Tente novamente.\033[0m");
            }
            System.out.println();
        }
    }

    // Menu Tipo de Empréstimo
    private void tipoEmprestimo() {
        while (true) {
            emprestimoView.mostrarTelaTipoEmprestimo();
            int opcao = lerOpcaoUsuario(scanner);

            switch (opcao) {
                case 1 -> telaEmprestimo(CONSIGNADO);
                case 2 -> telaEmprestimo(PESSOAL);
                case 0 -> {
                    System.out.println("\n\033[1;33mVoltando ao menu principal...\033[0m");
                    return;
                }
                default -> System.out.println("\033[1;31m⚠ Opção inválida! Tente novamente.\033[0m");
            }
            System.out.println();
        }
    }

    // Tela de Empréstimo
    private void telaEmprestimo(TipoEmpEnum tipoEmprestimo) {
        while (true) {
            if (tipoEmprestimo == CONSIGNADO) {
                emprestimoView.mostrarMenuConsignado();
            } else {
                emprestimoView.mostrarMenuPessoal();
            }

            int opcao = lerOpcaoUsuario(scanner);

            switch (opcao) {
                case 1 -> {
                    emprestimo = emprestimoView.simularEmprestimo(cliente, tipoEmprestimo);
                    emprestimo.setCliente(cliente);
                    if (emprestimo == null) return;
                    contratarEmprestimo();
                }
                case 2 -> {
                    consultaView.buscarEmprestimo();
                    buscarEmprestimo();
                }
                case 0 -> {
                    System.out.println("\n\033[1;33mVoltando...\033[0m");
                    return;
                }
                default -> System.out.println("\033[1;31m⚠ Opção inválida! Tente novamente.\033[0m");
            }
            System.out.println();
        }
    }

    // Contratar Empréstimo
    private void contratarEmprestimo() {
        while (true) {
            emprestimoView.mostrarMenuContratar();
            int opcao = lerOpcaoUsuario(scanner);

            switch (opcao) {
                case 1 -> {
                    emprestimoView.mostrarConfirmarContrato(emprestimo);
                    System.out.println("\033[1;32m✓ Contrato confirmado com sucesso!\033[0m");
                    return;
                }
                case 0 -> {
                    System.out.println("\n\033[1;33mVoltando...\033[0m");
                    return;
                }
                default -> System.out.println("\033[1;31m⚠ Opção inválida! Tente novamente.\033[0m");
            }
            System.out.println();
        }
    }

    // Buscar Empréstimo
    private void buscarEmprestimo() {
        while (true) {
            pagamentoView.mostrarMenuPagar();
            int opcao = lerOpcaoUsuario(scanner);

            switch (opcao) {
                case 1 -> {
                    pagamentoView.exibePagarParcelas();
                    System.out.println("\033[1;32m✓ Operação realizada!\033[0m");
                    return;
                }
                case 0 -> {
                    System.out.println("\n\033[1;33mVoltando...\033[0m");
                    return;
                }
                default -> System.out.println("\033[1;31m⚠ Opção inválida! Tente novamente.\033[0m");
            }
            System.out.println();
        }
    }

    // Leitura de Opção
    private static int lerOpcaoUsuario(Scanner scanner) {
        try {
            System.out.print("\033[1;32m➜ Digite sua opção: \033[0m");
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("\033[1;31m✗ Entrada inválida! Por favor, digite um número.\033[0m");
            return -1;
        }
    }
}