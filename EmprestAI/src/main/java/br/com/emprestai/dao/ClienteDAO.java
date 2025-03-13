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
    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (" +
                "id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "cpf_cliente VARCHAR(14) UNIQUE NOT NULL, " +
                "nome_cliente VARCHAR(100) NOT NULL, " +
                "renda_mensal_liquida DECIMAL(10,2) NOT NULL, " +
                "data_nascimento INT NOT NULL, " +
                "renda_familiar_liquida DECIMAL(10,2) NOT NULL, " +
                "qtd_pessoas_na_casa INT NOT NULL, " +
                "id_tipo_cliente ENUM('APOSENTADO', 'SERVIDOR', 'PENSIONISTA', 'EMPREGADO', 'NULO') NOT NULL, " +
                "score INT NOT NULL" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela clientes criada ou já existente!");
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao criar tabela: " + e.getMessage(), 500);
        }
    }

    // Criar cliente
    public Cliente criar(Cliente cliente) {
        String sql = "INSERT INTO clientes (cpf_cliente, nome_cliente, renda_mensal_liquida, data_nascimento, " +
                "renda_familiar_liquida, qtd_pessoas_na_casa, id_tipo_cliente, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getCpf_cliente());
            stmt.setString(2, cliente.getNome_cliente());
            stmt.setBigDecimal(3, cliente.getRenda_mensal_liquida());
            stmt.setDate(4, Date.valueOf(cliente.getData_nascimento()));
            stmt.setBigDecimal(5, cliente.getRenda_familiar_liquida());
            stmt.setInt(6, cliente.getQtd_pessoas_na_casa());
            stmt.setString(7, cliente.getId_tipo_cliente().name());
            stmt.setInt(8, cliente.getScore());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar cliente, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
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

            stmt.setString(1, cliente.getCpf_cliente());
            stmt.setString(2, cliente.getNome_cliente());
            stmt.setBigDecimal(3, cliente.getRenda_mensal_liquida());
            stmt.setDate(4, Date.valueOf(cliente.getData_nascimento()));
            stmt.setBigDecimal(5, cliente.getRenda_familiar_liquida());
            stmt.setInt(6, cliente.getQtd_pessoas_na_casa());
            stmt.setString(7, cliente.getId_tipo_cliente().name());
            stmt.setInt(8, cliente.getScore());
            stmt.setLong(9, id);

            int affectedRows = stmt.executeUpdate();

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
        cliente.setId_tipo_cliente(VinculoEnum.fromValor(rs.getInt("id_tipo_cliente")));
        cliente.setScore(rs.getInt("score"));
        return cliente;
    }
}
