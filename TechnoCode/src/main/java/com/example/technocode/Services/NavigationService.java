package com.example.technocode.Services;

import com.example.technocode.Controllers.Aluno.PrincipalAlunoController;
import com.example.technocode.Controllers.Orientador.OrientadorPrincipalController;
import com.example.technocode.Controllers.ProfessorTG.ProfessorTGPrincipalController;
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
     * Tenta usar navegação interna se estiver em uma tela principal.
     * 
     * @param node O Node que está na cena atual
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarPara(Node node, String fxmlPath, Consumer<Object> configController) throws IOException {
        // Tenta usar navegação interna se estiver em uma tela principal
        if (navegarParaTelaInterna(node, fxmlPath, configController)) {
            return;
        }
        
        Stage stage = (Stage) node.getScene().getWindow();
        navegarPara(stage, fxmlPath, configController);
    }

    /**
     * Navega para uma nova tela a partir de um ActionEvent, permitindo configurar o controller.
     * Tenta usar navegação interna se estiver em uma tela principal.
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
        
        // Tenta usar navegação interna se estiver em uma tela principal
        if (navegarParaTelaInterna(event, fxmlPath, configController)) {
            return;
        }
        
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        navegarPara(stage, fxmlPath, configController);
    }

    /**
     * Navega para uma nova tela a partir de um Stage, permitindo configurar o controller.
     * Preserva o estado de maximização da janela.
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
        
        // Preserva o estado de maximização
        boolean estavaMaximizado = stage.isMaximized();
        
        // Obtém dimensões da tela se estiver navegando para telas principais
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene;
        
        if (fxmlPath.contains("aluno-principal.fxml") || fxmlPath.contains("orientador-principal.fxml") ||
            fxmlPath.contains("login.fxml") || fxmlPath.contains("cadastro.fxml") ||
            fxmlPath.contains("tela-inicial-orientador.fxml")) {
            // Telas principais: cria com tamanho da tela
            scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        } else {
            // Outras telas: cria sem tamanho fixo
            scene = new Scene(root);
        }
        
        stage.setScene(scene);
        
        // Restaura ou mantém maximização
        if (estavaMaximizado) {
            stage.setMaximized(true);
        }
        
        stage.show();
        
        // Garante maximização após mostrar para telas principais
        if (fxmlPath.contains("aluno-principal.fxml") || fxmlPath.contains("orientador-principal.fxml") ||
            fxmlPath.contains("login.fxml") || fxmlPath.contains("cadastro.fxml") ||
            fxmlPath.contains("tela-inicial-orientador.fxml")) {
            javafx.application.Platform.runLater(() -> {
                if (!stage.isMaximized()) {
                    stage.setMaximized(true);
                }
            });
        }
    }
    
    /**
     * Navega para uma nova tela em tela cheia (maximizada).
     * Garante que a janela sempre abra maximizada, independente do estado anterior.
     * 
     * @param event O ActionEvent do botão/clique que disparou a navegação
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static void navegarParaTelaCheia(ActionEvent event, String fxmlPath, Consumer<Object> configController) throws IOException {
        if (event == null || event.getSource() == null) {
            throw new IllegalArgumentException("ActionEvent não pode ser null");
        }
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(NavigationService.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        // Configura o controller se um Consumer foi fornecido
        if (configController != null) {
            Object controller = loader.getController();
            if (controller != null) {
                configController.accept(controller);
            }
        }
        
        // Obtém dimensões da tela para telas principais
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        Scene scene;
        
        if (fxmlPath.contains("aluno-principal.fxml") || fxmlPath.contains("orientador-principal.fxml") ||
            fxmlPath.contains("login.fxml") || fxmlPath.contains("cadastro.fxml") ||
            fxmlPath.contains("tela-inicial-orientador.fxml")) {
            // Telas principais: cria com tamanho da tela
            scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        } else {
            // Outras telas: cria sem tamanho fixo
            scene = new Scene(root);
        }
        
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        
        // Garante maximização após mostrar
        javafx.application.Platform.runLater(() -> {
            if (!stage.isMaximized()) {
                stage.setMaximized(true);
            }
        });
    }
    
    /**
     * Navega para uma tela, tentando usar a navegação interna do center
     * se estivermos na tela principal, caso contrário navega normalmente.
     * 
     * @param event O ActionEvent do botão/clique que disparou a navegação
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @return true se usou navegação interna, false caso contrário
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static boolean navegarParaTelaInterna(ActionEvent event, String fxmlPath, Consumer<Object> configController) throws IOException {
        if (event == null || event.getSource() == null) {
            return false;
        }
        Node node = (Node) event.getSource();
        return navegarParaTelaInterna(node, fxmlPath, configController);
    }
    
    /**
     * Versão sem Consumer do método navegarParaTelaInterna
     */
    public static boolean navegarParaTelaInterna(ActionEvent event, String fxmlPath) throws IOException {
        return navegarParaTelaInterna(event, fxmlPath, null);
    }
    
    /**
     * Versão usando Node ao invés de ActionEvent.
     * Tenta usar navegação interna se estiver em uma tela principal (aluno ou orientador).
     * 
     * @param node O Node que está na cena atual
     * @param fxmlPath Caminho do arquivo FXML
     * @param configController Consumer para configurar o controller após carregar (pode ser null)
     * @return true se usou navegação interna, false caso contrário
     * @throws IOException Se ocorrer erro ao carregar o FXML
     */
    public static boolean navegarParaTelaInterna(Node node, String fxmlPath, Consumer<Object> configController) throws IOException {
        // Verifica se é uma tela do aluno e tenta usar navegação interna
        if (fxmlPath.contains("/Aluno/") || fxmlPath.contains("aluno-")) {
            try {
                PrincipalAlunoController principalController = PrincipalAlunoController.getInstance();
                if (principalController != null) {
                    principalController.navegarParaTelaDoCenter(fxmlPath, configController);
                    return true;
                }
            } catch (Exception e) {
                // Se não conseguir acessar o controller principal, continua
            }
        }
        
        // Verifica se é uma tela do orientador e tenta usar navegação interna
        if (fxmlPath.contains("/Orientador/") || fxmlPath.contains("orientador-")) {
            try {
                OrientadorPrincipalController orientadorController = OrientadorPrincipalController.getInstance();
                if (orientadorController != null) {
                    orientadorController.navegarParaTelaDoCenter(fxmlPath, configController);
                    return true;
                }
            } catch (Exception e) {
                // Se não conseguir acessar o controller principal, continua
            }
        }
        
        // Verifica se é uma tela do professor de TG e tenta usar navegação interna
        if (fxmlPath.contains("/ProfessorTG/") || fxmlPath.contains("professor-tg-") || fxmlPath.contains("dashboard-professor-tg")) {
            try {
                ProfessorTGPrincipalController professorController = ProfessorTGPrincipalController.getInstance();
                if (professorController != null) {
                    professorController.navegarParaTelaDoCenter(fxmlPath, configController);
                    return true;
                }
            } catch (Exception e) {
                // Se não conseguir acessar o controller principal, continua
            }
        }
        
        // Não está em uma tela principal ou não conseguiu usar navegação interna
        return false;
    }
    
    /**
     * Versão usando Node sem Consumer
     */
    public static boolean navegarParaTelaInterna(Node node, String fxmlPath) throws IOException {
        return navegarParaTelaInterna(node, fxmlPath, null);
    }

}

