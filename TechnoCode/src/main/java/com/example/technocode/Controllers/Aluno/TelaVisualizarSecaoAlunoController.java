package com.example.technocode.Controllers.Aluno;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class TelaVisualizarSecaoAlunoController {

    // Identificador da seção
    private String alunoId;
    private int versaoId;

    @FXML private TextArea alunoTextNome;
    @FXML private TextArea alunoTextIdade;
    @FXML private TextArea alunoTextCurso;
    @FXML private TextArea alunoTextMotivacao;
    @FXML private TextArea alunoTextHistorico;
    @FXML private TextArea alunoTextGithub;
    @FXML private TextArea alunoTextLinkedin;
    @FXML private TextArea alunoTextConhecimentos;
    @FXML private Button btnFeedback;

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        this.alunoId = aluno;
        this.versaoId = versao;
        carregarSecaoAluno();
        if (btnFeedback != null) {
            boolean existe = existeFeedbackApresentacao(alunoId, versaoId);
            btnFeedback.setDisable(!existe);
        }
    }

    // Carrega dados da secao_apresentacao
    public void carregarSecaoAluno() {
        if (alunoId == null) return;
        String sql = "SELECT nome, idade, curso, motivacao, historico, link_github, link_linkedin, principais_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setInt(2, versaoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    if (alunoTextNome != null) alunoTextNome.setText(rs.getString("nome"));
                    if (alunoTextIdade != null) {
                        String dataNascimentoStr = rs.getString("idade"); // ex: "2002-05-10"
                        if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                            LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);
                            LocalDate hoje = LocalDate.now();
                            int idade = Period.between(dataNascimento, hoje).getYears();
                            alunoTextIdade.setText(String.valueOf(idade));
                        }
                    }
                    if (alunoTextCurso != null) alunoTextCurso.setText(rs.getString("curso"));
                    if (alunoTextMotivacao != null) alunoTextMotivacao.setText(rs.getString("motivacao"));
                    if (alunoTextHistorico != null) alunoTextHistorico.setText(rs.getString("historico"));
                    if (alunoTextGithub != null) alunoTextGithub.setText(rs.getString("link_github"));
                    if (alunoTextLinkedin != null) alunoTextLinkedin.setText(rs.getString("link_linkedin"));
                    if (alunoTextConhecimentos != null) alunoTextConhecimentos.setText(rs.getString("principais_conhecimentos"));
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar seção do aluno", e);
        }
    }

    @FXML
    private void verFeedback(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-feedback-apresentacao-aluno.fxml"));
            Parent root = loader.load();
            
            TelaFeedbackApresentacaoAlunoController controller = loader.getController();
            controller.setIdentificadorSecao(alunoId, versaoId);
            
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-historico-versoes.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-inicial-aluno.fxml"));
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

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }
}
