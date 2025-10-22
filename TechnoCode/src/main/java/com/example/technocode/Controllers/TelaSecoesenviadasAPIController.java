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

public class TelaSecoesenviadasAPIController {

    // Identificador da seção
    private String alunoId;
    private String semestreCursoId;
    private int anoId;
    private String semestreAnoId;
    private int versaoId;

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
        this.alunoId = aluno;
        this.semestreCursoId = semestreCurso;
        this.anoId = ano;
        this.semestreAnoId = semestreAno;
        this.versaoId = versao;
        carregarSecaoAluno();
    }

    // 1) Carrega dados da secao_api
    public void carregarSecaoAluno() {
        if (alunoId == null) return;
        String sql = "SELECT problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
                "FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setString(2, semestreCursoId);
            pst.setInt(3, anoId);
            pst.setString(4, semestreAnoId);
            pst.setInt(5, versaoId);
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
    }

    private void revisarCampo(String campo, TextArea areaFeedback) {
        statusPorCampo.put(campo, "Revisar");
        if (areaFeedback != null) {
            areaFeedback.setVisible(true);
            areaFeedback.setPromptText("Digite seu feedback aqui...");
            areaFeedback.setPrefHeight(100);
            areaFeedback.setWrapText(true);
        }
    }

    // 3) Enviar feedbacks (INSERT único)
    @FXML
    public void enviarFeedback(ActionEvent event) {
        String sql = "INSERT INTO feedback_api (" +
                "status_problema, feedback_problema, " +
                "status_solucao, feedback_solucao, " +
                "status_tecnologias, feedback_tecnologias, " +
                "status_contribuicoes, feedback_contribuicoes, " +
                "status_hard_skills, feedback_hard_skills, " +
                "status_soft_skills, feedback_soft_skills, " +
                "aluno, semestre_curso, ano, semestre_ano, versao) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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

            pst.setString(13, alunoId);
            pst.setString(14, semestreCursoId);
            pst.setInt(15, anoId);
            pst.setString(16, semestreAnoId);
            pst.setInt(17, versaoId);

            pst.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Feedbacks enviados com sucesso!");
        alert.showAndWait();
        } catch (SQLException e) {
            mostrarErro("Erro ao enviar feedback", e);
        }
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-entregasDoAluno.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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