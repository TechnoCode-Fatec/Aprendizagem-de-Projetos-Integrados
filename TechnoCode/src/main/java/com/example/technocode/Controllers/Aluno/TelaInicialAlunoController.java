package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.*;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        // Container principal do card
        VBox card = new VBox();
        card.setPrefHeight(80.0); // Aumentado para acomodar a linha do horário
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
        
        // Busca e adiciona informação do horário do feedback (se existir)
        String horarioFeedback = null;
        if ("apresentacao".equals(tipo)) {
            int versao = Integer.parseInt(secao.get("versao"));
            horarioFeedback = buscarHorarioFeedbackApresentacao(emailAluno, versao);
        } else {
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            int versao = Integer.parseInt(secao.get("versao"));
            horarioFeedback = buscarHorarioFeedbackApi(emailAluno, semestreCurso, ano, semestreAno, versao);
        }
        
        // Se existe feedback, adiciona label com o horário
        if (horarioFeedback != null) {
            Label labelHorario = new Label("Feedback recebido em: " + horarioFeedback);
            labelHorario.setStyle("-fx-font-size: 11px; -fx-text-fill: #3e3e3e; -fx-font-style: italic;");
            textBox.getChildren().add(labelHorario);
        }
        
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

        // Desativa por padrão
        btnFeedback.setDisable(true);

// Verifica se existe feedback para esta seção
        if ("apresentacao".equals(tipo)) {
            int versao = Integer.parseInt(secao.get("versao"));
            if (existeFeedbackApresentacao(emailAluno, versao)) { // retorna boolean
                btnFeedback.setDisable(false);
            }
        } else {
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            int versao = Integer.parseInt(secao.get("versao"));
            if (existeFeedbackApi(emailAluno, semestreCurso, ano, semestreAno, versao)) { // retorna boolean
                btnFeedback.setDisable(false);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-visualizar-secao-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(emailAluno, Integer.parseInt(secao.get("versao")));
            
            Stage stage = (Stage) containerApresentacoes.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-visualizar-secao-api-aluno.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/formulario-apresentacao.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/formulario-api.fxml"));
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
    
    private void verFeedbackApresentacao(ActionEvent event, Map<String, String> secao) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }

        int versao = Integer.parseInt(secao.get("versao"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-feedback-apresentacao-aluno.fxml"));
        Parent root = loader.load();

        TelaFeedbackApresentacaoAlunoController controller = loader.getController();
        controller.setIdentificadorSecao(emailAluno, versao);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    private void verFeedbackApi(ActionEvent event, Map<String, String> secao) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        String idSecao = secao.get("id");
        System.out.println("Abrindo feedback da seção: " + idSecao);
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }

        String semestreCurso = secao.get("semestre_curso");
        String ano = secao.get("ano");
        String semestreAno = secao.get("semestre_ano");
        String versao = secao.get("versao");

            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                String anoExtraido = ano.split("-")[0];
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-feedback-api-aluno.fxml"));
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

    public boolean existeFeedbackApi(String aluno, String semestreCurso, String ano, String semestreAno, int versao) {
        String sql = "SELECT 1 FROM feedback_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? LIMIT 1";
        try(Connection con = new Connector().getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, aluno);
            ps.setString(2, semestreCurso);
            ps.setString(3, ano);
            ps.setString(4, semestreAno);
            ps.setInt(5, versao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean existeFeedbackApresentacao(String aluno, int versao) {
        String sql = "SELECT 1 FROM feedback_apresentacao WHERE aluno = ? AND versao = ? LIMIT 1";
        try(Connection con = new Connector().getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, aluno);
            ps.setInt(2, versao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Busca o horário do feedback para uma seção de API
     * @return String formatada com data e hora, ou null se não existir feedback
     */
    private String buscarHorarioFeedbackApi(String aluno, String semestreCurso, String ano, String semestreAno, int versao) {
        String sql = "SELECT DATE_FORMAT(horario, '%d/%m/%Y às %H:%i') as horario_formatado " +
                     "FROM feedback_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? LIMIT 1";
        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, aluno);
            ps.setString(2, semestreCurso);
            ps.setString(3, ano);
            ps.setString(4, semestreAno);
            ps.setInt(5, versao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("horario_formatado");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca o horário do feedback para uma seção de apresentação
     * @return String formatada com data e hora, ou null se não existir feedback
     */
    private String buscarHorarioFeedbackApresentacao(String aluno, int versao) {
        String sql = "SELECT DATE_FORMAT(horario, '%d/%m/%Y às %H:%i') as horario_formatado " +
                     "FROM feedback_apresentacao WHERE aluno = ? AND versao = ? LIMIT 1";
        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, aluno);
            ps.setInt(2, versao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("horario_formatado");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
