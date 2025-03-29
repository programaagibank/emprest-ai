package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.controller.EmprestimoController;
import br.com.emprestai.controller.LoginController;
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

    private Cliente clienteLogado;

    private EmprestimoController emprestimoController = new EmprestimoController(new EmprestimoDAO(), new ClienteDAO());

    // Metodo para definir o cliente logado
    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
    }

    // Set user data
    public void setUserData(String userName, String cpf, String debt, String creditMargin) {
        greetingLabel.setText("Olá, " + clienteLogado.getNomecliente() + "!");
        this.userCpf.setText("CPF: " + clienteLogado.getCpfCliente());
        this.debtAmount.setText("R$ " + debt);
        this.creditMargin.setText("R$ " + creditMargin);
    }

    @FXML
    private void onConsignadoClick() {
        try {
            // Carrega o arquivo FXML da tela Consignado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);

            Emprestimo emprestimo = null;
            try {
                emprestimo = emprestimoController.get(clienteLogado.getIdCliente(), CONSIGNADO);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            // Obtém o controlador da emprestimo
            EmprestimoViewController emprestimoViewController = loader.getController();
            // Passa o emprestimo para o controlador
            emprestimoViewController.setEmprestimo(emprestimo);

            emprestimoViewController.setClienteLogado(clienteLogado);

            // Obtém o Stage atual a partir do botão consignadoButton
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
            // Carrega o arquivo FXML da tela Consignado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene consignadoScene = new Scene(loader.load(), 360, 640);
            Emprestimo emprestimo = null;
            try {
                emprestimo = emprestimoController.get(clienteLogado.getIdCliente(), PESSOAL);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }

            // Obtém o controlador da emprestimo
            EmprestimoViewController emprestimoViewController = loader.getController();
            // Passa o emprestimo para o controlador
            emprestimoViewController.setEmprestimo(emprestimo);

            emprestimoViewController.setClienteLogado(clienteLogado);

            // Obtém o Stage atual a partir do botão consignadoButton
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
    private void onHomeClick() {
        try {
            // Corrigido o caminho do FXML
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
            // Corrigido o caminho do FXML
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

    public void onProfileClick(MouseEvent mouseEvent) {
    }
}