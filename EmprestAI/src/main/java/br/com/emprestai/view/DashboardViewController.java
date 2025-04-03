package br.com.emprestai.view;

import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static br.com.emprestai.enums.TipoEmprestimoEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmprestimoEnum.PESSOAL;

public class DashboardViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private Label  greetingLabel;
    @FXML private Label  debtAmount;
    @FXML private Label  userName;
    @FXML private Label  userCpf;
    @FXML private Label  creditMargin;
    @FXML private Label  creditMarginConsig;
    @FXML private Label  creditMarginPessoal;
    @FXML private Button consignadoButton;
    @FXML private Button pessoalButton;
    @FXML private Button homeButton;
    @FXML private Button exitButton;
    @FXML private Button profileButton;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private EmprestimoController emprestimoController = new EmprestimoController(new EmprestimoDAO(), new ClienteDAO());

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    public void initialize() {
        SessionManager.getInstance().refreshClienteLogado();
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();

        if (clienteLogado == null) {
            System.err.println("Nenhum cliente logado encontrado no SessionManager!");
            onExitClick(); // Redireciona para o login se não houver cliente
            return;
        }
        atualizarMargens();
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void onConsignadoClick() {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (clienteLogado == null) {
            onExitClick();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);

            EmprestimoViewController emprestimoViewController = loader.getController();
            emprestimoViewController.setTipoEmprestimo(CONSIGNADO);

            Stage stage = (Stage) consignadoButton.getScene().getWindow();
            stage.setScene(consignadoScene);
            stage.setTitle("EmprestAI - Consignado");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar emprestimos.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onPessoalClick() {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (clienteLogado == null) {
            onExitClick();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);

            EmprestimoViewController emprestimoViewController = loader.getController();
            emprestimoViewController.setTipoEmprestimo(PESSOAL);

            Stage stage = (Stage) consignadoButton.getScene().getWindow();
            stage.setScene(consignadoScene);
            stage.setTitle("EmprestAI - Pessoal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar emprestimos.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onHomeClick() {
        // Já estamos no dashboard, então não faz nada ou recarrega a tela, se desejado
    }

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
    public void onProfileClick(MouseEvent mouseEvent) {
        // Implementar navegação para perfil, se necessário
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void atualizarMargens() {
        Cliente clienteLogado = SessionManager.getInstance().getClienteLogado();
        if (clienteLogado != null) {
            try {
                double margemConsig = clienteLogado.getMargemConsignavelDisponivel();
                double margemPessoal = clienteLogado.getMargemPessoalDisponivel();
                creditMarginConsig.setText(String.format("R$ %.2f", margemConsig));
                creditMarginPessoal.setText(String.format("R$ %.2f", margemPessoal));
            } catch (Exception e) {
                creditMarginConsig.setText("Indisponível");
                creditMarginPessoal.setText("Indisponível");
            }
        } else {
            creditMarginConsig.setText("R$ 0,00");
            creditMarginPessoal.setText("R$ 0,00");
        }
    }

    private String formatCpf(String cpf) {
        if (cpf != null && cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf != null ? cpf : "";
    }
}