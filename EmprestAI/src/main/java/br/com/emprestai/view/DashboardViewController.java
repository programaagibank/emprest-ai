package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmprestimoEnum.PESSOAL;

public class DashboardViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label greetingLabel;
    @FXML private Label consignadoCredit;
    @FXML private Label pessoalCredit;
    @FXML private Button consignadoButton;
    @FXML private Button pessoalButton;
    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private Button exitButton;
    @FXML private VBox upcomingPaymentsBox; // To dynamically add payment cards

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private ClienteController clienteController = new ClienteController(new ClienteDAO());
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());
    private static final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        SessionManager.getInstance().refreshClienteLogado();
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (clienteLogado == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick();
            return;
        }

        homeButton.setStyle("-fx-text-fill: #0056D2;");
        greetingLabel.setText("Olá, " + clienteLogado.getNomeCliente() + "!");
        updateCreditLimits(clienteLogado);
        loadUpcomingPayments(clienteLogado);

        // Ajuste dinâmico da largura para responsividade
        upcomingPaymentsBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Listener para ajustar com a largura
                newScene.widthProperty().addListener((obsW, oldW, newW) -> {
                    upcomingPaymentsBox.setPrefWidth(newW.doubleValue() - 40);
                });

                // Listener para ajustar com a altura
                newScene.heightProperty().addListener((obsH, oldH, newH) -> {
                    // Ajusta a altura do conteúdo proporcionalmente
                    double contentHeight = newH.doubleValue() - 200; // Desconta header e navbar
                    if (contentHeight > 300) {
                        upcomingPaymentsBox.setPrefHeight(contentHeight * 0.4); // 40% do espaço disponível
                    }
                });
            }
        });
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onConsignadoClick() {
        navigateToEmprestimos(CONSIGNADO);
    }

    @FXML
    private void onPessoalClick() {
        navigateToEmprestimos(PESSOAL);
    }

    @FXML
    private void onHomeClick() {
        // Already on the dashboard, no action needed
    }

    @FXML
    private void onProfileClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/perfilCliente.fxml"));
            Parent root = loader.load();
            Scene scene = profileButton.getScene();
            Stage stage = (Stage) scene.getWindow();
            scene.setRoot(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar tela");
            alert.setHeaderText(null);
            alert.setContentText("Ocorreu um erro ao carregar a tela de perfil: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 700);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar login.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onChatBot() {
        try {
            // Carrega o FXML do chatbot
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatbot.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("Erro: Não foi possível encontrar chatbot.fxml no caminho especificado.");
                return;
            }
            Scene chatbotScene = new Scene(loader.load(), 350, 500);
            Stage chatbotStage = new Stage();
            chatbotStage.setScene(chatbotScene);
            chatbotStage.setTitle("EmprestAI - Chatbot");
            chatbotStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar chatbot.fxml: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void updateCreditLimits(Cliente cliente) {
        // Alterado para mostrar o limite ao invés da margem
        double limiteConsignavel = cliente.getLimiteCreditoConsignado(); // Alterado para acessar o limite
        double limitePessoal = cliente.getLimiteCreditoPessoal(); // Alterado para acessar o limite
        consignadoCredit.setText(df.format(limiteConsignavel));
        pessoalCredit.setText(df.format(limitePessoal));
    }

    private void loadUpcomingPayments(Cliente cliente) {
        try {
            // Fetch unpaid parcels for Consignado (id_tipo_emprestimo = 1)
            List<Parcela> consignadoParcels = parcelaController.getUltimasNaoPagas(cliente.getIdCliente(), CONSIGNADO);
            // Fetch unpaid parcels for Pessoal (id_tipo_emprestimo = 2)
            List<Parcela> pessoalParcels = parcelaController.getUltimasNaoPagas(cliente.getIdCliente(), PESSOAL);

            // Combine the lists
            List<Parcela> allUnpaidParcels = new ArrayList<>();
            allUnpaidParcels.addAll(consignadoParcels);
            allUnpaidParcels.addAll(pessoalParcels);

            // Sort by due date (data_vencimento)
            allUnpaidParcels.sort(Comparator.comparing(Parcela::getDataVencimento));

            // Clear the existing content
            upcomingPaymentsBox.getChildren().clear();

            if (allUnpaidParcels.isEmpty()) {
                Label noPaymentsLabel = new Label("Nenhum pagamento pendente.");
                noPaymentsLabel.getStyleClass().add("info-label");
                upcomingPaymentsBox.getChildren().add(noPaymentsLabel);
            } else {
                // Add Consignado parcels
                for (Parcela parcela : consignadoParcels) {
                    HBox paymentCard = createPaymentCard(parcela, "Consignado");
                    upcomingPaymentsBox.getChildren().add(paymentCard);
                }
                // Add Pessoal parcels
                for (Parcela parcela : pessoalParcels) {
                    HBox paymentCard = createPaymentCard(parcela, "Pessoal");
                    upcomingPaymentsBox.getChildren().add(paymentCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Erro ao carregar pagamentos: " + e.getMessage());
            errorLabel.getStyleClass().add("info-label");
            upcomingPaymentsBox.getChildren().add(errorLabel);
        }
    }

    private HBox createPaymentCard(Parcela parcela, String tipoEmprestimo) {
        HBox card = new HBox(10);
        card.getStyleClass().add("payment-card");

        Label dateLabel = new Label(parcela.getDataVencimento().format(dateFormat));
        dateLabel.getStyleClass().add("payment-date");

        // Use valorPresenteParcela, which now comes from emprestimos.valor_parcela
        double valorAPagar = parcela.getValorPresenteParcela() + parcela.getMulta() + parcela.getJurosMora();
        Label amountLabel = new Label(df.format(valorAPagar));
        amountLabel.getStyleClass().add("payment-amount");

        Label typeLabel = new Label(tipoEmprestimo);
        typeLabel.getStyleClass().add("payment-type");

        Label contractLabel = new Label("Nº " + parcela.getIdEmprestimo());
        contractLabel.getStyleClass().add("payment-contract");

        card.getChildren().addAll(dateLabel, amountLabel, typeLabel, contractLabel);
        return card;
    }

    private void navigateToEmprestimos(TipoEmprestimoEnum tipoEmprestimo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene emprestimosScene = new Scene(loader.load(), 400, 700); // Ajustada dimensão para match com dashboard
            EmprestimoViewController controller = loader.getController();
            controller.setTipoEmprestimo(tipoEmprestimo);
            Stage stage = (Stage) consignadoButton.getScene().getWindow();
            stage.setScene(emprestimosScene);
            stage.setTitle("EmprestAI - Empréstimos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar emprestimos.fxml: " + e.getMessage());
        }
    }
    // Método a ser adicionado ao DashboardViewController.java


}