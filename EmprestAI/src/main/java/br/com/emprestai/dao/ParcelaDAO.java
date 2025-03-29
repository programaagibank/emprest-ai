package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Parcela;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelaDAO {
    // Criar parcela
    public Parcela criar(Parcela parcela) throws SQLException, IOException {
        String sql = "INSERT INTO parcelas (id_parcela, id_emprestimo, numero_parcela, data_vencimento, " +
                "valor_pago, data_pagamento, id_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, parcela.getIdParcela());
            stmt.setLong(2, parcela.getIdEmprestimo());
            stmt.setDouble(3, parcela.getNumeroParcela());
            stmt.setDate(4, Date.valueOf(parcela.getDataVencimento()));
            stmt.setDouble(5, parcela.getValorPago());
            stmt.setDate(6, Date.valueOf(parcela.getDataPagamento()));
            stmt.setInt(7, parcela.getIdStatus().getValor());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar parcela, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    parcela.setIdParcela(generatedKeys.getLong(1));
                } else {
                    throw new ApiException("Falha ao criar parcela, nenhum ID obtido.", 500);
                }
            }

            return parcela;
        }
    }

    // Buscar todas as parcelas
    public List<Parcela> buscarTodos() {
        List<Parcela> parcelas = new ArrayList<>();
        String sql = "SELECT * FROM parcelas";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                parcelas.add(mapearResultSet(rs));
            }

            return parcelas;
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar parcelas: " + e.getMessage(), 500);
        }
    }

    // Buscar cliente por ID
    public Parcela buscarPorId(Long id) {
        String sql = "SELECT * FROM parcelas WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Parcela não encontrado com ID: " + id, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar parcela: " + e.getMessage(), 500);
        }
    }

    // Atualizar parcela
    public Parcela atualizar(Long id, Parcela parcela) {
        String sql = "UPDATE parcelas SET id_parcela = ?, id_emprestimo = ?, numero_parcela = ?, " +
                "data_vencimento = ?, valor_pago = ?, id_status = ?, data_pagamento = ? " +
                "WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, parcela.getIdParcela());
            stmt.setLong(2, parcela.getIdEmprestimo());
            stmt.setDouble(3, parcela.getNumeroParcela());
            stmt.setDate(4, Date.valueOf(parcela.getDataVencimento()));
            stmt.setDouble(5, parcela.getValorPago());
            stmt.setInt(6, parcela.getIdStatus().getValor());
            stmt.setDate(7, Date.valueOf(parcela.getDataPagamento()));
            stmt.setLong(8, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }

            parcela.setIdParcela(id);
            return parcela;
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Excluir parcela
    public void excluir(Long id) {
        String sql = "DELETE FROM parcelas WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Parcela não encontrada com ID: " + id, 404);
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao excluir parcela: " + e.getMessage(), 500);
        }
    }

    private Parcela mapearResultSet(ResultSet rs) throws SQLException {
        Parcela parcela = new Parcela();
        parcela.setIdParcela(rs.getLong("id_parcela"));
        parcela.setIdEmprestimo(Long.valueOf(rs.getString("id_emprestimo")));
        parcela.setNumeroParcela(rs.getInt("numero_parcela"));

        // Handle data_vencimento
        java.sql.Date dataVencimento = rs.getDate("data_vencimento");
        parcela.setDataVencimento(dataVencimento != null ? dataVencimento.toLocalDate() : null);

        parcela.setValorPago(rs.getDouble("valor_pago"));

        // Handle data_pagamento
        java.sql.Date dataPagamento = rs.getDate("data_pagamento");
        parcela.setDataPagamento(dataPagamento != null ? dataPagamento.toLocalDate() : null);

        parcela.setStatusParcela(StatusEmpParcela.fromValor(rs.getInt("id_status")));
        return parcela;
    }

    // Metodo para pagar uma parcela
    public Parcela pagarParcela(Parcela parcela) {
        String sql = "UPDATE parcelas SET valor_pago = ?, data_pagamento = ?, id_status = ? WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Definir os parâmetros para a consulta
            stmt.setDouble(1, parcela.getValorPresenteParcela() + parcela.getMulta() + parcela.getJurosMora());
            stmt.setDate(2, Date.valueOf(parcela.getDataPagamento()));
            stmt.setInt(3, StatusEmpParcela.PAGA.getValor()); // Supondo que o status "PAGA" seja o correspondente
            stmt.setLong(4, parcela.getIdParcela());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Parcela não encontrada com ID: " + parcela.getIdParcela(), 404);
            }

            // Retornar a parcela atualizada
            return parcela;
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao pagar parcela: " + e.getMessage(), 500);
        }
    }
    public List<Parcela> buscarParcelasPorEmprestimoETipo(Long idEmprestimo, TipoEmpEnum idTipoEmprestimo) throws SQLException {
        List<Parcela> parcelas = new ArrayList<>();
        String sql = "SELECT p.* FROM parcelas p " +
                "INNER JOIN emprestimos e ON p.id_emprestimo = e.id_emprestimo " +
                "WHERE e.id_emprestimo = ? AND e.id_tipo_emprestimo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os parâmetros da consulta
            stmt.setLong(1, idEmprestimo);
            stmt.setInt(2, idTipoEmprestimo.getValor());

            // Executa a consulta
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Parcela parcela = mapearResultSet(rs);
                    parcelas.add(parcela);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao pagar parcela: " + e.getMessage(), 500);
        }
        return parcelas;
    }

    // Metodo para pagar uma lista de parcelas
    public void pagarParcelas(List<Parcela> parcelas) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Desabilita o autocommit

            String sql = "UPDATE parcelas SET valor_pago = ?, data_pagamento = ?, id_status = ? WHERE id_parcela = ?";

            pstmt = conn.prepareStatement(sql);

            for (Parcela parcela : parcelas) {
                pstmt.setDouble(1, parcela.getValorPresenteParcela() + parcela.getMulta() + parcela.getJurosMora());
                pstmt.setDate(2, Date.valueOf(parcela.getDataPagamento()));
                pstmt.setInt(3, StatusEmpParcela.PAGA.getValor());
                pstmt.setLong(4, parcela.getIdParcela());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new ApiException("Erro ao pagar parcelas: " + e.getMessage(), 500);
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}