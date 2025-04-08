package br.com.emprestai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carregar o FXML
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 700); // Resolução de celular

        // Definir o título da janela
        stage.setTitle("Emprest.AI - Gerenciador de Empréstimos");

        // Definir o ícone da janela (substitua pelo caminho correto)
        stage.getIcons().add(new Image(getClass().getResource("/br/com/emprestai/images/Icon.png").toString())); // Ou use "classpath:/path/to/your/icon.png" se for um recurso interno

        // Configurar a cena e mostrar a janela
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}