package br.com.emprestai.repository;

import br.com.emprestai.model.Cliente;
import br.com.emprestai.server.DataBaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements Repository<Cliente, Integer> {
    @Override
    public Optional<Cliente> findById(Integer clienteId) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (Connection conn = DataBaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cliente cliente = mapCliente(rs);
                return Optional.of(cliente);
            }
            return Optional.empty();
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao buscar cliente", e);
        }
    }

    @Override
    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = DataBaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapCliente(rs));
            }
            return clientes;
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao listar clientes", e);
        }
    }

    @Override
    public Cliente save(Cliente cliente) {
        boolean isInsert = cliente.getClienteId() == 0;
        String sql = isInsert
                ? "INSERT INTO clientes (nome_cliente, renda_m_liq, dt_nasc, tp_vinculo, renda_familia_liq, qtde_pess_familia, score_credito) VALUES (?, ?, ?, ?, ?, ?, ?)"
                : "UPDATE clientes SET nome_cliente=?, renda_m_liq=?, dt_nasc=?, tp_vinculo=?, renda_familia_liq=?, qtde_pess_familia=?, score_credito=? WHERE id_cliente=?";

        try (Connection conn = DataBaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setDouble(2, cliente.getRemuneracaoLiqMes());
            stmt.setDate(3, Date.valueOf(cliente.getDtNasc()));
            stmt.setString(4, cliente.getTipoVinculo());
            stmt.setDouble(5, cliente.getRendaPessFamilia());
            stmt.setInt(6, cliente.getQtdePessFamilia());
            stmt.setString(7, cliente.getScoreCredito());

            if (!isInsert) {
                stmt.setInt(8, cliente.getClienteId());
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0 && isInsert) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    // Criamos um novo Cliente com o ID gerado, já que não podemos modificar o
                    // original
                    Cliente novoCliente = new Cliente(
                            cliente.getNome(),
                            cliente.getRemuneracaoLiqMes(),
                            cliente.getDtNasc());
                    // Precisamos setar os outros campos manualmente
                    novoCliente.setTipoVinculo(cliente.getTipoVinculo());
                    novoCliente.setRendaPessFamilia(cliente.getRendaPessFamilia());
                    novoCliente.setQtdePessFamilia(cliente.getQtdePessFamilia());
                    novoCliente.setScoreCredito(cliente.getScoreCredito());
                    // Note que clienteId é readonly, então ele será retornado pelo getClienteId()
                    return novoCliente; // O ID será recuperado do banco ao buscar
                }
            }
            return cliente; // Para updates, retornamos o mesmo objeto
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection conn = DataBaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao deletar cliente", e);
        }
    }

    public Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getString("nome_cliente"),
                rs.getDouble("renda_m_liq"),
                rs.getDate("dt_nasc").toLocalDate());
        // Como não podemos setar o clienteId diretamente, ele será preenchido pelo
        // banco
        cliente.setTipoVinculo(rs.getString("tp_vinculo"));
        cliente.setRendaPessFamilia(rs.getDouble("renda_familia_liq"));
        cliente.setQtdePessFamilia(rs.getInt("qtde_pess_familia"));
        cliente.setScoreCredito(rs.getString("score_credito"));
        return cliente;
    }
}