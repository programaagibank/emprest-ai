package br.com.emprestai.repository;

import br.com.emprestai.model.Cliente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository{
    @Override
    public Optional<Cliente> findById(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (Connection conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cliente cliente = mapCliente(rs);
                System.out.println("Cliente encontrado: " + idCliente);
                return Optional.of(cliente);
            }
            System.out.println("Cliente não encontrado: " + idCliente);
            return Optional.empty();
        } catch (SQLException | IOException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar cliente", e);
        }
    }

    public Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nome_cliente"),
                rs.getDouble("renda_m_liq"),
                rs.getDate("dt_nasc").toLocalDate()
        );
        cliente.setTipoVinculo(rs.getString("tp_vinculo"));
        cliente.setRendaPessFamilia( rs.getDouble("renda_familia_liq"));
        cliente.setQtdePessFamilia(rs.getInt("qtde_pess_familia"));

        return cliente;
    }

}
