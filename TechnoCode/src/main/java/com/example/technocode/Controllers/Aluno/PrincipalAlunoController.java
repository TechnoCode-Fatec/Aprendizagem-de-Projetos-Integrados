package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
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
 * Controller principal da tela do aluno.
 * Gerencia o cabeçalho, navegação lateral e troca de conteúdo no center.
 */
public class PrincipalAlunoController {

    // Referência estática para permitir que outras telas naveguem dentro do center
    private static PrincipalAlunoController instance;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label labelNomeAluno;

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

        // Carrega o nome do aluno atual
        carregarNomeAluno();

        // Carrega a tela inicial no center
        carregarTelaInicial();
    }

    /**
     * Obtém a instância atual do PrincipalAlunoController.
     * Permite que outras telas naveguem dentro do center.
     */
    public static PrincipalAlunoController getInstance() {
        return instance;
    }

    /**
     * Carrega o nome do aluno atual no label do cabeçalho
     */
    private void carregarNomeAluno() {
        String emailLogado = LoginController.getEmailLogado();
        if (emailLogado != null && !emailLogado.isBlank()) {
            Map<String, String> dadosAluno = Aluno.buscarDadosPorEmail(emailLogado);
            String nome = dadosAluno.get("nome");
            if (nome != null && !nome.isBlank()) {
                labelNomeAluno.setText(nome);
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
     * Carrega a tela inicial (dashboard) no center
     */
    private void carregarTelaInicial() {
        navegarParaTela("/com/example/technocode/Aluno/dashboard-aluno.fxml", null);
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
        navegarParaTela("/com/example/technocode/Aluno/dashboard-aluno.fxml",
                controller -> {
                    if (controller instanceof com.example.technocode.Controllers.Aluno.DashboardAlunoController) {
                        // Recarrega as estatísticas quando volta para o dashboard
                        ((com.example.technocode.Controllers.Aluno.DashboardAlunoController) controller).atualizarEstatisticas();
                    }
                });
    }

    /**
     * Botão Enviar Api - carrega o formulário de API
     */
    @FXML
    private void navegarEnviarApi() {
        navegarParaTela("/com/example/technocode/Aluno/formulario-api.fxml", null);
    }

    /**
     * Botão Enviar Apresentação - carrega o formulário de apresentação
     */
    @FXML
    private void navegarEnviarApresentacao() {
        navegarParaTela("/com/example/technocode/Aluno/formulario-apresentacao.fxml", null);
    }

    /**
     * Botão Sessões Atuais - carrega a tela de sessões atuais
     */
    @FXML
    private void navegarSessoesAtuais() {
        navegarParaTela("/com/example/technocode/Aluno/sessoes-atuais.fxml",
                controller -> {
                    if (controller instanceof SessoesAtuaisAlunoController) {
                        ((SessoesAtuaisAlunoController) controller).recarregarSecoes();
                    }
                });
    }

    /**
     * Botão Histórico - carrega a tela de histórico
     */
    @FXML
    private void navegarHistorico() {
        navegarParaTela("/com/example/technocode/Aluno/aluno-historico.fxml", null);
    }

    /**
     * Botão Solicitar Orientação - carrega a tela de solicitar orientação
     */
    @FXML
    private void navegarSolicitarOrientacao() {
        navegarParaTela("/com/example/technocode/Aluno/solicitar-orientacao.fxml",
                controller -> {
                    if (controller instanceof SolicitarOrientacaoController) {
                        ((SolicitarOrientacaoController) controller).recarregarDados();
                    }
                });
    }

    /**
     * Botão Gerar README - carrega a tela de gerar README.md
     */
    @FXML
    private void navegarGerarReadme() {
        navegarParaTela("/com/example/technocode/Aluno/gerar-readme.fxml", null);
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


