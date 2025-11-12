package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Services.NavigationService;

import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApi;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class OrientadorCorrigirApiController {

    // Identificador da seção usando classe modelo
    private SecaoApi secaoApi;

    // Status por campo (Aprovado | Revisar | null)
    private final Map<String, String> statusPorCampo = new HashMap<>();

    // TextAreas de conteúdo do aluno (coluna esquerda)
    @FXML private TextArea alunoProblema;
    @FXML private TextArea alunoSolucao;
    @FXML private TextArea alunoTecnologias;
    @FXML private TextArea alunoContribuicoes;
    @FXML private TextArea alunoHardSkills;
    @FXML private TextArea alunoSoftSkills;

    // TextAreas de feedback (coluna direita)
    @FXML private TextArea feedbackProblema;
    @FXML private TextArea feedbackSolucao;
    @FXML private TextArea feedbackTecnologias;
    @FXML private TextArea feedbackContribuicoes;
    @FXML private TextArea feedbackHardSkills;
    @FXML private TextArea feedbackSoftSkills;

    @FXML
    public void initialize() {
        // Todos feedbacks começam ocultos
        if (feedbackProblema != null) feedbackProblema.setVisible(false);
        if (feedbackSolucao != null) feedbackSolucao.setVisible(false);
        if (feedbackTecnologias != null) feedbackTecnologias.setVisible(false);
        if (feedbackContribuicoes != null) feedbackContribuicoes.setVisible(false);
        if (feedbackHardSkills != null) feedbackHardSkills.setVisible(false);
        if (feedbackSoftSkills != null) feedbackSoftSkills.setVisible(false);
    }

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        // Cria objeto SecaoApi para identificar a seção
        this.secaoApi = new SecaoApi(aluno, semestreCurso, ano, semestreAno, versao);
        carregarSecaoAluno();
    }

    // 1) Carrega dados da secao_api
    public void carregarSecaoAluno() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        String sql = "SELECT problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
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
                    if (alunoProblema != null) alunoProblema.setText(rs.getString("problema"));
                    if (alunoSolucao != null) alunoSolucao.setText(rs.getString("solucao"));
                    if (alunoTecnologias != null) alunoTecnologias.setText(rs.getString("tecnologias"));
                    if (alunoContribuicoes != null) alunoContribuicoes.setText(rs.getString("contribuicoes"));
                    if (alunoHardSkills != null) alunoHardSkills.setText(rs.getString("hard_skills"));
                    if (alunoSoftSkills != null) alunoSoftSkills.setText(rs.getString("soft_skills"));
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar seção do aluno", e);
        }
        
        // Carrega feedback existente se houver
        carregarFeedbackExistente();
    }
    
    // Carrega feedback existente do orientador
    private void carregarFeedbackExistente() {
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
                    carregarCampoFeedbackExistente("problema", rs, feedbackProblema);
                    carregarCampoFeedbackExistente("solucao", rs, feedbackSolucao);
                    carregarCampoFeedbackExistente("tecnologias", rs, feedbackTecnologias);
                    carregarCampoFeedbackExistente("contribuicoes", rs, feedbackContribuicoes);
                    carregarCampoFeedbackExistente("hard_skills", rs, feedbackHardSkills);
                    carregarCampoFeedbackExistente("soft_skills", rs, feedbackSoftSkills);
                }
            }
        } catch (SQLException e) {
            // Não é erro crítico se não houver feedback existente
        }
    }
    
    private void carregarCampoFeedbackExistente(String campo, ResultSet rs, TextArea feedbackArea) throws SQLException {
        String status = rs.getString("status_" + campo);
        String feedback = rs.getString("feedback_" + campo);
        
        if (status != null) {
            statusPorCampo.put(campo, status);
            
            if ("Revisar".equals(status) && feedback != null && !feedback.trim().isEmpty()) {
                // Se foi marcado para revisar e tem feedback, mostra o campo
                if (feedbackArea != null) {
                    feedbackArea.setVisible(true);
                    feedbackArea.setText(feedback);
                    feedbackArea.setPrefHeight(100);
                    feedbackArea.setWrapText(true);
                }
            }
            
            // Atualiza as cores dos botões baseado no status carregado
            // Usa Platform.runLater para garantir que a cena esteja carregada
            Platform.runLater(() -> atualizarCorBotoes(campo, status));
        }
    }

    // 2) Aprovar/Revisar por campo (handlers dos botões)
    @FXML private void aprovarProblema(ActionEvent e) { aprovarCampo("problema", feedbackProblema); }
    @FXML private void revisarProblema(ActionEvent e) { revisarCampo("problema", feedbackProblema); }
    @FXML private void aprovarSolucao(ActionEvent e) { aprovarCampo("solucao", feedbackSolucao); }
    @FXML private void revisarSolucao(ActionEvent e) { revisarCampo("solucao", feedbackSolucao); }
    @FXML private void aprovarTecnologias(ActionEvent e) { aprovarCampo("tecnologias", feedbackTecnologias); }
    @FXML private void revisarTecnologias(ActionEvent e) { revisarCampo("tecnologias", feedbackTecnologias); }
    @FXML private void aprovarContribuicoes(ActionEvent e) { aprovarCampo("contribuicoes", feedbackContribuicoes); }
    @FXML private void revisarContribuicoes(ActionEvent e) { revisarCampo("contribuicoes", feedbackContribuicoes); }
    @FXML private void aprovarHardSkills(ActionEvent e) { aprovarCampo("hard_skills", feedbackHardSkills); }
    @FXML private void revisarHardSkills(ActionEvent e) { revisarCampo("hard_skills", feedbackHardSkills); }
    @FXML private void aprovarSoftSkills(ActionEvent e) { aprovarCampo("soft_skills", feedbackSoftSkills); }
    @FXML private void revisarSoftSkills(ActionEvent e) { revisarCampo("soft_skills", feedbackSoftSkills); }

    public void aprovarCampo(String campo) {
        // Mantido para compatibilidade se chamado diretamente; sem TextArea
        statusPorCampo.put(campo, "Aprovado");
    }

    public void revisarCampo(String campo) {
        statusPorCampo.put(campo, "Revisar");
    }

    private void aprovarCampo(String campo, TextArea areaFeedback) {
        statusPorCampo.put(campo, "Aprovado");
        if (areaFeedback != null) {
            areaFeedback.setVisible(false);
            areaFeedback.clear();
        }
        atualizarCorBotoes(campo, "Aprovado");
    }

    private void revisarCampo(String campo, TextArea areaFeedback) {
        statusPorCampo.put(campo, "Revisar");
        if (areaFeedback != null) {
            areaFeedback.setVisible(true);
            areaFeedback.setPromptText("Digite seu feedback aqui...");
            areaFeedback.setPrefHeight(100);
            areaFeedback.setWrapText(true);
        }
        atualizarCorBotoes(campo, "Revisar");
    }
    
    private void atualizarCorBotoes(String campo, String status) {
        // Mapeia campos para seus respectivos botões
        Button btnAprovar = null;
        Button btnRevisar = null;
        
        switch (campo) {
            case "problema":
                btnAprovar = (Button) alunoProblema.getScene().lookup("#aprovarProblema");
                btnRevisar = (Button) alunoProblema.getScene().lookup("#revisarProblema");
                break;
            case "solucao":
                btnAprovar = (Button) alunoSolucao.getScene().lookup("#aprovarSolucao");
                btnRevisar = (Button) alunoSolucao.getScene().lookup("#revisarSolucao");
                break;
            case "tecnologias":
                btnAprovar = (Button) alunoTecnologias.getScene().lookup("#aprovarTecnologias");
                btnRevisar = (Button) alunoTecnologias.getScene().lookup("#revisarTecnologias");
                break;
            case "contribuicoes":
                btnAprovar = (Button) alunoContribuicoes.getScene().lookup("#aprovarContribuicoes");
                btnRevisar = (Button) alunoContribuicoes.getScene().lookup("#revisarContribuicoes");
                break;
            case "hard_skills":
                btnAprovar = (Button) alunoHardSkills.getScene().lookup("#aprovarHardSkills");
                btnRevisar = (Button) alunoHardSkills.getScene().lookup("#revisarHardSkills");
                break;
            case "soft_skills":
                btnAprovar = (Button) alunoSoftSkills.getScene().lookup("#aprovarSoftSkills");
                btnRevisar = (Button) alunoSoftSkills.getScene().lookup("#revisarSoftSkills");
                break;
        }
        
        if (btnAprovar != null && btnRevisar != null) {
            if ("Aprovado".equals(status)) {
                btnAprovar.setStyle("-fx-background-color: #00AA00; -fx-text-fill: white;");
                btnRevisar.setStyle("-fx-background-color: #5E5555; -fx-text-fill: white;");
            } else if ("Revisar".equals(status)) {
                btnAprovar.setStyle("-fx-background-color: #5E5555; -fx-text-fill: white;");
                btnRevisar.setStyle("-fx-background-color: #AA0000; -fx-text-fill: white;");
            }
        }
    }

    // 3) Enviar feedbacks (UPDATE na tabela secao_api)
    @FXML
    public void enviarFeedback(ActionEvent event) {
        // No novo esquema, sempre faz UPDATE na tabela secao_api
        // pois a seção já existe quando o feedback é dado
        String sql = "UPDATE secao_api SET " +
                    "status_problema = ?, feedback_problema = ?, " +
                    "status_solucao = ?, feedback_solucao = ?, " +
                    "status_tecnologias = ?, feedback_tecnologias = ?, " +
                    "status_contribuicoes = ?, feedback_contribuicoes = ?, " +
                    "status_hard_skills = ?, feedback_hard_skills = ?, " +
                    "status_soft_skills = ?, feedback_soft_skills = ?, " +
                    "horario_feedback = CURRENT_TIMESTAMP " +
                    "WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            setNullableString(pst, 1, statusPorCampo.get("problema"));
            setNullableString(pst, 2, textOrNull(feedbackProblema));
            setNullableString(pst, 3, statusPorCampo.get("solucao"));
            setNullableString(pst, 4, textOrNull(feedbackSolucao));
            setNullableString(pst, 5, statusPorCampo.get("tecnologias"));
            setNullableString(pst, 6, textOrNull(feedbackTecnologias));
            setNullableString(pst, 7, statusPorCampo.get("contribuicoes"));
            setNullableString(pst, 8, textOrNull(feedbackContribuicoes));
            setNullableString(pst, 9, statusPorCampo.get("hard_skills"));
            setNullableString(pst, 10, textOrNull(feedbackHardSkills));
            setNullableString(pst, 11, statusPorCampo.get("soft_skills"));
            setNullableString(pst, 12, textOrNull(feedbackSoftSkills));

            // Para UPDATE, adiciona WHERE
            pst.setString(13, secaoApi.getEmailAluno());
            pst.setString(14, secaoApi.getSemestreCurso());
            pst.setInt(15, secaoApi.getAno());
            pst.setString(16, secaoApi.getSemestreAno());
            pst.setInt(17, secaoApi.getVersao());

            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Feedbacks atualizados com sucesso!");
            alert.showAndWait();
        } catch (SQLException e) {
            mostrarErro("Erro ao enviar feedback", e);
        }
    }
    
    private boolean verificarFeedbackExistente() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return false;
        String sql = "SELECT COUNT(*) FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? " +
                     "AND (status_problema IS NOT NULL OR status_solucao IS NOT NULL OR status_tecnologias IS NOT NULL " +
                     "OR status_contribuicoes IS NOT NULL OR status_hard_skills IS NOT NULL OR status_soft_skills IS NOT NULL)";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApi.getEmailAluno());
            pst.setString(2, secaoApi.getSemestreCurso());
            pst.setInt(3, secaoApi.getAno());
            pst.setString(4, secaoApi.getSemestreAno());
            pst.setInt(5, secaoApi.getVersao());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar feedback existente: " + e.getMessage());
        }
        return false;
    }

    private static void setNullableString(PreparedStatement pst, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            pst.setNull(index, Types.VARCHAR);
        } else {
            pst.setString(index, value);
        }
    }

    private static String textOrNull(TextArea area) {
        if (area == null) return null;
        String v = area.getText();
        return (v == null || v.isBlank()) ? null : v;
    }

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        final String emailAluno = secaoApi != null ? secaoApi.getEmailAluno() : null;
        NavigationService.navegarPara(event, "/com/example/technocode/Orientador/entregas-do-aluno.fxml",
            controller -> {
                if (controller instanceof EntregasDoAlunoController) {
                    EntregasDoAlunoController entregasController = (EntregasDoAlunoController) controller;
                    entregasController.setEmailAlunoParaConsulta(emailAluno);
                    entregasController.setDadosAluno(emailAluno);
                    entregasController.recarregarDados();
                }
            });
    }

    private void mostrarErro(String titulo, Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
        }
    }