package br.com.emprestai.view;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class PerfilClienteViewController implements Initializable {

    @FXML
    private Label greetingLabel;

    @FXML
    private Label nomeClienteLabel;

    @FXML
    private Label nomeCompletoLabel;

    @FXML
    private Label dataNascimentoLabel;

    @FXML
    private Label idadeLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label cpfLabel;

    @FXML
    private Label vencimentoLiquidoLabel;

    @FXML
    private Label margemConsignavelLabel;

    @FXML
    private Label limiteConsignadoLabel;

    @FXML
    private Label limitePessoalLabel;

    @FXML
    private Button editarPerfilButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button exitButton;


    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Inicializar o DAO
            clienteDAO = new ClienteDAO();

            // Obter o cliente da sessão ou pelo ID armazenado na sessão
            SessionManager.getInstance().refreshClienteLogado();
            cliente = SessionManager.getInstance().getClienteLogado();

            if (cliente == null) {
                System.err.println("Nenhum cliente logado encontrado!");
                onExitClick(new ActionEvent()); // Voltar para tela de login
                return;
            }

            // Preencher os dados do cliente na interface
            atualizarInformacoesPerfil();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar o perfil do cliente: " + e.getMessage());
        }
    }

    private void atualizarInformacoesPerfil() {
        // Dados pessoais
        greetingLabel.setText("Olá, " + cliente.getNomeCliente().split(" ")[0]);
        nomeClienteLabel.setText(cliente.getNomeCliente());
        nomeCompletoLabel.setText(cliente.getNomeCliente());
        dataNascimentoLabel.setText(cliente.getDataNascimento().format(dateFormatter));
        idadeLabel.setText(cliente.getIdade() + " anos");
        scoreLabel.setText(String.valueOf(cliente.getScore()));
        cpfLabel.setText(formatarCPF(cliente.getCpfCliente()));

        // Dados financeiros
        vencimentoLiquidoLabel.setText(currencyFormat.format(cliente.getVencimentoLiquidoTotal()));
        margemConsignavelLabel.setText(currencyFormat.format(cliente.getMargemConsignavelDisponivel()));
        limiteConsignadoLabel.setText(currencyFormat.format(cliente.getLimiteCreditoConsignado()));
        limitePessoalLabel.setText(currencyFormat.format(cliente.getLimiteCreditoPessoal()));
    }

    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

//    @FXML
//    private void onHomeClick(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
//            Parent root = loader.load();
//
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) homeButton.getScene().getWindow();
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
@FXML
private void onHomeClick(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = (Stage) homeButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    @FXML
    private void onProfileClick(ActionEvent event) {
        // Já estamos na tela de perfil, não é necessário fazer nada
    }

    @FXML
    private void onExitClick(ActionEvent event) {
        try {
            // Limpar a sessão do usuário usando SessionManager
            SessionManager.getInstance().clearSession();

            // Voltar para tela de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditarPerfilClick(ActionEvent event) {
        // Implementar a lógica para editar o perfil do cliente
        // Por exemplo, abrir uma nova janela ou diálogo para edição
        System.out.println("Funcionalidade de edição do perfil ainda não implementada");
    }
}