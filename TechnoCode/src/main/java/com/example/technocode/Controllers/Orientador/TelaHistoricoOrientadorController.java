package com.example.technocode.Controllers.Orientador;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

public class TelaHistoricoOrientadorController {

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
        
        Connector connector = new Connector();
        
        // Carrega histórico de apresentações
        List<Map<String, String>> historicoApresentacoes = connector.historicoVersoesApresentacao(emailAlunoSelecionado);
        exibirHistoricoApresentacoes(historicoApresentacoes);
        
        // Carrega histórico de APIs
        List<Map<String, String>> historicoApis = connector.historicoVersoesApi(emailAlunoSelecionado);
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
            horarioFeedback = buscarHorarioFeedbackApresentacao(emailAlunoSelecionado, versaoNum);
        } else {
            String semestreCurso = versao.get("semestre_curso");
            String ano = versao.get("ano");
            String semestreAno = versao.get("semestre_ano");
            int versaoNum = Integer.parseInt(versao.get("versao"));
            horarioFeedback = buscarHorarioFeedbackApi(emailAlunoSelecionado, semestreCurso, ano, semestreAno, versaoNum);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-secoesenviadas.fxml"));
            Parent root = loader.load();
            
            TelaSecoesenviadasController controller = loader.getController();
            controller.setIdentificadorSecao(emailAlunoSelecionado, Integer.parseInt(versao.get("versao")));
            
            Stage stage = (Stage) containerApresentacoes.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-secoesenviadasAPI.fxml"));
            Parent root = loader.load();
            
            TelaSecoesenviadasAPIController controller = loader.getController();
            
            String semestreCurso = versao.get("semestre_curso");
            String ano = versao.get("ano");
            String semestreAno = versao.get("semestre_ano");
            String versaoNum = versao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versaoNum != null) {
                // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                String anoExtraido = ano.split("-")[0];
                
                controller.setIdentificadorSecao(
                    emailAlunoSelecionado,
                    semestreCurso,
                    Integer.parseInt(anoExtraido),
                    semestreAno,
                    Integer.parseInt(versaoNum)
                );
            }
            
            Stage stage = (Stage) containerApis.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void voltarTelaEntregas(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-entregasDoAluno.fxml"));
            Parent root = loader.load();
            
            // Obtém o controller e recarrega os dados do aluno
            TelaEntregasDoAluno controller = loader.getController();
            controller.setEmailAlunoParaConsulta(emailAlunoSelecionado);
            controller.setDadosAluno(emailAlunoSelecionado);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela de entregas: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca o horário do feedback para uma seção de API
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

