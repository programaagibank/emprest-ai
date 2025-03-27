package br.com.emprestai.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Login extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 640); // Mobile phone resolution
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/login.css")).toExternalForm());
        stage.setTitle("Emprest.AI - Gerenciador de Empréstimos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}