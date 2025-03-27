package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Parcela;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ParcelaDAO {

    // Criar parcela
    public Parcela criar(Parcela parcela) throws SQLException, IOException {
        if (parcela == null) {
            throw new IllegalArgumentException("Parcela não pode ser nula.");
        }

        String sql = "INSERT INTO parcelas (id_emprestimo, numero_parcela, data_vencimento, valor_pago, " +
                "data_pagamento, id_status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transação

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, parcela.getIdEmprestimo());
                stmt.setInt(2, parcela.getNumeroParcela());
                stmt.setDate(3, Date.valueOf(parcela.getDataVencimento()));
                stmt.setDouble(4, parcela.getValorPago());
                stmt.setDate(5, parcela.getDataPagamento() != null ? Date.valueOf(parcela.getDataPagamento()) : null);
                stmt.setInt(6, parcela.getIdStatus().getValor());

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

                conn.commit(); // Confirmar transação
                return parcela;
            } catch (SQLException e) {
                conn.rollback(); // Reverter em caso de erro
                throw new ApiException("Erro ao criar parcela: " + e.getMessage(), 500);
            }
        }
    }

    // Buscar todas as parcelas
    public List<Parcela> buscarTodos() throws SQLException, IOException {
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

    // Buscar parcela por ID
    public Parcela buscarPorId(Long id) throws SQLException, IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID da parcela não pode ser nulo.");
        }

        String sql = "SELECT * FROM parcelas WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Parcela não encontrada com ID: " + id, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar parcela: " + e.getMessage(), 500);
        }
    }

    // Buscar parcela por id_emprestimo e numero_parcela (método auxiliar)
    private Parcela buscarPorEmprestimoENumero(Long idEmprestimo, int numeroParcela)
            throws SQLException, IOException {
        String sql = "SELECT * FROM parcelas WHERE id_emprestimo = ? AND numero_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEmprestimo);
            stmt.setInt(2, numeroParcela);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Parcela não encontrada!", 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar parcela: " + e.getMessage(), 500);
        }
    }

    // Atualizar parcela
    public Parcela atualizar(Long id, Parcela parcela) throws SQLException, IOException {
        if (id == null || parcela == null) {
            throw new IllegalArgumentException("ID e parcela não podem ser nulos.");
        }

        String sql = "UPDATE parcelas SET id_emprestimo = ?, numero_parcela = ?, data_vencimento = ?, " +
                "valor_pago = ?, data_pagamento = ?, id_status = ? WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transação

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, parcela.getIdEmprestimo());
                stmt.setInt(2, parcela.getNumeroParcela());
                stmt.setDate(3, Date.valueOf(parcela.getDataVencimento()));
                stmt.setDouble(4, parcela.getValorPago());
                stmt.setDate(5, parcela.getDataPagamento() != null ? Date.valueOf(parcela.getDataPagamento()) : null);
                stmt.setInt(6, parcela.getIdStatus().getValor());
                stmt.setLong(7, id);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new ApiException("Parcela não encontrada com ID: " + id, 404);
                }

                conn.commit(); // Confirmar transação
                parcela.setIdParcela(id);
                return parcela;
            } catch (SQLException e) {
                conn.rollback(); // Reverter em caso de erro
                throw new ApiException("Erro ao atualizar parcela: " + e.getMessage(), 500);
            }
        }
    }

    // Excluir parcela
    public void excluir(Long id) throws SQLException, IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID da parcela não pode ser nulo.");
        }

        String sql = "DELETE FROM parcelas WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transação

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new ApiException("Parcela não encontrada com ID: " + id, 404);
                }

                conn.commit(); // Confirmar transação
            } catch (SQLException e) {
                conn.rollback(); // Reverter em caso de erro
                throw new ApiException("Erro ao excluir parcela: " + e.getMessage(), 500);
            }
        }
    }

    // Mapear ResultSet para objeto Parcela
    private Parcela mapearResultSet(ResultSet rs) throws SQLException {
        Parcela parcela = new Parcela();
        parcela.setIdParcela(rs.getLong("id_parcela"));
        parcela.setIdEmprestimo(rs.getLong("id_emprestimo"));
        parcela.setNumeroParcela(rs.getInt("numero_parcela"));
        parcela.setDataVencimento(rs.getDate("data_vencimento").toLocalDate());
        parcela.setValorPago(rs.getDouble("valor_pago"));
        Date dataPagamento = rs.getDate("data_pagamento");
        parcela.setDataPagamento(dataPagamento != null ? dataPagamento.toLocalDate() : null);
        parcela.setStatusParcela(StatusEmpParcela.fromValor(rs.getInt("id_status")));
        return parcela;
    }

    // Método para pagar uma parcela
    public Parcela pagarParcela(Long idParcela, double valorPago, LocalDate dataPagamento) {
        String sql = "UPDATE parcelas SET valor_pago = ?, data_pagamento = ?, id_status = ? WHERE id_parcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Definir os parâmetros para a consulta
            stmt.setDouble(1, valorPago);
            stmt.setDate(2, Date.valueOf(dataPagamento));
            stmt.setInt(3, StatusEmpParcela.PAGA.getValor()); // Supondo que o status "PAGA" seja o correspondente
            stmt.setLong(4, idParcela);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Parcela não encontrada com ID: " + idParcela, 404);
            }

            // Retornar a parcela atualizada
            return buscarPorId(idParcela);
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao pagar parcela: " + e.getMessage(), 500);
        }
    }

}