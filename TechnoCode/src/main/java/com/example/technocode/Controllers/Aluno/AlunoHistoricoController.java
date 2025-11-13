package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.SecaoApi;
import com.example.technocode.model.SecaoApresentacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AlunoHistoricoController {

    @FXML
    private VBox containerApresentacoes;
    
    @FXML
    private VBox containerApis;
    
    @FXML
    public void initialize() {
        carregarHistorico();
    }
    
    private void carregarHistorico() {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }
        
        // Carrega histórico de apresentações
        List<Map<String, String>> historicoApresentacoes = SecaoApresentacao.buscarHistoricoVersoes(emailAluno);
        exibirHistoricoApresentacoes(historicoApresentacoes);
        
        // Carrega histórico de APIs
        List<Map<String, String>> historicoApis = SecaoApi.buscarHistoricoVersoes(emailAluno);
        exibirHistoricoApis(historicoApis);
    }
    
    private void exibirHistoricoApresentacoes(List<Map<String, String>> historico) {
        // Remove todos os filhos exceto o label de título (se existir)
        containerApresentacoes.getChildren().clear();
        
        if (historico.isEmpty()) {
            Label semDados = new Label("Nenhuma apresentação cadastrada");
            semDados.setStyle("-fx-text-fill: #95A5A6; -fx-font-style: italic; -fx-font-size: 14px; -fx-padding: 20;");
            containerApresentacoes.getChildren().add(semDados);
        } else {
            for (Map<String, String> versao : historico) {
                VBox cardVersao = criarCardVersao(versao, "apresentacao");
                containerApresentacoes.getChildren().add(cardVersao);
            }
        }
    }
    
    private void exibirHistoricoApis(List<Map<String, String>> historico) {
        // Remove todos os filhos exceto o label de título (se existir)
        containerApis.getChildren().clear();
        
        if (historico.isEmpty()) {
            Label semDados = new Label("Nenhuma API cadastrada");
            semDados.setStyle("-fx-text-fill: #95A5A6; -fx-font-style: italic; -fx-font-size: 14px; -fx-padding: 20;");
            containerApis.getChildren().add(semDados);
        } else {
            for (Map<String, String> versao : historico) {
                VBox cardVersao = criarCardVersao(versao, "api");
                containerApis.getChildren().add(cardVersao);
            }
        }
    }
    
    private VBox criarCardVersao(Map<String, String> versao, String tipo) {
        // Container principal do card moderno
        VBox card = new VBox();
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setSpacing(6);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-padding: 18; -fx-cursor: hand; " +
                     "-fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        
        // Hover effect será aplicado via código
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 10; -fx-padding: 18; -fx-cursor: hand; " +
                                                  "-fx-border-color: #B82E1A; -fx-border-width: 1.5; -fx-border-radius: 10; " +
                                                  "-fx-effect: dropshadow(gaussian, rgba(184,46,26,0.15), 8, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-padding: 18; -fx-cursor: hand; " +
                                                 "-fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 10; " +
                                                 "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);"));
        
        Label titulo = new Label();
        Label subtitulo = new Label();
        Label versaoLabel = new Label();
        
        if ("apresentacao".equals(tipo)) {
            titulo.setText(versao.get("nome"));
            subtitulo.setText("Apresentação");
            versaoLabel.setText("Versão " + versao.get("versao"));
        } else {
            String semestreCurso = versao.get("semestre_curso");
            String ano = versao.get("ano");
            String semestreAno = versao.get("semestre_ano");
            String empresa = versao.get("empresa");
            
            titulo.setText(semestreCurso + " " + ano + "/" + semestreAno);
            subtitulo.setText(empresa != null ? empresa : "");
            versaoLabel.setText("Versão " + versao.get("versao"));
        }
        
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D;");
        versaoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95A5A6; -fx-font-weight: bold; -fx-padding: 4 8 0 0;");
        
        card.getChildren().addAll(titulo, subtitulo, versaoLabel);
        
        // Adiciona evento de clique para visualizar a versão específica
        card.setOnMouseClicked(event -> {
            try {
                abrirVisualizacaoVersao(versao, tipo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        return card;
    }
    
    private void abrirVisualizacaoVersao(Map<String, String> versao, String tipo) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        
        if ("apresentacao".equals(tipo)) {
            Node node = containerApresentacoes;
            NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/aluno-visualizar-apresentacao.fxml",
                controller -> {
                    if (controller instanceof AlunoVisualizarApresentacaoController) {
                        ((AlunoVisualizarApresentacaoController) controller).setIdentificadorSecao(
                            emailAluno, Integer.parseInt(versao.get("versao"))
                        );
                    }
                });
        } else {
            Node node = containerApis;
            String semestreCurso = versao.get("semestre_curso");
            String ano = versao.get("ano");
            String semestreAno = versao.get("semestre_ano");
            String versaoNum = versao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versaoNum != null) {
                String anoExtraido = ano.split("-")[0];
                final int anoInt = Integer.parseInt(anoExtraido);
                final int versaoInt = Integer.parseInt(versaoNum);
                
                NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/aluno-visualizar-api.fxml",
                    controller -> {
                        if (controller instanceof AlunoVisualizarApiController) {
                            ((AlunoVisualizarApiController) controller).setIdentificadorSecao(
                                emailAluno, semestreCurso, anoInt, semestreAno, versaoInt
                            );
                        }
                    });
            }
        }
    }

    @FXML
    private void voltarTelaInicial(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/tela-inicial-aluno.fxml",
            controller -> {
                if (controller instanceof TelaInicialAlunoController) {
                    ((TelaInicialAlunoController) controller).recarregarSecoes();
                }
            });
    }
}
