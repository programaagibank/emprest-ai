package br.com.emprestai.server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnection {
    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException, IOException {

        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            try (InputStream in = DataBaseConnection.class.getResourceAsStream("/config.properties")) {
                if (in == null) {
                    throw new RuntimeException("Arquivo config.properties não existe!");
                }
                props.load(in);
            } catch (Exception e) {
                String message = String.format("Erro ao carregar arquivo, %s", e.getMessage());
                throw new RuntimeException(message);
            }

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            if (url == null || username == null || password == null) {
                throw new RuntimeException("Propriedades não setadas");
            }

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Conexão realizada!");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão fechada");
            } catch (SQLException e) {
                String message = String.format("Erro ao fechar conexão %S", e.getMessage());
                System.out.println(message);
            }
        }
    }
}
