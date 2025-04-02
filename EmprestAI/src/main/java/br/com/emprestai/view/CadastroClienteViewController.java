package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
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
            if (cpf.isEmpty() || nome.isEmpty() || senha.isEmpty() ||
                    dataNascimento == null) {
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
            clienteController.post(cliente);

            // Feedback e limpeza
            showInfo("Cliente cadastrado com sucesso!");
            limparCampos();

        } catch (ApiException e) {
            showError(e.getMessage());
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

    @FXML
    private void onVoltarClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) cpfField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHomeClick(ActionEvent event) {
        // Navegar para a tela inicial/dashboard
    }

    @FXML
    private void onExitClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            DashboardViewController dashboardController = loader.getController();
            Stage stage = (Stage) cpfField.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}