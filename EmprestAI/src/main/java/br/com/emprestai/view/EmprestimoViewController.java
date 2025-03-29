package br.com.emprestai.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class EmprestimoViewController {

    @FXML
    private Circle statusCircle;

    @FXML
    private Label loanStatus;

    @FXML
    private Button homeButton;

    @FXML
    private Button exitButton;

    @FXML
    private void initialize() {
        updateStatus(loanStatus.getText()); // Chama o método com o texto inicial
    }

    public void updateStatus(String newStatus) {
        loanStatus.setText(newStatus);
        // Remove todas as classes de cor anteriores
        statusCircle.getStyleClass().removeAll("green", "red");
        // Garante que a classe base esteja presente
        if (!statusCircle.getStyleClass().contains("status-circle")) {
            statusCircle.getStyleClass().add("status-circle");
        }

        // Aplica a cor com base no status
        switch (newStatus.toLowerCase()) {
            case "ativo":
                statusCircle.getStyleClass().add("green");
                break;
            case "atrasado":
                statusCircle.getStyleClass().add("red");
                break;
            default:
                // Deixa a cor padrão (gray) se não for nenhum dos casos
                break;
        }
    }

    @FXML
    private void onHomeClick() {
        try {
            // Corrigido o caminho do FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);

            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar dashboard.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onExitClick() {
        try {
            // Corrigido o caminho do FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);

            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar login.fxml: " + e.getMessage());
        }
    }

    public void onProfileClick(ActionEvent actionEvent) {
    }

    public void onClickDetalhes(ActionEvent actionEvent) {
    }
}
