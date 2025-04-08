package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.GeneratePDF;
import br.com.emprestai.util.SessionManager;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmprestimoEnum.PESSOAL;
import static br.com.emprestai.util.Formatos.formatCurrency;

public class EmprestimoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Circle statusCircle;
    @FXML private Label loanStatus;
    @FXML private Label contractTitle;
    @FXML private Label contractType;
    @FXML private Label totalAmount;
    @FXML private Label currentDebt;
    @FXML private Label installmentAmount;
    @FXML private Label remainingInstallments;
    @FXML private Label solicitacaoMensagem;
    @FXML private Button homeButton;
    @FXML private Button exitButton;
    @FXML private Button solicitacaoButton;
    @FXML private VBox loanInfoBox;
    @FXML private Button verParcelasButton; // Novo botão único

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private List<Emprestimo> emprestimos;
    private TipoEmprestimoEnum tipoEmprestimo;
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());
    private EmprestimoController emprestimoController = new EmprestimoController(new EmprestimoDAO(), null);

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
        System.out.println("CSS carregado: " + getClass().getResource("../css/emprestimos.css"));
        if (tipoEmprestimo != null) {
            carregarEmprestimos(clienteLogado);
        }
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (clienteLogado != null) {
            carregarEmprestimos(clienteLogado);
        }
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
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
    private void onProfileClick() {}

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar login.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onSolicitacaoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("solicitacao-emprestimo.fxml"));
            Scene solicitacaoScene = new Scene(loader.load(), 360, 640);
            SolicitacaoEmprestimoViewController solicitacaoController = loader.getController();
            solicitacaoController.setTipoEmprestimo(tipoEmprestimo);
            Stage stage = (Stage) solicitacaoButton.getScene().getWindow();
            stage.setScene(solicitacaoScene);
            stage.setTitle("EmprestAI - Ofertas de Empréstimo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar solicitacao-emprestimo.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onVerParcelasClick(Emprestimo emprestimo) {
        try {
            if (emprestimo.getTipoEmprestimo() == TipoEmprestimoEnum.PESSOAL) {
                // Abrir tela de ordenação para empréstimos pessoais
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ordenacao-parcelas.fxml"));
                Scene ordenacaoScene = new Scene(loader.load(), 360, 640);
                OrdenacaoParcelasViewController ordenacaoController = loader.getController();
                ordenacaoController.setEmprestimo(emprestimo);
                ordenacaoController.setTipoEmprestimo(tipoEmprestimo);

                Stage stage = (Stage) loanInfoBox.getScene().getWindow();
                stage.setScene(ordenacaoScene);
                stage.setTitle("EmprestAI - Ordenar Parcelas");
                stage.show();
            } else {
                // Abrir diretamente a tela de parcelas para outros tipos (ex.: CONSIGNADO)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
                Scene parcelaScene = new Scene(loader.load(), 360, 640);
                ParcelaViewController parcelaViewController = loader.getController();
                parcelaViewController.setEmprestimo(emprestimo);
                parcelaViewController.setTipoEmprestimo(tipoEmprestimo);

                Stage stage = (Stage) loanInfoBox.getScene().getWindow();
                stage.setScene(parcelaScene);
                stage.setTitle("EmprestAI - Parcelas");
                stage.show();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void carregarEmprestimos(Cliente cliente) {
        try {
            emprestimos = emprestimoController.getByClienteTipoEmprestimo(cliente.getIdCliente(), tipoEmprestimo);
            System.out.println("Total de empréstimos buscados: " + (emprestimos != null ? emprestimos.size() : 0));
            if (emprestimos != null) {
                emprestimos.forEach(e -> System.out.println("Empréstimo ID: " + e.getIdEmprestimo() + ", Tipo: " + e.getTipoEmprestimo() + ", Parcelas Pagas: " + e.getParcelasPagas() + ", Total Parcelas: " + e.getQuantidadeParcelas()));
            }
            exibirInformacoesEmprestimo();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar empréstimos: " + e.getMessage());
            emprestimos = null;
            exibirInformacoesEmprestimo();
        }
    }

    private void exibirInformacoesEmprestimo() {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (clienteLogado == null || tipoEmprestimo == null) {
            showNoLoanState();
            return;
        }

        boolean hasMargin = checkMarginAvailability();
        boolean hasLoans = emprestimos != null && !emprestimos.isEmpty();

        if (hasLoans) {
            List<Emprestimo> emprestimosFiltrados = emprestimos.stream()
                    .filter(e -> e.getTipoEmprestimo() == tipoEmprestimo)
                    .toList();
            System.out.println("Empréstimos filtrados por tipo " + tipoEmprestimo + ": " + emprestimosFiltrados.size());

            if (!emprestimosFiltrados.isEmpty()) {
                showLoanDetails(emprestimosFiltrados);
                solicitacaoButton.setVisible(hasMargin);
                solicitacaoButton.setManaged(hasMargin);
                if (hasMargin) {
                    solicitacaoMensagem.setText("Confira nossas ofertas!");
                    solicitacaoMensagem.setVisible(true);
                    solicitacaoMensagem.setManaged(true);
                } else {
                    solicitacaoMensagem.setText("Sem margem disponível");
                    solicitacaoMensagem.setVisible(true);
                    solicitacaoMensagem.setManaged(true);
                }
            } else {
                showNoLoanState();
            }
        } else {
            showNoLoanState();
        }
    }

    private void showLoanDetails(List<Emprestimo> emprestimosFiltrados) {
        toggleVisibility(true, false);
        contractTitle.setText("Contratos Ativos");
        contractType.setText(tipoEmprestimo.name());

        loanInfoBox.getChildren().clear();

        for (Emprestimo emprestimo : emprestimosFiltrados) {
            VBox loanCard = createLoanCard(emprestimo);
            loanInfoBox.getChildren().add(loanCard); // Corrigido aqui
        }
    }

    private VBox createLoanCard(Emprestimo emprestimo) {
        VBox card = new VBox(15);
        card.getStyleClass().add("payment-card");

       // HBox titleBox = new HBox(10);
       // Label titleLabel = new Label(emprestimo.getTipoEmprestimo().name() + " R$ " + formatCurrency(emprestimo.getValorTotal() - emprestimo.getValorSeguro() - emprestimo.getOutrosCustos() - emprestimo.getValorIOF()));
      //  titleLabel.getStyleClass().add("payment-date");
      //  titleBox.getChildren().add(titleLabel);
        //card.getChildren().add(titleBox);

        Label valorLabel = new Label("Valor Empréstimo: " + formatCurrency(emprestimo.getValorTotal()));
        valorLabel.getStyleClass().add("payment-type");
        Label parcelaLabel = new Label("Valor da Parcela: " + formatCurrency(emprestimo.getValorParcela()));
        parcelaLabel.getStyleClass().add("payment-type");
        Label parcelasLabel = new Label("Parcelas Pagas: " + emprestimo.getParcelasPagas() + " de " + emprestimo.getQuantidadeParcelas());
        parcelasLabel.getStyleClass().add("payment-type");

        HBox statusBox = new HBox(5);
        Circle statusCircle = new Circle(5);
        statusCircle.getStyleClass().add("status-circle");
        updateStatusCircle(statusCircle, emprestimo.getStatusEmprestimo());
        Label statusLabel = new Label("Status: " + emprestimo.getStatusEmprestimo().name());
        statusLabel.getStyleClass().add("payment-type");
        statusBox.getChildren().addAll(statusCircle, statusLabel);

        card.getChildren().addAll(valorLabel, parcelaLabel, parcelasLabel, statusBox);

        Button verParcelasBtn = new Button("Ver Parcelas");
        verParcelasBtn.getStyleClass().add("banner-button");
        verParcelasBtn.setOnAction(e -> onVerParcelasClick(emprestimo));

        Button gerarPdfBtn = new Button("Gerar Contrato em PDF");
        gerarPdfBtn.getStyleClass().add("banner-button");
        gerarPdfBtn.setOnAction(e -> handleGeneratePdf(emprestimo));

        card.getChildren().addAll(verParcelasBtn, gerarPdfBtn);

        return card;
    }

    private void updateStatusCircle(Circle circle, StatusEmprestimoEnum status) {
        circle.getStyleClass().removeAll("green", "gray", "yellow");
        String style = switch (status) {
            case ABERTO -> "green";
            case QUITADO -> "gray";
            case RENEGOCIADO -> "yellow";
            default -> "gray";
        };
        circle.setStyle("-fx-fill: " + style + ";");
    }

    private void handleGeneratePdf(Emprestimo emprestimo) {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (emprestimo == null || clienteLogado == null) {
            System.out.println("Nenhum empréstimo ou cliente encontrado para gerar o contrato.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Contrato em PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Contrato_" + emprestimo.getIdEmprestimo() + ".pdf");
        File file = fileChooser.showSaveDialog(loanInfoBox.getScene().getWindow());

        if (file != null) {
            try {
                generateContractPDF(file, emprestimo);
                System.out.println("PDF gerado com sucesso: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao gerar o PDF: " + e.getMessage());
            }
        }
    }

    private void showNoLoanState() {
        contractTitle.setText("Nenhum Empréstimo Ativo");
        boolean canOffer = checkMarginAvailability();

        solicitacaoButton.setVisible(canOffer);
        solicitacaoButton.setManaged(canOffer);
        if (canOffer) {
            solicitacaoMensagem.setText("Confira nossas ofertas!");
        } else {
            solicitacaoMensagem.setText("Produto não disponível ou sem margem");
        }
        solicitacaoMensagem.setVisible(true);
        solicitacaoMensagem.setManaged(true);
    }

    private boolean checkMarginAvailability() {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (tipoEmprestimo == PESSOAL) {
            return clienteLogado.getMargemPessoalDisponivel() > 0;
        } else if (tipoEmprestimo == CONSIGNADO) {
            return clienteLogado.getMargemConsignavelDisponivel() > 0;
        }
        return false;
    }

    private void toggleVisibility(boolean loanVisible, boolean solicitacaoVisible) {
        loanInfoBox.setVisible(loanVisible);
        loanInfoBox.setManaged(loanVisible);
        solicitacaoButton.setVisible(solicitacaoVisible);
        solicitacaoButton.setManaged(solicitacaoVisible);
    }

    private void generateContractPDF(File file, Emprestimo emprestimo) throws Exception {
        String htmlContent = GeneratePDF.generateHtmlContent(emprestimo, parcelaController);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            HtmlConverter.convertToPdf(htmlContent, fos);
        }
    }
}