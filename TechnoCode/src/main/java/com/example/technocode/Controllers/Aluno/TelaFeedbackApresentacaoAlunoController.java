package com.example.technocode.Controllers.Aluno;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaFeedbackApresentacaoAlunoController {

    // Identificador da seção
    private String alunoId;
    private int versaoId;

    @FXML private TextArea feedbackNome;
    @FXML private TextArea feedbackIdade;
    @FXML private TextArea feedbackCurso;
    @FXML private TextArea feedbackMotivacao;
    @FXML private TextArea feedbackHistorico;
    @FXML private TextArea feedbackGithub;
    @FXML private TextArea feedbackLinkedin;
    @FXML private TextArea feedbackConhecimentos;

    @FXML private Label statusNome;
    @FXML private Label statusIdade;
    @FXML private Label statusCurso;
    @FXML private Label statusMotivacao;
    @FXML private Label statusHistorico;
    @FXML private Label statusGithub;
    @FXML private Label statusLinkedin;
    @FXML private Label statusConhecimentos;

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        this.alunoId = aluno;
        this.versaoId = versao;
        carregarFeedback();
    }

    // Carrega dados do feedback_apresentacao
    public void carregarFeedback() {
        if (alunoId == null) return;
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
                "status_github, feedback_github, " +
                "status_linkedin, feedback_linkedin, " +
                "status_conhecimentos, feedback_conhecimentos " +
                "FROM feedback_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setInt(2, versaoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    carregarCampoFeedback("nome", rs, feedbackNome, statusNome);
                    carregarCampoFeedback("idade", rs, feedbackIdade, statusIdade);
                    carregarCampoFeedback("curso", rs, feedbackCurso, statusCurso);
                    carregarCampoFeedback("motivacao", rs, feedbackMotivacao, statusMotivacao);
                    carregarCampoFeedback("historico", rs, feedbackHistorico, statusHistorico);
                    carregarCampoFeedback("github", rs, feedbackGithub, statusGithub);
                    carregarCampoFeedback("linkedin", rs, feedbackLinkedin, statusLinkedin);
                    carregarCampoFeedback("conhecimentos", rs, feedbackConhecimentos, statusConhecimentos);
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar feedback", e);
        }
    }

    private void carregarCampoFeedback(String campo, ResultSet rs, TextArea textArea, Label statusLabel) throws SQLException {
        String status = rs.getString("status_" + campo);
        String feedback = rs.getString("feedback_" + campo);
        
        if (status != null) {
            statusLabel.setText(status);
            if ("Aprovado".equals(status)) {
                statusLabel.setStyle("-fx-text-fill: #00AA00; -fx-font-weight: bold;");
            } else if ("Revisar".equals(status)) {
                statusLabel.setStyle("-fx-text-fill: #AA0000; -fx-font-weight: bold;");
            }
        } else {
            statusLabel.setText("Sem avaliação");
            statusLabel.setStyle("-fx-text-fill: #666666; -fx-font-weight: bold;");
        }
        
        if (feedback != null && !feedback.trim().isEmpty()) {
            textArea.setText(feedback);
        } else {
            textArea.setText("Nenhum feedback disponível para este campo.");
            textArea.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
        }
    }

    @FXML
    private void voltarTelaSecao(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-visualizar-secao-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(alunoId, versaoId);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela de seção: " + e.getMessage());
            throw e;
        }
    }

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }
}
