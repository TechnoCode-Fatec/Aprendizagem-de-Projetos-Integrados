package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
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

public class TelaVisualizarSecaoApiAlunoController {

    // Identificador da seção
    private String alunoId;
    private String semestreCursoId;
    private int anoId;
    private String semestreAnoId;
    private int versaoId;
    private String emailAluno = LoginController.getEmailLogado();

    @FXML private TextArea alunoProblema;
    @FXML private TextArea alunoSolucao;
    @FXML private TextArea alunoTecnologias;
    @FXML private TextArea alunoContribuicoes;
    @FXML private TextArea alunoHardSkills;
    @FXML private TextArea alunoSoftSkills;
    @FXML private Button btnFeedback;


    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        this.alunoId = aluno;
        this.semestreCursoId = semestreCurso;
        this.anoId = ano;
        this.semestreAnoId = semestreAno;
        this.versaoId = versao;
        carregarSecaoAluno();
        if (btnFeedback != null) {
            boolean existe = existeFeedbackApi(alunoId, semestreCursoId, String.valueOf(anoId), semestreAnoId, versaoId);
            btnFeedback.setDisable(!existe);
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-feedback-api-aluno.fxml"));
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

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }

    public boolean existeFeedbackApi(String aluno, String semestreCurso, String ano, String semestreAno, int versao) {
        String sql = "SELECT 1 FROM feedback_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? LIMIT 1";
        try (Connection con = new Connector().getConnection()) {
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

    /**
     * Carrega os dados da versão atual e abre o formulário preenchido
     * para criar uma nova versão baseada na anterior
     */
    public void carregarVersaoAnterior(ActionEvent event) {
        if (alunoId == null) return;

        String sql = "SELECT semestre_curso, ano, semestre_ano, empresa, link_repositorio, problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
                     "FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, alunoId);
            ps.setString(2, semestreCursoId);
            ps.setInt(3, anoId);
            ps.setString(4, semestreAnoId);
            ps.setInt(5, versaoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtém todos os dados da versão atual
                    String semestreCurso = rs.getString("semestre_curso");
                    int ano = rs.getInt("ano");
                    String semestreAno = rs.getString("semestre_ano");
                    String empresa = rs.getString("empresa");
                    String repositorio = rs.getString("link_repositorio");
                    String problema = rs.getString("problema");
                    String solucao = rs.getString("solucao");
                    String tecnologias = rs.getString("tecnologias");
                    String contribuicoes = rs.getString("contribuicoes");
                    String hardSkills = rs.getString("hard_skills");
                    String softSkills = rs.getString("soft_skills");
                    
                    // Carrega o formulário
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/formulario-api.fxml"));
                    Parent root = loader.load();
                    
                    // Obtém o controller e preenche os dados
                    FormularioApiController controller = loader.getController();
                    controller.setDadosVersaoAnterior(
                        semestreCurso, 
                        String.valueOf(ano), 
                        semestreAno,
                            empresa,
                            repositorio,
                        problema, 
                        solucao, 
                        tecnologias, 
                        contribuicoes, 
                        hardSkills, 
                        softSkills
                    );
                    
                    // Abre a tela do formulário
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                    
                } else {
                    System.err.println("Seção não encontrada para criar nova versão");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar dados da versão anterior: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erro ao carregar formulário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
