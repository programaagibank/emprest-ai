package br.com.emprestai.view;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.util.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private static final Scanner scanner = new Scanner(System.in);
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
                isLoggedIn = realizarLogin();
                break;
            case 2:
                executarSeLogado(() -> System.out.println("Simulando Empréstimo Consignado..."));
                break;
            case 3:
                executarSeLogado(() -> System.out.println("Simulando Empréstimo Pessoal..."));
                break;
            case 4:
                executarSeLogado(() -> System.out.println("Fazendo Empréstimo Consignado..."));
                break;
            case 5:
                executarSeLogado(() -> System.out.println("Fazendo Empréstimo Pessoal..."));
                break;
            case 6:
                executarSeLogado(() -> System.out.println("Buscando Empréstimo..."));
                break;
            case 0:
                System.out.println("Saindo...");
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static boolean realizarLogin() {
        System.out.print("Digite seu usuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM clientes WHERE login_usuarios = ? AND senha = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, usuario);
                stmt.setString(2, senha);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Login bem-sucedido!");
                        return true;
                    } else {
                        System.out.println("Usuário ou senha inválidos.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private static void executarSeLogado(Runnable acao) {
        if (isLoggedIn) {
            acao.run();
        } else {
            System.out.println("Você precisa estar logado para acessar esta opção.");
        }
    }
}
