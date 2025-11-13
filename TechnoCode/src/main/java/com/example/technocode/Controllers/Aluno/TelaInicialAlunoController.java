package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.*;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.SecaoApi;
import com.example.technocode.model.SecaoApresentacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TelaInicialAlunoController {
    
    @FXML
    private VBox containerApresentacoes;
    
    @FXML
    private VBox containerApis;
    
    @FXML
    private Button btnAdicionarApresentacao;
    
    @FXML
    private Button btnAdicionarApi;
    
    
    @FXML
    public void initialize() {
        carregarSecoesDoAluno();
    }

    String emailAluno = LoginController.getEmailLogado();
    
    private void carregarSecoesDoAluno() {
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }
        
        // Carrega seções de apresentação
        List<Map<String, String>> secoesApresentacao = SecaoApresentacao.buscarSecoesPorAluno(emailAluno);
        exibirSecoesApresentacao(secoesApresentacao);
        
        // Carrega seções de API
        List<Map<String, String>> secoesApi = SecaoApi.buscarSecoesPorAluno(emailAluno);
        exibirSecoesApi(secoesApi);
    }
    
    private void exibirSecoesApresentacao(List<Map<String, String>> secoes) {
        containerApresentacoes.getChildren().clear();
        
        if (secoes.isEmpty()) {
            btnAdicionarApresentacao.setVisible(true);
        } else {
            btnAdicionarApresentacao.setVisible(false);
            btnAdicionarApresentacao.setManaged(false);
            
            for (Map<String, String> secao : secoes) {
                VBox secaoCard = criarCardSecao(secao, "apresentacao");
                containerApresentacoes.getChildren().add(secaoCard);
            }
        }
    }
    
    private void exibirSecoesApi(List<Map<String, String>> secoes) {
        containerApis.getChildren().clear();
        
        if (secoes.isEmpty()) {
            btnAdicionarApi.setVisible(true);
        } else {
            btnAdicionarApi.setVisible(false);
            btnAdicionarApi.setManaged(false);
            
            for (Map<String, String> secao : secoes) {
                VBox secaoCard = criarCardSecao(secao, "api");
                containerApis.getChildren().add(secaoCard);
            }
        }
    }
    
    private VBox criarCardSecao(Map<String, String> secao, String tipo) {
        // Container principal do card moderno
        VBox card = new VBox();
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
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
        
        // VBox para título e subtítulo (lado esquerdo)
        VBox textBox = new VBox();
        textBox.setSpacing(4);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);
        
        Label titulo = new Label();
        Label subtitulo = new Label();
        
        if ("apresentacao".equals(tipo)) {
            titulo.setText(secao.get("id")); // Nome da apresentação
            subtitulo.setText("Apresentação - Versão " + secao.get("versao"));
        } else {
            titulo.setText(secao.get("id")); // Semestre + Ano/Semestre
            subtitulo.setText(secao.get("empresa"));
        }
        
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D;");
        
        textBox.getChildren().addAll(titulo, subtitulo);
        
        // Busca e adiciona informação do horário do feedback (se existir)
        String horarioFeedback = null;
        if ("apresentacao".equals(tipo)) {
            int versao = Integer.parseInt(secao.get("versao"));
            horarioFeedback = SecaoApresentacao.buscarHorarioFeedback(emailAluno, versao);
        } else {
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            int versao = Integer.parseInt(secao.get("versao"));
            // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
            String anoExtraido = ano != null ? ano.split("-")[0] : ano;
            horarioFeedback = SecaoApi.buscarHorarioFeedback(emailAluno, semestreCurso, anoExtraido, semestreAno, versao);
        }
        
        // Se existe feedback, adiciona label com o horário
        if (horarioFeedback != null) {
            Label labelHorario = new Label("✓ Feedback recebido em: " + horarioFeedback);
            labelHorario.setStyle("-fx-font-size: 11px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
            textBox.getChildren().add(labelHorario);
        }
        
        // Botão de feedback (lado direito)
        Button btnFeedback = new Button();
        btnFeedback.setPrefHeight(36.0);
        btnFeedback.setPrefWidth(120.0);
        btnFeedback.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; " +
                            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);");
        btnFeedback.setText("Ver Feedback");
        btnFeedback.setFont(new javafx.scene.text.Font(12.0));

        // Desativa por padrão
        btnFeedback.setDisable(true);
        btnFeedback.setStyle("-fx-background-color: #BDC3C7; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: default;");

        // Verifica se existe feedback para esta seção
        if ("apresentacao".equals(tipo)) {
            int versao = Integer.parseInt(secao.get("versao"));
            if (SecaoApresentacao.verificarFeedback(emailAluno, versao)) {
                btnFeedback.setDisable(false);
                btnFeedback.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; " +
                                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);");
            }
        } else {
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            int versao = Integer.parseInt(secao.get("versao"));
            // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
            String anoExtraido = ano.split("-")[0];
            if (SecaoApi.verificarFeedback(emailAluno, semestreCurso, Integer.parseInt(anoExtraido), semestreAno, versao)) {
                btnFeedback.setDisable(false);
                btnFeedback.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; " +
                                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);");
            }
        }

        // Evento do botão de feedback
        btnFeedback.setOnAction(event -> {
            try {
                if ("apresentacao".equals(tipo)) {
                    verFeedbackApresentacao(event, secao);
                } else {
                    verFeedbackApi(event, secao);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        // Adiciona componentes ao HBox
        contentBox.getChildren().addAll(textBox, btnFeedback);
        
        // Adiciona o HBox ao card
        card.getChildren().add(contentBox);
        
        // Adiciona evento de clique no card (exceto no botão)
        card.setOnMouseClicked(event -> {
            // Verifica se o clique não foi no botão
            if (!btnFeedback.getBoundsInParent().contains(event.getX(), event.getY())) {
                try {
                    abrirVisualizacaoSecao(secao, tipo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        return card;
    }

    private void abrirVisualizacaoSecao(Map<String, String> secao, String tipo) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        
        if ("apresentacao".equals(tipo)) {
            Node node = containerApresentacoes;
            NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/aluno-visualizar-apresentacao.fxml",
                controller -> {
                    if (controller instanceof AlunoVisualizarApresentacaoController) {
                        ((AlunoVisualizarApresentacaoController) controller).setIdentificadorSecao(
                            emailAluno, Integer.parseInt(secao.get("versao"))
                        );
                    }
                });
        } else {
            Node node = containerApis;
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            String versao = secao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                String anoExtraido = ano.split("-")[0];
                final int anoInt = Integer.parseInt(anoExtraido);
                final int versaoInt = Integer.parseInt(versao);
                
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
    private void voltarLogin(ActionEvent event) throws IOException {
        NavigationService.navegarPara(event, "/com/example/technocode/login.fxml");
    }

    @FXML
    private void adicionarApresentacao(ActionEvent event) throws IOException {
        Node node = (event != null && event.getSource() != null) 
            ? (Node) event.getSource() 
            : btnAdicionarApresentacao;
        NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/formulario-apresentacao.fxml");
    }

    @FXML
    private void adicionarApi(ActionEvent event) throws IOException {
        Node node = (event != null && event.getSource() != null) 
            ? (Node) event.getSource() 
            : btnAdicionarApi;
        NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/formulario-api.fxml");
    }
    
    // Método público para ser chamado quando retornar dos formulários
    public void recarregarSecoes() {
        carregarSecoesDoAluno();
    }
    
    private void verFeedbackApresentacao(ActionEvent event, Map<String, String> secao) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }

        int versao = Integer.parseInt(secao.get("versao"));
        final int versaoFinal = versao;

        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-feedback-apresentacao.fxml",
            controller -> {
                if (controller instanceof AlunoFeedbackApresentacaoController) {
                    ((AlunoFeedbackApresentacaoController) controller).setIdentificadorSecao(emailAluno, versaoFinal);
                }
            });
    }

    private void verFeedbackApi(ActionEvent event, Map<String, String> secao) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }

        String semestreCurso = secao.get("semestre_curso");
        String ano = secao.get("ano");
        String semestreAno = secao.get("semestre_ano");
        String versao = secao.get("versao");

        if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
            String anoExtraido = ano.split("-")[0];
            final int anoInt = Integer.parseInt(anoExtraido);
            final int versaoInt = Integer.parseInt(versao);
            
            NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-feedback-api.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApiController) {
                        ((AlunoFeedbackApiController) controller).setIdentificadorSecao(
                            emailAluno, semestreCurso, anoInt, semestreAno, versaoInt
                        );
                    }
                });
        }
    }

    @FXML
    private void mostrarOpcoesNovaSessao(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nova Sessão");
        alert.setHeaderText("Escolha o tipo de sessão");
        alert.setContentText("Que tipo de sessão você deseja criar?");
        
        ButtonType btnApresentacao = new ButtonType("Apresentação");
        ButtonType btnApi = new ButtonType("API");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnApresentacao, btnApi, btnCancelar);
        
        alert.showAndWait().ifPresent(buttonType -> {
            try {
                if (buttonType == btnApresentacao) {
                    adicionarApresentacao(null);
                } else if (buttonType == btnApi) {
                    adicionarApi(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
