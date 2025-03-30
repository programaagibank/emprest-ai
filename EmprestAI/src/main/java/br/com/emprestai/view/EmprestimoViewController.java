package br.com.emprestai.view;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class EmprestimoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Circle  statusCircle;
    @FXML private Label   loanStatus;
    @FXML private Label   contractTitle;
    @FXML private Label   contractType;
    @FXML private Label   totalAmount;
    @FXML private Label   currentDebt;
    @FXML private Label   installmentAmount;
    @FXML private Label   remainingInstallments;
    @FXML private Label   creditMarginConsig;
    @FXML private Label   creditMarginPessoal;
    @FXML private Button  homeButton;
    @FXML private Button  exitButton;
    @FXML private Button  simulateButton;
    @FXML private VBox    loanInfoBox;
    @FXML private Button  ordemVencimentoButton;
    @FXML private Button  maiorDescontoButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private Emprestimo          emprestimo;
    private TipoEmprestimoEnum  tipoEmprestimo;
    private Cliente             clienteLogado;
    private ParcelaController   parcelaController = new ParcelaController(new ParcelaDAO());

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        exibirInformacoesEmprestimo();
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
        exibirInformacoesEmprestimo();
    }

    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
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
    private void onExitClick() {
        try {
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

    @FXML
    private void onSimulateClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("simulacaoEmprestimo.fxml"));
            Scene simulateScene = new Scene(loader.load(), 360, 640);

            SimulacaoViewController simulacaoController = loader.getController();
            simulacaoController.setClienteLogado(clienteLogado);
            simulacaoController.setTipoEmprestimo(tipoEmprestimo);

            Stage stage = (Stage) simulateButton.getScene().getWindow();
            stage.setScene(simulateScene);
            stage.setTitle("EmprestAI - Simulação de Empréstimo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar simulacaoEmprestimo.fxml: " + e.getMessage());
        }
    }

    public void onProfileClick(ActionEvent actionEvent) {
    }

    @FXML
    private void onOrdemVencimentoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
            Scene parcelaScene = new Scene(loader.load(), 360, 640);

            ParcelaViewController parcelaViewController = loader.getController();
            List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
            // Ordenar por data de vencimento
            parcelas.sort(Comparator.comparing(Parcela::getDataVencimento));
            parcelaViewController.setEmprestimo(emprestimo);
            parcelaViewController.setClienteLogado(clienteLogado);
            parcelaViewController.setParcelas(parcelas);

            Stage stage = (Stage) ordemVencimentoButton.getScene().getWindow();
            stage.setScene(parcelaScene);
            stage.setTitle("EmprestAI - Parcelas (Ordem de Vencimento)");
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar parcela.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onMaiorDescontoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
            Scene parcelaScene = new Scene(loader.load(), 360, 640);

            ParcelaViewController parcelaViewController = loader.getController();
            List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
            // Ordenar por maior desconto (assumindo que Parcela tem um campo "desconto")
            parcelas.sort(Comparator.comparing(Parcela::getDataVencimento, Comparator.reverseOrder()));
            parcelaViewController.setEmprestimo(emprestimo);
            parcelaViewController.setClienteLogado(clienteLogado);
            parcelaViewController.setParcelas(parcelas);

            Stage stage = (Stage) maiorDescontoButton.getScene().getWindow();
            stage.setScene(parcelaScene);
            stage.setTitle("EmprestAI - Parcelas (Maior Desconto)");
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar parcela.fxml: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void exibirInformacoesEmprestimo() {
        if (emprestimo != null) {
            loanInfoBox.setVisible(true);
            loanInfoBox.setManaged(true);
            simulateButton.setVisible(false);
            simulateButton.setManaged(false);

            contractTitle.setText("Contrato");
            contractType.setText("Consignado");
            totalAmount.setText(String.format("R$ %.2f", emprestimo.getValorEmprestimo()));
            currentDebt.setText(String.format("R$ %.2f", emprestimo.getValorEmprestimo()));
            installmentAmount.setText(String.format("R$ %.2f", emprestimo.getValorParcela()));
            remainingInstallments.setText(String.format("%d de %d", emprestimo.getParcelasPagas(), emprestimo.getQuantidadeParcelas()));
            loanStatus.setText(emprestimo.getStatusEmprestimo().name());
            updateStatus(emprestimo.getStatusEmprestimo());
        } else {
            loanInfoBox.setVisible(false);
            loanInfoBox.setManaged(false);
            simulateButton.setVisible(true);
            simulateButton.setManaged(true);
            contractTitle.setText("Nenhum Empréstimo Ativo");
        }
    }

    public void updateStatus(StatusEmprestimoEnum tipoEmprestimo) {
        statusCircle.getStyleClass().removeAll("green", "gray", "yellow");
        if (!statusCircle.getStyleClass().contains("status-circle")) {
            statusCircle.getStyleClass().add("status-circle");
        }

        if (tipoEmprestimo == null) {
            statusCircle.getStyleClass().add("gray");
            return;
        }

        switch (tipoEmprestimo) {
            case ABERTO:
                statusCircle.getStyleClass().add("green");
                break;
            case QUITADO:
                statusCircle.getStyleClass().add("gray");
                break;
            case RENEGOCIADO:
                statusCircle.getStyleClass().add("yellow");
                break;
            default:
                statusCircle.getStyleClass().add("gray");
                break;
        }
    }
}