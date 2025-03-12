package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.model.Cliente;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {


    public void conectar() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement()) {
         //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao conectar: " + e.getMessage(), 500);
        }
    }

    // Criar cliente
    public Cliente criar(Cliente cliente) {
        String sql = "INSERT INTO clientes (cpf_cliente, nome_cliente, renda_mensal_liquida, data_nascimento, " +
                "renda_familiar_liquida, qtd_pessoas_na_casa, id_tipo_cliente, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cliente.getCpf_cliente());
            statement.setString(2, cliente.getNome_cliente());
            statement.setBigDecimal(3, cliente.getRenda_mensal_liquida());
            statement.setDate(4, Date.valueOf(cliente.getData_nascimento()));
            statement.setBigDecimal(5, cliente.getRenda_familiar_liquida());
            statement.setInt(6, cliente.getQtd_pessoas_na_casa());
            statement.setString(7, cliente.getId_tipo_cliente().name());
            statement.setInt(8, cliente.getScore());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar cliente, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId_cliente(generatedKeys.getLong(1));
                } else {
                    throw new ApiException("Falha ao criar cliente, nenhum ID obtido.", 500);
                }
            }

            return cliente;
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf_cliente")) {
                throw new ApiException("CPF já cadastrado no sistema", 409);
            }
            throw new ApiException("Erro ao criar cliente: " + e.getMessage(), 500);
        }
    }

    // Buscar todos os clientes
    public List<Cliente> buscarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }

            return clientes;
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar clientes: " + e.getMessage(), 500);
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Cliente não encontrado com ID: " + id, 404);
                }
            }
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar cliente: " + e.getMessage(), 500);
        }
    }

    // Atualizar cliente
    public Cliente atualizar(Long id, Cliente cliente) {
        String sql = "UPDATE clientes SET cpf_cliente = ?, nome_cliente = ?, renda_mensal_liquida = ?, " +
                "data_nascimento = ?, renda_familiar_liquida = ?, qtd_pessoas_na_casa = ?, " +
                "id_tipo_cliente = ?, score = ? WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, cliente.getCpf_cliente());
            statement.setString(2, cliente.getNome_cliente());
            statement.setBigDecimal(3, cliente.getRenda_mensal_liquida());
            statement.setDate(4, Date.valueOf(cliente.getData_nascimento()));
            statement.setBigDecimal(5, cliente.getRenda_familiar_liquida());
            statement.setInt(6, cliente.getQtd_pessoas_na_casa());
            statement.setString(7, cliente.getId_tipo_cliente().name());
            statement.setInt(8, cliente.getScore());
            statement.setLong(9, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }

            cliente.setId_cliente(id);
            return cliente;
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Entada duplicada") && e.getMessage().contains("cpf_cliente")) {
                throw new ApiException("CPF já cadastrado para outro cliente", 409);
            }
            throw new ApiException("Erro ao atualizar cliente: " + e.getMessage(), 500);
        }
    }

    // Atualização parcial de cliente
    public Cliente atualizarParcial(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);

        if (clienteAtualizado.getCpf_cliente() != null) {
            clienteExistente.setCpf_cliente(clienteAtualizado.getCpf_cliente());
        }
        if (clienteAtualizado.getNome_cliente() != null) {
            clienteExistente.setNome_cliente(clienteAtualizado.getNome_cliente());
        }
        if (clienteAtualizado.getRenda_mensal_liquida() != null) {
            clienteExistente.setRenda_mensal_liquida(clienteAtualizado.getRenda_mensal_liquida());
        }
        if (clienteAtualizado.getData_nascimento() != null) {
            clienteExistente.setData_nascimento(clienteAtualizado.getData_nascimento());
        }
        if (clienteAtualizado.getRenda_familiar_liquida() != null) {
            clienteExistente.setRenda_familiar_liquida(clienteAtualizado.getRenda_familiar_liquida());
        }
        if (clienteAtualizado.getQtd_pessoas_na_casa() != 0) {
            clienteExistente.setQtd_pessoas_na_casa(clienteAtualizado.getQtd_pessoas_na_casa());
        }
        if (clienteAtualizado.getId_tipo_cliente() != null) {
            clienteExistente.setId_tipo_cliente(clienteAtualizado.getId_tipo_cliente());
        }
        if (clienteAtualizado.getScore() != 0) {
            clienteExistente.setScore(clienteAtualizado.getScore());
        }

        return atualizar(id, clienteExistente);
    }

    // Excluir cliente
    public void excluir(Long id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao excluir cliente: " + e.getMessage(), 500);
        }
    }

    // Método auxiliar para mapear ResultSet para objeto Cliente
    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId_cliente(rs.getLong("id_cliente"));
        cliente.setCpf_cliente(rs.getString("cpf_cliente"));
        cliente.setNome_cliente(rs.getString("nome_cliente"));
        cliente.setRenda_mensal_liquida(rs.getBigDecimal("renda_mensal_liquida"));
        cliente.setData_nascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setRenda_familiar_liquida(rs.getBigDecimal("renda_familiar_liquida"));
        cliente.setQtd_pessoas_na_casa(rs.getInt("qtd_pessoas_na_casa"));
        cliente.setId_tipo_cliente(Cliente.TipoVinculo.fromValor(rs.getInt("id_tipo_cliente")));
        cliente.setScore(rs.getInt("score"));
        return cliente;
    }
}
