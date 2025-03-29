package br.com.emprestai.view;

import br.com.emprestai.controller.ParcelaController;
import br.com.emprestai.dao.ParcelaDAO;
import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Parcela;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static br.com.emprestai.enums.StatusEmpParcela.*;

public class ParcelaViewController {

    @FXML
    private VBox parcelaList;

    @FXML
    private Label totalLabel;

    private ObservableList<ParcelaWrapper> parcelasList;
    private static final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ParcelaController parcelaController = new ParcelaController(new ParcelaDAO());

    public void setParcelas(List<Parcela> parcelas) {
        parcelasList = FXCollections.observableArrayList();
        int totalParcelas = parcelas.size();

        for (int i = 0; i < parcelas.size(); i++) {
            Parcela p = parcelas.get(i);
            String numeroParcela = p.getNumeroParcela() + " de " + totalParcelas;
            ParcelaWrapper wrapper = new ParcelaWrapper(p, parcelasList, numeroParcela);
            wrapper.selectedProperty().addListener((obs, oldValue, newValue) -> {
                updateTotal();
                updateCheckboxState(wrapper); // Sempre atualiza, independentemente do tipo
            });
            parcelasList.add(wrapper);
        }
        populateParcelaList();
        updateTotal();
    }

    @FXML
    private void initialize() {
        carregarDadosFicticios();
    }

    private void carregarDadosFicticios() {
        List<Parcela> parcelasFicticias = List.of();
        try {
            parcelasFicticias = parcelaController.get(37L, TipoEmpEnum.CONSIGNADO); // Tipo só no backend
            setParcelas(parcelasFicticias);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void populateParcelaList() {
        parcelaList.getChildren().clear();
        for (ParcelaWrapper wrapper : parcelasList) {
            HBox row = new HBox(10);
            row.getStyleClass().add("parcela-row");

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(wrapper.selectedProperty());
            checkBox.setDisable(!canEnableCheckbox(wrapper)); // Define o estado inicial

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
        updateCheckboxState(null); // Garante o estado inicial correto
    }

    private boolean canEnableCheckbox(ParcelaWrapper currentWrapper) {
        if (currentWrapper.getParcela().getStatus() != PENDENTE && currentWrapper.getParcela().getStatus() != ATRASADA) {
            return false; // Parcelas pagas não são editáveis
        }
        int currentIndex = parcelasList.indexOf(currentWrapper);
        if (currentIndex == 0) {
            return true; // Primeira parcela habilitada, se não paga
        }
        return parcelasList.get(currentIndex - 1).isSelected(); // Depende da anterior
    }

    private void updateCheckboxState(ParcelaWrapper changedWrapper) {
        for (int i = 0; i < parcelasList.size(); i++) {
            ParcelaWrapper wrapper = parcelasList.get(i);
            CheckBox checkBox = (CheckBox) ((HBox) parcelaList.getChildren().get(i)).getChildren().get(0);
            checkBox.setDisable(!canEnableCheckbox(wrapper));
        }
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        for (ParcelaWrapper wrapper : parcelasList) {
            if (wrapper.isSelected()) {
                Parcela parcela = wrapper.getParcela();
                System.out.println("Parcela selecionada: " + parcela);
            }
        }
    }

    private void updateTotal() {
        double total = 0.0;
        for (ParcelaWrapper wrapper : parcelasList) {
            if (wrapper.isSelected()) {
                total += wrapper.getValorAPagar();
            }
        }
        totalLabel.setText("Total a Pagar: " + df.format(total));
    }

    public void handleReturn() {
    }

    public static class ParcelaWrapper {
        private final Parcela parcela;
        private final SimpleBooleanProperty selected;
        private final String numeroParcela;
        private final ObservableList<ParcelaWrapper> parcelasList;

        public ParcelaWrapper(Parcela parcela, ObservableList<ParcelaWrapper> parcelasList, String numeroParcela) {
            this.parcela = parcela;
            this.selected = new SimpleBooleanProperty( parcela.getStatus() != ATRASADA && parcela.getStatus() != PENDENTE); // Verifica se é PAGA
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
            return parcela.getValorPresenteParcela();
        }

        public String getNumeroParcela() {
            return numeroParcela;
        }

        public LocalDate getVencimento() {
            return parcela.getDataVencimento();
        }
    }
}