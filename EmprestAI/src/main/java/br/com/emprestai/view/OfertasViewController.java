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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    @FXML private VBox      offersContainer;
    @FXML private Button    selectOfferButton;
    @FXML private Button    homeButton;
    @FXML private Button    profileButton;
    @FXML private Button    exitButton;
    @FXML private Button    backButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private double              valorSolicitado;
    private TipoEmprestimoEnum  tipoEmprestimo;
    private Cliente             clienteLogado;
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
        System.out.println("CSS carregado: " + getClass().getResource("../css/ofertas.css"));
        emprestimoController = new EmprestimoController(new EmprestimoDAO());
        clienteLogado = SessionManager.getInstance().getClienteLogado();

        if (clienteLogado == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick();
            return;
        }

        // Configuração do percentFormatter
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);
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
            return;
        }
        selectedOffer.setIdCliente(clienteLogado.getIdCliente());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("contratar-emprestimo.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
            ContratarEmprestimoViewController contratarController = loader.getController();
            contratarController.setEmprestimoParaContratar(selectedOffer);
            contratarController.setTipoEmprestimo(tipoEmprestimo);
            Stage stage = (Stage) selectOfferButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Contratar Empréstimo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
            EmprestimoViewController emprestimoController = loader.getController();
            emprestimoController.setTipoEmprestimo(tipoEmprestimo);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Empréstimos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
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
    private void onProfileClick() {
        System.out.println("Navegar para a tela de perfil (não implementado).");
    }

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void loadOffers() {
        try {
            List<Emprestimo> ofertas = emprestimoController.gerarOfertasEmprestimo(valorSolicitado, tipoEmprestimo, clienteLogado);
            offersContainer.getChildren().clear();

            // Quando há ofertas
            backButton.setVisible(false); // Esconde o botão Voltar
            selectOfferButton.setVisible(true); // Mostra o botão Selecionar Ofertas
            selectOfferButton.setDisable(true); // Desabilita até uma oferta ser selecionada

            // Limita o número de ofertas exibidas (máximo 4)
            int maxOffers = Math.min(ofertas.size(), 4);
            for (int i = 0; i < maxOffers; i++) {
                HBox card = createOfferCard(ofertas.get(i));
                offersContainer.getChildren().add(card);
            }

            // Se houver mais ofertas, exibe uma mensagem
            if (ofertas.size() > maxOffers) {
                Label moreOffersLabel = new Label("Mais ofertas disponíveis. Ajuste os parâmetros para ver outras opções.");
                moreOffersLabel.getStyleClass().add("more-offers-label");
                offersContainer.getChildren().add(moreOffersLabel);
            }

        } catch (Exception e) {
            // Tratamento de erro
            offersContainer.getChildren().clear();
            Label errorLabel = new Label("Não há ofertas disponíveis para você neste valor.");
            errorLabel.getStyleClass().add("more-offers-label");
            offersContainer.getChildren().add(errorLabel);
            selectOfferButton.setVisible(false); // Esconde o botão Selecionar Ofertas
            backButton.setVisible(true); // Mostra o botão Voltar
            e.printStackTrace();
        }
    }

    private HBox createOfferCard(Emprestimo oferta) {
        HBox card = new HBox();
        card.getStyleClass().add("offer-card");
        card.setUserData(oferta);

        // Labels para os dados da oferta
        VBox parcelasBox = new VBox(5);
        parcelasBox.getStyleClass().add("offer-field");
        Label parcelasTitle = new Label("Parcelas");
        parcelasTitle.getStyleClass().addAll("offer-label", "title");
        Label parcelasValue = new Label(String.valueOf(oferta.getQuantidadeParcelas()));
        parcelasValue.getStyleClass().addAll("offer-label", "value");
        parcelasBox.getChildren().addAll(parcelasTitle, parcelasValue);

        VBox valorParcelaBox = new VBox(5);
        valorParcelaBox.getStyleClass().add("offer-field");
        Label valorParcelaTitle = new Label("Valor Parcela");
        valorParcelaTitle.getStyleClass().addAll("offer-label", "title");
        Label valorParcelaValue = new Label(currencyFormatter.format(oferta.getValorParcela()));
        valorParcelaValue.getStyleClass().addAll("offer-label", "value");
        valorParcelaBox.getChildren().addAll(valorParcelaTitle, valorParcelaValue);

        VBox taxaJurosBox = new VBox(5);
        taxaJurosBox.getStyleClass().add("offer-field");
        Label taxaJurosTitle = new Label("Taxa Juros");
        taxaJurosTitle.getStyleClass().addAll("offer-label", "title");
        Label taxaJurosValue = new Label(percentFormatter.format(oferta.getTaxaJuros() / 100));
        taxaJurosValue.getStyleClass().addAll("offer-label", "value");
        taxaJurosBox.getChildren().addAll(taxaJurosTitle, taxaJurosValue);

        VBox totalBox = new VBox(5);
        totalBox.getStyleClass().add("offer-field");
        Label totalTitle = new Label("Total");
        totalTitle.getStyleClass().addAll("offer-label", "title");
        Label totalValue = new Label(currencyFormatter.format(oferta.getValorTotal()));
        totalValue.getStyleClass().addAll("offer-label", "value");
        totalBox.getChildren().addAll(totalTitle, totalValue);

        card.getChildren().addAll(parcelasBox, valorParcelaBox, taxaJurosBox, totalBox);

        // Listener para seleção do card
        card.setOnMouseClicked(event -> {
            // Remove a seleção de outros cards
            for (var child : offersContainer.getChildren()) {
                child.getStyleClass().remove("selected");
            }
            // Adiciona a classe "selected" ao card clicado
            card.getStyleClass().add("selected");
            selectedOffer = (Emprestimo) card.getUserData();
            selectOfferButton.setDisable(false);
        });

        return card;
    }
}