package br.com.emprestai;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.LoginController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Login;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Carregar propriedades do banco de dados
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo database.properties não encontrado no classpath.");
            }
            properties.load(input);
        } catch (Exception e) {
            System.out.println("Erro ao carregar configurações do banco de dados: " + e.getMessage());
            return;
        }

        // Exemplo de uso das propriedades carregadas
        String dbUrl = properties.getProperty("db.url");
        String dbUsername = properties.getProperty("db.username");
        String dbPassword = properties.getProperty("db.password");

        System.out.println("Conectando ao banco de dados em: " + dbUrl);

        ClienteDAO clienteDAO = new ClienteDAO();
        ClienteController clienteController = new ClienteController(clienteDAO);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Logar");
            System.out.println("2 - Cadastrar Usuário");
            System.out.println("0 - Sair");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (opcao) {
                case 1 -> {
                    System.out.print("Digite o CPF: ");
                    String cpf = scanner.nextLine();
                    System.out.print("Digite a senha: ");
                    String senha = scanner.nextLine();

                    try {
                        if (LoginController.validaLogin(cpf, senha)) {
                            System.out.println("Login realizado com sucesso!");
                        } else {
                            System.out.println("CPF ou senha inválidos.");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.print("Digite o CPF: ");
                    String cpf = scanner.nextLine();
                    System.out.print("Digite o nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Digite a renda mensal líquida: ");
                    double rendaMensal = scanner.nextDouble();
                    scanner.nextLine(); // Consumir a quebra de linha
                    System.out.print("Digite a data de nascimento (AAAA-MM-DD): ");
                    LocalDate dataNascimento = LocalDate.parse(scanner.nextLine());
                    System.out.print("Digite a senha: ");
                    String senha = scanner.nextLine();

                    try {
                        Cliente novoCliente = new Cliente(null, cpf, nome, rendaMensal, dataNascimento, 0, 0, null, 0);
                        Login login = LoginController.criarLogin(cpf, senha);
                        clienteController.criarCliente(novoCliente);
                        System.out.println("Cliente cadastrado com sucesso!");
                    } catch (Exception e) {
                        System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
                    }
                }
                case 0 -> {
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}
