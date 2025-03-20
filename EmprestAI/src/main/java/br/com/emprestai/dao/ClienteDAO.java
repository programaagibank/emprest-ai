package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.model.Cliente;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente criar(Cliente cliente, Login login) {
        if (cliente == null || login == null) {
            throw new IllegalArgumentException("Cliente e Login não podem ser nulos.");
        }

        String sqlCliente = "INSERT INTO clientes (cpf_cliente, nome_cliente, renda_mensal_liquida, data_nascimento, " +
                "renda_familiar_liquida, qtd_pessoas_na_casa, id_tipo_cliente, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlLogin = "INSERT INTO login (id_cliente, username, senha) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                stmtCliente.setString(1, cliente.getCpfCliente());
                stmtCliente.setString(2, cliente.getNomecliente());
                stmtCliente.setDouble(3, cliente.getRendaMensalLiquida());
                stmtCliente.setDate(4, Date.valueOf(cliente.getDataNascimento()));
                stmtCliente.setDouble(5, cliente.getRendaFamiliarLiquida());
                stmtCliente.setInt(6, cliente.getQtdePessoasNaCasa());
                stmtCliente.setString(7, cliente.getIdTipoCliente().name());
                stmtCliente.setInt(8, cliente.getScore());

                int affectedRows = stmtCliente.executeUpdate();
                if (affectedRows == 0) {
                    throw new ApiException("Falha ao criar cliente, nenhuma linha afetada.", 500);
                }

                try (ResultSet generatedKeys = stmtCliente.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cliente.setIdCliente(generatedKeys.getLong(1));
                    } else {
                        throw new ApiException("Falha ao criar cliente, nenhum ID obtido.", 500);
                    }
                }

                String senhaCriptografada = BCrypt.hashpw(login.getSenha(), BCrypt.gensalt());
                try (PreparedStatement stmtLogin = conn.prepareStatement(sqlLogin, Statement.RETURN_GENERATED_KEYS)) {
                    stmtLogin.setLong(1, cliente.getIdCliente());
                    stmtLogin.setString(2, login.getUsername());
                    stmtLogin.setString(3, senhaCriptografada);

                    int affectedRowsLogin = stmtLogin.executeUpdate();
                    if (affectedRowsLogin == 0) {
                        throw new ApiException("Falha ao criar login, nenhuma linha afetada.", 500);
                    }

                    try (ResultSet generatedKeysLogin = stmtLogin.getGeneratedKeys()) {
                        if (generatedKeysLogin.next()) {
                            login.setIdLogin(generatedKeysLogin.getLong(1)); // Mantém o ID gerado para login
                        } else {
                            throw new ApiException("Falha ao criar login, nenhum ID obtido.", 500);
                        }
                    }
                }

                conn.commit();
                return cliente;
            } catch (SQLException e) {
                conn.rollback();
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf_cliente")) {
                    throw new ApiException("CPF já cadastrado no sistema", 409);
                }
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("username")) {
                    throw new ApiException("Username já cadastrado no sistema", 409);
                }
                throw new ApiException("Erro ao criar cliente e login: " + e.getMessage(), 500);
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao conectar ou executar transação: " + e.getMessage(), 500);
        }
    }

    // Buscar todos os clientes
    public List<Cliente> buscarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }

            return clientes;
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar clientes: " + e.getMessage(), 500);
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Cliente não encontrado com ID: " + id, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar cliente: " + e.getMessage(), 500);
        }
    }

    // Atualizar cliente
    public Cliente atualizar(Long id, Cliente cliente) {
        if (id == null || cliente == null) {
            throw new IllegalArgumentException("ID e cliente não podem ser nulos.");
        }

        String sql = "UPDATE clientes SET cpf_cliente = ?, nome_cliente = ?, renda_mensal_liquida = ?, " +
                "data_nascimento = ?, renda_familiar_liquida = ?, qtd_pessoas_na_casa = ?, " +
                "id_tipo_cliente = ?, score = ? WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getCpfCliente());
            stmt.setString(2, cliente.getNomecliente());
            stmt.setDouble(3, cliente.getRendaMensalLiquida());
            stmt.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            stmt.setDouble(5, cliente.getRendaFamiliarLiquida());
            stmt.setInt(6, cliente.getQtdePessoasNaCasa());
            stmt.setString(7, cliente.getIdTipoCliente().name());
            stmt.setInt(8, cliente.getScore());
            stmt.setLong(9, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }

            cliente.setIdCliente(id);
            return cliente;
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf_cliente")) {
                throw new ApiException("CPF já cadastrado para outro cliente", 409);
            }
            throw new ApiException("Erro ao atualizar cliente: " + e.getMessage(), 500);
        }
    }

    // Atualização parcial de cliente
    public Cliente atualizarParcial(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);

        if (clienteAtualizado.getCpfCliente() != null) {
            clienteExistente.setCpfCliente(clienteAtualizado.getCpfCliente());
        }
        if (clienteAtualizado.getNomecliente() != null) {
            clienteExistente.setNomecliente(clienteAtualizado.getNomecliente());
        }
        if (clienteAtualizado.getRendaMensalLiquida() != 0) {
            clienteExistente.setRendaMensalLiquida(clienteAtualizado.getRendaMensalLiquida());
        }
        if (clienteAtualizado.getDataNascimento() != null) {
            clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        }
        if (clienteAtualizado.getRendaFamiliarLiquida() != 0) {
            clienteExistente.setRendaFamiliarLiquida(clienteAtualizado.getRendaFamiliarLiquida());
        }
        if (clienteAtualizado.getQtdePessoasNaCasa() != 0) {
            clienteExistente.setQtdePessoasNaCasa(clienteAtualizado.getQtdePessoasNaCasa());
        }
        if (clienteAtualizado.getIdTipoCliente() != null) {
            clienteExistente.setIdTipoCliente(clienteAtualizado.getIdTipoCliente());
        }
        if (clienteAtualizado.getScore() != 0) {
            clienteExistente.setScore(clienteAtualizado.getScore());
        }

        return atualizar(id, clienteExistente);
    }

    // Excluir cliente
    public void excluir(Long id) {
        String sqlLogin = "DELETE FROM login WHERE id_cliente = ?";
        String sqlCliente = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transação

            try {
                // 1. Deletar o login associado
                try (PreparedStatement stmtLogin = conn.prepareStatement(sqlLogin)) {
                    stmtLogin.setLong(1, id);
                    stmtLogin.executeUpdate();
                }

                // 2. Deletar o cliente
                try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                    stmtCliente.setLong(1, id);
                    int affectedRows = stmtCliente.executeUpdate();

                    if (affectedRows == 0) {
                        throw new ApiException("Cliente não encontrado com ID: " + id, 404);
                    }
                }

                conn.commit(); // Confirmar transação
            } catch (SQLException e) {
                conn.rollback(); // Reverter em caso de erro
                throw new ApiException("Erro ao excluir cliente: " + e.getMessage(), 500);
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao conectar ou executar transação: " + e.getMessage(), 500);
        }
    }

    // Buscar cliente por CPF
    public Cliente buscarPorCPF(String cpf_cliente) {
        String sql = "SELECT * FROM clientes WHERE cpf_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf_cliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Cliente não encontrado com CPF: " + cpf_cliente, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar cliente: " + e.getMessage(), 500);
        }
    }

    // Método auxiliar para mapear ResultSet para objeto Cliente
    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getLong("id_cliente"));
        cliente.setCpfCliente(rs.getString("cpf_cliente"));
        cliente.setNomecliente(rs.getString("nome_cliente"));
        cliente.setRendaMensalLiquida(rs.getDouble("renda_mensal_liquida"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setRendaFamiliarLiquida(rs.getDouble("renda_familiar_liquida"));
        cliente.setQtdePessoasNaCasa(rs.getInt("qtd_pessoas_na_casa"));
        cliente.setIdTipoCliente(VinculoEnum.fromValor(rs.getInt("id_tipo_cliente")));
        cliente.setScore(rs.getInt("score"));
        return cliente;
    }
}