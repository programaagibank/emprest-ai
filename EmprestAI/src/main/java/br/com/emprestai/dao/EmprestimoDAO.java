package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.enums.MotivosEncerramentosEmpEnum;
import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Emprestimo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmprestimoDAO {
    public Emprestimo buscarEmpPorCPF(String cpf_cliente) {
        String sql = "select e.id_emprestimo, e.id_cliente , e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo,\n" +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.id_motivo_encerramento, e.juros_mora,\n" +
                "e.taxa_multa, e.id_emprestimo_origem, c.cpf_cliente, c.nome_cliente\n" +
                "from emprestimos e\n" +
                "inner join clientes c on e.id_cliente = c.id_cliente\n" +
                "where c.cpf_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf_cliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Emprestimo não encontrado com CPF: " + cpf_cliente, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar emprestimo: " + e.getMessage(), 500);
        }
    }
    private Emprestimo mapearResultSet(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdCliente(rs.getLong("id_cliente"));
        emprestimo.setIdEmprestimoOrigem(rs.getLong("id_emprestimo_origem"));
        emprestimo.setCpf_cliente(rs.getString("cpf_cliente"));
        emprestimo.setIdStatusEmprestimo(StatusEmpEnum.fromValor(rs.getInt("id_status_emprestimo")));
        emprestimo.setIdContrato(rs.getLong("id_emprestimo"));
        emprestimo.setNome_cliente(rs.getString("nome_cliente"));
        emprestimo.setValorTotal(rs.getBigDecimal("valor_total"));
        emprestimo.setQuantidadeParcelas(rs.getInt("quantidade_parcelas"));
        emprestimo.setJuros(rs.getDouble("juros"));
        emprestimo.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        emprestimo.setIdTipoEmprestimo(TipoEmpEnum.fromValor(rs.getInt("id_tipo_emprestimo")));
        emprestimo.setValorSeguro(rs.getBigDecimal("valor_seguro"));
        emprestimo.setValorIOF(rs.getBigDecimal("valor_IOF"));
        emprestimo.setOutrosCustos(rs.getBigDecimal("outros_custos"));
        emprestimo.setDataContratacao(rs.getDate("data_contratacao").toLocalDate());
        emprestimo.setIdMotivoEncerramento(MotivosEncerramentosEmpEnum.fromValor(rs.getInt("id_motivo_encerramento")));
        emprestimo.setJurosMora(rs.getDouble("juros_mora"));
        emprestimo.setTaxaMulta(rs.getDouble("taxa_multa"));
        //TODO Adcionar metodo set para a lista de parcerlas
        return emprestimo;
    }
}
