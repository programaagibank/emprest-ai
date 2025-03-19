package br.com.emprestai.dao;

import br.com.emprestai.database.DatabaseConnection;
import br.com.emprestai.database.exception.ApiException;
import br.com.emprestai.model.Login;

import java.io.IOException;
import java.sql.*;

public class LoginDAO {

    // Criar login
    public Login criar(Login login) {
        String sql = "INSERT INTO login_usuarios (id_login_usuario, login_cpf_cliente, senha" +
                "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, login.getUsername());
            stmt.setString(2, login.getSenha());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ApiException("Falha ao criar login, nenhuma linha afetada.", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    login.setUsername(String.valueOf(generatedKeys.getLong(1)));
                    login.setSenha(String.valueOf(generatedKeys.getLong(2)));
                } else {
                    throw new ApiException("Falha ao criar login, nenhum usuário obtido.", 500);
                }
            }

            return login;
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("UsuarioBanco")) {
                throw new ApiException("Usuario já cadastrado no sistema", 409);
            }
            throw new ApiException("Erro ao criar login: " + e.getMessage(), 500);
        }
    }

    // Buscar usuario por CPF
    public Login buscarPorUsuario(String usuarioBanco) {
        String sql = "SELECT * FROM login_usuarios WHERE login_cpf_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuarioBanco);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                } else {
                    throw new ApiException("Usuario não encontrado com login: " + usuarioBanco, 404);
                }
            }
            //Adicionei o IOException para parar de reclamar erro
        } catch (SQLException | IOException e) {
            throw new ApiException("Erro ao buscar usuario: " + e.getMessage(), 500);
        }
    }

    private Login mapearResultSet(ResultSet rs) throws SQLException {
        Login login = new Login(null,
                String.valueOf(rs.getLong("usuarioBanco")),
                rs.getString("senhaBanco"));
        return login;
    }
}
