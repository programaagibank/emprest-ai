package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Cliente;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements GenericDAO<Cliente> {

    // --------------------------------------------------------------------------------
    // CRUD Methods
    // --------------------------------------------------------------------------------

    // POST - Criar um novo cliente no banco de dados
    public Cliente criar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        if (cliente.getSenha() == null || cliente.getSenha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia.");
        }

        String sqlCliente = "INSERT INTO clientes (cpf_cliente, nome_cliente, data_nascimento, senha_acesso) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                stmtCliente.setString(1, cliente.getCpfCliente());
                stmtCliente.setString(2, cliente.getNomeCliente());
                stmtCliente.setDate(4, Date.valueOf(cliente.getDataNascimento()));
                String senhaCriptografada = org.mindrot.jbcrypt.BCrypt.hashpw(cliente.getSenha(),
                        org.mindrot.jbcrypt.BCrypt.gensalt());
                stmtCliente.setString(5, senhaCriptografada);

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

                conn.commit();
                return cliente;
            } catch (SQLException e) {
                conn.rollback();
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf_cliente")) {
                    throw new ApiException("CPF já cadastrado no sistema", 409);
                }
                throw new ApiException("Erro ao criar cliente: " + e.getMessage(), 500);
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao conectar ou executar transação: " + e.getMessage(), 500);
        }
    }

    // GET - Buscar todos os clientes
    public List<Cliente> buscarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "COALESCE(SUM(s.vencimento_liquido), 0) AS vencimento_liquido_total, " +
                "COALESCE(SUM(CASE WHEN s.id_vinculo IN (1, 2, 3) THEN s.vencimento_liquido ELSE 0 END), 0) AS vencimento_consignavel_total, " +
                "(SELECT COALESCE(SUM(e.valor_parcela * (e.quantidade_parcelas - " +
                "COALESCE((SELECT COUNT(*) FROM parcelas p " +
                "WHERE p.id_emprestimo = e.id_emprestimo AND p.id_status IN (1, 4, 5)), 0))), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1) AS valor_comprometido, " +
                "(SELECT COALESCE(SUM(e.valor_parcela), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1 " +
                "AND e.id_tipo_emprestimo = 1) AS valor_parcelas_mensais_consignado, " +
                "(SELECT COALESCE(SUM(e.valor_parcela), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1) AS valor_parcelas_mensais_total " +
                "FROM clientes c " +
                "LEFT JOIN salarios s ON c.id_cliente = s.id_pessoa " +
                "GROUP BY c.id_cliente";

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

    // GET - Buscar cliente por ID com margem disponível
    public Cliente buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }

        String sql = "SELECT c.*, " +
                "COALESCE(SUM(s.vencimento_liquido), 0) AS vencimento_liquido_total, " +
                "COALESCE(SUM(CASE WHEN s.id_vinculo IN (1, 2, 3) THEN s.vencimento_liquido ELSE 0 END), 0) AS vencimento_consignavel_total, " +
                "(SELECT COALESCE(SUM(e.valor_parcela * (e.quantidade_parcelas - " +
                "COALESCE((SELECT COUNT(*) FROM parcelas p " +
                "WHERE p.id_emprestimo = e.id_emprestimo AND p.id_status IN (1, 4, 5)), 0))), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1) AS valor_comprometido, " +
                "(SELECT COALESCE(SUM(e.valor_parcela), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1 " +
                "AND e.id_tipo_emprestimo = 1) AS valor_parcelas_mensais_consignado, " +
                "(SELECT COALESCE(SUM(e.valor_parcela), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1) AS valor_parcelas_mensais_total " +
                "FROM clientes c " +
                "LEFT JOIN salarios s ON c.id_cliente = s.id_pessoa " +
                "WHERE c.id_cliente = ? " +
                "GROUP BY c.id_cliente";

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

    // GET - Buscar cliente por CPF com margem disponível
    public Cliente buscarPorCpf(String cpfCliente) {
        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
        }

        String sql = "SELECT c.*, " +
                "COALESCE(SUM(s.vencimento_liquido), 0) AS vencimento_liquido_total, " +
                "COALESCE(SUM(CASE WHEN s.id_vinculo IN (1, 2, 3) THEN s.vencimento_liquido ELSE 0 END), 0) AS vencimento_consignavel_total, " +
                "(SELECT COALESCE(SUM(e.valor_parcela * (e.quantidade_parcelas - " +
                "COALESCE((SELECT COUNT(*) FROM parcelas p " +
                "WHERE p.id_emprestimo = e.id_emprestimo AND p.id_status IN (1, 4, 5)), 0))), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1) AS valor_comprometido, " +
                "(SELECT COALESCE(SUM(e.valor_parcela), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1 " +
                "AND e.id_tipo_emprestimo = 1) AS valor_parcelas_mensais_consignado, " +
                "(SELECT COALESCE(SUM(e.valor_parcela), 0) " +
                "FROM emprestimos e " +
                "WHERE e.id_cliente = c.id_cliente " +
                "AND e.id_status_emprestimo = 1) AS valor_parcelas_mensais_total " +
                "FROM clientes c " +
                "LEFT JOIN salarios s ON c.id_cliente = s.id_pessoa " +
                "WHERE c.cpf_cliente = ? " +
                "GROUP BY c.id_cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Cliente não encontrado com CPF: " + cpfCliente, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar cliente: " + e.getMessage(), 500);
        }
    }

    // PUT - Atualizar cliente existente
    public Cliente atualizar(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new IllegalArgumentException("ID e cliente não podem ser nulos.");
        }

        String sql = "UPDATE clientes SET cpf_cliente = ?, nome_cliente = ?, " +
                "data_nascimento = ?, senha_acesso = ? WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getCpfCliente());
            stmt.setString(2, cliente.getNomeCliente());
            stmt.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            String senhaCriptografada = cliente.getSenha() != null
                    ? org.mindrot.jbcrypt.BCrypt.hashpw(cliente.getSenha(), org.mindrot.jbcrypt.BCrypt.gensalt())
                    : null;
            stmt.setString(5, senhaCriptografada != null ? senhaCriptografada : null);
            stmt.setLong(6, cliente.getIdCliente());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + cliente.getIdCliente(), 404);
            }

            return cliente;
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf_cliente")) {
                throw new ApiException("CPF já cadastrado para outro cliente", 409);
            }
            throw new ApiException("Erro ao atualizar cliente: " + e.getMessage(), 500);
        }
    }

    // PATCH - Atualização parcial de cliente
    public Cliente atualizarParcial(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }

        Cliente clienteExistente = buscarPorId(cliente.getIdCliente());

        if (cliente.getCpfCliente() != null) {
            clienteExistente.setCpfCliente(cliente.getCpfCliente());
        }
        if (cliente.getNomeCliente() != null) {
            clienteExistente.setNomeCliente(cliente.getNomeCliente());
        }
        if (cliente.getDataNascimento() != null) {
            clienteExistente.setDataNascimento(cliente.getDataNascimento());
        }
        if (cliente.getSenha() != null) {
            clienteExistente.setSenha(cliente.getSenha());
        }

        return atualizar(clienteExistente);
    }

    // DELETE - Excluir cliente por ID
    public boolean excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }

        String sqlCliente = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                    stmtCliente.setLong(1, id);
                    int affectedRows = stmtCliente.executeUpdate();

                    if (affectedRows == 0) {
                        throw new ApiException("Cliente não encontrado com ID: " + id, 404);
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new ApiException("Erro ao excluir cliente: " + e.getMessage(), 500);
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao conectar ou executar transação: " + e.getMessage(), 500);
        }
        return true;
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------

    // Mapear ResultSet para objeto Cliente
    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getLong("id_cliente"));
        cliente.setCpfCliente(rs.getString("cpf_cliente"));
        cliente.setNomeCliente(rs.getString("nome_cliente"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setSenha(rs.getString("senha_acesso"));
        cliente.setVencimentoLiquidoTotal(rs.getDouble("vencimento_liquido_total")); // Adicionado
        cliente.setVencimentoConsignavelTotal(rs.getDouble("vencimento_consignavel_total"));
        cliente.setValorComprometido(rs.getDouble("valor_comprometido"));
        cliente.setValorParcelasMensaisConsignado(rs.getDouble("valor_parcelas_mensais_consignado"));
        cliente.setValorParcelasMensaisTotal(rs.getDouble("valor_parcelas_mensais_total"));
        cliente.setScore(rs.getInt("score"));
        return cliente;
    }
}