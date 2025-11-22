package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Orientador;
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


public class OrientadorPrincipalController {

    // Referência estática para permitir que outras telas naveguem dentro do center
    private static OrientadorPrincipalController instance;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label labelNomeOrientador;

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

        carregarNomeOrientador();

        // Carrega a tela inicial no center
        carregarTelaInicial();
    }

    public static OrientadorPrincipalController getInstance() {
        return instance;
    }

    private void carregarNomeOrientador() {
        String emailLogado = LoginController.getEmailLogado();
        if (emailLogado != null && !emailLogado.isBlank()) {
            Map<String, String> dadosOrientador = Orientador.buscarDadosPorEmail(emailLogado);
            String nome = dadosOrientador.get("nome");
            if (nome != null && !nome.isBlank()) {
                labelNomeOrientador.setText(nome);
            }
        }
    }

    /**
     * Botão Sair - volta para a tela de login
     */
    @FXML
    private void sair(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/login.fxml", null);
    }

    /**
     * Carrega a tela inicial no center
     */
    private void carregarTelaInicial() {
        navegarParaTela("/com/example/technocode/Orientador/dashboard-orientador.fxml", null);
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
     * Botão Início - carrega a tela inicial (dashboard)
     */
    @FXML
    private void navegarInicio() {
        navegarParaTela("/com/example/technocode/Orientador/dashboard-orientador.fxml",
                controller -> {
                    if (controller instanceof com.example.technocode.Controllers.Orientador.DashboardOrientadorController) {
                        // Recarrega as estatísticas quando volta para o dashboard
                        ((com.example.technocode.Controllers.Orientador.DashboardOrientadorController) controller).initialize();
                    }
                });
    }

    /**
     * Botão Histórico - carrega a tela de histórico
     */
    @FXML
    private void navegarHistorico() {
        navegarParaTela("/com/example/technocode/Orientador/orientador-historico.fxml", null);
    }

    /**
     * Botão Alunos Orientados - carrega a tela de alunos orientados
     */
    @FXML
    private void navegarAlunosOrientados() {
        navegarParaTela("/com/example/technocode/Orientador/alunos-orientados.fxml",
                controller -> {
                    if (controller instanceof AlunosOrientadosController) {
                        ((AlunosOrientadosController) controller).recarregarTabela();
                    }
                });
    }

    /**
     * Botão Solicitações - carrega a tela de solicitações de orientação
     */
    @FXML
    private void navegarSolicitacoes() {
        navegarParaTela("/com/example/technocode/Orientador/solicitacoes-orientacao.fxml",
                controller -> {
                    if (controller instanceof SolicitacoesOrientacaoController) {
                        ((SolicitacoesOrientacaoController) controller).recarregarSolicitacoes();
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
         * Botão Saida - força logout do perfil do usuário
         */
        NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/login.fxml", null);
    }
}


