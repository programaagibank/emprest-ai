package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.enums.StatusEmpEnum;
import br.com.emprestai.enums.TipoEmpEnum;
import br.com.emprestai.model.Emprestimo;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class EmprestimoDAO implements GenericDAO<Emprestimo> {

    @Override
    public Emprestimo criar(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos (id_cliente, valor_total, quantidade_parcelas, juros, data_inicio, id_status_emprestimo, id_tipo_emprestimo,\n" +
                "valor_seguro, valor_IOF, outros_custos, data_contratacao, juros_mora, taxa_multa, valor_parcela)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, emprestimo.getIdCliente());
            stmt.setDouble(2, emprestimo.getValorTotal());
            stmt.setInt(3, emprestimo.getQuantidadeParcelas());
            stmt.setDouble(4, emprestimo.getJuros());
            stmt.setDate(5, Date.valueOf(emprestimo.getDataInicio()));
            stmt.setInt(6, emprestimo.getStatusEmprestimo().getValor());
            stmt.setInt(7, emprestimo.getTipoEmprestimo().getValor());
            stmt.setDouble(8, emprestimo.getValorSeguro());
            stmt.setDouble(9, emprestimo.getValorIOF());
            stmt.setDouble(10, emprestimo.getOutrosCustos());
            stmt.setDate(11, Date.valueOf(emprestimo.getDataContratacao()));
            stmt.setInt(12, emprestimo.getStatusEmprestimo().getValor());
            stmt.setDouble(13, emprestimo.getTaxaJurosMora());
            stmt.setDouble(14, emprestimo.getTaxaMulta());
            stmt.setDouble(15, emprestimo.getValorParcela());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar emprestimo, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emprestimo.setIdContrato(generatedKeys.getLong(1));
                } else {
                    throw new ApiException("Falha ao criar emprestimo, nenhum ID obtido.", 500);
                }
            }

            return emprestimo;
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao criar emprestimo: " + e.getMessage(), 500);
        }
    }

    @Override
    public List<Emprestimo> buscarTodos() throws ApiException {
        return List.of();
    }

    @Override
    public Emprestimo buscarPorId(Long idEmprestimo) {
        String sql = "select e.id_emprestimo, e.id_cliente , e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo,\n" +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.juros_mora,\n" +
                "e.taxa_multa, e.id_emprestimo_origem, c.cpf_cliente, c.nome_cliente\n" +
                "from emprestimos e\n" +
                "inner join clientes c on e.id_cliente = c.id_cliente\n" +
                "where c.cpf_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEmprestimo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Emprestimo não encontrado com contrato: " + idEmprestimo, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar emprestimo: " + e.getMessage(), 500);
        }
    }

    @Override
    public Emprestimo buscarPorCpf(String cpfCliente) {
        String sql = "select e.id_emprestimo, e.id_cliente , e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo,\n" +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.juros_mora,\n" +
                "e.taxa_multa, e.id_emprestimo_origem, c.cpf_cliente, c.nome_cliente\n" +
                "from emprestimos e\n" +
                "inner join clientes c on e.id_cliente = c.id_cliente\n" +
                "where e.id_emprestimo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Emprestimo não encontrado com CPF: " + cpfCliente, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar emprestimo: " + e.getMessage(), 500);
        }
    }

    public Emprestimo buscarPorIdCliente(Long idCliente, TipoEmpEnum empEnum) {
        String sql = "select e.id_emprestimo, e.id_cliente , e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo,\n" +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.juros_mora,\n" +
                "e.taxa_multa, e.id_emprestimo_origem, c.cpf_cliente, c.nome_cliente\n" +
                "from emprestimos e\n" +
                "inner join clientes c on e.id_cliente = c.id_cliente\n" +
                "where e.id_cliente = ? and e.id_tipo_emprestimo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCliente);
            stmt.setInt(2, empEnum.getValor());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Emprestimo não encontrado para ID cliente: " + idCliente + " e tipo de emprestimo: " + empEnum, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar emprestimo: " + e.getMessage(), 500);
        }
    }

    @Override
    public Emprestimo atualizar(Emprestimo entidade) throws ApiException {
        return null;
    }

    @Override
    public void excluir(Long id) throws ApiException {

    }

    private Emprestimo mapearResultSet(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdCliente(rs.getLong("id_cliente"));
        emprestimo.setIdEmprestimoOrigem(rs.getLong("id_emprestimo_origem"));
        emprestimo.setStatusEmprestimo(StatusEmpEnum.fromValor(rs.getInt("id_status_emprestimo")));
        emprestimo.setIdContrato(rs.getLong("id_emprestimo"));
        emprestimo.setValorTotal(rs.getDouble("valor_total"));
        emprestimo.setQuantidadeParcelas(rs.getInt("quantidade_parcelas"));
        emprestimo.setJuros(rs.getDouble("juros"));
        emprestimo.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        emprestimo.setTipoEmprestimo(TipoEmpEnum.fromValor(rs.getInt("id_tipo_emprestimo")));
        emprestimo.setValorSeguro(rs.getDouble("valor_seguro"));
        emprestimo.setValorIOF(rs.getDouble("valor_IOF"));
        emprestimo.setOutrosCustos(rs.getDouble("outros_custos"));
        emprestimo.setDataContratacao(rs.getDate("data_contratacao").toLocalDate());
        emprestimo.setTaxaJurosMora(rs.getDouble("juros_mora"));
        emprestimo.setTaxaMulta(rs.getDouble("taxa_multa"));
        //TODO Adcionar metodo set para a lista de parcerlas
        return emprestimo;

    }

    public Emprestimo atualizarRefin(Long idEmprestimo, Long idEmprestimoOrigem) throws ApiException {
        Emprestimo emprestimo = buscarPorId(idEmprestimo);
        if (idEmprestimo == null || idEmprestimoOrigem == null) {
            throw new IllegalArgumentException("Os IDs não podem ser nulos.");

        }

        Emprestimo emprestimoDeOrigem = buscarPorId(idEmprestimoOrigem);
        if (emprestimoDeOrigem == null) {
        }

        return emprestimo;
    }
}








