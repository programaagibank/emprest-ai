package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class CadastroClienteViewController {

    // --------------------------------------------------------------------------------
    // FXML Components
    // --------------------------------------------------------------------------------
    @FXML private TextField     cpfField;
    @FXML private TextField     nomeField;
    @FXML private DatePicker    dataNascimentoField;
    @FXML private ComboBox<String> tipoClienteComboBox;
    @FXML private PasswordField senhaField;

    // --------------------------------------------------------------------------------
    // Class Properties
    // --------------------------------------------------------------------------------
    private final ClienteController clienteController;

    // --------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------
    public CadastroClienteViewController() {
        this.clienteController = new ClienteController(new ClienteDAO());
    }

    // --------------------------------------------------------------------------------
    // Event Handlers
    // --------------------------------------------------------------------------------
    @FXML
    private void cadastrarCliente() {
        try {
            // Captura dos dados
            String cpf = cpfField.getText();
            String nome = nomeField.getText();

            // Validação de campos obrigatórios
            if (cpf.isEmpty() || nome.isEmpty() || senhaField.getText().isEmpty() ||
                    dataNascimentoField.getValue() == null) {
                showError("Todos os campos são obrigatórios.");
                return;
            }

            LocalDate dataNascimento = dataNascimentoField.getValue();
            String tipoClienteStr = tipoClienteComboBox.getValue();

            if (tipoClienteStr == null) {
                showError("Por favor, selecione um tipo de cliente.");
                return;
            }

            String senha = senhaField.getText();

            // Criação do objeto Cliente
            Cliente cliente = new Cliente();
            cliente.setCpfCliente(cpf);
            cliente.setNomeCliente(nome);
            cliente.setDataNascimento(dataNascimento);
            cliente.setSenha(senha);

            // Cadastro no banco
            clienteController.post(cliente);

            // Feedback e limpeza
            showInfo("Cliente cadastrado com sucesso!");
            limparCampos();

        } catch (ApiException e) {
            showError(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------
    private void limparCampos() {
        cpfField.clear();
        nomeField.clear();
        dataNascimentoField.setValue(null);
        tipoClienteComboBox.setValue(null);
        senhaField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}