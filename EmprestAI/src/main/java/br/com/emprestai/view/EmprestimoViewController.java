package br.com.emprestai.view;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
            // Preenche os campos com base nos dados do empréstimo
            contractTitle.setText("Contrato");
            contractType.setText("Consignado"); // Ajuste se houver um campo real para tipo
            totalAmount.setText(String.format("R$ %.2f", emprestimo.getValorEmprestimo()));
            currentDebt.setText(String.format("R$ %.2f", emprestimo.getValorEmprestimo())); // Ajuste se houver dívida atual
            installmentAmount.setText(String.format("R$ %.2f", emprestimo.getValorParcela())); // Supondo getValorParcela()
            remainingInstallments.setText(String.format("%d de %d", emprestimo.getQuantidadeParcelas(), emprestimo.getQuantidadeParcelas())); // Supondo métodos
            loanStatus.setText(emprestimo.getStatusEmprestimo().name());
            updateStatus(emprestimo.getStatusEmprestimo().name());
        } else {
            // Valores padrão caso o empréstimo seja nulo
            contractTitle.setText("Contrato");
            contractType.setText("Nenhum");
            totalAmount.setText("R$ 0,00");
            currentDebt.setText("R$ 0,00");
            installmentAmount.setText("R$ 0,00");
            remainingInstallments.setText("0 de 0");
            loanStatus.setText("Indisponível");
            updateStatus("indisponível");
        }
    }

    public void updateStatus(String newStatus) {
        statusCircle.getStyleClass().removeAll("green", "red", "gray");
        if (!statusCircle.getStyleClass().contains("status-circle")) {
            statusCircle.getStyleClass().add("status-circle");
        }

        switch (newStatus.toLowerCase()) {
            case "aprovado":
                statusCircle.getStyleClass().add("green");
                break;
            case "negado":
                statusCircle.getStyleClass().add("red");
                break;
            case "encerrado":
                statusCircle.getStyleClass().add("gray");
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

    public void onProfileClick(ActionEvent actionEvent) {
    }

    public void onClickDetalhes(ActionEvent actionEvent) {
    }
}