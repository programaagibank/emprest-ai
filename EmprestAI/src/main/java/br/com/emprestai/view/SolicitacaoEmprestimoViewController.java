package br.com.emprestai.view;

import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class SolicitacaoEmprestimoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label     loanTypeLabel;
    @FXML private TextField loanAmountField;
    @FXML private Button    continueButton;
    @FXML private Button    backButton;
    @FXML private Button    exitButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private TipoEmprestimoEnum tipoEmprestimo;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        // Verifica se há cliente logado
        if (SessionManager.getInstance().getClienteLogado() == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick();
            return;
        }

        // Configura o campo de valor do empréstimo para aceitar apenas números
        loanAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                loanAmountField.setText(oldValue);
            }
        });
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
        if (tipoEmprestimo != null) {
            loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));
        }
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onContinueClick() {
        try {
            if (loanAmountField.getText().isEmpty()) {
                showAlert("Valor Requerido", "Por favor, informe o valor do empréstimo.");
                return;
            }

            double loanAmount = Double.parseDouble(loanAmountField.getText());

            // Carrega a tela de ofertas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ofertasView.fxml"));
            Scene scene = new Scene(loader.load(), 360, 640);
            OfertasViewController controller = loader.getController();
            controller.setOfertaData(loanAmount, tipoEmprestimo);

            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("EmprestAI - Ofertas de Empréstimo");
            stage.show();

        } catch (NumberFormatException e) {
            showAlert("Erro de Formato", "Por favor, insira um valor numérico válido.");
        } catch (IOException e) {
            showAlert("Erro", "Erro ao carregar tela de ofertas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            EmprestimoViewController emprestimoController = loader.getController();
            emprestimoController.setTipoEmprestimo(tipoEmprestimo);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Empréstimos");
            stage.show();
        } catch (IOException e) {
            showAlert("Erro", "Erro ao voltar para tela de empréstimos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            showAlert("Erro", "Erro ao voltar para tela de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}