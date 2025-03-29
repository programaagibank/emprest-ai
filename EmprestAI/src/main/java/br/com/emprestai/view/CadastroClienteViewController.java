package br.com.emprestai.view;

import br.com.emprestai.controller.ClienteController;
import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.enums.VinculoEnum;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class CadastroClienteViewController {

    @FXML
    private TextField cpfField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField rendaField;
    @FXML
    private DatePicker dataNascimentoField;
    @FXML
    private TextField rendaFamiliarField;
    @FXML
    private TextField qtdPessoasField;
    @FXML
    private ComboBox<String> tipoClienteComboBox;
    @FXML
    private TextField scoreField;
    @FXML
    private PasswordField senhaField;

    private final ClienteController clienteController;

    public CadastroClienteViewController() {
        // Criando instância do ClienteController com o DAO
        this.clienteController = new ClienteController(new ClienteDAO());
    }

    @FXML
    private void cadastrarCliente() {
        try {
            // Captura dos dados dos campos
            String cpf = cpfField.getText();
            String nome = nomeField.getText();

            // Validação de campos obrigatórios
            if (cpf.isEmpty() || nome.isEmpty() || senhaField.getText().isEmpty() ||
                    dataNascimentoField.getValue() == null) {
                showError("Todos os campos são obrigatórios.");
                return;
            }

            double rendaMensal = 0;
            double rendaFamiliar = 0;
            int qtdPessoas = 0;
            int score = 0;

            try {
                rendaMensal = Double.parseDouble(rendaField.getText());
                rendaFamiliar = Double.parseDouble(rendaFamiliarField.getText());
                qtdPessoas = Integer.parseInt(qtdPessoasField.getText());
                score = Integer.parseInt(scoreField.getText());
            } catch (NumberFormatException e) {
                showError("Os campos numéricos devem conter apenas números válidos.");
                return;
            }

            LocalDate dataNascimento = dataNascimentoField.getValue();
            String tipoClienteStr = tipoClienteComboBox.getValue();

            if (tipoClienteStr == null) {
                showError("Por favor, selecione um tipo de cliente.");
                return;
            }

            // Converter a string do ComboBox para o enum VinculoEnum
            VinculoEnum tipoCliente;
            switch (tipoClienteStr) {
                case "APOSENTADO":
                    tipoCliente = VinculoEnum.APOSENTADO;
                    break;
                case "SERVIDOR":
                    tipoCliente = VinculoEnum.SERVIDOR;
                    break;
                case "PENSIONISTA":
                    tipoCliente = VinculoEnum.PENSIONISTA;
                    break;
                case "EMPREGADO":
                    tipoCliente = VinculoEnum.EMPREGADO;
                    break;
                default:
                    showError("Tipo de cliente inválido.");
                    return;
            }

            String senha = senhaField.getText();

            // Criação do objeto Cliente
            Cliente cliente = new Cliente();
            cliente.setCpfCliente(cpf);
            cliente.setNomecliente(nome);
            cliente.setRendaMensalLiquida(rendaMensal);
            cliente.setDataNascimento(dataNascimento);
            cliente.setRendaFamiliarLiquida(rendaFamiliar);
            cliente.setQtdePessoasNaCasa(qtdPessoas);
            cliente.setTipoCliente(tipoCliente);
            cliente.setScore(score);
            cliente.setSenha(senha);

            // Chamando o método para cadastrar no banco através do controller
            clienteController.post(cliente);

            // Exibindo mensagem de sucesso
            showInfo("Cliente cadastrado com sucesso!");

            // Limpar os campos após cadastro bem-sucedido
            limparCampos();

        } catch (ApiException | NumberFormatException e) {
            // Tratamento de erro
            showError(e.getMessage());
        }
    }

    private void limparCampos() {
        cpfField.clear();
        nomeField.clear();
        rendaField.clear();
        dataNascimentoField.setValue(null);
        rendaFamiliarField.clear();
        qtdPessoasField.clear();
        tipoClienteComboBox.setValue(null);
        scoreField.clear();
        senhaField.clear();
    }

    // Método para exibir alertas de erro
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para exibir mensagens de sucesso
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}