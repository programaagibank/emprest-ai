package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.service.SessionManager;
import br.com.emprestai.util.FormatUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PerfilClienteViewController {

    // --------------------------------------------------------------------------------
    // FXML Elements
    // --------------------------------------------------------------------------------
    @FXML private Label profileInitials;
    @FXML private Label nomeClienteLabel;
    @FXML private Label cpfLabel;
    @FXML private Label dataNascimentoLabel;
    @FXML private Label idadeLabel;
    @FXML private Label scoreLabel;
    @FXML private ProgressBar scoreProgress;
    @FXML private Label rendaLiquidaLabel;
    @FXML private Label rendaConsignavelLabel;
    @FXML private Label valorComprometidoLabel;
    @FXML private Label limiteConsignadoLabel;
    @FXML private Label limitePessoalLabel;
    @FXML private Button backButton;
    @FXML private Button alterarSenhaButton;
    @FXML private Button atualizarDadosButton;

    // --------------------------------------------------------------------------------
    // Controllers and Data
    // --------------------------------------------------------------------------------
    private ClienteController clienteController;
    private Cliente clienteAtual;

    // --------------------------------------------------------------------------------
    // Initialize Method
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        // Inicializar o controller e carregar dados do cliente
        clienteController = new ClienteController(new ClienteDAO());

        try {
            // Recupera o cliente logado usando o SessionManager
            Long clienteId = SessionManager.getInstance().getClienteLogadoId();
            if (clienteId != null) {
                carregarDadosCliente(clienteId);
            } else {
                mostrarErro("Sessão não encontrada", "Não foi possível identificar o cliente logado.");
                voltarParaLogin();
            }
        } catch (Exception e) {
            mostrarErro("Erro ao carregar dados", "Ocorreu um erro ao carregar os dados do cliente: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Data Loading Methods
    // --------------------------------------------------------------------------------
    private void carregarDadosCliente(Long clienteId) {
        try {
            clienteAtual = clienteController.getByCpf(SessionManager.getInstance().getClienteLogadoCpf());

            if (clienteAtual == null) {
                mostrarErro("Cliente não encontrado", "Não foi possível recuperar os dados do cliente.");
                return;
            }

            // Preencher as iniciais do perfil
            if (clienteAtual.getNomeCliente() != null && !clienteAtual.getNomeCliente().isEmpty()) {
                String[] nomes = clienteAtual.getNomeCliente().split(" ");
                if (nomes.length > 1) {
                    // Pegar primeira letra do primeiro e último nome
                    profileInitials.setText(String.valueOf(nomes[0].charAt(0)) +
                            String.valueOf(nomes[nomes.length - 1].charAt(0)));
                } else {
                    // Se só tem um nome, pegar as duas primeiras letras
                    profileInitials.setText(nomes[0].substring(0, Math.min(2, nomes[0].length())));
                }
            }

            // Dados pessoais
            nomeClienteLabel.setText(clienteAtual.getNomeCliente());
            cpfLabel.setText(formatarCPF(clienteAtual.getCpfCliente()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimentoLabel.setText(clienteAtual.getDataNascimento().format(formatter));

            idadeLabel.setText(clienteAtual.getIdade() + " anos");

            // Score
            int score = clienteAtual.getScore();
            scoreLabel.setText(String.valueOf(score));
            // Converter score para 0-1 para o ProgressBar (assumindo score de 0-1000)
            double scoreRatio = score / 1000.0;
            scoreProgress.setProgress(scoreRatio);

            // Estilizar o ProgressBar baseado no score
            if (scoreRatio < 0.4) {
                scoreProgress.getStyleClass().add("score-low");
            } else if (scoreRatio < 0.7) {
                scoreProgress.getStyleClass().add("score-medium");
            } else {
                scoreProgress.getStyleClass().add("score-high");
            }

            // Informações financeiras
            rendaLiquidaLabel.setText(FormatUtils.formatarMoeda(clienteAtual.getVencimentoLiquidoTotal()));
            rendaConsignavelLabel.setText(FormatUtils.formatarMoeda(clienteAtual.getVencimentoConsignavelTotal()));
            valorComprometidoLabel.setText(FormatUtils.formatarMoeda(clienteAtual.getValorComprometido()));

            // Limites de crédito
            limiteConsignadoLabel.setText(FormatUtils.formatarMoeda(clienteAtual.getLimiteCreditoConsignado()));
            limitePessoalLabel.setText(FormatUtils.formatarMoeda(clienteAtual.getLimiteCreditoPessoal()));

        } catch (ApiException e) {
            mostrarErro("Erro ao carregar dados", "Ocorreu um erro ao carregar os dados do cliente: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onBackClick() {
        carregarTela("/fxml/dashboard.fxml");
    }

    @FXML
    private void onAlterarSenhaClick() {
        // Implementação futura: mostrar dialog para alterar senha
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alterar Senha");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidade de alteração de senha será implementada em breve.");
        alert.showAndWait();
    }

    @FXML
    private void onAtualizarDadosClick() {
        // Implementação futura: mostrar tela para atualizar dados do cliente
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atualizar Dados");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidade de atualização de dados será implementada em breve.");
        alert.showAndWait();
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void voltarParaLogin() {
        try {
            SessionManager.getInstance().logout();
            carregarTela("/fxml/login.fxml");
        } catch (Exception e) {
            mostrarErro("Erro ao deslogar", "Ocorreu um erro ao voltar para a tela de login: " + e.getMessage());
        }
    }

    private void carregarTela(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = backButton.getScene();
            Stage stage = (Stage) scene.getWindow();
            scene.setRoot(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarErro("Erro ao carregar tela", "Ocorreu um erro ao carregar a tela: " + e.getMessage());
        }
    }

    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9);
    }
}