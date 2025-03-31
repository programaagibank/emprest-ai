package br.com.emprestai.view;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.Comparator;
import java.util.List;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmprestimoEnum.PESSOAL;
import static br.com.emprestai.enums.VinculoEnum.*;

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
    @FXML private Button generatePdfButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private Emprestimo emprestimo;
    private TipoEmprestimoEnum tipoEmprestimo;
    private Cliente clienteLogado;
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
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
        exibirInformacoesEmprestimo();
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
        exibirInformacoesEmprestimo();
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

    @FXML
    private void onGeneratePdfClick() {
        if (emprestimo == null || clienteLogado == null) {
            System.out.println("Nenhum empréstimo ou cliente encontrado para gerar o contrato.");
            return;
        }

        // Abre um FileChooser para o usuário escolher onde salvar o PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Contrato em PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Contrato_" + emprestimo.getIdEmprestimo() + ".pdf");
        File file = fileChooser.showSaveDialog(generatePdfButton.getScene().getWindow());

        if (file != null) {
            try {
                generateContractPDF(file);
                System.out.println("PDF gerado com sucesso: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao gerar o PDF: " + e.getMessage());
            }
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void exibirInformacoesEmprestimo() {
        if (emprestimo != null) {
            showLoanDetails();
        } else {
            showNoLoanState();
        }
    }

    private void showLoanDetails() {
        toggleVisibility(true, false);
        contractTitle.setText("Contrato");
        contractType.setText("Consignado");
        totalAmount.setText(formatCurrency(emprestimo.getValorEmprestimo()));
        currentDebt.setText(formatCurrency(emprestimo.getValorEmprestimo()));
        installmentAmount.setText(formatCurrency(emprestimo.getValorParcela()));
        remainingInstallments.setText(String.format("%d de %d",
                emprestimo.getParcelasPagas(), emprestimo.getQuantidadeParcelas()));
        loanStatus.setText(emprestimo.getStatusEmprestimo().name());
        updateStatus(emprestimo.getStatusEmprestimo());
    }

    private void showNoLoanState() {
        contractTitle.setText("Nenhum Empréstimo Ativo");
        boolean isConsignadoEligible = tipoEmprestimo == CONSIGNADO &&
                clienteLogado != null &&
                (clienteLogado.getTipoCliente() == APOSENTADO ||
                        clienteLogado.getTipoCliente() == PENSIONISTA ||
                        clienteLogado.getTipoCliente() == SERVIDOR);

        if (tipoEmprestimo == PESSOAL || isConsignadoEligible) {
            toggleVisibility(false, true);
        } else {
            toggleVisibility(false, false);
            simulationMessage.setText("Produto não disponível");
            simulationMessage.setVisible(true);
            simulationMessage.setManaged(true);
        }
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

    private void generateContractPDF(File file) throws Exception {
        // Gera o HTML dinamicamente
        String htmlContent = generateHtmlContent();

        // Converte o HTML em PDF usando html2pdf
        try (FileOutputStream fos = new FileOutputStream(file)) {
            HtmlConverter.convertToPdf(htmlContent, fos);
        }
    }

    private String generateHtmlContent() throws SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
                .append("<html lang=\"pt-br\">")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<title>Resumo do Empréstimo</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; font-size: 14px; }")
                .append("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }")
                .append("th, td { border: 1px solid #333; padding: 5px; text-align: left; }")
                .append("th { background-color: #eee; }")
                .append(".section-title { font-weight: bold; font-size: 16px; margin-top: 20px; border-bottom: 1px solid #333; padding-bottom: 5px; }") // Linha de separação
                .append("</style>")
                .append("</head>")
                .append("<body>");

        // Seção: Informações do Contrato
        html.append("<div class=\"section-title\">Informações do Contrato</div>")
                .append("<table>")
                .append("<tr><td>Modalidade de Operação</td><td>").append(emprestimo.getTipoEmprestimo().name()).append("</td></tr>")
                .append("<tr><td>Valor da Operação</td><td>").append(formatCurrency(emprestimo.getValorEmprestimo())).append("</td></tr>")
                .append("<tr><td>Número do Contrato</td><td>").append(emprestimo.getIdEmprestimo()).append("</td></tr>") // Ajuste se tiver um número real
                //.append("<tr><td>Data e Hora da Contratação</td><td>").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                //.append("<tr><td>Sistema de Pagamento</td><td>DÉBITO EM CONTA CORRENTE</td></tr>")
                //.append("<tr><td>Data de Liberação do Crédito</td><td>").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td></tr>")
                //.append("<tr><td>Data do Último Vencimento</td><td>31/03/2028</td></tr>")
                //.append("<tr><td>Canal de Contratação</td><td>066 MOBILE BANK PF</td></tr>")
                .append("</table>");

        // Seção: Taxas
        html.append("<div class=\"section-title\">Taxas</div>")
                .append("<table>")
                .append("<tr>")
                .append("<th>Taxa de Juros Mensal</th>")
                //.append("<th>Taxa de Juros Anual</th>")
                //.append("<th>Taxa de Juros Efetiva (CET)</th>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(emprestimo.getTaxaEfetivaMensal()+"</td>")
                //.append("<td>20,27% a.a.</td>")
                //.append("<td>1,96% a.m. / 26,16% a.a.</td>")
                .append("</tr>")
                .append("</table>");

        // Seção: Custos
        //html.append("<div class=\"section-title\">Custos</div>")
                //.append("<table>")
                //.append("<tr>")
                //.append("<td>Tributos</td><td>R$ 940,57</td>")
                //.append("<td>Seguros</td><td>R$ 1.888,43</td>")
                //.append("</tr>")
                //.append("<tr>")
                //.append("<td>Registros</td><td>R$ 0,00</td>")
                //.append("<td>Pagos Serv. Terceiros</td><td>R$ 0,00</td>")
                //.append("</tr>")
                //.append("<tr>")
                //.append("<td>Tarifas</td><td>R$ 0,00</td>")
                //.append("<td></td><td></td>")
                //.append("</tr>")
                //.append("</table>");

        // Seção: Resumo
        html.append("<div class=\"section-title\">Resumo</div>")
                .append("<table>")
                .append("<tr>")
                .append("<td>Prazo Total da Operação</td><td>").append(emprestimo.getQuantidadeParcelas()).append("</td>")
                .append("<td>Prazo Remanescente</td><td>").append(emprestimo.getQuantidadeParcelas() - emprestimo.getParcelasPagas()).append("</td>")
                .append("<td>Saldo Devedor Atualizado</td><td>").append(formatCurrency(emprestimo.getValorEmprestimo())).append("</td>")
                .append("</tr>")
                .append("</table>");

        // Seção: Demonstrativo de Evolução do Saldo Devedor
        html.append("<div class=\"section-title\">Demonstrativo de Evolução do Saldo Devedor / Composição do Valor das Parcelas</div>")
                .append("<table>")
                .append("<tr>")
                .append("<th>No Parcela</th>")
                .append("<th>Vencimento</th>")
                .append("<th>Valor Parcela</th>")
                .append("<th>Valor Principal</th>")
                .append("<th>Valor Juros</th>")
                .append("<th>Saldo Devedor</th>")
                .append("<th>Situação da Parcela</th>")
                .append("</tr>");

        // Obtém as parcelas do empréstimo
        List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
        if (parcelas != null && !parcelas.isEmpty()) {
            double saldoDevedor = emprestimo.getValorEmprestimo();
            for (int i = 0; i < parcelas.size(); i++) {
                Parcela parcela = parcelas.get(i);
                double valorParcela = parcela.getValorPresenteParcela();
                double valorPrincipal = valorParcela * 0.7; // Exemplo: 70% do valor da parcela
                double valorJuros = valorParcela * 0.3; // Exemplo: 30% do valor da parcela
                saldoDevedor -= valorPrincipal;

                html.append("<tr>")
                        .append("<td>").append(i + 1).append("</td>")
                        .append("<td>").append(parcela.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>")
                        .append("<td>").append(formatCurrency(valorParcela)).append("</td>")
                        .append("<td>").append(formatCurrency(valorPrincipal)).append("</td>")
                        .append("<td>").append(formatCurrency(valorJuros)).append("</td>")
                        .append("<td>").append(formatCurrency(saldoDevedor)).append("</td>")
                        .append("<td>").append(parcela.getStatus() != null ? parcela.getStatus().toString() : "PARCELA PAGA").append("</td>")
                        .append("</tr>");
            }
        } else {
            // Caso não haja parcelas, preenche com valores simulados
            double saldoDevedor = emprestimo.getValorEmprestimo();
            double valorParcela = emprestimo.getValorParcela();
            double valorPrincipal = valorParcela * 0.7;
            double valorJuros = valorParcela * 0.3;
            LocalDate vencimento = LocalDate.now();
            int parcelasRestantes = emprestimo.getQuantidadeParcelas() - emprestimo.getParcelasPagas();
            for (int i = 1; i <= parcelasRestantes; i++) {
                html.append("<tr>")
                        .append("<td>").append(i).append("</td>")
                        .append("<td>").append(vencimento.plusMonths(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>")
                        .append("<td>").append(formatCurrency(valorParcela)).append("</td>")
                        .append("<td>").append(formatCurrency(valorPrincipal)).append("</td>")
                        .append("<td>").append(formatCurrency(valorJuros)).append("</td>")
                        .append("<td>").append(formatCurrency(saldoDevedor)).append("</td>")
                        .append("<td>PARCELA PAGA</td>")
                        .append("</tr>");
                saldoDevedor -= valorPrincipal;
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