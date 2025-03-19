package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.enums.StatusEmpParcela;
import br.com.emprestai.model.Parcela;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelaDAO {

    // Método para criar a tabela no banco de dados (executar uma vez)
    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS parcelas (" +
                "idParcela BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "idEmprestimo BIGINT NOT NULL, " +
                "numeroParcela INT NOT NULL, " +
                "dataVencimento DATE NOT NULL, " +
                "valorPresenteParcela DECIMAL(10,2) NOT NULL, " +
                "juros DECIMAL(10,2) NOT NULL, " +
                "amortizacao DECIMAL(10,2) NOT NULL, " +
                "idStatusParcela ENUM('PAGA', 'PENDENTE') NOT NULL, " +
                "dataPagamento DATE, " +
                "taxaMulta DECIMAL(10,2) NOT NULL, " +
                "jurosMora DECIMAL(10,2) NOT NULL, " +
                "FOREIGN KEY (idEmprestimo) REFERENCES emprestimos(idEmprestimo)" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela parcelas criada ou já existente!");
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao criar tabela: " + e.getMessage(), 500);
        }
    }

    // Criar parcela
    public Parcela criar(Parcela parcela) throws SQLException, IOException {
        String sql = "INSERT INTO parcelas (idParcela, idEmprestimo, numeroParcela, dataVencimento, " +
                "valorPresenteParcela, juros, amortização, idStatusParcela, dataPagamento, taxaMulta, jurosMora) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, parcela.getIdParcela());
            stmt.setLong(2, parcela.getIdEmprestimo());
            stmt.setDouble(3, parcela.getNumeroParcela());
            stmt.setDate(4, Date.valueOf(parcela.getDataVencimento()));
            stmt.setDouble(5, parcela.getValorPresenteParcela());
            stmt.setDouble(6, parcela.getJuros());
            stmt.setDouble(7, parcela.getAmortizacao());
            stmt.setString(8, String.valueOf(parcela.getstatusParcela()));
            stmt.setDate(9, Date.valueOf(parcela.getDataPagamento()));
            stmt.setDouble(10, parcela.getMulta());
            stmt.setDouble(11, parcela.getJurosMora());

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
            //Adicionei o IOException para parar de reclamar erro
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
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar parcelas: " + e.getMessage(), 500);
        }
    }

    // Buscar cliente por ID
    public Parcela buscarPorId(Long id) {
        String sql = "SELECT * FROM parcelas WHERE idParcela = ?";

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
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar parcela: " + e.getMessage(), 500);
        }
    }

    // Atualizar parcela
    public Parcela atualizar(Long id, Parcela parcela) {
        String sql = "UPDATE parcelas SET idParcela = ?, idEmprestimo = ?, numeroParcela = ?, " +
                "dataVencimento = ?, valorPresenteParcela = ?, juros = ?, " +
                "amortização = ?, idStatusParcela = ?, dataPagamento = ?, " +
                "taxaMulta = ?, jurosMora = ? WHERE idParcela = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, parcela.getIdParcela());
            stmt.setLong(2, parcela.getIdEmprestimo());
            stmt.setDouble(3, parcela.getNumeroParcela());
            stmt.setDate(4, Date.valueOf(parcela.getDataVencimento()));
            stmt.setDouble(5, parcela.getValorPresenteParcela());
            stmt.setDouble(6, parcela.getJuros());
            stmt.setDouble(7, parcela.getAmortizacao());
            stmt.setString(8, String.valueOf(parcela.getstatusParcela()));
            stmt.setDate(9, Date.valueOf(parcela.getDataPagamento()));
            stmt.setDouble(10, parcela.getMulta());
            stmt.setDouble(11, parcela.getJurosMora());
            stmt.setLong(12, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }

            parcela.setIdParcela(id);
            return parcela;
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
        // Excluir parcela
        public void excluir(Long id){
            String sql = "DELETE FROM parcelas WHERE idParcela = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, id);

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new ApiException("Parcela não encontrada com ID: " + id, 404);
                }
                //Adicionei o IOException para parar de reclamar erro
            } catch (SQLException | IOException e) {
                throw new ApiException("Erro ao excluir parcela: " + e.getMessage(), 500);
            }
        }
    private Parcela mapearResultSet(ResultSet rs) throws SQLException {
        Parcela parcela = new Parcela();
        parcela.setIdParcela(rs.getLong("idParcela"));
        parcela.setIdEmprestimo(Long.valueOf(rs.getString("idEmprestimo")));
        parcela.setNumeroParcela(rs.getInt("numeroParcela"));
        parcela.setDataVencimento(rs.getDate("dataVencimento").toLocalDate());
        parcela.setValorPresenteParcela(rs.getDouble("valorPresenteParcela"));
        parcela.setJuros(rs.getDouble("juros"));
        parcela.setAmortizacao(rs.getInt("amortização"));
        parcela.setStatusParcela(StatusEmpParcela.fromValor(rs.getInt("idStatusParcela")));
        parcela.setDataPagamento(rs.getDate("dataPagamento").toLocalDate());
        parcela.setJurosMora(rs.getDouble("jurosMora"));
        parcela.setMulta(rs.getDouble("taxaMulta"));

        return parcela;
    }
}
