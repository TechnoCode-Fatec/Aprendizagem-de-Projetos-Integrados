package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlunoFeedbackApiController {

    // Identificador da seção usando classe modelo
    private SecaoApi secaoApi;

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
        // Cria objeto SecaoApi para identificar a seção
        this.secaoApi = new SecaoApi(aluno, semestreCurso, ano, semestreAno, versao);
        carregarFeedback();
    }

    // Carrega dados do feedback da secao_api
    public void carregarFeedback() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        String sql = "SELECT status_problema, feedback_problema, " +
                "status_solucao, feedback_solucao, " +
                "status_tecnologias, feedback_tecnologias, " +
                "status_contribuicoes, feedback_contribuicoes, " +
                "status_hard_skills, feedback_hard_skills, " +
                "status_soft_skills, feedback_soft_skills " +
                "FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApi.getEmailAluno());
            pst.setString(2, secaoApi.getSemestreCurso());
            pst.setInt(3, secaoApi.getAno());
            pst.setString(4, secaoApi.getSemestreAno());
            pst.setInt(5, secaoApi.getVersao());
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
                // Badge verde moderno
                statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold; " +
                        "-fx-background-color: #D5F4E6; -fx-background-radius: 12; " +
                        "-fx-padding: 4 12 4 12; -fx-border-color: #27AE60; -fx-border-width: 1; -fx-border-radius: 12;");
                textArea.setVisible(false);
                textArea.setManaged(false);
            } else if ("Revisar".equals(status)) {
                // Badge vermelho moderno
                statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; " +
                        "-fx-background-color: #FADBD8; -fx-background-radius: 12; " +
                        "-fx-padding: 4 12 4 12; -fx-border-color: #E74C3C; -fx-border-width: 1; -fx-border-radius: 12;");
            }
        } else {
            statusLabel.setText("Sem avaliação");
            // Badge cinza moderno
            statusLabel.setStyle("-fx-text-fill: #95A5A6; -fx-font-weight: bold; " +
                    "-fx-background-color: #ECF0F1; -fx-background-radius: 12; " +
                    "-fx-padding: 4 12 4 12; -fx-border-color: #95A5A6; -fx-border-width: 1; -fx-border-radius: 12;");
        }
        
        if (feedback != null && !feedback.trim().isEmpty()) {
            textArea.setText(feedback);
            textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; " +
                    "-fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        } else {
            textArea.setText("Nenhum feedback disponível para este campo.");
            textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; " +
                    "-fx-wrap-text: true; -fx-text-fill: #95A5A6; -fx-font-style: italic;");
        }
    }

    @FXML
    private void visualizarSecao(ActionEvent event) throws IOException {
        if (secaoApi != null) {
            final String emailAluno = secaoApi.getEmailAluno();
            final String semestreCurso = secaoApi.getSemestreCurso();
            final int ano = secaoApi.getAno();
            final String semestreAno = secaoApi.getSemestreAno();
            final int versao = secaoApi.getVersao();
            
            NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-visualizar-api.fxml",
                controller -> {
                    if (controller instanceof AlunoVisualizarApiController) {
                        ((AlunoVisualizarApiController) controller).setIdentificadorSecao(
                            emailAluno, semestreCurso, ano, semestreAno, versao);
                    }
                });
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

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }
}
