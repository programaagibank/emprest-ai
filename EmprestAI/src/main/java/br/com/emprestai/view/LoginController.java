package br.com.emprestai.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

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
        if ("admin".equals(cpf) && "1234".equals(password)) { // Exemplo simples
            try {
                // Carrega a tela principal com o MainController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/emprestai/view/dashboard.fxml"));
                Scene mainScene = new Scene(loader.load(), 360, 640); // Mobile phone resolution

                Stage stage = (Stage) cpfField.getScene().getWindow();
                stage.setScene(mainScene);
                stage.setTitle("EmprestAI - Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Login inválido!");
        }
        // Add your authentication logic here

    }

    @FXML
    private void onPrivacyLinkClick() {
        System.out.println("Política de Privacidade link clicked");
        // Add logic to open the privacy policy (e.g., open a URL in a browser)
    }
}