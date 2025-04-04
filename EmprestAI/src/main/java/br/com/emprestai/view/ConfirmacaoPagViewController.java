package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.LoginController;
import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.util.SessionManager;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
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

    // Formatters
    private static final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        System.out.println("CSS carregado: " + getClass().getResource("../css/confirmacao-pag.css"));
        SessionManager.getInstance().refreshClienteLogado();
        // Verifica se há cliente logado
        if (SessionManager.getInstance().getClienteLogado() == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            voltarParaLogin();
            return;
        }

        // Remover foco automático da senha
        javafx.application.Platform.runLater(() -> {
            confirmacaoContainer.requestFocus();
        });

        // Password field formatting
        senhaField.setPromptText("Digite sua senha");
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

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onConfirmarClick() {
        if (SessionManager.getInstance().getClienteLogado() == null) {
            mensagemLabel.setText("Sessão expirada. Redirecionando para login...");
            voltarParaLogin();
            return;
        }

        String senha = senhaField.getText();
        if (senha.isEmpty()) {
            mensagemLabel.setText("Digite a senha para confirmar o pagamento.");
            return;
        }

        try {
            // Valida a senha usando o cliente do SessionManager
            boolean senhaValida = loginController.validaSenha(senha, SessionManager.getInstance().getClienteLogado().getSenha());
            if (senhaValida) {
                parcelaController.putListParcelas(parcelasSelecionadas);
                mensagemLabel.setText("Pagamento confirmado com sucesso!");
                mensagemLabel.setStyle("-fx-text-fill: #008000;");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
            Scene parcelaScene = new Scene(loader.load(), 360, 640);
            ParcelaViewController controller = loader.getController();
            controller.setParcelas(parcelasSelecionadas);
            if (parcelaViewController != null && parcelaViewController.getEmprestimo() != null) {
                controller.setEmprestimo(parcelaViewController.getEmprestimo());
                controller.setTipoEmprestimo(parcelaViewController.getTipoEmprestimo());
            }

            Stage stage = (Stage) cancelarButton.getScene().getWindow();
            stage.setScene(parcelaScene);
            stage.setTitle("EmprestAI - Parcelas");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao voltar para parcelas: " + e.getMessage());
        }
    }

    private void voltarParaLogin() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load(), 360, 640);
            Stage stage = (Stage) cancelarButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao redirecionar para login: " + e.getMessage());
        }
    }
}