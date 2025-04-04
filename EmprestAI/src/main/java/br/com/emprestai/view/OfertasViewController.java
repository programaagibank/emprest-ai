package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class OfertasViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label     loanTypeLabel;
    @FXML private Label     requestedAmountLabel;
    @FXML private TableView<Emprestimo> offersTable;
    @FXML private TableColumn<Emprestimo, Integer> installmentsColumn;
    @FXML private TableColumn<Emprestimo, String> installmentValueColumn;
    @FXML private TableColumn<Emprestimo, String> interestRateColumn;
    @FXML private TableColumn<Emprestimo, String> totalAmountColumn;
    @FXML private Button    selectOfferButton;
    @FXML private Button    backButton;
    @FXML private Button    exitButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private double              valorSolicitado;
    private TipoEmprestimoEnum  tipoEmprestimo;
    private Cliente             cliente;
    private EmprestimoController emprestimoController;
    private Emprestimo          selectedOffer;

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
        cliente = SessionManager.getInstance().getClienteLogado();

        if (cliente == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick();
            return;
        }

        // Configuração das colunas da tabela
        installmentsColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantidadeParcelas()).asObject());

        installmentValueColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(currencyFormatter.format(cellData.getValue().getValorParcela())));

        interestRateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(percentFormatter.format(cellData.getValue().getTaxaJuros() / 100)));

        totalAmountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(currencyFormatter.format(cellData.getValue().getValorTotal())));

        // Configuração do percentFormatter
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);

        // Listener para habilitação do botão de seleção
        offersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectOfferButton.setDisable(newSelection == null);
            selectedOffer = newSelection;
        });
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setOfertaData(double valorSolicitado, TipoEmprestimoEnum tipoEmprestimo) {
        this.valorSolicitado = valorSolicitado;
        this.tipoEmprestimo = tipoEmprestimo;

        loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));
        requestedAmountLabel.setText("Valor Solicitado: " + currencyFormatter.format(valorSolicitado));

        loadOffers();
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onSelectOfferClick() {
        if (selectedOffer == null) {
            showAlert("Seleção Requerida", "Por favor, selecione uma oferta da tabela.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("contratarEmprestimo.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            ContratarEmprestimoViewController contratarController = loader.getController();
            contratarController.setEmprestimoParaContratar(selectedOffer);
            Stage stage = (Stage) selectOfferButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Contratar Empréstimo");
            stage.show();
        } catch (IOException e) {
            showAlert("Erro", "Erro ao abrir tela de contratação: " + e.getMessage());
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
    private void loadOffers() {
        try {
            List<Emprestimo> ofertas = emprestimoController.gerarOfertasEmprestimo(valorSolicitado, tipoEmprestimo, cliente);
            offersTable.setItems(FXCollections.observableArrayList(ofertas));

            if (ofertas.isEmpty()) {
                showAlert("Sem Ofertas", "Não foram encontradas ofertas disponíveis para os parâmetros informados.");
                selectOfferButton.setDisable(true);
            }
        } catch (Exception e) {
            showAlert("Erro", "Erro ao carregar ofertas: " + e.getMessage());
            e.printStackTrace();
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