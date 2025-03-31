package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.LoginController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label       greetingLabel;
    @FXML private TextField   cpfField;
    @FXML private PasswordField passwordField;
    @FXML private Button      loginButton;
    @FXML private Hyperlink   criarConta;
    @FXML private Hyperlink   privacyLink;
    @FXML private VBox        errorBox;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private Cliente           clienteLogado;
    private LoginController   loginController = new LoginController(new ClienteController(new ClienteDAO()));

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    public void initialize() {
        // Setup greeting label
        TextFlow textFlow = new TextFlow();
        Text part1 = new Text("Que bom ter você de volta,\n");
        part1.setStyle("-fx-font-size: 20px; -fx-fill: #333333;");

        Text name = new Text();
        name.setStyle("-fx-font-size: 20px; -fx-fill: #333333; -fx-font-weight: bold;");

        textFlow.getChildren().addAll(part1, name);
        greetingLabel.setGraphic(textFlow);
        greetingLabel.setText("");

        // CPF field formatting
        cpfField.setPromptText("Ex: 123.456.789-00");
        cpfField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                String numbersOnly = newValue.replaceAll("[^0-9]", "");
                if (numbersOnly.length() > 11) {
                    numbersOnly = numbersOnly.substring(0, 11);
                }
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < numbersOnly.length(); i++) {
                    if (i == 3 || i == 6) {
                        formatted.append(".");
                    } else if (i == 9) {
                        formatted.append("-");
                    }
                    formatted.append(numbersOnly.charAt(i));
                }

                if (!formatted.toString().equals(newValue)) { // Evita recursão infinita
                    cpfField.setText(formatted.toString());
                    cpfField.positionCaret(formatted.length());
                }
            });
        });

        // Password field formatting
        passwordField.setPromptText("6 dígitos");
        passwordField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            String numbersOnly = newValue.replaceAll("[^0-9]", "");
            if (numbersOnly.length() > 6) {
                numbersOnly = numbersOnly.substring(0, 6);
            }
            passwordField.setText(numbersOnly);
            passwordField.positionCaret(numbersOnly.length());
        });
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onLoginButtonClick() {
        String cpf = cpfField.getText().replaceAll("[^0-9]", "");
        String password = passwordField.getText();

        ClienteController clienteController = new ClienteController(new ClienteDAO());
        clienteLogado = loginController.autenticaLogin(cpf, password);

        if (clienteLogado != null) {
            try {
                errorBox.setVisible(false);
                errorBox.setManaged(false);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Scene mainScene = new Scene(loader.load(), 360, 640);

                DashboardViewController dashboardController = loader.getController();
                dashboardController.setClienteLogado(clienteLogado);

                Stage stage = (Stage) criarConta.getScene().getWindow();
                stage.setScene(mainScene);
                stage.setTitle("EmprestAI - Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorBox.setVisible(true);
            errorBox.setManaged(true);
        }
    }

    @FXML
    private void onCriarContaClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cadastroCliente.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Cadastro de Cliente");
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPrivacyLinkClick() {
        System.out.println("Política de Privacidade link clicked");
    }
}