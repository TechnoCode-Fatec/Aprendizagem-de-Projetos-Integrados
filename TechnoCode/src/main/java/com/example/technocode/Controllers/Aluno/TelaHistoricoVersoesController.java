package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TelaHistoricoVersoesController {

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
        
        Connector connector = new Connector();
        
        // Carrega histórico de apresentações
        List<Map<String, String>> historicoApresentacoes = connector.historicoVersoesApresentacao(emailAluno);
        exibirHistoricoApresentacoes(historicoApresentacoes);
        
        // Carrega histórico de APIs
        List<Map<String, String>> historicoApis = connector.historicoVersoesApi(emailAluno);
        exibirHistoricoApis(historicoApis);
    }
    
    private void exibirHistoricoApresentacoes(List<Map<String, String>> historico) {
        // Remove o label de título se existir
        if (containerApresentacoes.getChildren().size() > 1) {
            containerApresentacoes.getChildren().remove(1, containerApresentacoes.getChildren().size());
        }
        
        if (historico.isEmpty()) {
            Label semDados = new Label("Nenhuma apresentação cadastrada");
            semDados.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
            containerApresentacoes.getChildren().add(semDados);
        } else {
            for (Map<String, String> versao : historico) {
                VBox cardVersao = criarCardVersao(versao, "apresentacao");
                containerApresentacoes.getChildren().add(cardVersao);
            }
        }
    }
    
    private void exibirHistoricoApis(List<Map<String, String>> historico) {
        // Remove o label de título se existir
        if (containerApis.getChildren().size() > 1) {
            containerApis.getChildren().remove(1, containerApis.getChildren().size());
        }
        
        if (historico.isEmpty()) {
            Label semDados = new Label("Nenhuma API cadastrada");
            semDados.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
            containerApis.getChildren().add(semDados);
        } else {
            for (Map<String, String> versao : historico) {
                VBox cardVersao = criarCardVersao(versao, "api");
                containerApis.getChildren().add(cardVersao);
            }
        }
    }
    
    private VBox criarCardVersao(Map<String, String> versao, String tipo) {
        VBox card = new VBox();
        card.setPrefHeight(60.0);
        card.setPrefWidth(700.0);
        card.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 5; -fx-padding: 15; -fx-cursor: hand;");
        
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
            subtitulo.setText(empresa);
            versaoLabel.setText("Versão " + versao.get("versao"));
        }
        
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        versaoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888; -fx-font-weight: bold;");
        
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-visualizar-secao-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(emailAluno, Integer.parseInt(versao.get("versao")));
            
            Stage stage = (Stage) containerApresentacoes.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-visualizar-secao-api-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoApiAlunoController controller = loader.getController();
            
            String semestreCurso = versao.get("semestre_curso");
            String ano = versao.get("ano");
            String semestreAno = versao.get("semestre_ano");
            String versaoNum = versao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versaoNum != null) {
                // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                String anoExtraido = ano.split("-")[0];
                
                controller.setIdentificadorSecao(
                    emailAluno,  // email do aluno
                    semestreCurso,          // semestre_curso
                    Integer.parseInt(anoExtraido),   // ano extraído da data
                    semestreAno,            // semestre_ano
                    Integer.parseInt(versaoNum) // versao
                );
            }
            
            Stage stage = (Stage) containerApis.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void voltarTelaInicial(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-inicial-aluno.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela inicial: " + e.getMessage());
            throw e;
        }
    }
}
