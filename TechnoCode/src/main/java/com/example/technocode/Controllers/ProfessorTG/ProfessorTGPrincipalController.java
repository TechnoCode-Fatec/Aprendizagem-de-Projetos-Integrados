package com.example.technocode.Controllers.ProfessorTG;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.ProfessorTG;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Controller principal da tela do professor de TG.
 * Gerencia o cabeçalho, navegação lateral e troca de conteúdo no center.
 */
public class ProfessorTGPrincipalController {

    // Referência estática para permitir que outras telas naveguem dentro do center
    private static ProfessorTGPrincipalController instance;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label labelNomeProfessor;

    @FXML
    private StackPane centerContent;

    @FXML
    private void initialize() {
        // Define a instância estática
        instance = this;

        // Garante que o rootPane preencha toda a área disponível
        if (rootPane != null) {
            rootPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
            rootPane.setMinSize(0, 0);
            rootPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        // Carrega o nome do professor atual
        carregarNomeProfessor();

        // Carrega o dashboard no center
        carregarDashboard();
    }

    /**
     * Obtém a instância atual do ProfessorTGPrincipalController.
     * Permite que outras telas naveguem dentro do center.
     */
    public static ProfessorTGPrincipalController getInstance() {
        return instance;
    }

    /**
     * Carrega o nome do professor atual no label do cabeçalho
     */
    private void carregarNomeProfessor() {
        String emailLogado = LoginController.getEmailLogado();
        if (emailLogado != null && !emailLogado.isBlank()) {
            Map<String, String> dadosProfessor = ProfessorTG.buscarDadosPorEmail(emailLogado);
            String nome = dadosProfessor.get("nome");
            if (nome != null && !nome.isBlank()) {
                labelNomeProfessor.setText(nome);
            }
        }
    }

    /**
     * Carrega o dashboard no center
     */
    private void carregarDashboard() {
        navegarParaTela("/com/example/technocode/ProfessorTG/dashboard-professor-tg-content.fxml", null);
    }

    /**
     * Navega para uma tela específica no center
     */
    public void navegarParaTela(String fxmlPath, Consumer<Object> configController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Configura o controller se necessário
            if (configController != null) {
                Object controller = loader.getController();
                if (controller != null) {
                    configController.accept(controller);
                }
            }

            // Limpa e adiciona o novo conteúdo no center
            centerContent.getChildren().clear();
            centerContent.getChildren().add(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Botão Início - carrega o dashboard
     */
    @FXML
    private void navegarInicio() {
        navegarParaTela("/com/example/technocode/ProfessorTG/dashboard-professor-tg-content.fxml",
                controller -> {
                    if (controller instanceof com.example.technocode.Controllers.DashboardProfessorTGController) {
                        // Atualiza o dashboard quando volta para ele
                        ((com.example.technocode.Controllers.DashboardProfessorTGController) controller).atualizarDashboard();
                    }
                });
    }

    /**
     * Método público para ser chamado por outras telas quando precisarem navegar
     * Mantém a estrutura principal mas troca o conteúdo do center
     */
    public void navegarParaTelaDoCenter(String fxmlPath, Consumer<Object> configController) {
        navegarParaTela(fxmlPath, configController);
    }

    @FXML
    public void onSair(ActionEvent event) throws IOException {
        /**
         * Botão Sair - força logout do perfil do usuário
         */
        NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/login.fxml", null);
    }
}

