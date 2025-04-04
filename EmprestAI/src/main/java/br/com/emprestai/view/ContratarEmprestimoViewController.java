package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContratarEmprestimoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label       confirmationTitle;
    @FXML private Label       loanTypeLabel;
    @FXML private Label       loanAmountLabel;
    @FXML private Label       installmentsLabel;
    @FXML private Label       installmentValueLabel;
    @FXML private Label       interestRateLabel;
    @FXML private Label       totalAmountLabel;
    @FXML private Label       iofValueLabel;
    @FXML private Label       insuranceValueLabel;
    @FXML private Label       contractDateLabel;
    @FXML private Label       releaseDateLabel;
    @FXML private Label       firstPaymentDateLabel;
    @FXML private CheckBox    termsCheckBox;
    @FXML private CheckBox    dataCheckBox;
    @FXML private Button      confirmButton;
    @FXML private Button      cancelButton;
    @FXML private Button      homeButton;
    @FXML private Button      backButton;
    @FXML private Button      exitButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private Emprestimo emprestimoParaContratar;
    private EmprestimoController emprestimoController;

    // Formatters
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final NumberFormat percentFormatter = NumberFormat.getPercentInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        System.out.println("CSS carregado: " + getClass().getResource("../css/contratar.css"));
        SessionManager.getInstance().refreshClienteLogado();
        emprestimoController = new EmprestimoController(new EmprestimoDAO(), new ClienteDAO());

        // Setup checkboxes to enable confirm button only when both are checked
        termsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                confirmButton.setDisable(!(newValue && dataCheckBox.isSelected())));

        dataCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                confirmButton.setDisable(!(newValue && termsCheckBox.isSelected())));

        // Percent formatter setup
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);

        // Verifica se há cliente logado
        if (SessionManager.getInstance().getClienteLogado() == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick();
        }
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setEmprestimoParaContratar(Emprestimo emprestimo) {
        this.emprestimoParaContratar = emprestimo;
        if (emprestimo != null) {
            updateLoanDetails();
        }
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onConfirmClick() {
        try {
            if (SessionManager.getInstance().getClienteLogado() == null) {
                showAlert("Sessão Expirada", "Sessão expirada. Redirecionando para login...");
                onExitClick();
                return;
            }

            if (emprestimoParaContratar == null) {
                showAlert("Erro", "Dados do empréstimo não encontrados.");
                return;
            }

            Emprestimo createdEmprestimo = emprestimoController.postEmprestimo(emprestimoParaContratar);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Empréstimo Contratado");
            alert.setHeaderText("Empréstimo Realizado com Sucesso");
            alert.setContentText("Seu empréstimo foi aprovado e contratado com sucesso!");
            alert.showAndWait();

            onHomeClick();

        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao confirmar o empréstimo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelClick() {
        onBackClick();
    }

    @FXML
    private void onHomeClick() {
        try {
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
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("simulacaoEmprestimo.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            SimulacaoViewController simulacaoController = loader.getController();
            simulacaoController.setTipoEmprestimo(emprestimoParaContratar.getTipoEmprestimo());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Simulação de Empréstimo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar simulacaoEmprestimo.fxml: " + e.getMessage());
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
            e.printStackTrace();
            System.err.println("Erro ao carregar login.fxml: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void updateLoanDetails() {
        // Set loan type
        TipoEmprestimoEnum tipoEmprestimo = emprestimoParaContratar.getTipoEmprestimo();
        loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));

        // Set loan details
        loanAmountLabel.setText(currencyFormatter.format(emprestimoParaContratar.getValorEmprestimo()));
        installmentsLabel.setText(String.valueOf(emprestimoParaContratar.getQuantidadeParcelas()));
        installmentValueLabel.setText(currencyFormatter.format(emprestimoParaContratar.getValorParcela()));
        interestRateLabel.setText(percentFormatter.format(emprestimoParaContratar.getTaxaJuros() / 100));
        totalAmountLabel.setText(currencyFormatter.format(emprestimoParaContratar.getValorTotal()));
        iofValueLabel.setText(currencyFormatter.format(emprestimoParaContratar.getValorIOF()));
        insuranceValueLabel.setText(currencyFormatter.format(emprestimoParaContratar.getValorSeguro()));

        // Set dates
        contractDateLabel.setText(emprestimoParaContratar.getDataContratacao().format(dateFormatter));

        if (emprestimoParaContratar.getDataLiberacaoCred() != null) {
            releaseDateLabel.setText(emprestimoParaContratar.getDataLiberacaoCred().format(dateFormatter));
        } else {
            releaseDateLabel.setText(emprestimoParaContratar.getDataContratacao().plusDays(1).format(dateFormatter));
        }

        firstPaymentDateLabel.setText(emprestimoParaContratar.getDataInicio().format(dateFormatter));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}