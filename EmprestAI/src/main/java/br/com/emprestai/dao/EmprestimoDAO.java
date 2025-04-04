package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.enums.StatusEmprestimoEnum;
import br.com.emprestai.enums.TipoEmprestimoEnum;
import br.com.emprestai.exception.ApiException;
import br.com.emprestai.model.Emprestimo;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO implements GenericDAO<Emprestimo> {

    // --------------------------------------------------------------------------------
    // CRUD Methods
    // --------------------------------------------------------------------------------

    // POST - Criar um novo empréstimo no banco de dados
    @Override
    public Emprestimo criar(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos (id_cliente, valor_total, quantidade_parcelas, juros, data_inicio, id_status_emprestimo, id_tipo_emprestimo, " +
                "valor_seguro, valor_IOF, outros_custos, data_contratacao, juros_mora, taxa_multa, data_liberacao_cred, taxa_efetiva_mensal, valor_parcela) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, emprestimo.getIdCliente());
            stmt.setDouble(2, emprestimo.getValorTotal());
            stmt.setInt(3, emprestimo.getQuantidadeParcelas());
            stmt.setDouble(4, emprestimo.getTaxaJuros());
            stmt.setDate(5, Date.valueOf(emprestimo.getDataInicio()));
            stmt.setInt(6, emprestimo.getStatusEmprestimo().getValor());
            stmt.setInt(7, emprestimo.getTipoEmprestimo().getValor());
            stmt.setDouble(8, emprestimo.getValorSeguro());
            stmt.setDouble(9, emprestimo.getValorIOF());
            stmt.setDouble(10, emprestimo.getOutrosCustos());
            stmt.setDate(11, Date.valueOf(emprestimo.getDataContratacao()));
            stmt.setDouble(12, emprestimo.getTaxaJurosMora());
            stmt.setDouble(13, emprestimo.getTaxaMulta());
            stmt.setDate(14, Date.valueOf(emprestimo.getDataLiberacaoCred())); // Novo campo
            stmt.setDouble(15, emprestimo.getTaxaEfetivaMensal()); // Novo campo
            stmt.setDouble(16, emprestimo.getValorParcela());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar emprestimo, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emprestimo.setIdEmprestimo(generatedKeys.getLong(1));
                } else {
                    throw new ApiException("Falha ao criar emprestimo, nenhum ID obtido.", 500);
                }
            }

            return emprestimo;
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao criar emprestimo: " + e.getMessage(), 500);
        }
    }

    // GET - Buscar todos os empréstimos
    @Override
    public List<Emprestimo> buscarTodos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emprestimos.add(mapearResultSet(rs));
            }
            return emprestimos;
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar emprestimos: " + e.getMessage(), 500);
        }
    }

    // GET - Buscar empréstimo por ID
    @Override
    public Emprestimo buscarPorId(Long idEmprestimo) {
        String sql = "SELECT e.id_emprestimo, e.id_cliente, e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo, " +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.juros_mora, " +
                "e.taxa_multa, e.id_emprestimo_origem, e.data_liberacao_cred, e.taxa_efetiva_mensal, e.valor_parcela, " +
                "c.cpf_cliente, c.nome_cliente " +
                "FROM emprestimos e " +
                "INNER JOIN clientes c ON e.id_cliente = c.id_cliente " +
                "WHERE e.id_emprestimo = ?";

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

    // GET - Buscar empréstimo por CPF do cliente
    @Override
    public Emprestimo buscarPorCpf(String cpfCliente) {
        String sql = "SELECT e.id_emprestimo, e.id_cliente, e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo, " +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.juros_mora, " +
                "e.taxa_multa, e.id_emprestimo_origem, e.data_liberacao_cred, e.taxa_efetiva_mensal, e.valor_parcela, " +
                "c.cpf_cliente, c.nome_cliente " +
                "FROM emprestimos e " +
                "INNER JOIN clientes c ON e.id_cliente = c.id_cliente " +
                "WHERE c.cpf_cliente = ?";

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

    // GET - Buscar todos os empréstimos por ID do cliente e tipo de empréstimo
    public List<Emprestimo> buscarPorIdClienteEmprestimo(Long idCliente, TipoEmprestimoEnum empEnum) {
        String sql = "SELECT e.id_emprestimo, e.id_cliente, e.valor_total, e.quantidade_parcelas, e.juros, e.data_inicio, e.id_status_emprestimo, e.id_tipo_emprestimo, " +
                "e.valor_seguro, e.valor_IOF, e.outros_custos, e.data_contratacao, e.juros_mora, " +
                "e.taxa_multa, e.id_emprestimo_origem, e.data_liberacao_cred, e.taxa_efetiva_mensal, e.valor_parcela, " +
                "c.cpf_cliente, c.nome_cliente, " +
                "(SELECT COUNT(*) FROM parcelas p WHERE p.id_emprestimo = e.id_emprestimo AND p.id_status NOT IN (2, 3)) AS parcelas_pagas " +
                "FROM emprestimos e " +
                "INNER JOIN clientes c ON e.id_cliente = c.id_cliente " +
                "WHERE e.id_cliente = ? AND e.id_tipo_emprestimo = ?";

        List<Emprestimo> emprestimos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCliente);
            stmt.setInt(2, empEnum.getValor());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Emprestimo emprestimo = mapearResultSet(rs);
                    emprestimos.add(emprestimo);
                }

                if (emprestimos.isEmpty()) {
                    throw new ApiException("Nenhum empréstimo encontrado para ID cliente: " + idCliente + " e tipo de empréstimo: " + empEnum, 404);
                }

                return emprestimos;
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar empréstimos: " + e.getMessage(), 500);
        }
    }

    // PUT - Atualizar empréstimo (não implementado)
    @Override
    public Emprestimo atualizar(Emprestimo entidade) throws ApiException {
        return null; // Método não implementado
    }

    // DELETE - Excluir empréstimo (não implementado)
    @Override
    public boolean excluir(Long id) throws ApiException {
        return false; // Método não implementado
    }

    // PUT - Atualizar refinanciamento de empréstimo
    public Emprestimo atualizarRefin(Long idEmprestimo, Long idEmprestimoOrigem) throws ApiException {
        Emprestimo emprestimo = buscarPorId(idEmprestimo);
        if (idEmprestimo == null || idEmprestimoOrigem == null) {
            throw new IllegalArgumentException("Os IDs não podem ser nulos.");
        }

        Emprestimo emprestimoDeOrigem = buscarPorId(idEmprestimoOrigem);
        if (emprestimoDeOrigem == null) {
            // Lógica incompleta no código original; pode ser necessário lançar uma exceção ou tratar de outra forma
        }

        return emprestimo;
    }

    // --------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------

    // Mapear ResultSet para objeto Emprestimo
    private Emprestimo mapearResultSet(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdCliente(rs.getLong("id_cliente"));
        emprestimo.setIdEmprestimoOrigem(rs.getLong("id_emprestimo_origem"));
        emprestimo.setStatusEmprestimo(StatusEmprestimoEnum.fromValor(rs.getInt("id_status_emprestimo")));
        emprestimo.setIdEmprestimo(rs.getLong("id_emprestimo"));
        emprestimo.setValorTotal(rs.getDouble("valor_total"));
        emprestimo.setQuantidadeParcelas(rs.getInt("quantidade_parcelas"));
        emprestimo.setTaxaJuros(rs.getDouble("juros"));
        emprestimo.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        emprestimo.setTipoEmprestimo(TipoEmprestimoEnum.fromValor(rs.getInt("id_tipo_emprestimo")));
        emprestimo.setValorSeguro(rs.getDouble("valor_seguro"));
        emprestimo.setValorIOF(rs.getDouble("valor_IOF"));
        emprestimo.setOutrosCustos(rs.getDouble("outros_custos"));
        emprestimo.setDataContratacao(rs.getDate("data_contratacao").toLocalDate());
        emprestimo.setTaxaJurosMora(rs.getDouble("juros_mora"));
        emprestimo.setTaxaMulta(rs.getDouble("taxa_multa"));
        emprestimo.setDataLiberacaoCred(rs.getDate("data_liberacao_cred").toLocalDate()); // Novo campo
        emprestimo.setTaxaEfetivaMensal(rs.getDouble("taxa_efetiva_mensal")); // Novo campo
        emprestimo.setValorParcela(rs.getDouble("valor_parcela"));
        emprestimo.setParcelasPagas(rs.getInt("parcelas_pagas")); // Já estava presente em buscarPorIdCliente
        return emprestimo;
    }
}