package br.com.emprestai.service;

import br.com.emprestai.App;
import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.EmprestimoParams;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmprestimoParams params = EmprestimoParams.getInstance();
    private Properties properties = new Properties();
    private ClienteController clienteController;

    public LoginService() {
        ClienteDAO clienteDAO = new ClienteDAO();
        this.clienteController = new ClienteController(clienteDAO);
    }

    // Método adicionado do App.java
    public void carregarPropriedadesBanco() {
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo database.properties não encontrado no classpath.");
            }
            properties.load(input);
            System.out.println("Conectando ao banco de dados em: " + properties.getProperty("db.url"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar configurações do banco de dados: " + e.getMessage());
        }
    }

    // Método adicionado do App.java (menu principal simplificado)
    public void mostrarMenuPrincipal() {
        System.out.println("\n===== Sistema de Gerenciamento de Clientes =====");
        System.out.println("1 - Logar");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    public boolean exibirLogin() {
        System.out.println("Digite seu CPF:"); // Ajustado para CPF
        String cpf = scanner.nextLine();
        System.out.println("Digite sua senha:");
        String senha = scanner.nextLine();

        if (authenticateUser(cpf, senha)) {
            System.out.println("Login realizado com sucesso!");
            try {
                Cliente cliente = clienteController.buscarClientePorCPF(cpf);
                System.out.println("Bem-vindo, " + cliente.getNomecliente() + "!");
            } catch (Exception e) {
                System.out.println("Bem-vindo ao sistema!");
            }
            return true;
        } else {
            System.out.println("CPF ou senha incorretos.");
            return false;
        }
    }

    private boolean authenticateUser(String cpf, String senha) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE cpf = ?"; // Ajustado para CPF
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cpf);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String senhaHash = resultSet.getString("password");
                return BCrypt.checkpw(senha, senhaHash);
            }
            return false;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void mostrarMenu() {
        System.out.println("Bem-vindo ao Gerenciador de Empréstimos");
        System.out.println("Escolha uma opção:");
        System.out.println("1. Logar na conta");
        System.out.println("2. Simular Empréstimo Consignado");
        System.out.println("3. Simular Empréstimo Pessoal");
        System.out.println("4. Fazer Empréstimo Consignado");
        System.out.println("5. Fazer Empréstimo Pessoal");
        System.out.println("6. Buscar Empréstimo");
        System.out.println("0. Sair");
    }

    public void mostrarTelaTipoEmprestimo() {
        System.out.println("Escolha o tipo de empréstimo:");
        System.out.println("1. Consignado");
        System.out.println("2. Pessoal");
    }

    public void simularEmprestimoConsignado() {
        System.out.println("Simulação de Empréstimo Consignado");
        System.out.println("Valor: R$ " + params.getValorMinimoConsignado());
        System.out.println("Taxa de juros: " + params.getJurosMinimoConsignado() + "% ao mês");
        System.out.println("Parcelas: " + params.getPrazoMinimoConsignado() + "x de R$ "
                + (params.getValorMinimoConsignado() / params.getPrazoMinimoConsignado()));
    }

    public void simularEmprestimoPessoal() {
        System.out.println("Simulação de Empréstimo Pessoal");
        System.out.println("Valor: R$ " + params.getValorMinimoPessoal());
        System.out.println("Taxa de juros: " + params.getJurosMinimoPessoal() + "% ao mês");
        System.out.println("Parcelas: " + params.getPrazoMinimoPessoal() + "x de R$ "
                + (params.getValorMinimoPessoal() / params.getPrazoMinimoPessoal()));
    }

    public void fazerEmprestimoConsignado() {
        System.out.println("Fazendo Empréstimo Consignado...");
        System.out.println("Empréstimo consignado aprovado!");
    }

    public void fazerEmprestimoPessoal() {
        System.out.println("Fazendo Empréstimo Pessoal...");
        System.out.println("Empréstimo pessoal aprovado!");
    }

    public void buscarEmprestimo() {
        System.out.println("Buscando Empréstimo...");
        System.out.println("Empréstimo consignado: R$ " + params.getValorMinimoConsignado() + " - "
                + params.getPrazoMinimoConsignado() + "x de R$ "
                + (params.getValorMinimoConsignado() / params.getPrazoMinimoConsignado()));
        System.out.println("Empréstimo pessoal: R$ " + params.getValorMinimoPessoal() + " - "
                + params.getPrazoMinimoPessoal() + "x de R$ "
                + (params.getValorMinimoPessoal() / params.getPrazoMinimoPessoal()));
    }
}