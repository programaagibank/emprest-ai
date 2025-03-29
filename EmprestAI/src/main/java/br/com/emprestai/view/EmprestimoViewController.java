package br.com.emprestai.view;

import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
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

public class EmprestimoViewController {

    @FXML
    private Circle statusCircle;

    @FXML
    private Label loanStatus;

    @FXML
    private Label contractTitle;

    @FXML
    private Label contractType;

    @FXML
    private Label totalAmount;

    @FXML
    private Label currentDebt;

    @FXML
    private Label installmentAmount;

    @FXML
    private Label remainingInstallments;

    @FXML
    private Button homeButton;

    @FXML
    private Button exitButton;

    @FXML
    private VBox loanInfoBox; // Added for the loan info VBox

    @FXML
    private Button simulateButton; // Added for the simulate button

    // Injete os Labels usando a anotação @FXML
    @FXML
    private Label creditMarginConsig;

    @FXML
    private Label creditMarginPessoal;

    private Emprestimo emprestimo;

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
        exibirInformacoesEmprestimo();
    }

    private Cliente clienteLogado;

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
    }

    @FXML
    private void initialize() {
        exibirInformacoesEmprestimo();
    }

    private void exibirInformacoesEmprestimo() {
        if (emprestimo != null) {
            // Show loan info and hide simulate button
            loanInfoBox.setVisible(true);
            loanInfoBox.setManaged(true);
            simulateButton.setVisible(false);
            simulateButton.setManaged(false);

            // Preenche os campos com base nos dados do empréstimo
            contractTitle.setText("Contrato");
            contractType.setText("Consignado"); // Ajuste se houver um campo real para tipo
            totalAmount.setText(String.format("R$ %.2f", emprestimo.getValorEmprestimo()));
            currentDebt.setText(String.format("R$ %.2f", emprestimo.getValorEmprestimo())); // Ajuste se houver dívida atual
            installmentAmount.setText(String.format("R$ %.2f", emprestimo.getValorParcela())); // Supondo getValorParcela()
            remainingInstallments.setText(String.format("%d de %d", emprestimo.getParcelasPagas(), emprestimo.getQuantidadeParcelas())); // Supondo métodos
            loanStatus.setText(emprestimo.getStatusEmprestimo().name());
            updateStatus(emprestimo.getStatusEmprestimo());
        } else {
            // Hide loan info and show simulate button
            loanInfoBox.setVisible(false);
            loanInfoBox.setManaged(false);
            simulateButton.setVisible(true);
            simulateButton.setManaged(true);

            // Set a default title when no loan exists
            contractTitle.setText("Nenhum Empréstimo Ativo");
        }
    }

    public void updateStatus(StatusEmpEnum empEnum) {
        statusCircle.getStyleClass().removeAll("green", "gray", "yellow");
        if (!statusCircle.getStyleClass().contains("status-circle")) {
            statusCircle.getStyleClass().add("status-circle");
        }

        if (empEnum == null) {
            statusCircle.getStyleClass().add("gray");
            return;
        }

        switch (empEnum) {
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
            // Assuming you have a simulation view (e.g., "simulate.fxml")
            FXMLLoader loader = new FXMLLoader(getClass().getResource("simulate.fxml"));
            Scene simulateScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) simulateButton.getScene().getWindow();
            stage.setScene(simulateScene);
            stage.setTitle("EmprestAI - Simular Empréstimo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar simulate.fxml: " + e.getMessage());
        }
    }

    public void onProfileClick(ActionEvent actionEvent) {
    }

    public void onClickDetalhes(ActionEvent actionEvent) {
    }
}