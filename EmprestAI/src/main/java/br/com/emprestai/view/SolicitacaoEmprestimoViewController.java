package br.com.emprestai.view;

import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class SolicitacaoEmprestimoViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label     loanTypeLabel;
    @FXML private TextField loanAmountField;
    @FXML private Button    continueButton;
    @FXML private Button    homeButton;
    @FXML private Button    cancelButton;
    @FXML private Button    profileButton;
    @FXML private Button    exitButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private TipoEmprestimoEnum tipoEmprestimo;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        System.out.println("CSS carregado: " + getClass().getResource("../css/solicitacao.css"));
        // Verifica se há cliente logado
        if (SessionManager.getInstance().getClienteLogado() == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick();
            return;
        }

        // Configura o TextField para formato de moeda
        configureCurrencyField();

        // Garante que o tipo de empréstimo seja exibido
        if (tipoEmprestimo != null) {
            loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));
        }
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
        if (tipoEmprestimo != null) {
            loanTypeLabel.setText("Tipo: " + (tipoEmprestimo == TipoEmprestimoEnum.CONSIGNADO ? "Consignado" : "Pessoal"));
        }
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onContinueClick() {
        try {
            if (loanAmountField.getText().isEmpty()) {
                return;
            }

            // Obtém o valor diretamente do TextFormatter
            Number formatterValue = ((TextFormatter<Number>)loanAmountField.getTextFormatter()).getValue();
            double loanAmount = (formatterValue != null) ? formatterValue.doubleValue() : 0.0;

            // Verifica se o valor é válido
            if (loanAmount <= 0.0) {
                System.out.println("Valor inválido: " + loanAmount);
                return;
            }

            System.out.println("Valor convertido: " + loanAmount);

            // Carrega a tela de ofertas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ofertas.fxml"));
            Scene scene = new Scene(loader.load(), 400, 700);
            OfertasViewController controller = loader.getController();
            controller.setOfertaData(loanAmount, tipoEmprestimo);

            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("EmprestAI - Ofertas de Empréstimo");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            EmprestimoViewController emprestimoController = loader.getController();
            emprestimoController.setTipoEmprestimo(tipoEmprestimo);
            Stage stage = (Stage) cancelButton.getScene().getWindow();
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
    private void onProfileClick() {
        System.out.println("Navegar para a tela de perfil (não implementado).");
    }

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
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
    private void configureCurrencyField() {
        // Configura o NumberFormat para reais
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        decimalFormat.setParseBigDecimal(true);

        // Conversor para formatar e parsear o texto como moeda
        StringConverter<Number> converter = new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                if (value == null || value.doubleValue() == 0.0) {
                    return "";
                }
                return decimalFormat.format(value);
            }

            @Override
            public Number fromString(String string) {
                try {
                    if (string == null || string.trim().isEmpty()) {
                        return 0.0;
                    }
                    // Remove tudo exceto números e vírgula para parsear
                    String cleanString = string.replaceAll("[^0-9,]", "").replace(",", ".");
                    return Double.parseDouble(cleanString);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        };

        // Filtro para aceitar apenas números e vírgula
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Permite apenas números e vírgula, com até 2 casas decimais
            if (newText.matches("\\d*([.,]\\d{0,2})?") || newText.isEmpty()) {
                return change;
            }
            return null;
        };

        // Aplica o TextFormatter ao TextField
        TextFormatter<Number> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        loanAmountField.setTextFormatter(textFormatter);

        // Listener para formatar o texto ao perder o foco
        loanAmountField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                Number value = textFormatter.getValue();
                if (value != null && value.doubleValue() > 0) {
                    loanAmountField.setText(decimalFormat.format(value));
                } else {
                    loanAmountField.setText("");
                }
            }
        });

        // Listener para limpar o formato ao ganhar foco, facilitando a edição
        loanAmountField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                Number value = textFormatter.getValue();
                if (value != null && value.doubleValue() > 0) {
                    loanAmountField.setText(String.format("%.2f", value.doubleValue()).replace(".", ","));
                }
            }
        });
    }
}