package br.com.emprestai.view;

import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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

    public void updateStatus(StatusEmprestimoEnum empEnum) {
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
            // Carregando o arquivo correto simulacaoEmprestimo.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("simulacaoEmprestimo.fxml"));
            Scene simulateScene = new Scene(loader.load(), 360, 640);

            // Obtendo o controller e configurando o cliente logado
            SimulacaoViewController simulacaoController = loader.getController();
            simulacaoController.setClienteLogado(clienteLogado);

            // Abre uma janela para escolher o tipo de empréstimo
            Alert tipoAlert = new Alert(Alert.AlertType.CONFIRMATION);
            tipoAlert.setTitle("Tipo de Empréstimo");
            tipoAlert.setHeaderText("Selecione o tipo de empréstimo");

            ButtonType consignadoButton = new ButtonType("Consignado");
            ButtonType pessoalButton = new ButtonType("Pessoal");
            ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            tipoAlert.getButtonTypes().setAll(consignadoButton, pessoalButton, cancelButton);

            Optional<ButtonType> resultado = tipoAlert.showAndWait();
            if (resultado.isPresent()) {
                if (resultado.get() == consignadoButton) {
                    simulacaoController.setTipoEmprestimo(TipoEmprestimoEnum.CONSIGNADO);
                } else if (resultado.get() == pessoalButton) {
                    simulacaoController.setTipoEmprestimo(TipoEmprestimoEnum.PESSOAL);
                } else {
                    // O usuário cancelou, não faça nada
                    return;
                }

                // Definindo a cena na janela atual
                Stage stage = (Stage) simulateButton.getScene().getWindow();
                stage.setScene(simulateScene);
                stage.setTitle("EmprestAI - Simulação de Empréstimo");
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar simulacaoEmprestimo.fxml: " + e.getMessage());
        }
    }

    public void onProfileClick(ActionEvent actionEvent) {
    }

    public void onClickDetalhes(ActionEvent actionEvent) {
    }
}