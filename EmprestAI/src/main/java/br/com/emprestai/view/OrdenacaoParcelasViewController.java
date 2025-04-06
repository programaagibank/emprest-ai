package br.com.emprestai.view;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
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
    @FXML private Label infoLabel;

    private Emprestimo emprestimo;
    private TipoEmprestimoEnum tipoEmprestimo;
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());

    @FXML
    private void initialize() {
        infoLabel.setText("Escolha como você pagar suas parcelas:");
        if (emprestimo != null) {
            verificarParcelasAtrasadas();
        }
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
        verificarParcelasAtrasadas(); // Verifica novamente ao setar o empréstimo
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
            Scene mainScene = new Scene(loader.load(), 360, 640);
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

    private void abrirParcelaView(boolean crescente) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("parcela.fxml"));
        Scene parcelaScene = new Scene(loader.load(), 360, 640);
        ParcelaViewController parcelaController = loader.getController();
        parcelaController.setEmprestimo(emprestimo);
        parcelaController.setTipoEmprestimo(tipoEmprestimo);
        parcelaController.setOrdenacaoCrescente(crescente);

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
                decrescenteButton.setManaged(false); // Remove o botão do layout
                infoLabel.setText("Você tem parcelas atrasadas. Necessário pagar elas primeiro para poder adiantar as ultimas.");
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