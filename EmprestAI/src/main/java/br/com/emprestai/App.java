package br.com.emprestai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/emprestimos.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 640); // Mobile phone resolution
        stage.setTitle("Emprest.AI - Gerenciador de Empréstimos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}