package com.example.technocode.Services;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Serviço responsável por gerenciar a navegação entre telas do aplicativo.
 * Centraliza a lógica de mudança de cena para evitar código repetitivo.
 */
public class NavigationService {

    /**
     * Navega para uma nova tela a partir de um Node (geralmente obtido de ActionEvent).
     * 
     * @param node O Node que está na cena atual (pode ser obtido de event.getSource())
     * @param fxmlPath Caminho do arquivo FXML (ex: "/com/example/technocode/Aluno/tela-inicial-aluno.fxml")
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarPara(Node node, String fxmlPath) throws IOException {
        navegarPara(node, fxmlPath, null);
    }

    /**
     * Navega para uma nova tela a partir de um ActionEvent.
     * 
     * @param event O ActionEvent do botão/clique que disparou a navegação
     * @param fxmlPath Caminho do arquivo FXML
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarPara(ActionEvent event, String fxmlPath) throws IOException {
        navegarPara(event, fxmlPath, null);
    }

    /**
     * Navega para uma nova tela a partir de um Node, permitindo configurar o controller antes de exibir.
     * 
     * @param node O Node que está na cena atual
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarPara(Node node, String fxmlPath, Consumer<Object> configController) throws IOException {
        Stage stage = (Stage) node.getScene().getWindow();
        navegarPara(stage, fxmlPath, configController);
    }

    /**
     * Navega para uma nova tela a partir de um ActionEvent, permitindo configurar o controller.
     * 
     * @param event O ActionEvent do botão/clique que disparou a navegação
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarPara(ActionEvent event, String fxmlPath, Consumer<Object> configController) throws IOException {
        if (event == null || event.getSource() == null) {
            throw new IllegalArgumentException("ActionEvent não pode ser null");
        }
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        navegarPara(stage, fxmlPath, configController);
    }

    /**
     * Navega para uma nova tela a partir de um Stage, permitindo configurar o controller.
     * 
     * @param stage O Stage (janela) onde a nova cena será exibida
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarPara(Stage stage, String fxmlPath, Consumer<Object> configController) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationService.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        // Configura o controller se um Consumer foi fornecido
        if (configController != null) {
            Object controller = loader.getController();
            if (controller != null) {
                configController.accept(controller);
            }
        }
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}

