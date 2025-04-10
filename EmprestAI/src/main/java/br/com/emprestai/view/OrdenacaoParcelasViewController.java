package br.com.emprestai.view;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import br.com.emprestai.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static br.com.emprestai.enums.StatusParcelaEnum.ATRASADA;

public class OrdenacaoParcelasViewController {

    @FXML private Button crescenteButton;
    @FXML private Button decrescenteButton;
    @FXML private Button returnButton;
    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private Button exitButton;
    @FXML private Label infoLabel;

    private Emprestimo emprestimo;
    private TipoEmprestimoEnum tipoEmprestimo;
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());

    @FXML
    private void initialize() {
        infoLabel.setText("Escolha como você quer pagar suas parcelas:");
        if (emprestimo != null) {
            verificarParcelasAtrasadas();
        }
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
        verificarParcelasAtrasadas();
    }

    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
    }

    @FXML
    private void onCrescenteClick() {
        try {
            abrirParcelaView(true); // True para ordem crescente
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao abrir as parcelas: " + e.getMessage());
        }
    }

    @FXML
    private void onDecrescenteClick() {
        try {
            abrirParcelaView(false); // False para ordem decrescente
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao abrir as parcelas: " + e.getMessage());
        }
    }

    @FXML
    private void onReturnClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
            EmprestimoViewController emprestimoViewController = loader.getController();
            emprestimoViewController.setTipoEmprestimo(tipoEmprestimo);

            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Empréstimos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao voltar: " + e.getMessage());
        }
    }

    @FXML
    private void onHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao carregar dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void onProfileClick() {
        // Implemente a lógica de navegação para o perfil aqui, se necessário
    }

    @FXML
    private void onExitClick() {
        try {
            SessionManager.getInstance().clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene mainScene = new Scene(loader.load(), 400, 700);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao sair: " + e.getMessage());
        }
    }

    private void abrirParcelaView(boolean crescente) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
        Scene parcelaScene = new Scene(loader.load(), 400, 700);
        ParcelaViewController parcelaController = loader.getController();
        parcelaController.setOrdenacaoCrescente(crescente);
        parcelaController.setEmprestimo(emprestimo);
        parcelaController.setTipoEmprestimo(tipoEmprestimo);

        Stage stage = (Stage) crescenteButton.getScene().getWindow();
        stage.setScene(parcelaScene);
        stage.setTitle("EmprestAI - Parcelas");
        stage.show();
    }

    private void verificarParcelasAtrasadas() {
        try {
            List<Parcela> parcelas = parcelaController.getParcelasByEmprestimo(emprestimo);
            boolean temParcelaAtrasada = parcelas.stream().anyMatch(p -> p.getStatus() == ATRASADA);
            if (temParcelaAtrasada) {
                decrescenteButton.setVisible(false);
                decrescenteButton.setManaged(false);
                infoLabel.setText("Você tem parcelas atrasadas. Necessário pagá-las primeiro para poder adiantar as últimas.");
            } else {
                decrescenteButton.setVisible(true);
                decrescenteButton.setManaged(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao verificar parcelas: " + e.getMessage());
        }
    }
}