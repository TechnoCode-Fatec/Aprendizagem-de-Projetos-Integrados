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

public class TelaFeedbackApiAlunoController {

    // Identificador da seção
    private String alunoId;
    private String semestreCursoId;
    private int anoId;
    private String semestreAnoId;
    private int versaoId;

    @FXML private TextArea feedbackProblema;
    @FXML private TextArea feedbackSolucao;
    @FXML private TextArea feedbackTecnologias;
    @FXML private TextArea feedbackContribuicoes;
    @FXML private TextArea feedbackHardSkills;
    @FXML private TextArea feedbackSoftSkills;

    @FXML private Label statusProblema;
    @FXML private Label statusSolucao;
    @FXML private Label statusTecnologias;
    @FXML private Label statusContribuicoes;
    @FXML private Label statusHardSkills;
    @FXML private Label statusSoftSkills;

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        this.alunoId = aluno;
        this.semestreCursoId = semestreCurso;
        this.anoId = ano;
        this.semestreAnoId = semestreAno;
        this.versaoId = versao;
        carregarFeedback();
    }

    // Carrega dados do feedback_api
    public void carregarFeedback() {
        if (alunoId == null) return;
        String sql = "SELECT status_problema, feedback_problema, " +
                "status_solucao, feedback_solucao, " +
                "status_tecnologias, feedback_tecnologias, " +
                "status_contribuicoes, feedback_contribuicoes, " +
                "status_hard_skills, feedback_hard_skills, " +
                "status_soft_skills, feedback_soft_skills " +
                "FROM feedback_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setString(2, semestreCursoId);
            pst.setInt(3, anoId);
            pst.setString(4, semestreAnoId);
            pst.setInt(5, versaoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    carregarCampoFeedback("problema", rs, feedbackProblema, statusProblema);
                    carregarCampoFeedback("solucao", rs, feedbackSolucao, statusSolucao);
                    carregarCampoFeedback("tecnologias", rs, feedbackTecnologias, statusTecnologias);
                    carregarCampoFeedback("contribuicoes", rs, feedbackContribuicoes, statusContribuicoes);
                    carregarCampoFeedback("hard_skills", rs, feedbackHardSkills, statusHardSkills);
                    carregarCampoFeedback("soft_skills", rs, feedbackSoftSkills, statusSoftSkills);
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
                textArea.setVisible(false);
                textArea.setManaged(false);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-visualizar-secao-api-aluno.fxml"));
            Parent root = loader.load();
            
            TelaVisualizarSecaoApiAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(alunoId, semestreCursoId, anoId, semestreAnoId, versaoId);
            
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
