package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class CadastroClienteViewController {
    @FXML
    private TextField cpfField;
    @FXML
    private TextField nomeField;
    @FXML
    private DatePicker dataNascimentoField;
    @FXML
    private PasswordField senhaField;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private final ClienteController clienteController;

    // --------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------
    public CadastroClienteViewController() {
        this.clienteController = new ClienteController(new ClienteDAO());
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void cadastrarCliente() {
        try {
            // Captura dos dados
            String cpf = cpfField.getText();
            String nome = nomeField.getText();
            LocalDate dataNascimento = dataNascimentoField.getValue();
            String senha = senhaField.getText();

            // Validação de campos obrigatórios
            if (cpf.isEmpty() || nome.isEmpty() || senha.isEmpty() || dataNascimento == null) {
                showError("Todos os campos são obrigatórios.");
                return;
            }

            // Criação do objeto Cliente
            Cliente cliente = new Cliente();
            cliente.setCpfCliente(cpf);
            cliente.setNomeCliente(nome);
            cliente.setDataNascimento(dataNascimento);
            cliente.setSenha(senha);

            // Cadastro no banco
            Cliente clienteCadastrado = clienteController.post(cliente);

            // Armazena o cliente no SessionManager
            SessionManager.getInstance().setClienteLogado(clienteCadastrado);

            // Feedback e redirecionamento
            showInfo("Cliente cadastrado com sucesso! Redirecionando para o dashboard...");
            limparCampos();
            irParaDashboard();

        } catch (ApiException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onVoltarClick(ActionEvent event) {
        try {
            SessionManager.getInstance().clearSession(); // Limpa qualquer sessão residual
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) cpfField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao voltar para o login: " + e.getMessage());
        }
    }

    @FXML
    private void onHomeClick(ActionEvent event) {
        // Como é uma tela de cadastro, não faz sentido ir para o dashboard antes de cadastrar
        // Redireciona para o login por consistência
        onVoltarClick(event);
    }

    @FXML
    private void onExitClick(ActionEvent event) {
        try {
            SessionManager.getInstance().clearSession(); // Limpa qualquer sessão residual
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) cpfField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao sair para o login: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void limparCampos() {
        cpfField.clear();
        nomeField.clear();
        dataNascimentoField.setValue(null);
        senhaField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void irParaDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) cpfField.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle("EmprestAI - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erro ao redirecionar para o dashboard: " + e.getMessage());
        }
    }
}