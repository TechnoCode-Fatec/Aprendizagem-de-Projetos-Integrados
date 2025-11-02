package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlunoVisualizarApiController {

    // Identificador da seção usando classe modelo
    private SecaoApi secaoApi;
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
        // Cria objeto SecaoApi para identificar a seção
        this.secaoApi = new SecaoApi(aluno, semestreCurso, ano, semestreAno, versao);
        carregarSecaoAluno();
        if (btnFeedback != null && secaoApi != null) {
            boolean existe = SecaoApi.verificarFeedback(secaoApi.getEmailAluno(), secaoApi.getSemestreCurso(), 
                    secaoApi.getAno(), secaoApi.getSemestreAno(), secaoApi.getVersao());
            btnFeedback.setDisable(!existe);
        }
    }

    // Carrega dados da secao_api
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
    }

    @FXML
    private void verFeedback(ActionEvent event) throws IOException {
        if (secaoApi != null) {
            final String emailAluno = secaoApi.getEmailAluno();
            final String semestreCurso = secaoApi.getSemestreCurso();
            final int ano = secaoApi.getAno();
            final String semestreAno = secaoApi.getSemestreAno();
            final int versao = secaoApi.getVersao();
            
            NavigationService.navegarPara(event, "/com/example/technocode/Aluno/aluno-feedback-api.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApiController) {
                        ((AlunoFeedbackApiController) controller).setIdentificadorSecao(
                            emailAluno, semestreCurso, ano, semestreAno, versao);
                    }
                });
        }
    }

    @FXML
    private void verHistorico(ActionEvent event) throws IOException {
        NavigationService.navegarPara(event, "/com/example/technocode/Aluno/aluno-historico.fxml");
    }

    @FXML
    private void voltarTelaInicial(ActionEvent event) throws IOException {
        NavigationService.navegarPara(event, "/com/example/technocode/Aluno/tela-inicial-aluno.fxml");
    }

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }


    /**
     * Carrega os dados da versão atual e abre o formulário preenchido
     * para criar uma nova versão baseada na anterior
     */
    public void carregarVersaoAnterior(ActionEvent event) {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;

        String sql = "SELECT semestre_curso, ano, semestre_ano, empresa, link_repositorio, problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
                     "FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, secaoApi.getEmailAluno());
            ps.setString(2, secaoApi.getSemestreCurso());
            ps.setInt(3, secaoApi.getAno());
            ps.setString(4, secaoApi.getSemestreAno());
            ps.setInt(5, secaoApi.getVersao());
            
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
                    
                    // Carrega o formulário e preenche os dados
                    final String semestreCursoFinal = semestreCurso;
                    final String anoFinal = String.valueOf(ano);
                    final String semestreAnoFinal = semestreAno;
                    final String empresaFinal = empresa;
                    final String repositorioFinal = repositorio;
                    final String problemaFinal = problema;
                    final String solucaoFinal = solucao;
                    final String tecnologiasFinal = tecnologias;
                    final String contribuicoesFinal = contribuicoes;
                    final String hardSkillsFinal = hardSkills;
                    final String softSkillsFinal = softSkills;
                    
                    NavigationService.navegarPara(event, "/com/example/technocode/Aluno/formulario-api.fxml",
                        controller -> {
                            if (controller instanceof FormularioApiController) {
                                ((FormularioApiController) controller).setDadosVersaoAnterior(
                                    semestreCursoFinal, anoFinal, semestreAnoFinal, empresaFinal,
                                    repositorioFinal, problemaFinal, solucaoFinal, tecnologiasFinal,
                                    contribuicoesFinal, hardSkillsFinal, softSkillsFinal
                                );
                            }
                        });
                    
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
