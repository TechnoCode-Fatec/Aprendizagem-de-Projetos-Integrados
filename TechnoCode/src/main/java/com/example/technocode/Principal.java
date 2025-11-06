package com.example.technocode;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Principal extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Principal.class.getResource("DashboardProfessorTG.fxml"));
        
        // Obtém as dimensões da tela primária
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        // Cria a Scene com o tamanho da tela
        Scene scene = new Scene(fxmlLoader.load(), screenBounds.getWidth(), screenBounds.getHeight());
        
        stage.setTitle("Gerenciador de login");
        stage.setScene(scene);
        stage.setMaximized(true);
        
        // Força maximização após a janela ser exibida
        stage.setOnShown(event -> {
            stage.setMaximized(true);
            stage.centerOnScreen();
        });
        
        stage.show();
        
        // Garante maximização uma última vez após tudo estar pronto
        javafx.application.Platform.runLater(() -> {
            if (!stage.isMaximized()) {
                stage.setMaximized(true);
            }
        });
    }
}

// feat (us-10): jajbbqjcda