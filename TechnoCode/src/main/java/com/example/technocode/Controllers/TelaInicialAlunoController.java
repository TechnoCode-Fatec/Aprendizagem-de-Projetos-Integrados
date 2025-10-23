package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    
    private void carregarSecoesDoAluno() {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }
        
        Connector connector = new Connector();
        
        // Carrega seções de apresentação
        List<Map<String, String>> secoesApresentacao = connector.secoesApresentacao(emailAluno);
        exibirSecoesApresentacao(secoesApresentacao);
        
        // Carrega seções de API
        List<Map<String, String>> secoesApi = connector.secoesApi(emailAluno);
        exibirSecoesApi(secoesApi);
    }
    
    private void exibirSecoesApresentacao(List<Map<String, String>> secoes) {
        containerApresentacoes.getChildren().clear();
        
        if (secoes.isEmpty()) {
            btnAdicionarApresentacao.setVisible(true);
        } else {
            btnAdicionarApresentacao.setVisible(false);
            
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
            
            for (Map<String, String> secao : secoes) {
                VBox secaoCard = criarCardSecao(secao, "api");
                containerApis.getChildren().add(secaoCard);
            }
        }
    }
    
    private VBox criarCardSecao(Map<String, String> secao, String tipo) {
        // Container principal do card
        VBox card = new VBox();
        card.setPrefHeight(60.0);
        card.setPrefWidth(600.0);
        card.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 5; -fx-padding: 10; -fx-cursor: hand;");
        
        // HBox para organizar conteúdo horizontalmente
        HBox contentBox = new HBox();
        contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contentBox.setSpacing(10);
        
        // VBox para título e subtítulo (lado esquerdo)
        VBox textBox = new VBox();
        textBox.setSpacing(2);
        
        Label titulo = new Label();
        Label subtitulo = new Label();
        
        if ("apresentacao".equals(tipo)) {
            titulo.setText(secao.get("id")); // Nome da apresentação
            subtitulo.setText("Apresentação - Versão " + secao.get("versao"));
        } else {
            titulo.setText(secao.get("id")); // Semestre + Ano/Semestre
            subtitulo.setText(secao.get("empresa"));
        }
        
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        textBox.getChildren().addAll(titulo, subtitulo);
        
        // Region para empurrar o botão para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Botão de feedback (lado direito)
        Button btnFeedback = new Button();
        btnFeedback.setPrefHeight(30.0);
        btnFeedback.setPrefWidth(100.0);
        btnFeedback.setStyle("-fx-background-color: #5E5555; -fx-text-fill: white; -fx-background-radius: 5;");
        btnFeedback.setText("Ver Feedback");
        btnFeedback.setFont(new javafx.scene.text.Font(12.0));
        
        // Evento do botão de feedback
        btnFeedback.setOnAction(event -> {
            try {
                if ("apresentacao".equals(tipo)) {
                    verFeedbackApresentacao(event);
                } else {
                    verFeedbackApi(event);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        // Adiciona componentes ao HBox
        contentBox.getChildren().addAll(textBox, spacer, btnFeedback);
        
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-visualizar-secao-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(emailAluno, Integer.parseInt(secao.get("versao")));
            
            Stage stage = (Stage) containerApresentacoes.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-visualizar-secao-api-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoApiAlunoController controller = loader.getController();
            
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            String versao = secao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                String anoExtraido = ano.split("-")[0];
                
                controller.setIdentificadorSecao(
                    emailAluno,  // email do aluno
                    semestreCurso,          // semestre_curso
                    Integer.parseInt(anoExtraido),   // ano extraído da data
                    semestreAno,            // semestre_ano
                    Integer.parseInt(versao) // versao
                );
            }
            
            Stage stage = (Stage) containerApis.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    
    @FXML
    private void voltarLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void adicionarApresentacao(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/formulario-apresentacao.fxml"));
        Parent root = loader.load();
        
        Stage stage;
        if (event != null && event.getSource() != null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            // Se chamado programaticamente, pega a janela atual
            stage = (Stage) btnAdicionarApresentacao.getScene().getWindow();
        }
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void adicionarApi(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/formulario-api.fxml"));
        Parent root = loader.load();
        
        Stage stage;
        if (event != null && event.getSource() != null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            // Se chamado programaticamente, pega a janela atual
            stage = (Stage) btnAdicionarApi.getScene().getWindow();
        }
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    // Método público para ser chamado quando retornar dos formulários
    public void recarregarSecoes() {
        carregarSecoesDoAluno();
    }
    
    private void verFeedbackApresentacao(ActionEvent event) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }
        
        Connector connector = new Connector();
        List<Map<String, String>> secoes = connector.secoesApresentacao(emailAluno);
        
        if (!secoes.isEmpty()) {
            // Pega a primeira seção (versão mais recente)
            Map<String, String> secao = secoes.get(0);
            int versao = Integer.parseInt(secao.get("versao"));
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-feedback-apresentacao-aluno.fxml"));
            Parent root = loader.load();
            
            TelaFeedbackApresentacaoAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(emailAluno, versao);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    private void verFeedbackApi(ActionEvent event) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }
        
        Connector connector = new Connector();
        List<Map<String, String>> secoes = connector.secoesApi(emailAluno);
        
        if (!secoes.isEmpty()) {
            // Pega a primeira seção (versão mais recente)
            Map<String, String> secao = secoes.get(0);
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            String versao = secao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                String anoExtraido = ano.split("-")[0];
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-feedback-api-aluno.fxml"));
                Parent root = loader.load();
                
                TelaFeedbackApiAlunoController controller = loader.getController();
                controller.setIdentificadorSecao(
                    emailAluno,  // email do aluno
                    semestreCurso,          // semestre_curso
                    Integer.parseInt(anoExtraido),   // ano extraído da data
                    semestreAno,            // semestre_ano
                    Integer.parseInt(versao) // versao
                );
                
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
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
