package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.enums.VinculoEnum;
import br.com.emprestai.model.Cliente;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Método para criar a tabela no banco de dados (executar uma vez)
    public void conexao() {

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao conetar com o banco: " + e.getMessage(), 500);
        }
    }
    // Criar cliente
    public Cliente criar(Cliente cliente) {
        String sql = "INSERT INTO clientes (cpf_cliente, nome_cliente, renda_mensal_liquida, data_nascimento, " +
                "renda_familiar_liquida, qtd_pessoas_na_casa, id_tipo_cliente, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getCpfCliente());
            stmt.setString(2, cliente.getNomecliente());
            stmt.setBigDecimal(3, cliente.getRendaMensalLiquida());
            stmt.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            stmt.setBigDecimal(5, cliente.getRendaFamiliarLiquida());
            stmt.setInt(6, cliente.getQtdePessoasNaCasa());
            stmt.setString(7, cliente.getIdTipoCliente().name());
            stmt.setInt(8, cliente.getScore());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar cliente, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getLong(1));
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
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
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
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getCpfCliente());
            stmt.setString(2, cliente.getNomecliente());
            stmt.setBigDecimal(3, cliente.getRendaMensalLiquida());
            stmt.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            stmt.setBigDecimal(5, cliente.getRendaFamiliarLiquida());
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

        if (clienteAtualizado.getCpfCliente() != null) {
            clienteExistente.setCpfCliente(clienteAtualizado.getCpfCliente());
        }
        if (clienteAtualizado.getNomecliente() != null) {
            clienteExistente.setNomecliente(clienteAtualizado.getNomecliente());
        }
        if (clienteAtualizado.getRendaMensalLiquida() != null) {
            clienteExistente.setRendaMensalLiquida(clienteAtualizado.getRendaMensalLiquida());
        }
        if (clienteAtualizado.getDataNascimento() != null) {
            clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        }
        if (clienteAtualizado.getRendaFamiliarLiquida() != null) {
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
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao excluir cliente: " + e.getMessage(), 500);
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
            //Adicionei o IOException para parar de reclamar erro
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
        cliente.setRendaMensalLiquida(rs.getBigDecimal("renda_mensal_liquida"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setRendaFamiliarLiquida(rs.getBigDecimal("renda_familiar_liquida"));
        cliente.setQtdePessoasNaCasa(rs.getInt("qtd_pessoas_na_casa"));
        cliente.setIdTipoCliente(VinculoEnum.fromValor(rs.getInt("id_tipo_cliente")));
        cliente.setScore(rs.getInt("score"));
        return cliente;
    }
}
