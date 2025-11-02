package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.SecaoApi;
import com.example.technocode.model.SecaoApresentacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OrientadorHistoricoController {

    @FXML
    private VBox containerApresentacoes;
    
    @FXML
    private VBox containerApis;
    
    // Email do aluno que está sendo visualizado
    private String emailAlunoSelecionado;
    
    @FXML
    public void initialize() {
        // Aguarda o email ser setado
    }
    
    /**
     * Define o email do aluno e carrega seu histórico
     */
    public void setEmailAluno(String emailAluno) {
        this.emailAlunoSelecionado = emailAluno;
        carregarHistorico();
    }
    
    private void carregarHistorico() {
        if (emailAlunoSelecionado == null || emailAlunoSelecionado.isBlank()) {
            return;
        }
        
        // Carrega histórico de apresentações
        List<Map<String, String>> historicoApresentacoes = SecaoApresentacao.buscarHistoricoVersoes(emailAlunoSelecionado);
        exibirHistoricoApresentacoes(historicoApresentacoes);
        
        // Carrega histórico de APIs
        List<Map<String, String>> historicoApis = SecaoApi.buscarHistoricoVersoes(emailAlunoSelecionado);
        exibirHistoricoApis(historicoApis);
    }
    
    private void exibirHistoricoApresentacoes(List<Map<String, String>> historico) {
        // Remove itens anteriores, mantendo apenas o título
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
        // Remove itens anteriores, mantendo apenas o título
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
        card.setPrefHeight(80.0);
        card.setPrefWidth(700.0);
        card.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 5; -fx-padding: 10; -fx-cursor: hand;");
        
        // HBox para organizar conteúdo horizontalmente
        HBox contentBox = new HBox();
        contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contentBox.setSpacing(10);
        
        // VBox para título, subtítulo e versão (lado esquerdo)
        VBox textBox = new VBox();
        textBox.setSpacing(2);
        
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
        
        textBox.getChildren().addAll(titulo, subtitulo, versaoLabel);
        
        // Busca e adiciona informação do horário do feedback (se existir)
        String horarioFeedback = null;
        if ("apresentacao".equals(tipo)) {
            int versaoNum = Integer.parseInt(versao.get("versao"));
            horarioFeedback = SecaoApresentacao.buscarHorarioFeedback(emailAlunoSelecionado, versaoNum);
        } else {
            String semestreCurso = versao.get("semestre_curso");
            String ano = versao.get("ano");
            String semestreAno = versao.get("semestre_ano");
            int versaoNum = Integer.parseInt(versao.get("versao"));
            // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
            String anoExtraido = ano != null ? ano.split("-")[0] : ano;
            horarioFeedback = SecaoApi.buscarHorarioFeedback(emailAlunoSelecionado, semestreCurso, anoExtraido, semestreAno, versaoNum);
        }
        
        // Se existe feedback, adiciona label com o horário
        if (horarioFeedback != null) {
            Label labelHorario = new Label("Feedback enviado em: " + horarioFeedback);
            labelHorario.setStyle("-fx-font-size: 11px; -fx-text-fill: #2E7D32; -fx-font-style: italic;");
            textBox.getChildren().add(labelHorario);
        }
        
        // Region para empurrar o botão para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botão de visualizar (lado direito)
        Button btnVisualizar = new Button("Visualizar");
        btnVisualizar.setPrefHeight(30.0);
        btnVisualizar.setPrefWidth(100.0);
        btnVisualizar.setStyle("-fx-background-color: #5E5555; -fx-text-fill: white; -fx-background-radius: 5;");
        btnVisualizar.setFont(new javafx.scene.text.Font(12.0));
        
        // Evento do botão de visualizar
        btnVisualizar.setOnAction(event -> {
            try {
                abrirVisualizacaoVersao(versao, tipo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        // Adiciona componentes ao HBox
        contentBox.getChildren().addAll(textBox, spacer, btnVisualizar);
        
        // Adiciona o HBox ao card
        card.getChildren().add(contentBox);
        
        // Adiciona evento de clique no card (exceto no botão)
        card.setOnMouseClicked(event -> {
            if (!btnVisualizar.getBoundsInParent().contains(event.getX(), event.getY())) {
                try {
                    abrirVisualizacaoVersao(versao, tipo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        return card;
    }
    
    private void abrirVisualizacaoVersao(Map<String, String> versao, String tipo) throws IOException {
        if ("apresentacao".equals(tipo)) {
            Node node = containerApresentacoes;
            final int versaoFinal = Integer.parseInt(versao.get("versao"));
            NavigationService.navegarPara(node, "/com/example/technocode/Orientador/orientador-corrigir-apresentacao.fxml",
                controller -> {
                    if (controller instanceof OrientadorCorrigirApresentacaoController) {
                        ((OrientadorCorrigirApresentacaoController) controller).setIdentificadorSecao(
                            emailAlunoSelecionado, versaoFinal);
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
                final int anoFinal = Integer.parseInt(anoExtraido);
                final int versaoFinal = Integer.parseInt(versaoNum);
                
                NavigationService.navegarPara(node, "/com/example/technocode/Orientador/orientador-corrigir-api.fxml",
                    controller -> {
                        if (controller instanceof OrientadorCorrigirApiController) {
                            ((OrientadorCorrigirApiController) controller).setIdentificadorSecao(
                                emailAlunoSelecionado, semestreCurso, anoFinal, semestreAno, versaoFinal);
                        }
                    });
            }
        }
    }

    @FXML
    private void voltarTelaEntregas(ActionEvent event) throws IOException {
        final String emailFinal = emailAlunoSelecionado;
        NavigationService.navegarPara(event, "/com/example/technocode/Orientador/entregas-do-aluno.fxml",
            controller -> {
                if (controller instanceof EntregasDoAlunoController) {
                    EntregasDoAlunoController entregasController = (EntregasDoAlunoController) controller;
                    entregasController.setEmailAlunoParaConsulta(emailFinal);
                    entregasController.setDadosAluno(emailFinal);
                }
            });
    }

}

