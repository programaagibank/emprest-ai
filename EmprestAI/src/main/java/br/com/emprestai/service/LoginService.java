package br.com.emprestai.service;

import br.com.emprestai.util.DatabaseUtil;
import br.com.emprestai.model.Login;
import br.com.emprestai.util.EmprestimoParams;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    public static String senhaHash(String plainPassword) {
        // Gera um hash com um salt automático (work factor padrão é 10, mas pode ser
        // ajustado)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checarSenha(String senhaEnviada, String senhaHash) {
        // Verifica se a senha em texto puro corresponde ao hash
        return BCrypt.checkpw(senhaEnviada, senhaHash);
    }

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmprestimoParams params = EmprestimoParams.getInstance();

    public boolean exibirLogin() {
        System.out.println("Digite seu usuário:");
        String usuario = scanner.nextLine();
        System.out.println("Digite sua senha:");
        String senha = scanner.nextLine();

        if (authenticateUser(usuario, senha)) {
            System.out.println("Login realizado com sucesso!");
            return true;
        } else {
            System.out.println("Usuário ou senha incorretos.");
            return false;
        }
    }

    private boolean authenticateUser(String usuario, String senha) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Obtém o hash da senha armazenada no banco de dados
                String senhaHash = resultSet.getString("password");

                // Verifica se a senha fornecida corresponde ao hash armazenado
                return BCrypt.checkpw(senha, senhaHash);
            } else {
                return false;
            }
        } catch (SQLException e) {
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
