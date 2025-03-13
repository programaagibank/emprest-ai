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

    // Criar cliente = O método retorna um objeto Cliente e recebe um objeto Cliente como parâmetro.
    public Cliente criar(Cliente cliente) {
        //string sql recebe o comando para criação de um novo cliente no banco.
        String sql = "INSERT INTO clientes (cpf_cliente, nome_cliente, renda_mensal_liquida, data_nascimento, " +
                "renda_familiar_liquida, qtd_pessoas_na_casa, id_tipo_cliente, score) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // ? são placeholderesultSet para evitar SQL Injection. o que é? é uma técnica de ataque cibernético que explora vulnerabilidades em sistemas que utilizam bancos de dados SQL

        //Faz a conexção com o banco de dados.
        try (Connection conn = DatabaseConnection.getConnection();

             //PreparedStatement prepara a query SQL e substitui os '?' pelos valores do cliente.
             //Statement.RETURN_GENERATED_KEYS indica que queremos recuperar a chave primária
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //Cada linha define um valor para um '?' no SQL.
            statement.setString(1, cliente.getCpf_cliente());
            statement.setString(2, cliente.getNome_cliente());
            statement.setBigDecimal(3, cliente.getRenda_mensal_liquida());
            statement.setDate(4, Date.valueOf(cliente.getData_nascimento()));
            statement.setBigDecimal(5, cliente.getRenda_familiar_liquida());
            statement.setInt(6, cliente.getQtd_pessoas_na_casa());
            statement.setString(7, cliente.getId_tipo_cliente().name());
            statement.setInt(8, cliente.getScore());

            //executeUpdate(): Executa o comando SQL e retorna o número de linhas afetadas.
            //Se affectedRows for 0, significa que a inserção falhou, então lançamos uma exceção.
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar cliente, nenhuma linha afetada.", 500);
            }

            //getGeneratedKeys(): Recupera a chave primária (id_cliente) gerada.
            //Se o banco retornar um ID, definimos ele no objeto cliente, senão, lançamos uma exceção.0
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId_cliente(generatedKeys.getLong(1));
                } else {
                    throw new ApiException("Falha ao criar cliente, nenhum ID obtido.", 500);
                }
            }
            return cliente;
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Entrada duplicada") && e.getMessage().contains("cpf_cliente")) {
                throw new ApiException("CPF já cadastrado no sistema", 409);
            }
            throw new ApiException("Erro ao criar cliente: " + e.getMessage(), 500);
        }
    }

    // Buscar todos os clientes
    public List<Cliente> buscarTodos() {  //Retorna uma lista de objetos Cliente
        List<Cliente> clientes = new ArrayList<>(); //Cria uma lista vazia para armazenar os clientes.
        String sql = "SELECT * FROM clientes"; //Define a query SQL para buscar todos os clientes.

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement(); // Cria um comando SQL.
             ResultSet resultSet = statement.executeQuery(sql)) { //Executa a consulta e retorna os resultados.

            while (resultSet.next()) {
                clientes.add(mapearResultSet(resultSet)); //Itera sobre os resultados e converte cada linha em um objeto Cliente usando mapearResultSet
            }

            return clientes;
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar clientes: " + e.getMessage(), 500);
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) { //Busca um cliente específico pelo id_cliente.
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {//Obtém conexão e prepara a query.

            statement.setLong(1, id); //Define o ? com o id do cliente.

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearResultSet(resultSet);
                } else {
                    throw new ApiException("Cliente não encontrado com ID: " + id, 404);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar cliente: " + e.getMessage(), 500);
        }
    }

    // Atualizar / cliente Atualiza os dados de um cliente.
    public Cliente atualizar(Long id, Cliente cliente) {

        //Monta a query SQL para atualizar os dados.
        String sql = "UPDATE clientes SET cpf_cliente = ?, nome_cliente = ?, renda_mensal_liquida = ?, " +
                "data_nascimento = ?, renda_familiar_liquida = ?, qtd_pessoas_na_casa = ?, " +
                "id_tipo_cliente = ?, score = ? WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, cliente.getCpf_cliente());
            statement.setString(2, cliente.getNome_cliente());
            statement.setBigDecimal(3, cliente.getRenda_mensal_liquida());
            statement.setDate(4, Date.valueOf(cliente.getData_nascimento()));
            statement.setBigDecimal(5, cliente.getRenda_familiar_liquida());
            statement.setInt(6, cliente.getQtd_pessoas_na_casa());
            statement.setString(7, cliente.getId_tipo_cliente().name());
            statement.setInt(8, cliente.getScore());
            statement.setLong(9, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }

            cliente.setId_cliente(id);
            return cliente;
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf_cliente")) {
                throw new ApiException("CPF já cadastrado para outro cliente", 409);
            }
            throw new ApiException("Erro ao atualizar cliente: " + e.getMessage(), 500);
        }
    }

    // Excluir cliente
    //Exclui um cliente pelo id_cliente
    public void excluir(Long id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Cliente não encontrado com ID: " + id, 404);
            }
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao excluir cliente: " + e.getMessage(), 500);
        }
    }

    // Método auxiliar para mapear ResultSet para objeto Cliente
    private Cliente mapearResultSet(ResultSet resultSet) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId_cliente(resultSet.getLong("id_cliente"));
        cliente.setCpf_cliente(resultSet.getString("cpf_cliente"));
        cliente.setNome_cliente(resultSet.getString("nome_cliente"));
        cliente.setRenda_mensal_liquida(resultSet.getBigDecimal("renda_mensal_liquida"));
        cliente.setData_nascimento(resultSet.getDate("data_nascimento").toLocalDate());
        cliente.setRenda_familiar_liquida(resultSet.getBigDecimal("renda_familiar_liquida"));
        cliente.setQtd_pessoas_na_casa(resultSet.getInt("qtd_pessoas_na_casa"));
        cliente.setId_tipo_cliente(VinculoEnum.fromValor(resultSet.getInt("id_tipo_cliente")));
        cliente.setScore(resultSet.getInt("score"));
        return cliente;
    }
}
