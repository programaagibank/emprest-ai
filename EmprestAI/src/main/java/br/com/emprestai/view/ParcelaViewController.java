package br.com.emprestai.view;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.model.Emprestimo;
import br.com.emprestai.model.Parcela;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static br.com.emprestai.enums.StatusParcelaEnum.ATRASADA;
import static br.com.emprestai.enums.StatusParcelaEnum.PENDENTE;

public class ParcelaViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private VBox  parcelaList;
    @FXML private Label totalLabel;
    @FXML private Button returnButton;
    @FXML private Button pagarButton; // Novo botão

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private Emprestimo          emprestimo;
    private TipoEmprestimoEnum  tipoEmprestimo;
    private Cliente             clienteLogado;
    private ObservableList<ParcelaWrapper> parcelasList;
    private ParcelaController   parcelaController = new ParcelaController(new ParcelaDAO());

    // Formatters
    private static final DecimalFormat    df         = new DecimalFormat("R$ #,##0.00");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --------------------------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------------------------
    @FXML
    private void initialize() {
    }

    // --------------------------------------------------------------------------------
    // Setters
    // --------------------------------------------------------------------------------
    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public void setTipoEmprestimo(TipoEmprestimoEnum tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
    }

    public void setClienteLogado(Cliente cliente) {
        this.clienteLogado = cliente;
    }

    public void setParcelas(List<Parcela> parcelas) {
        parcelasList = FXCollections.observableArrayList();
        int totalParcelas = parcelas.size();

        for (int i = 0; i < parcelas.size(); i++) {
            Parcela p = parcelas.get(i);
            String numeroParcela = p.getNumeroParcela() + " de " + totalParcelas;
            ParcelaWrapper wrapper = new ParcelaWrapper(p, parcelasList, numeroParcela);
            wrapper.selectedProperty().addListener((obs, oldValue, newValue) -> {
                updateTotal();
                updateCheckboxState(wrapper);
            });
            parcelasList.add(wrapper);
        }

        populateParcelaList();
        updateTotal();
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------

    @FXML
    private void onPagarClick() {
        try {
            // Coletar apenas as parcelas selecionadas que estão PENDENTE ou ATRASADA
            List<Parcela> parcelasSelecionadas = new ArrayList<>();
            for (ParcelaWrapper wrapper : parcelasList) {
                Parcela parcela = wrapper.getParcela();
                if (wrapper.isSelected() &&
                        (parcela.getStatus() == PENDENTE || parcela.getStatus() == ATRASADA)) {
                    parcelasSelecionadas.add(parcela);
                }
            }

            if (parcelasSelecionadas.isEmpty()) {
                totalLabel.setText("Nenhuma parcela válida selecionada para pagamento.");
                return;
            }

            // Enviar para o controller para salvar
            parcelaController.putListParcelas(parcelasSelecionadas);

            // Atualizar a interface ou redirecionar após o pagamento
            totalLabel.setText("Pagamento realizado com sucesso!");
            populateParcelaList(); // Atualiza a lista para refletir o novo status
            updateTotal();
            onClickReturn();
            // Redirecionar de volta à tela de empréstimos
        } catch (Exception e) {
            e.printStackTrace();
            totalLabel.setText("Erro ao realizar pagamento: " + e.getMessage());
        }
    }

    public void onClickReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("emprestimos.fxml"));
            Scene mainScene = new Scene(loader.load(), 360, 640);
            EmprestimoViewController emprestimoViewController = loader.getController();
            emprestimoViewController.setClienteLogado(clienteLogado);
            emprestimoViewController.setTipoEmprestimo(tipoEmprestimo);
            emprestimoViewController.setEmprestimo(emprestimo);

            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("EmprestAI - Emprestimos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar emprestimos.fxml: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void populateParcelaList() {
        parcelaList.getChildren().clear();

        for (ParcelaWrapper wrapper : parcelasList) {
            HBox row = new HBox(10);
            row.getStyleClass().add("parcela-row");

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(wrapper.selectedProperty());
            checkBox.setDisable(!canEnableCheckbox(wrapper));

            Label numeroLabel = new Label(wrapper.getNumeroParcela());
            numeroLabel.getStyleClass().add("parcela-numero");

            VBox valorDataBox = new VBox(2);
            valorDataBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            valorDataBox.getStyleClass().add("valor-data-box");
            valorDataBox.setTranslateX(30);

            Label valorLabel = new Label(df.format(wrapper.getValorAPagar()));
            valorLabel.getStyleClass().add("parcela-valor");

            Label vencimentoLabel = new Label(wrapper.getVencimento().format(dateFormat));
            vencimentoLabel.getStyleClass().add("parcela-vencimento");

            valorDataBox.getChildren().addAll(valorLabel, vencimentoLabel);
            row.getChildren().addAll(checkBox, numeroLabel, valorDataBox);
            parcelaList.getChildren().add(row);
        }

        updateCheckboxState(null);
    }

    private boolean canEnableCheckbox(ParcelaWrapper currentWrapper) {
        if (currentWrapper.getParcela().getStatus() != PENDENTE && currentWrapper.getParcela().getStatus() != ATRASADA) {
            return false;
        }

        int currentIndex = parcelasList.indexOf(currentWrapper);
        if (currentIndex == 0) {
            return true;
        }

        return parcelasList.get(currentIndex - 1).isSelected();
    }

    private void updateCheckboxState(ParcelaWrapper changedWrapper) {
        if (changedWrapper != null && !changedWrapper.isSelected()) {
            int changedIndex = parcelasList.indexOf(changedWrapper);
            for (int i = changedIndex + 1; i < parcelasList.size(); i++) {
                ParcelaWrapper subsequentWrapper = parcelasList.get(i);
                if (subsequentWrapper.isSelected()) {
                    subsequentWrapper.setSelected(false);
                }
            }
        }

        for (int i = 0; i < parcelasList.size(); i++) {
            ParcelaWrapper wrapper = parcelasList.get(i);
            CheckBox checkBox = (CheckBox) ((HBox) parcelaList.getChildren().get(i)).getChildren().get(0);
            checkBox.setDisable(!canEnableCheckbox(wrapper));
        }
    }

    private void updateTotal() {
        double total = 0.0;
        for (ParcelaWrapper wrapper : parcelasList) {
            if (wrapper.isSelected() &&
                    (wrapper.getParcela().getStatus() == PENDENTE || wrapper.getParcela().getStatus() == ATRASADA)) {
                total += wrapper.getValorAPagar();
            }
        }
        totalLabel.setText("Total a Pagar: " + df.format(total));
    }

    // --------------------------------------------------------------------------------
    // Inner Class
    // --------------------------------------------------------------------------------
    public static class ParcelaWrapper {
        private final Parcela                parcela;
        private final SimpleBooleanProperty  selected;
        private final String                 numeroParcela;
        private final ObservableList<ParcelaWrapper> parcelasList;

        public ParcelaWrapper(Parcela parcela, ObservableList<ParcelaWrapper> parcelasList, String numeroParcela) {
            this.parcela = parcela;
            this.selected = new SimpleBooleanProperty(parcela.getStatus() != ATRASADA && parcela.getStatus() != PENDENTE);
            this.numeroParcela = numeroParcela;
            this.parcelasList = parcelasList;
        }

        public Parcela getParcela() {
            return parcela;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean value) {
            selected.set(value);
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }

        public Double getValorAPagar() {
            return parcela.getValorPresenteParcela() + parcela.getMulta() + parcela.getJurosMora();
        }

        public String getNumeroParcela() {
            return numeroParcela;
        }

        public LocalDate getVencimento() {
            return parcela.getDataVencimento();
        }
    }
}