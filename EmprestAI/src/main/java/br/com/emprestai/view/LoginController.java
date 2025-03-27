package br.com.emprestai.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LoginController {

    @FXML
    private Label greetingLabel;

    @FXML
    private TextField cpfField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink privacyLink;

    @FXML
    public void initialize() {
        // Create a TextFlow to style the greeting with a bold name
        TextFlow textFlow = new TextFlow();
        Text part1 = new Text("Que bom ter você de volta,\n");
        part1.setStyle("-fx-font-size: 20px; -fx-fill: #333333;");

        Text name = new Text("Josue!");
        name.setStyle("-fx-font-size: 20px; -fx-fill: #333333; -fx-font-weight: bold;");

        textFlow.getChildren().addAll(part1, name);
        greetingLabel.setGraphic(textFlow);
        greetingLabel.setText(""); // Clear the original text since we're using TextFlow
    }

    @FXML
    private void onLoginButtonClick() {
        String cpf = cpfField.getText();
        String password = passwordField.getText();
        System.out.println("CPF: " + cpf);
        System.out.println("Senha: " + password);
        // Add your authentication logic here

    }

    @FXML
    private void onPrivacyLinkClick() {
        System.out.println("Política de Privacidade link clicked");
        // Add logic to open the privacy policy (e.g., open a URL in a browser)
    }
}