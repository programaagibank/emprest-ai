package br.com.emprestai.view;

import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmacaoSucessoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private BorderPane confirmacaoContainer;
    @FXML private Label mensagemLabel;
    @FXML private Button voltarButton;
    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private Button exitButton;

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        // Remover foco automático
        javafx.application.Platform.runLater(() -> confirmacaoContainer.requestFocus());
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setMensagem(String mensagem) {
        mensagemLabel.setText(mensagem);
        mensagemLabel.setStyle("-fx-text-fill: #008000;"); // Verde para sucesso
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onVoltarClick() {
        irParaDashboard();
    }

    @FXML
    private void onHomeClick() {
        irParaDashboard();
    }

    @FXML
    private void onProfileClick() {
        System.out.println("Navegar para a tela de perfil (não implementado).");
        mensagemLabel.setText("Funcionalidade de perfil ainda não implementada.");
        mensagemLabel.setStyle("-fx-text-fill: #FF0000;"); // Vermelho para erro
    }

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 700);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao redirecionar para login: " + e.getMessage());
            mensagemLabel.setStyle("-fx-text-fill: #FF0000;"); // Vermelho para erro
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void irParaDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load(), 400, 700);
            Stage stage = (Stage) voltarButton.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle("EmprestAI - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao voltar para o dashboard: " + e.getMessage());
            mensagemLabel.setStyle("-fx-text-fill: #FF0000;"); // Vermelho para erro
        }
    }
}