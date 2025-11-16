package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AlunoFeedbackApiController {

    // Identificador da seção usando classe modelo
    private SecaoApi secaoApi;

    // TextAreas e TextFields com dados do aluno
    @FXML private TextField alunoEmpresa;
    @FXML private TextArea alunoDescricaoEmpresa;
    @FXML private Hyperlink linkRepositorio;
    @FXML private TextArea alunoProblema;
    @FXML private TextArea alunoSolucao;
    @FXML private TextArea alunoTecnologias;
    @FXML private TextArea alunoContribuicoes;
    @FXML private TextArea alunoHardSkills;
    @FXML private TextArea alunoSoftSkills;

    // TextAreas com feedbacks
    @FXML private TextArea feedbackEmpresa;
    @FXML private TextArea feedbackDescricaoEmpresa;
    @FXML private TextArea feedbackRepositorio;
    @FXML private TextArea feedbackProblema;
    @FXML private TextArea feedbackSolucao;
    @FXML private TextArea feedbackTecnologias;
    @FXML private TextArea feedbackContribuicoes;
    @FXML private TextArea feedbackHardSkills;
    @FXML private TextArea feedbackSoftSkills;

    // Labels de status
    @FXML private Label statusEmpresa;
    @FXML private Label statusDescricaoEmpresa;
    @FXML private Label statusRepositorio;
    @FXML private Label statusProblema;
    @FXML private Label statusSolucao;
    @FXML private Label statusTecnologias;
    @FXML private Label statusContribuicoes;
    @FXML private Label statusHardSkills;
    @FXML private Label statusSoftSkills;

    // Containers de feedback expansíveis
    @FXML private VBox containerFeedbackEmpresa;
    @FXML private VBox containerFeedbackDescricaoEmpresa;
    @FXML private VBox containerFeedbackRepositorio;
    @FXML private VBox containerFeedbackProblema;
    @FXML private VBox containerFeedbackSolucao;
    @FXML private VBox containerFeedbackTecnologias;
    @FXML private VBox containerFeedbackContribuicoes;
    @FXML private VBox containerFeedbackHardSkills;
    @FXML private VBox containerFeedbackSoftSkills;

    // Botões de expandir
    @FXML private Button btnExpandEmpresa;
    @FXML private Button btnExpandDescricaoEmpresa;
    @FXML private Button btnExpandRepositorio;
    @FXML private Button btnExpandProblema;
    @FXML private Button btnExpandSolucao;
    @FXML private Button btnExpandTecnologias;
    @FXML private Button btnExpandContribuicoes;
    @FXML private Button btnExpandHardSkills;
    @FXML private Button btnExpandSoftSkills;

    @FXML private Button btnNovaVersao;

    // Mapa para controlar estado de expansão
    private Map<String, Boolean> estadosExpansao = new HashMap<>();

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        // Cria objeto SecaoApi para identificar a seção
        this.secaoApi = new SecaoApi(aluno, semestreCurso, ano, semestreAno, versao);
        carregarDadosAluno();
        carregarFeedback();
    }

    // Carrega dados do aluno (o que ele escreveu)
    private void carregarDadosAluno() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        String sql = "SELECT empresa, descricao_empresa, link_repositorio, problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
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
                    if (alunoEmpresa != null) alunoEmpresa.setText(rs.getString("empresa") != null ? rs.getString("empresa") : "");
                    if (alunoDescricaoEmpresa != null) alunoDescricaoEmpresa.setText(rs.getString("descricao_empresa") != null ? rs.getString("descricao_empresa") : "");
                    
                    // Configura link do repositório
                    String linkRepo = rs.getString("link_repositorio");
                    if (linkRepositorio != null && linkRepo != null && !linkRepo.trim().isEmpty()) {
                        linkRepositorio.setText(linkRepo);
                        final String linkFinal = linkRepo;
                        linkRepositorio.setOnAction(e -> abrirURL(linkFinal, linkRepositorio));
                    } else if (linkRepositorio != null) {
                        linkRepositorio.setText("Nenhum link disponível");
                        linkRepositorio.setDisable(true);
                    }
                    
                    if (alunoProblema != null) alunoProblema.setText(rs.getString("problema") != null ? rs.getString("problema") : "");
                    if (alunoSolucao != null) alunoSolucao.setText(rs.getString("solucao") != null ? rs.getString("solucao") : "");
                    if (alunoTecnologias != null) alunoTecnologias.setText(rs.getString("tecnologias") != null ? rs.getString("tecnologias") : "");
                    if (alunoContribuicoes != null) alunoContribuicoes.setText(rs.getString("contribuicoes") != null ? rs.getString("contribuicoes") : "");
                    if (alunoHardSkills != null) alunoHardSkills.setText(rs.getString("hard_skills") != null ? rs.getString("hard_skills") : "");
                    if (alunoSoftSkills != null) alunoSoftSkills.setText(rs.getString("soft_skills") != null ? rs.getString("soft_skills") : "");
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar dados do aluno", e);
        }
    }

    private void abrirURL(String url, javafx.scene.Node node) {
        try {
            // Garante que a URL tenha protocolo
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            // Tenta usar Desktop via reflection (funciona mesmo sem módulo java.desktop)
            try {
                Class<?> desktopClass = Class.forName("java.awt.Desktop");
                Object desktop = desktopClass.getMethod("getDesktop").invoke(null);
                Boolean isSupported = (Boolean) desktopClass.getMethod("isDesktopSupported").invoke(desktop);
                if (isSupported != null && isSupported) {
                    desktopClass.getMethod("browse", URI.class).invoke(desktop, new URI(url));
                    return;
                }
            } catch (Exception e) {
                // Ignora erros de reflection
            }
            // Se Desktop não funcionar, mostra mensagem
            System.err.println("Não foi possível abrir a URL automaticamente: " + url);
            System.out.println("Por favor, copie e cole a URL no navegador: " + url);
        } catch (Exception e) {
            System.err.println("Erro ao abrir URL: " + e.getMessage());
        }
    }

    // Carrega dados do feedback da secao_api
    private void carregarFeedback() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        String sql = "SELECT status_empresa, feedback_empresa, " +
                "status_descricao_empresa, feedback_descricao_empresa, " +
                "status_repositorio, feedback_repositorio, " +
                "status_problema, feedback_problema, " +
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
                    carregarCampoFeedback("empresa", rs, feedbackEmpresa, statusEmpresa, containerFeedbackEmpresa, btnExpandEmpresa);
                    carregarCampoFeedback("descricao_empresa", rs, feedbackDescricaoEmpresa, statusDescricaoEmpresa, containerFeedbackDescricaoEmpresa, btnExpandDescricaoEmpresa);
                    carregarCampoFeedback("repositorio", rs, feedbackRepositorio, statusRepositorio, containerFeedbackRepositorio, btnExpandRepositorio);
                    carregarCampoFeedback("problema", rs, feedbackProblema, statusProblema, containerFeedbackProblema, btnExpandProblema);
                    carregarCampoFeedback("solucao", rs, feedbackSolucao, statusSolucao, containerFeedbackSolucao, btnExpandSolucao);
                    carregarCampoFeedback("tecnologias", rs, feedbackTecnologias, statusTecnologias, containerFeedbackTecnologias, btnExpandTecnologias);
                    carregarCampoFeedback("contribuicoes", rs, feedbackContribuicoes, statusContribuicoes, containerFeedbackContribuicoes, btnExpandContribuicoes);
                    carregarCampoFeedback("hard_skills", rs, feedbackHardSkills, statusHardSkills, containerFeedbackHardSkills, btnExpandHardSkills);
                    carregarCampoFeedback("soft_skills", rs, feedbackSoftSkills, statusSoftSkills, containerFeedbackSoftSkills, btnExpandSoftSkills);
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar feedback", e);
        }
    }

    private void carregarCampoFeedback(String campo, ResultSet rs, TextArea textAreaFeedback, Label statusLabel, 
                                       VBox containerFeedback, Button btnExpand) throws SQLException {
        String status = rs.getString("status_" + campo);
        String feedback = rs.getString("feedback_" + campo);
        
        // Inicializa estado de expansão
        estadosExpansao.put(campo, false);
        
        if (status != null) {
            statusLabel.setText(status);
            if ("Aprovado".equals(status)) {
                // Badge verde moderno
                statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold; " +
                        "-fx-background-color: #D5F4E6; -fx-background-radius: 12; " +
                        "-fx-padding: 4 12 4 12; -fx-border-color: #27AE60; -fx-border-width: 1; -fx-border-radius: 12;");
                // Esconde feedback se aprovado
                if (containerFeedback != null) {
                    containerFeedback.setVisible(false);
                    containerFeedback.setManaged(false);
                }
                if (btnExpand != null) {
                    btnExpand.setVisible(false);
                    btnExpand.setManaged(false);
                }
            } else if ("Revisar".equals(status)) {
                // Badge vermelho moderno
                statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; " +
                        "-fx-background-color: #FADBD8; -fx-background-radius: 12; " +
                        "-fx-padding: 4 12 4 12; -fx-border-color: #E74C3C; -fx-border-width: 1; -fx-border-radius: 12;");
                // Mostra botão de expandir se houver feedback
                if (feedback != null && !feedback.trim().isEmpty() && btnExpand != null) {
                    btnExpand.setVisible(true);
                    btnExpand.setManaged(true);
                    btnExpand.setOnAction(e -> toggleFeedback(campo, containerFeedback, btnExpand));
                }
            }
        } else {
            statusLabel.setText("Sem avaliação");
            // Badge cinza moderno
            statusLabel.setStyle("-fx-text-fill: #95A5A6; -fx-font-weight: bold; " +
                    "-fx-background-color: #ECF0F1; -fx-background-radius: 12; " +
                    "-fx-padding: 4 12 4 12; -fx-border-color: #95A5A6; -fx-border-width: 1; -fx-border-radius: 12;");
            if (containerFeedback != null) {
                containerFeedback.setVisible(false);
                containerFeedback.setManaged(false);
            }
            if (btnExpand != null) {
                btnExpand.setVisible(false);
                btnExpand.setManaged(false);
            }
        }
        
        if (textAreaFeedback != null) {
            if (feedback != null && !feedback.trim().isEmpty()) {
                textAreaFeedback.setText(feedback);
            } else {
                textAreaFeedback.setText("Nenhum feedback disponível para este campo.");
            }
        }
    }

    private void toggleFeedback(String campo, VBox container, Button btn) {
        boolean expandido = estadosExpansao.getOrDefault(campo, false);
        expandido = !expandido;
        estadosExpansao.put(campo, expandido);
        
        if (container != null) {
            container.setVisible(expandido);
            container.setManaged(expandido);
        }
        
        if (btn != null && btn.getGraphic() != null && btn.getGraphic() instanceof Label) {
            Label label = (Label) btn.getGraphic();
            label.setText(expandido ? "▲" : "▼");
        }
    }

    @FXML
    private void carregarVersaoAnterior(ActionEvent event) {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;

        String sql = "SELECT semestre_curso, ano, semestre_ano, empresa, descricao_empresa, link_repositorio, problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
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
                    String semestreCurso = rs.getString("semestre_curso");
                    int ano = rs.getInt("ano");
                    String semestreAno = rs.getString("semestre_ano");
                    String empresa = rs.getString("empresa");
                    String descricaoEmpresa = rs.getString("descricao_empresa");
                    String repositorio = rs.getString("link_repositorio");
                    String problema = rs.getString("problema");
                    String solucao = rs.getString("solucao");
                    String tecnologias = rs.getString("tecnologias");
                    String contribuicoes = rs.getString("contribuicoes");
                    String hardSkills = rs.getString("hard_skills");
                    String softSkills = rs.getString("soft_skills");
                    
                    NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/formulario-api.fxml",
                        controller -> {
                            if (controller instanceof FormularioApiController) {
                                ((FormularioApiController) controller).setDadosVersaoAnterior(
                                    semestreCurso, String.valueOf(ano), semestreAno, empresa, descricaoEmpresa,
                                    repositorio, problema, solucao, tecnologias,
                                    contribuicoes, hardSkills, softSkills
                                );
                            }
                        });
                }
            }
        } catch (SQLException | IOException e) {
            mostrarErro("Erro ao carregar versão anterior", e);
        }
    }

    @FXML
    private void voltarTelaInicial(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/sessoes-atuais.fxml",
            controller -> {
                if (controller instanceof SessoesAtuaisAlunoController) {
                    ((SessoesAtuaisAlunoController) controller).recarregarSecoes();
                }
            });
    }

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }
}
