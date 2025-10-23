package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaVisualizarSecaoApiAlunoController {

    // Identificador da seção
    private String alunoId;
    private String semestreCursoId;
    private int anoId;
    private String semestreAnoId;
    private int versaoId;

    @FXML private TextArea alunoProblema;
    @FXML private TextArea alunoSolucao;
    @FXML private TextArea alunoTecnologias;
    @FXML private TextArea alunoContribuicoes;
    @FXML private TextArea alunoHardSkills;
    @FXML private TextArea alunoSoftSkills;

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        this.alunoId = aluno;
        this.semestreCursoId = semestreCurso;
        this.anoId = ano;
        this.semestreAnoId = semestreAno;
        this.versaoId = versao;
        carregarSecaoAluno();
    }

    // Carrega dados da secao_api
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

    @FXML
    private void verFeedback(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-feedback-api-aluno.fxml"));
            Parent root = loader.load();
            
            TelaFeedbackApiAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(alunoId, semestreCursoId, anoId, semestreAnoId, versaoId);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de feedback: " + e.getMessage());
            throw e;
        }
    }

    @FXML
    private void verHistorico(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-historico-versoes.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de histórico: " + e.getMessage());
            throw e;
        }
    }

    @FXML
    private void voltarTelaInicial(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-inicial-aluno.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela inicial: " + e.getMessage());
            throw e;
        }
    }

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }
}
