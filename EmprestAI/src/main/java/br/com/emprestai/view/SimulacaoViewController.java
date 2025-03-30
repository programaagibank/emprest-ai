package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.StatusParcelaEnum;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class SimulacaoViewController {

    @FXML
    private Label simulationTitle;

    @FXML
    private Label loanTypeLabel;

    @FXML
    private Label rendaMensalLabel;

    @FXML
    private Label idadeLabel;

    @FXML
    private Label scoreOrClienteTypeLabel;

    @FXML
    private Label scoreOrClienteTypeValueLabel;

    @FXML
    private TextField loanAmountField;

    @FXML
    private Slider installmentsSlider;

    @FXML
    private Label installmentsLabel;

    @FXML
    private CheckBox insuranceCheckBox;

    @FXML
    private VBox gracePeriodContainer;

    @FXML
    private ComboBox<String> gracePeriodComboBox;

    @FXML
    private VBox paydayDateContainer;

    @FXML
    private DatePicker paydayDatePicker;

    @FXML
    private Label installmentValueLabel;

    @FXML
    private Label interestRateLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Label iofValueLabel;

    @FXML
    private Label insuranceValueLabel;

    @FXML
    private Label contractDateLabel;

    @FXML
    private Label releaseDateLabel;

    @FXML
    private Label firstPaymentDateLabel;

    @FXML
    private Button calculateButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button backButton;

    @FXML
    private Button exitButton;

    // Non-FXML properties
    private Cliente clienteLogado;
    private TipoEmprestimoEnum tipoEmprestimo;
    private Emprestimo emprestimoSimulado;
    private EmprestimoController emprestimoController;

    // Brazilian currency formatter
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final NumberFormat percentFormatter = NumberFormat.getPercentInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        // Initialize EmprestimoController
        emprestimoController = new EmprestimoController(new EmprestimoDAO(), new ClienteDAO());

        // Setup slider change listener
        installmentsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int installments = newValue.intValue();
            // Round to multiples of 6
            installments = Math.round(installments / 6) * 6;
            installmentsLabel.setText(String.valueOf(installments));
        });

        // Add text formatter to ensure only numbers and decimal point
        loanAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                loanAmountField.setText(oldValue);
            }
        });

        // Initialize grace period options
        gracePeriodComboBox.setItems(FXCollections.observableArrayList(
                "Sem carência",
                "15 dias",
                "30 dias"
        ));
        gracePeriodComboBox.getSelectionModel().selectFirst();

        // Set today's date as default for payday date picker
        paydayDatePicker.setValue(LocalDate.now().plusMonths(1));

        // Format percentage with 2 decimal places
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;

        // Update client information in UI
        if (cliente != null) {
            rendaMensalLabel.setText(currencyFormatter.format(cliente.getRendaMensalLiquida()));

            // Calculate age
            int idade = (int) ChronoUnit.YEARS.between(cliente.getDataNascimento(), LocalDate.now());
            idadeLabel.setText(String.valueOf(idade));
        }
    }

    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;

        // Update UI based on loan type
        if (tipoEmprestimo != null) {
            loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));

            // Show proper fields based on loan type
            if (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO) {
                // For consignado
                scoreOrClienteTypeLabel.setText("Tipo de Cliente:");
                scoreOrClienteTypeValueLabel.setText(clienteLogado.getTipoCliente().toString());

                // Limit max installments to 48
                installmentsSlider.setMax(48);

                // Show payday date picker, hide grace period
                gracePeriodContainer.setVisible(false);
                gracePeriodContainer.setManaged(false);
                paydayDateContainer.setVisible(true);
                paydayDateContainer.setManaged(true);
            } else {
                // For personal loans
                scoreOrClienteTypeLabel.setText("Score:");
                scoreOrClienteTypeValueLabel.setText(String.valueOf(clienteLogado.getScore()));

                // Show grace period, hide payday date
                gracePeriodContainer.setVisible(true);
                gracePeriodContainer.setManaged(true);
                paydayDateContainer.setVisible(false);
                paydayDateContainer.setManaged(false);
            }
        }
    }

    @FXML
    private void onCalculateClick() {
        try {
            // Validate inputs
            if (loanAmountField.getText().isEmpty()) {
                showAlert("Valor Requerido", "Por favor, informe o valor do empréstimo.");
                return;
            }
            // Get input values
            double loanAmount = Double.parseDouble(loanAmountField.getText());
            int installments = Integer.parseInt(installmentsLabel.getText());

            // Create loan object
            emprestimoSimulado = new Emprestimo();
            emprestimoSimulado.setValorEmprestimo(loanAmount);
            emprestimoSimulado.setQuantidadeParcelas(installments);
            emprestimoSimulado.setTipoEmprestimo(tipoEmprestimo);
            emprestimoSimulado.setIdCliente(clienteLogado.getIdCliente());
            emprestimoSimulado.setContratarSeguro(insuranceCheckBox.isSelected());

            // Set dates
            LocalDate now = LocalDate.now();
            emprestimoSimulado.setDataContratacao(now);

            // Set start date based on loan type
            if (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO) {
                // For consignado, use payday date
                if (paydayDatePicker.getValue() == null) {
                    showAlert("Data Requerida", "Por favor, informe a data do próximo pagamento.");
                    return;
                }
                emprestimoSimulado.setDataInicio(paydayDatePicker.getValue());
            } else {
                // For personal loans, use grace period
                String gracePeriod = gracePeriodComboBox.getValue();
                if (gracePeriod.equals("15 dias")) {
                    emprestimoSimulado.setDataInicio(now.plusDays(15));
                } else if (gracePeriod.equals("30 dias")) {
                    emprestimoSimulado.setDataInicio(now.plusDays(30));
                } else {
                    emprestimoSimulado.setDataInicio(now);  // No grace period
                }
            }

            // Process loan using controller
            emprestimoSimulado = emprestimoController.get(emprestimoSimulado, clienteLogado);

            // Check if loan is eligible - usando StatusEmprestimoEnum
            if (emprestimoSimulado.getStatusEmprestimo() == StatusEmprestimoEnum.NEGADO) {
                showAlert("Empréstimo Não Aprovado",
                        "Infelizmente o empréstimo não pode ser aprovado com os valores informados.");
                return;
            }

            // Update UI with results
            updateSimulationResults();

            // Enable confirm button
            confirmButton.setDisable(false);

        } catch (NumberFormatException e) {
            showAlert("Erro de Formato", "Por favor, verifique se os valores estão no formato correto.");
        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao processar a simulação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateSimulationResults() {
        // Update all result labels
        installmentValueLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorParcela()));
        interestRateLabel.setText(percentFormatter.format(emprestimoSimulado.getJuros() / 100));
        totalAmountLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorTotal()));
        iofValueLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorIOF()));
        insuranceValueLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorSeguro()));

        // Update date information
        contractDateLabel.setText(emprestimoSimulado.getDataContratacao().format(dateFormatter));

        // Credit release date is typically the same as contract date or day after
        LocalDate releaseDate = emprestimoSimulado.getDataLiberacaoCred() != null ?
                emprestimoSimulado.getDataLiberacaoCred() : emprestimoSimulado.getDataContratacao().plusDays(1);
        releaseDateLabel.setText(releaseDate.format(dateFormatter));

        // First payment date
        firstPaymentDateLabel.setText(emprestimoSimulado.getDataInicio().format(dateFormatter));
    }

    @FXML
    private void onConfirmClick() {
        try {
            if (emprestimoSimulado == null) {
                showAlert("Erro", "É necessário calcular a simulação primeiro.");
                return;
            }

            // Create the loan
            Emprestimo createdEmprestimo = emprestimoController.post(emprestimoSimulado);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Empréstimo Contratado");
            alert.setHeaderText("Empréstimo Realizado com Sucesso");
            alert.setContentText("Seu empréstimo foi aprovado e contratado com sucesso!");
            alert.showAndWait();

            // Navigate to dashboard
            onHomeClick();

        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao confirmar o empréstimo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            DashboardViewController dashboardController = loader.getController();
            dashboardController.setClienteLogado(clienteLogado);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            EmprestimoViewController emprestimoController = loader.getController();
            emprestimoController.setClienteLogado(clienteLogado);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Empréstimos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar emprestimos.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onExitClick() {
        try {
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}