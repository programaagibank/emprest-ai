package br.com.emprestai.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CadastroClienteViewController {
    @FXML
    private TextField cpfField;
    @FXML
    private TextField nomeField;
    @FXML
    private DatePicker dataNascimentoField;
    @FXML
    private PasswordField senhaField;

    @FXML
    private void cadastrarCliente(ActionEvent event) {
        // Lógica para cadastrar cliente
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