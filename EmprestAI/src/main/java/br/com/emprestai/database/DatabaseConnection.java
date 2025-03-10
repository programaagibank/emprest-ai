package br.com.emprestai.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream("src/main/resources/database.properties");
        props.load(in);
        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String password = props.getProperty("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Somente retornei a conexão
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado", e);
        }
    }
}

