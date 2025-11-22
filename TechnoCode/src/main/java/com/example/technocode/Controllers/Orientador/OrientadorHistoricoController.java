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
        
        // HBox para organizar conteúdo horizontalmente
        HBox contentBox = new HBox();
        contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contentBox.setSpacing(15);
        
        // VBox para título, subtítulo e versão (lado esquerdo)
        VBox textBox = new VBox();
        textBox.setSpacing(6);
        VBox.setVgrow(textBox, Priority.ALWAYS);
        
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
        
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");
        versaoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #586069; -fx-font-weight: bold;");
        
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
            Label labelHorario = new Label("✓ Feedback enviado em: " + horarioFeedback);
            labelHorario.setStyle("-fx-font-size: 11px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
            textBox.getChildren().add(labelHorario);
        }
        
        // Region para empurrar o botão para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botão de visualizar (lado direito) - modernizado
        Button btnVisualizar = new Button("Visualizar");
        btnVisualizar.setPrefHeight(36.0);
        btnVisualizar.setPrefWidth(120.0);
        btnVisualizar.setStyle("-fx-background-color: #B82E1A; -fx-background-radius: 8; -fx-cursor: hand; " +
                               "-fx-effect: dropshadow(gaussian, rgba(184,46,26,0.3), 5, 0, 0, 2); " +
                               "-fx-text-fill: WHITE;");
        btnVisualizar.setFont(new javafx.scene.text.Font("System Bold", 12.0));
        
        // Hover effect no botão
        btnVisualizar.setOnMouseEntered(e -> btnVisualizar.setStyle("-fx-background-color: #A0261A; -fx-background-radius: 8; -fx-cursor: hand; " +
                                                                   "-fx-effect: dropshadow(gaussian, rgba(184,46,26,0.4), 6, 0, 0, 3); " +
                                                                   "-fx-text-fill: WHITE;"));
        btnVisualizar.setOnMouseExited(e -> btnVisualizar.setStyle("-fx-background-color: #B82E1A; -fx-background-radius: 8; -fx-cursor: hand; " +
                                                                    "-fx-effect: dropshadow(gaussian, rgba(184,46,26,0.3), 5, 0, 0, 2); " +
                                                                    "-fx-text-fill: WHITE;"));
        
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

