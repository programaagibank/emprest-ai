package br.com.emprestai.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class DashboardController {

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
    private Button accountButton;

    @FXML
    private Button cardsButton;

    @FXML
    private Button purchasesButton;

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
        System.out.println("Consignado button clicked");
        // Add navigation logic to Consignado loan screen
    }

    @FXML
    private void onPessoalClick() {
        System.out.println("Pessoal button clicked");
        // Add navigation logic to Pessoal loan screen
    }

    @FXML
    private void onHomeClick() {
        System.out.println("Início button clicked");
    }

    @FXML
    private void onAccountClick() {
        System.out.println("Conta button clicked");
    }

    @FXML
    private void onCardsClick() {
        System.out.println("Cartões button clicked");
    }

    @FXML
    private void onPurchasesClick() {
        System.out.println("Compras button clicked");
    }

    @FXML
    private void onProfileClick() {
        System.out.println("Perfil button clicked");
    }
}