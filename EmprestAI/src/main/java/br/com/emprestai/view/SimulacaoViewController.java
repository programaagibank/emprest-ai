package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimulacaoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label       simulationTitle;
    @FXML private Label       loanTypeLabel;
    @FXML private Label       rendaMensalLabel;
    @FXML private Label       idadeLabel;
    @FXML private Label       scoreOrClienteTypeLabel;
    @FXML private Label       scoreOrClienteTypeValueLabel;
    @FXML private TextField   loanAmountField;
    @FXML private ComboBox<Integer> installmentsComboBox;   // Substituído o slider por ComboBox
    @FXML private CheckBox    insuranceCheckBox;
    @FXML private VBox        gracePeriodContainer;
    @FXML private ComboBox<String> gracePeriodComboBox;
    @FXML private VBox        paydayDateContainer;
    @FXML private DatePicker  paydayDatePicker;
    @FXML private Label       installmentValueLabel;
    @FXML private Label       interestRateLabel;
    @FXML private Label       totalAmountLabel;
    @FXML private Label       iofValueLabel;
    @FXML private Label       insuranceValueLabel;
    @FXML private Label       contractDateLabel;
    @FXML private Label       releaseDateLabel;
    @FXML private Label       firstPaymentDateLabel;
    @FXML private Button      calculateButton;
    @FXML private Button      confirmButton;
    @FXML private Button      homeButton;
    @FXML private Button      backButton;
    @FXML private Button      exitButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private Cliente              clienteLogado;
    private TipoEmprestimoEnum   tipoEmprestimo;
    private Emprestimo           emprestimoSimulado;
    private EmprestimoController emprestimoController;

    // Formatters
    private final NumberFormat   currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final NumberFormat   percentFormatter  = NumberFormat.getPercentInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        emprestimoController = new EmprestimoController(new EmprestimoDAO(), new ClienteDAO());

        // Configuração do ComboBox de parcelas
        configureInstallmentsComboBox(72);

        // Loan amount field listener
        loanAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                loanAmountField.setText(oldValue);
            }
        });

        // Grace period setup
        gracePeriodComboBox.setItems(FXCollections.observableArrayList(
                "Sem carência",
                "15 dias",
                "30 dias"
        ));
        gracePeriodComboBox.getSelectionModel().selectFirst();

        // Date picker setup
        paydayDatePicker.setValue(LocalDate.now().plusMonths(1));

        // Percent formatter setup
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);
    }

    /**
     * Configura o ComboBox de parcelas com opções de 6 em 6 até o máximo especificado
     */
    private void configureInstallmentsComboBox(int maxInstallments) {
        List<Integer> installmentOptions = new ArrayList<>();
        for (int i = 6; i <= maxInstallments; i += 6) {
            installmentOptions.add(i);
        }
        installmentsComboBox.setItems(FXCollections.observableArrayList(installmentOptions));
        installmentsComboBox.getSelectionModel().select(Integer.valueOf(36)); // Valor padrão: 36 parcelas
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
        if (cliente != null) {
            rendaMensalLabel.setText(currencyFormatter.format(cliente.getVencimentoLiquidoTotal()));
            int idade = (int) ChronoUnit.YEARS.between(cliente.getDataNascimento(), LocalDate.now());
            idadeLabel.setText(String.valueOf(idade));
        }
    }

    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
        if (tipoEmprestimo != null) {
            loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));

            if (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO) {
                configureInstallmentsComboBox(48); // Máximo de 48 parcelas para consignado
                gracePeriodContainer.setVisible(false);
                gracePeriodContainer.setManaged(false);
                paydayDateContainer.setVisible(true);
                paydayDateContainer.setManaged(true);
            } else {
                configureInstallmentsComboBox(72); // Máximo de 72 parcelas para pessoal
                scoreOrClienteTypeLabel.setText("Score:");
                scoreOrClienteTypeValueLabel.setText(String.valueOf(clienteLogado.getScore()));
                gracePeriodContainer.setVisible(true);
                gracePeriodContainer.setManaged(true);
                paydayDateContainer.setVisible(false);
                paydayDateContainer.setManaged(false);
            }
        }
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onCalculateClick() {
        try {
            if (loanAmountField.getText().isEmpty()) {
                showAlert("Valor Requerido", "Por favor, informe o valor do empréstimo.");
                return;
            }

            if (installmentsComboBox.getValue() == null) {
                showAlert("Parcelas Requeridas", "Por favor, selecione o número de parcelas.");
                return;
            }

            double loanAmount = Double.parseDouble(loanAmountField.getText());
            int installments = installmentsComboBox.getValue();

            emprestimoSimulado = new Emprestimo();
            emprestimoSimulado.setValorEmprestimo(loanAmount);
            emprestimoSimulado.setQuantidadeParcelas(installments);
            emprestimoSimulado.setTipoEmprestimo(tipoEmprestimo);
            emprestimoSimulado.setIdCliente(clienteLogado.getIdCliente());
            emprestimoSimulado.setContratarSeguro(insuranceCheckBox.isSelected());

            LocalDate now = LocalDate.now();
            emprestimoSimulado.setDataContratacao(now);

            if (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO) {
                if (paydayDatePicker.getValue() == null) {
                    showAlert("Data Requerida", "Por favor, informe a data do próximo pagamento.");
                    return;
                }
                emprestimoSimulado.setDataInicio(paydayDatePicker.getValue());
            } else {
                String gracePeriod = gracePeriodComboBox.getValue();
                if (gracePeriod.equals("15 dias")) {
                    emprestimoSimulado.setDataInicio(now.plusDays(15));
                } else if (gracePeriod.equals("30 dias")) {
                    emprestimoSimulado.setDataInicio(now.plusDays(30));
                } else {
                    emprestimoSimulado.setDataInicio(now);
                }
            }

            emprestimoSimulado = emprestimoController.getPrice(emprestimoSimulado, clienteLogado);

            if (emprestimoSimulado.getStatusEmprestimo() == StatusEmprestimoEnum.NEGADO) {
                showAlert("Empréstimo Não Aprovado",
                        "Infelizmente o empréstimo não pode ser aprovado com os valores informados.");
                return;
            }

            updateSimulationResults();
            confirmButton.setDisable(false);

        } catch (NumberFormatException e) {
            showAlert("Erro de Formato", "Por favor, verifique se os valores estão no formato correto.");
        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao processar a simulação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onConfirmClick() {
        try {
            if (emprestimoSimulado == null) {
                showAlert("Erro", "É necessário calcular a simulação primeiro.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("contratarEmprestimo.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            ContratarEmprestimoViewController contratarController = loader.getController();
            contratarController.setClienteLogado(clienteLogado);
            contratarController.setEmprestimoParaContratar(emprestimoSimulado);
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Contratar Empréstimo");
            stage.show();

        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao preparar a contratação: " + e.getMessage());
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

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void updateSimulationResults() {
        installmentValueLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorParcela()));
        interestRateLabel.setText(percentFormatter.format(emprestimoSimulado.getTaxaJuros() / 100));
        totalAmountLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorTotal()));
        iofValueLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorIOF()));
        insuranceValueLabel.setText(currencyFormatter.format(emprestimoSimulado.getValorSeguro()));

        contractDateLabel.setText(emprestimoSimulado.getDataContratacao().format(dateFormatter));

        LocalDate releaseDate = emprestimoSimulado.getDataLiberacaoCred() != null ?
                emprestimoSimulado.getDataLiberacaoCred() : emprestimoSimulado.getDataContratacao().plusDays(1);
        releaseDateLabel.setText(releaseDate.format(dateFormatter));

        firstPaymentDateLabel.setText(emprestimoSimulado.getDataInicio().format(dateFormatter));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}