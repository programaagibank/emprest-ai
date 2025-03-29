package br.com.emprestai.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardViewController {

    @FXML
    private Label greetingLabel;

    @FXML
    private Label debtAmount;

    @FXML
    private Label userName;

    @FXML
    private Label userCpf;

    @FXML
    private Label creditMargin;

    @FXML
    private Button consignadoButton;

    @FXML
    private Button pessoalButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button exitButton;


    @FXML
    private Button profileButton;

    // Set user data
    public void setUserData(String userName, String cpf, String debt, String creditMargin) {
        greetingLabel.setText("Olá, " + userName + "!");
        this.userName.setText("Nome: " + userName);
        this.userCpf.setText("CPF: " + cpf);
        this.debtAmount.setText("R$ " + debt);
        this.creditMargin.setText("R$ " + creditMargin);
    }

    @FXML
    private void onConsignadoClick() {
        try {
            // Carrega o arquivo FXML da tela Consignado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);

            // Obtém o Stage atual a partir do botão consignadoButton
            Stage stage = (Stage) consignadoButton.getScene().getWindow();
            stage.setScene(consignadoScene);
            stage.setTitle("EmprestAI - Consignado");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar emprestimos.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onPessoalClick() {
        System.out.println("Pessoal button clicked");
        // Add navigation logic to Pessoal loan screen
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

    public void onProfileClick(MouseEvent mouseEvent) {
    }
}