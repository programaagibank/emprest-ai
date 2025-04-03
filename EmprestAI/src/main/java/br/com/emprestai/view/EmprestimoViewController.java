package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.service.calculos.CalculadoraSaldos;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmprestimoEnum.PESSOAL;

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
    @FXML private Label simulationMessage;
    @FXML private Button homeButton;
    @FXML private Button exitButton;
    @FXML private Button simulateButton;
    @FXML private VBox loanInfoBox;
    @FXML private Button ordemVencimentoButton;
    @FXML private Button maiorDescontoButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private List<Emprestimo> emprestimos;
    private TipoEmprestimoEnum tipoEmprestimo;
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());
    private EmprestimoController emprestimoController = new EmprestimoController(new EmprestimoDAO(), null); // Ajuste conforme necessário

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
        // Carrega os empréstimos apenas se o tipoEmprestimo já estiver definido
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
            SessionManager.getInstance().clearSession(); // Limpa a sessão ao sair
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
    private void onSimulateClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("simulacaoEmprestimo.fxml"));
            Scene simulateScene = new Scene(loader.load(), 360, 640);
            SimulacaoViewController simulacaoController = loader.getController();
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

    @FXML
    private void onOrdemVencimentoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
            Scene parcelaScene = new Scene(loader.load(), 360, 640);
            ParcelaViewController parcelaViewController = loader.getController();

            List<Parcela> todasParcelas = new ArrayList<>();
            for (Emprestimo emprestimo : emprestimos) {
                List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
                if (parcelas != null) {
                    todasParcelas.addAll(parcelas);
                }
            }
            todasParcelas.sort(Comparator.comparing(Parcela::getDataVencimento));

            parcelaViewController.setEmprestimo(null);
            parcelaViewController.setTipoEmprestimo(tipoEmprestimo); // Passa o tipo para manter o contexto
            parcelaViewController.setParcelas(todasParcelas);

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

            List<Parcela> todasParcelas = new ArrayList<>();
            for (Emprestimo emprestimo : emprestimos) {
                List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
                if (parcelas != null) {
                    todasParcelas.addAll(parcelas);
                }
            }
            todasParcelas.sort(Comparator.comparing(Parcela::getDataVencimento, Comparator.reverseOrder()));

            parcelaViewController.setEmprestimo(null);
            parcelaViewController.setTipoEmprestimo(tipoEmprestimo); // Passa o tipo para manter o contexto
            parcelaViewController.setParcelas(todasParcelas);

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
    private void carregarEmprestimos(Cliente cliente) {
        try {
            // Busca os empréstimos do cliente filtrados pelo tipoEmprestimo
            emprestimos = emprestimoController.getByCliente(cliente.getIdCliente(), tipoEmprestimo);
            System.out.println("Total de empréstimos buscados: " + (emprestimos != null ? emprestimos.size() : 0));
            if (emprestimos != null) {
                emprestimos.forEach(e -> System.out.println("Empréstimo ID: " + e.getIdEmprestimo() + ", Tipo: " + e.getTipoEmprestimo() + ", Parcelas Pagas: " + e.getParcelasPagas() + ", Total Parcelas: " + e.getQuantidadeParcelas()));
            }
            exibirInformacoesEmprestimo();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar empréstimos: " + e.getMessage());
            emprestimos = null; // Limpa a lista em caso de erro
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
                simulateButton.setVisible(hasMargin);
                simulateButton.setManaged(hasMargin);
                simulationMessage.setVisible(!hasMargin);
                simulationMessage.setManaged(!hasMargin);
                simulationMessage.setText(hasMargin ? "" : "Sem margem disponível");
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
            loanInfoBox.getChildren().add(loanCard);
        }
    }

    private VBox createLoanCard(Emprestimo emprestimo) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-border-radius: 5;");

        HBox titleBox = new HBox(10);
        Label titleLabel = new Label(emprestimo.getTipoEmprestimo().name() + " R$ " + formatCurrency(emprestimo.getValorEmprestimo()));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        titleBox.getChildren().add(titleLabel);
        card.getChildren().add(titleBox);

        Label valorLabel = new Label("Valor Empréstimo: " + formatCurrency(emprestimo.getValorEmprestimo()));
        Label parcelaLabel = new Label("Valor da Parcela: " + formatCurrency(emprestimo.getValorParcela()));
        Label parcelasLabel = new Label("Parcelas Pagas: " + emprestimo.getParcelasPagas() + " de " + emprestimo.getQuantidadeParcelas());

        System.out.println("Empréstimo ID: " + emprestimo.getIdEmprestimo() + " - Parcelas Pagas: " + emprestimo.getParcelasPagas() + ", Total Parcelas: " + emprestimo.getQuantidadeParcelas());

        HBox statusBox = new HBox(5);
        Circle statusCircle = new Circle(5);
        updateStatusCircle(statusCircle, emprestimo.getStatusEmprestimo());
        Label statusLabel = new Label("Status: " + emprestimo.getStatusEmprestimo().name());
        statusBox.getChildren().addAll(statusCircle, statusLabel);

        card.getChildren().addAll(valorLabel, parcelaLabel, parcelasLabel, statusBox);

        Button ordemVencimentoBtn = new Button("Ordem de Vencimento");
        ordemVencimentoBtn.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-size: 14;");
        ordemVencimentoBtn.setOnAction(e -> handleOrdemVencimento(emprestimo));

        Button maiorDescontoBtn = new Button("Maior Desconto");
        maiorDescontoBtn.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-size: 14;");
        maiorDescontoBtn.setOnAction(e -> handleMaiorDesconto(emprestimo));

        Button gerarPdfBtn = new Button("Gerar Contrato em PDF");
        gerarPdfBtn.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-size: 14;");
        gerarPdfBtn.setOnAction(e -> handleGeneratePdf(emprestimo));

        card.getChildren().addAll(ordemVencimentoBtn, maiorDescontoBtn, gerarPdfBtn);

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

    private void handleOrdemVencimento(Emprestimo emprestimo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
            Scene parcelaScene = new Scene(loader.load(), 360, 640);
            ParcelaViewController parcelaViewController = loader.getController();

            List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
            if (parcelas != null) {
                parcelas.sort(Comparator.comparing(Parcela::getDataVencimento));
            }

            parcelaViewController.setEmprestimo(emprestimo);
            parcelaViewController.setTipoEmprestimo(tipoEmprestimo); // Mantém o contexto
            parcelaViewController.setParcelas(parcelas);

            Stage stage = (Stage) loanInfoBox.getScene().getWindow();
            stage.setScene(parcelaScene);
            stage.setTitle("EmprestAI - Parcelas (Ordem de Vencimento)");
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar parcela.fxml: " + e.getMessage());
        }
    }

    private void handleMaiorDesconto(Emprestimo emprestimo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
            Scene parcelaScene = new Scene(loader.load(), 360, 640);
            ParcelaViewController parcelaViewController = loader.getController();

            List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
            if (parcelas != null) {
                parcelas.sort(Comparator.comparing(Parcela::getDataVencimento, Comparator.reverseOrder()));
            }

            parcelaViewController.setEmprestimo(emprestimo);
            parcelaViewController.setTipoEmprestimo(tipoEmprestimo); // Mantém o contexto
            parcelaViewController.setParcelas(parcelas);

            Stage stage = (Stage) loanInfoBox.getScene().getWindow();
            stage.setScene(parcelaScene);
            stage.setTitle("EmprestAI - Parcelas (Maior Desconto)");
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar parcela.fxml: " + e.getMessage());
        }
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
        boolean canSimulate = checkMarginAvailability();

        if (canSimulate) {
            toggleVisibility(false, true);
            simulationMessage.setText("Simule seu primeiro empréstimo!");
            simulationMessage.setVisible(true);
            simulationMessage.setManaged(true);
        } else {
            toggleVisibility(false, false);
            simulationMessage.setText("Produto não disponível ou sem margem");
            simulationMessage.setVisible(true);
            simulationMessage.setManaged(true);
        }
    }

    private boolean checkMarginAvailability() {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (tipoEmprestimo == PESSOAL) {
            return true;
        } else if (tipoEmprestimo == CONSIGNADO) {
            return clienteLogado.getMargemConsignavelDisponivel() > 0;
        }
        return false;
    }

    private void toggleVisibility(boolean loanVisible, boolean simulateVisible) {
        loanInfoBox.setVisible(loanVisible);
        loanInfoBox.setManaged(loanVisible);
        simulateButton.setVisible(simulateVisible);
        simulateButton.setManaged(simulateVisible);
        simulationMessage.setVisible(false);
        simulationMessage.setManaged(false);
    }

    private String formatCurrency(double value) {
        return String.format("R$ %.2f", value);
    }

    public void updateStatus(StatusEmprestimoEnum status) {
        statusCircle.getStyleClass().removeAll("green", "gray", "yellow");
        statusCircle.getStyleClass().add("status-circle");

        String style = switch (status) {
            case ABERTO -> "green";
            case QUITADO -> "gray";
            case RENEGOCIADO -> "yellow";
            default -> "gray";
        };
        statusCircle.getStyleClass().add(style);
    }

    private void generateContractPDF(File file, Emprestimo emprestimo) throws Exception {
        String htmlContent = generateHtmlContent(emprestimo);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            HtmlConverter.convertToPdf(htmlContent, fos);
        }
    }

    private String generateHtmlContent(Emprestimo emprestimo) throws SQLException {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        StringBuilder html = new StringBuilder();
        List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
        html.append("<!DOCTYPE html>")
                .append("<html lang=\"pt-br\">")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<title>Documento Descritivo de Crédito</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; font-size: 14px; }")
                .append("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }")
                .append("th, td { border: 1px solid #333; padding: 5px; text-align: left; }")
                .append("th { background-color: #eee; }")
                .append(".section-title { font-weight: bold; font-size: 16px; margin-top: 20px; border-bottom: 1px solid #333; padding-bottom: 5px; }")
                .append(".header { margin-bottom: 20px; }")
                .append(".header div { margin-bottom: 5px; }")
                .append(".separator { border-top: 1px solid #333; margin: 20px 0; }")
                .append(".summary { display: flex; justify-content: space-between; margin-bottom: 20px; }")
                .append(".summary div { width: 30%; }")
                .append("</style>")
                .append("</head>")
                .append("<body>");

        html.append("<div class=\"header\">")
                .append("<h2>Documento Descritivo de Crédito</h2>")
                .append("<div><strong>Nome:</strong> ").append(clienteLogado != null ? clienteLogado.getNomeCliente() : "Não disponível").append("</div>")
                .append("<div><strong>CPF:</strong> ").append(clienteLogado != null ? formatCpf(clienteLogado.getCpfCliente()) : "Não disponível").append("</div>")
                .append("<div><strong>Emissão:</strong> ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</div>")
                .append("</div>");

        html.append("<div class=\"separator\"></div>");

        html.append("<div class=\"section-title\">Característica do Contrato</div>")
                .append("<table>")
                .append("<tr><td>Modalidade de Operação</td><td>").append(emprestimo.getTipoEmprestimo().name()).append("</td></tr>")
                .append("<tr><td>Valor da Operação</td><td>").append(formatCurrency(emprestimo.getValorEmprestimo())).append("</td></tr>")
                .append("<tr><td>Número do Contrato</td><td>").append(emprestimo.getIdEmprestimo()).append("</td></tr>")
                .append("<tr><td>Data da Contratação</td><td>").append(emprestimo.getDataContratacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td></tr>")
                .append("<tr><td>Data de Liberação do Crédito</td><td>").append(emprestimo.getDataLiberacaoCred().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td></tr>")
                .append("<tr><td>Data do Último Vencimento</td><td>")
                .append(emprestimo.getDataInicio().plusMonths(emprestimo.getQuantidadeParcelas()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append("</td></tr>")
                .append("</table>");

        html.append("<div class=\"separator\"></div>");

        double taxaJurosAnual = emprestimo.getTaxaJuros() * 12;
        double tributos = emprestimo.getValorIOF();
        double tarifas = emprestimo.getOutrosCustos() + (emprestimo.getContratarSeguro() != null && emprestimo.getContratarSeguro() ? emprestimo.getValorSeguro() : 0);
        html.append("<div class=\"section-title\">Taxa de Juros</div>")
                .append("<table>")
                .append("<tr><td>Taxa de Juros Mensal Nominal</td><td>").append(String.format("%.2f%% a.m.", emprestimo.getTaxaJuros())).append("</td></tr>")
                .append("<tr><td>Taxa de Juros Anual Nominal</td><td>").append(String.format("%.2f%% a.a.", taxaJurosAnual)).append("</td></tr>")
                .append("<tr><td>Taxa de Juros Efetiva (CET)</td><td>").append(String.format("%.2f%% a.m.", emprestimo.getTaxaEfetivaMensal())).append("</td></tr>")
                .append("<tr><td>Tarifas</td><td>").append(formatCurrency(tarifas)).append("</td></tr>")
                .append("<tr><td>Tributos</td><td>").append(formatCurrency(tributos)).append("</td></tr>")
                .append("</table>");

        html.append("<div class=\"separator\"></div>");

        int prazoTotal = emprestimo.getQuantidadeParcelas();
        int prazoRemanescente = emprestimo.getQuantidadeParcelas() - emprestimo.getParcelasPagas();
        double saldoDevedorAtualizado = CalculadoraSaldos.calcularSaldoDevedorAtualizado(parcelas);
        html.append("<div class=\"section-title\">Resumo</div>")
                .append("<div class=\"summary\">")
                .append("<div><strong>Prazo Total da Operação:</strong> ").append(prazoTotal).append(" meses</div>")
                .append("<div><strong>Prazo Remanescente:</strong> ").append(prazoRemanescente).append(" meses</div>")
                .append("<div><strong>Saldo Devedor Atual:</strong> ").append(formatCurrency(saldoDevedorAtualizado)).append("</div>")
                .append("</div>");

        html.append("<div class=\"separator\"></div>");

        html.append("<div class=\"section-title\">Demonstrativo de Evolução do Saldo Devedor/Composição do Valor das Parcelas</div>")
                .append("<table>")
                .append("<tr>")
                .append("<th>Nro Parcela</th>")
                .append("<th>Vencimento da Parcela</th>")
                .append("<th>Valor Parcela</th>")
                .append("<th>Valor Principal da Parcela</th>")
                .append("<th>Valor dos Juros da Parcela</th>")
                .append("<th>Saldo Devedor/Encargos</th>")
                .append("<th>Situação da Parcela</th>")
                .append("</tr>");

        if (parcelas != null && !parcelas.isEmpty()) {
            for (int i = 0; i < parcelas.size(); i++) {
                Parcela parcela = parcelas.get(i);
                double valorParcela = emprestimo.getValorParcela();
                double valorPrincipal = parcela.getAmortizacao();
                double valorJuros = parcela.getJuros();
                double valorPresenteParcela = parcela.getValorPresenteParcela();

                html.append("<tr>")
                        .append("<td>").append(i + 1).append("</td>")
                        .append("<td>").append(parcela.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>")
                        .append("<td>").append(formatCurrency(valorParcela)).append("</td>")
                        .append("<td>").append(formatCurrency(valorPrincipal)).append("</td>")
                        .append("<td>").append(formatCurrency(valorJuros)).append("</td>")
                        .append("<td>").append(formatCurrency(valorPresenteParcela)).append("</td>")
                        .append("<td>").append(parcela.getStatus() != null ? parcela.getStatus().toString() : "PARCELA PAGA").append("</td>")
                        .append("</tr>");
            }
        }
        html.append("</table>");

        html.append("</body>")
                .append("</html>");

        return html.toString();
    }

    private String formatCpf(String cpf) {
        if (cpf != null && cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf != null ? cpf : "";
    }
}