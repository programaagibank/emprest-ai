package br.com.emprestai.view;

import br.com.emprestai.model.Parcela;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ParcelaViewController {

    @FXML
    private VBox parcelaList;

    @FXML
    private Label totalLabel;

    private ObservableList<ParcelaWrapper> parcelasList;

    private static final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setParcelas(List<Parcela> parcelas) {
        parcelasList = FXCollections.observableArrayList();
        for (int i = 0; i < parcelas.size(); i++) {
            Parcela p = parcelas.get(i);
            ParcelaWrapper wrapper = new ParcelaWrapper(p, i + 1, parcelas.size());
            wrapper.selectedProperty().addListener((obs, oldValue, newValue) -> updateTotal());
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
        List<Parcela> parcelasFicticias = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            double valor = 100.0 + (i * 10);
            LocalDate vencimento = LocalDate.now().plusMonths(i);
            parcelasFicticias.add(new Parcela(valor, 0.0, 0.0, vencimento));
        }
        setParcelas(parcelasFicticias);
    }

    private void populateParcelaList() {
        parcelaList.getChildren().clear();
        for (ParcelaWrapper wrapper : parcelasList) {
            HBox row = new HBox(10);
            row.getStyleClass().add("parcela-row");

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(wrapper.selectedProperty());

            Label numeroLabel = new Label(wrapper.getNumeroParcela());
            numeroLabel.getStyleClass().add("parcela-numero");

            // Criar um VBox para empilhar o valor e a data de vencimento
            VBox valorDataBox = new VBox(2);
            valorDataBox.setPrefWidth(Region.USE_COMPUTED_SIZE); // Permite que o VBox ocupe o espaço disponível
            valorDataBox.getStyleClass().add("valor-data-box");
            valorDataBox.setTranslateX(30); // Move o VBox 50 pixels para a direita

            Label valorLabel = new Label(df.format(wrapper.getValorAPagar()));
            valorLabel.getStyleClass().add("parcela-valor");

            Label vencimentoLabel = new Label(wrapper.getVencimento().format(dateFormat));
            vencimentoLabel.getStyleClass().add("parcela-vencimento");

            valorDataBox.getChildren().addAll(valorLabel, vencimentoLabel);

            row.getChildren().addAll(checkBox, numeroLabel, valorDataBox);
            parcelaList.getChildren().add(row);
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

    public static class ParcelaWrapper {
        private final Parcela parcela;
        private final SimpleBooleanProperty selected;
        private final String numeroParcela;

        public ParcelaWrapper(Parcela parcela, int numero, int total) {
            this.parcela = parcela;
            this.selected = new SimpleBooleanProperty(false);
            this.numeroParcela = numero + " de " + total;
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