package br.com.emprestai.view;

import br.com.emprestai.dao.ClienteDAO;
import br.com.emprestai.model.Cliente;
import br.com.emprestai.exception.ApiException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class CadastroClienteViewController {

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtCpf;
    @FXML
    private TextField txtRendaMensal;
    @FXML
    private TextField txtRendaFamiliar;
    @FXML
    private TextField txtQtdPessoas;
    @FXML
    private TextField txtDataNascimento;
    @FXML
    private PasswordField txtSenha;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML
    public void cadastrarCliente() {
        try {
            Cliente cliente = new Cliente();
            cliente.setNomecliente(txtNome.getText());
            cliente.setCpfCliente(txtCpf.getText());
            cliente.setRendaMensalLiquida(Double.parseDouble(txtRendaMensal.getText()));
            cliente.setRendaFamiliarLiquida(Double.parseDouble(txtRendaFamiliar.getText()));
            cliente.setQtdePessoasNaCasa(Integer.parseInt(txtQtdPessoas.getText()));
            cliente.setDataNascimento(LocalDate.parse(txtDataNascimento.getText()));
            cliente.setSenha(txtSenha.getText());

            clienteDAO.criar(cliente);

            mostrarAlerta("Cadastro realizado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();
        } catch (ApiException e) {
            mostrarAlerta("Erro: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Erro ao cadastrar cliente. Verifique os campos.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Cadastro de Cliente");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        txtRendaMensal.clear();
        txtRendaFamiliar.clear();
        txtQtdPessoas.clear();
        txtDataNascimento.clear();
        txtSenha.clear();
    }
}

