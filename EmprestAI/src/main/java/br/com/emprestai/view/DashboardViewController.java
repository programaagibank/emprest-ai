package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.dao.EmprestimoDAO;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import static br.com.emprestai.enums.TipoEmpEnum.CONSIGNADO;
import static br.com.emprestai.enums.TipoEmpEnum.PESSOAL;

public class DashboardViewController {

    @FXML
    private Label greetingLabel;

    @FXML
    private Label debtAmount;

    @FXML
    private Label userName;

    @FXML
    private Label userCpf;

    @FXML
    private Label creditMargin;

    @FXML
    private Button consignadoButton;

    @FXML
    private Button pessoalButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button profileButton;

    @FXML
    private Label creditMarginConsig;

    @FXML
    private Label creditMarginPessoal;

    private Cliente clienteLogado;

    private EmprestimoController emprestimoController = new EmprestimoController(new EmprestimoDAO(), new ClienteDAO());

    // Método para definir o cliente logado
    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
        atualizarMargens(); // Sempre atualiza as margens quando o cliente é setado
    }

    @FXML
    public void initialize() {
        // Atualiza as margens na inicialização, se o cliente já estiver definido
        atualizarMargens();
    }

    // Método auxiliar para atualizar as margens
    private void atualizarMargens() {
        if (clienteLogado != null) {
            try {
                double margemConsig = clienteLogado.getMargemConsignavel();
                double margemPessoal = clienteLogado.getMargemPessoal();
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

    @FXML
    private void onConsignadoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);

            Emprestimo emprestimo = null;
            try {
                emprestimo = emprestimoController.get(clienteLogado.getIdCliente(), CONSIGNADO);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            EmprestimoViewController emprestimoViewController = loader.getController();
            emprestimoViewController.setEmprestimo(emprestimo);
            emprestimoViewController.setClienteLogado(clienteLogado);

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);
            Emprestimo emprestimo = null;
            try {
                emprestimo = emprestimoController.get(clienteLogado.getIdCliente(), PESSOAL);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            EmprestimoViewController emprestimoViewController = loader.getController();
            emprestimoViewController.setEmprestimo(emprestimo);
            emprestimoViewController.setClienteLogado(clienteLogado);

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
    private void onExitClick() {
        try {
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

    public void onProfileClick(MouseEvent mouseEvent) {
    }
}