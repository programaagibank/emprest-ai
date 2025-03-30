package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.controller.LoginController;
import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Parcela;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConfirmacaoPagViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private VBox confirmacaoContainer;
    @FXML private Label valorLabel;
    @FXML private Label parcelasLabel;
    @FXML private Label dataLabel;
    @FXML private PasswordField senhaField;
    @FXML private Button confirmarButton;
    @FXML private Button cancelarButton;
    @FXML private Label mensagemLabel;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private List<Parcela> parcelasSelecionadas;
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());
    private ParcelaViewController parcelaViewController;
    private LoginController loginController = new LoginController(new ClienteController(new ClienteDAO()));
    private Cliente             clienteLogado;

    // Formatters
    private static final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {

        // Password field formatting
        senhaField.setPromptText("6 dígitos");
        senhaField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            String numbersOnly = newValue.replaceAll("[^0-9]", "");
            if (numbersOnly.length() > 6) {
                numbersOnly = numbersOnly.substring(0, 6);
            }
            senhaField.setText(numbersOnly);
            senhaField.positionCaret(numbersOnly.length());
        });
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setParcelasSelecionadas(List<Parcela> parcelas) {
        this.parcelasSelecionadas = parcelas;
        preencherDados();
    }

    public void setParcelaViewController(ParcelaViewController controller) {
        this.parcelaViewController = controller;
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onConfirmarClick() {
        String senha = senhaField.getText();
        if (senha.isEmpty()) {
            mensagemLabel.setText("Digite a senha para confirmar o pagamento.");
            return;
        }

        try {
            // Aqui você pode chamar o método do controller que valida a senha
            boolean senhaValida = loginController.validaSenha(senha, clienteLogado.getSenha()); // Supondo que existe esse método
            if (senhaValida) {
                parcelaController.putListParcelas(parcelasSelecionadas);
                mensagemLabel.setText("Pagamento confirmado com sucesso!");
                voltarParaParcelas();
            } else {
                mensagemLabel.setText("Senha inválida. Tente novamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao confirmar pagamento: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelarClick() {
        voltarParaParcelas();
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void preencherDados() {
        double total = parcelasSelecionadas.stream()
                .mapToDouble(p -> p.getValorPresenteParcela() + p.getMulta() + p.getJurosMora())
                .sum();
        valorLabel.setText("Valor: " + df.format(total));
        parcelasLabel.setText("Quantidade de Parcelas: " + parcelasSelecionadas.size());
        dataLabel.setText("Data: " + LocalDate.now().format(dateFormat));
    }

    private void voltarParaParcelas() {
        try {
            // Abrir tela de confirmação
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load(), 360, 640);
            DashboardViewController dashboardViewController = loader.getController();
            dashboardViewController.setClienteLogado(clienteLogado);

            Stage stage = (Stage) cancelarButton.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle("EmprestAI - Confirmação de Pagamento");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao abrir confirmação: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
}