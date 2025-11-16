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
    @FXML private TextField alunoLinkRepositorioEditavel; // Campo editável quando recusado
    @FXML private TextArea alunoProblema;
    @FXML private TextArea alunoSolucao;
    @FXML private TextArea alunoTecnologias;
    @FXML private TextArea alunoContribuicoes;
    @FXML private TextArea alunoHardSkills;
    @FXML private TextArea alunoSoftSkills;
    
    // Valores originais para detectar modificações
    private Map<String, String> valoresOriginais = new HashMap<>();
    
    // Valores da versão anterior para comparação quando for nova versão
    private Map<String, String> valoresVersaoAnterior = new HashMap<>();
    
    // Campos que foram modificados
    private Map<String, Boolean> camposModificados = new HashMap<>();

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
    @FXML private Button btnEnviarNovaVersao;

    // Mapa para controlar estado de expansão
    private Map<String, Boolean> estadosExpansao = new HashMap<>();

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        // Cria objeto SecaoApi para identificar a seção
        this.secaoApi = new SecaoApi(aluno, semestreCurso, ano, semestreAno, versao);
        valoresOriginais.clear();
        valoresVersaoAnterior.clear();
        camposModificados.clear();
        carregarDadosAluno();
        carregarFeedback();
        configurarListenersModificacao();
        if (btnEnviarNovaVersao != null) {
            btnEnviarNovaVersao.setVisible(false);
            btnEnviarNovaVersao.setManaged(false);
        }
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
                    String empresa = rs.getString("empresa") != null ? rs.getString("empresa") : "";
                    String descricaoEmpresa = rs.getString("descricao_empresa") != null ? rs.getString("descricao_empresa") : "";
                    String linkRepo = rs.getString("link_repositorio") != null ? rs.getString("link_repositorio") : "";
                    String problema = rs.getString("problema") != null ? rs.getString("problema") : "";
                    String solucao = rs.getString("solucao") != null ? rs.getString("solucao") : "";
                    String tecnologias = rs.getString("tecnologias") != null ? rs.getString("tecnologias") : "";
                    String contribuicoes = rs.getString("contribuicoes") != null ? rs.getString("contribuicoes") : "";
                    String hardSkills = rs.getString("hard_skills") != null ? rs.getString("hard_skills") : "";
                    String softSkills = rs.getString("soft_skills") != null ? rs.getString("soft_skills") : "";
                    
                    // Armazena valores originais
                    valoresOriginais.put("empresa", empresa);
                    valoresOriginais.put("descricao_empresa", descricaoEmpresa);
                    valoresOriginais.put("link_repositorio", linkRepo);
                    valoresOriginais.put("problema", problema);
                    valoresOriginais.put("solucao", solucao);
                    valoresOriginais.put("tecnologias", tecnologias);
                    valoresOriginais.put("contribuicoes", contribuicoes);
                    valoresOriginais.put("hard_skills", hardSkills);
                    valoresOriginais.put("soft_skills", softSkills);
                    
                    if (alunoEmpresa != null) alunoEmpresa.setText(empresa);
                    if (alunoDescricaoEmpresa != null) alunoDescricaoEmpresa.setText(descricaoEmpresa);
                    
                    // Configura link do repositório (será configurado depois baseado no status)
                    if (linkRepositorio != null && linkRepo != null && !linkRepo.trim().isEmpty()) {
                        linkRepositorio.setText(linkRepo);
                        final String linkFinal = linkRepo;
                        linkRepositorio.setOnAction(e -> abrirURL(linkFinal, linkRepositorio));
                    } else if (linkRepositorio != null) {
                        linkRepositorio.setText("Nenhum link disponível");
                        linkRepositorio.setDisable(true);
                    }
                    
                    // Inicializa TextField editável (será mostrado apenas se recusado)
                    if (alunoLinkRepositorioEditavel != null) {
                        alunoLinkRepositorioEditavel.setText(linkRepo);
                        alunoLinkRepositorioEditavel.setVisible(false);
                        alunoLinkRepositorioEditavel.setManaged(false);
                    }
                    
                    if (alunoProblema != null) alunoProblema.setText(problema);
                    if (alunoSolucao != null) alunoSolucao.setText(solucao);
                    if (alunoTecnologias != null) alunoTecnologias.setText(tecnologias);
                    if (alunoContribuicoes != null) alunoContribuicoes.setText(contribuicoes);
                    if (alunoHardSkills != null) alunoHardSkills.setText(hardSkills);
                    if (alunoSoftSkills != null) alunoSoftSkills.setText(softSkills);
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
        
        // Primeiro tenta carregar feedback da versão atual
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
                    // Verifica se há algum feedback na versão atual
                    boolean temFeedback = false;
                    for (String campo : new String[]{"empresa", "descricao_empresa", "repositorio", "problema", "solucao", 
                            "tecnologias", "contribuicoes", "hard_skills", "soft_skills"}) {
                        if (rs.getString("status_" + campo) != null) {
                            temFeedback = true;
                            break;
                        }
                    }
                    
                    if (temFeedback) {
                        // Carrega feedback da versão atual
                        carregarCampoFeedback("empresa", rs, feedbackEmpresa, statusEmpresa, containerFeedbackEmpresa, btnExpandEmpresa, false);
                        carregarCampoFeedback("descricao_empresa", rs, feedbackDescricaoEmpresa, statusDescricaoEmpresa, containerFeedbackDescricaoEmpresa, btnExpandDescricaoEmpresa, false);
                        carregarCampoFeedback("repositorio", rs, feedbackRepositorio, statusRepositorio, containerFeedbackRepositorio, btnExpandRepositorio, false);
                        carregarCampoFeedback("problema", rs, feedbackProblema, statusProblema, containerFeedbackProblema, btnExpandProblema, false);
                        carregarCampoFeedback("solucao", rs, feedbackSolucao, statusSolucao, containerFeedbackSolucao, btnExpandSolucao, false);
                        carregarCampoFeedback("tecnologias", rs, feedbackTecnologias, statusTecnologias, containerFeedbackTecnologias, btnExpandTecnologias, false);
                        carregarCampoFeedback("contribuicoes", rs, feedbackContribuicoes, statusContribuicoes, containerFeedbackContribuicoes, btnExpandContribuicoes, false);
                        carregarCampoFeedback("hard_skills", rs, feedbackHardSkills, statusHardSkills, containerFeedbackHardSkills, btnExpandHardSkills, false);
                        carregarCampoFeedback("soft_skills", rs, feedbackSoftSkills, statusSoftSkills, containerFeedbackSoftSkills, btnExpandSoftSkills, false);
                    } else {
                        // Não há feedback na versão atual, carrega da versão anterior
                        carregarFeedbackVersaoAnterior();
                    }
                } else {
                    // Não há registro na versão atual, carrega da versão anterior
                    carregarFeedbackVersaoAnterior();
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar feedback", e);
        }
    }
    
    // Carrega feedback da versão anterior quando não há feedback na versão atual
    private void carregarFeedbackVersaoAnterior() {
        if (secaoApi == null || secaoApi.getVersao() <= 1) {
            // Não há versão anterior, todos os campos ficam sem avaliação
            inicializarCamposSemAvaliacao();
            return;
        }
        
        int versaoAnterior = secaoApi.getVersao() - 1;
        
        // Primeiro carrega os valores da versão anterior para comparação
        String sqlValores = "SELECT empresa, descricao_empresa, link_repositorio, problema, solucao, tecnologias, contribuicoes, hard_skills, soft_skills " +
                "FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sqlValores)) {
            pst.setString(1, secaoApi.getEmailAluno());
            pst.setString(2, secaoApi.getSemestreCurso());
            pst.setInt(3, secaoApi.getAno());
            pst.setString(4, secaoApi.getSemestreAno());
            pst.setInt(5, versaoAnterior);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    valoresVersaoAnterior.put("empresa", rs.getString("empresa") != null ? rs.getString("empresa") : "");
                    valoresVersaoAnterior.put("descricao_empresa", rs.getString("descricao_empresa") != null ? rs.getString("descricao_empresa") : "");
                    valoresVersaoAnterior.put("repositorio", rs.getString("link_repositorio") != null ? rs.getString("link_repositorio") : "");
                    valoresVersaoAnterior.put("problema", rs.getString("problema") != null ? rs.getString("problema") : "");
                    valoresVersaoAnterior.put("solucao", rs.getString("solucao") != null ? rs.getString("solucao") : "");
                    valoresVersaoAnterior.put("tecnologias", rs.getString("tecnologias") != null ? rs.getString("tecnologias") : "");
                    valoresVersaoAnterior.put("contribuicoes", rs.getString("contribuicoes") != null ? rs.getString("contribuicoes") : "");
                    valoresVersaoAnterior.put("hard_skills", rs.getString("hard_skills") != null ? rs.getString("hard_skills") : "");
                    valoresVersaoAnterior.put("soft_skills", rs.getString("soft_skills") != null ? rs.getString("soft_skills") : "");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar valores da versão anterior: " + e.getMessage());
        }
        
        // Agora carrega os feedbacks da versão anterior
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
            pst.setInt(5, versaoAnterior);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Carrega feedback da versão anterior, mas verifica se o campo foi modificado
                    carregarCampoFeedback("empresa", rs, feedbackEmpresa, statusEmpresa, containerFeedbackEmpresa, btnExpandEmpresa, true);
                    carregarCampoFeedback("descricao_empresa", rs, feedbackDescricaoEmpresa, statusDescricaoEmpresa, containerFeedbackDescricaoEmpresa, btnExpandDescricaoEmpresa, true);
                    carregarCampoFeedback("repositorio", rs, feedbackRepositorio, statusRepositorio, containerFeedbackRepositorio, btnExpandRepositorio, true);
                    carregarCampoFeedback("problema", rs, feedbackProblema, statusProblema, containerFeedbackProblema, btnExpandProblema, true);
                    carregarCampoFeedback("solucao", rs, feedbackSolucao, statusSolucao, containerFeedbackSolucao, btnExpandSolucao, true);
                    carregarCampoFeedback("tecnologias", rs, feedbackTecnologias, statusTecnologias, containerFeedbackTecnologias, btnExpandTecnologias, true);
                    carregarCampoFeedback("contribuicoes", rs, feedbackContribuicoes, statusContribuicoes, containerFeedbackContribuicoes, btnExpandContribuicoes, true);
                    carregarCampoFeedback("hard_skills", rs, feedbackHardSkills, statusHardSkills, containerFeedbackHardSkills, btnExpandHardSkills, true);
                    carregarCampoFeedback("soft_skills", rs, feedbackSoftSkills, statusSoftSkills, containerFeedbackSoftSkills, btnExpandSoftSkills, true);
                } else {
                    inicializarCamposSemAvaliacao();
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar feedback da versão anterior", e);
            inicializarCamposSemAvaliacao();
        }
    }
    
    private void inicializarCamposSemAvaliacao() {
        // Inicializa todos os campos como sem avaliação
        String[] campos = {"empresa", "descricao_empresa", "repositorio", "problema", "solucao", 
                          "tecnologias", "contribuicoes", "hard_skills", "soft_skills"};
        for (String campo : campos) {
            estadosExpansao.put(campo, false);
            tornarCampoNaoEditavel(campo);
        }
    }

    private void carregarCampoFeedback(String campo, ResultSet rs, TextArea textAreaFeedback, Label statusLabel, 
                                       VBox containerFeedback, Button btnExpand, boolean isVersaoAnterior) throws SQLException {
        String status = rs.getString("status_" + campo);
        String feedback = rs.getString("feedback_" + campo);
        
        // Verifica se o campo foi modificado comparando com valor da versão anterior
        boolean campoModificado = false;
        if (isVersaoAnterior) {
            String valorAtual = obterValorAtualCampo(campo);
            String valorVersaoAnterior = valoresVersaoAnterior.get(campo);
            // Compara com valor da versão anterior
            campoModificado = valorVersaoAnterior != null && !valorVersaoAnterior.equals(valorAtual);
        }
        
        // Se o campo foi modificado, mostra como sem avaliação
        if (campoModificado) {
            statusLabel.setText("Sem avaliação");
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
            tornarCampoNaoEditavel(campo);
            estadosExpansao.put(campo, false);
            return;
        }
        
        // Inicializa estado de expansão
        estadosExpansao.put(campo, false);
        
        if (status != null) {
            // Se é versão anterior, adiciona indicador visual
            String textoStatus = isVersaoAnterior ? status + " (v." + (secaoApi.getVersao() - 1) + ")" : status;
            statusLabel.setText(textoStatus);
            
            if ("Aprovado".equals(status)) {
                // Badge verde moderno (com indicador de versão anterior se aplicável)
                String estiloBase = "-fx-text-fill: #27AE60; -fx-font-weight: bold; " +
                        "-fx-background-color: #D5F4E6; -fx-background-radius: 12; " +
                        "-fx-padding: 4 12 4 12; -fx-border-color: #27AE60; -fx-border-width: 1; -fx-border-radius: 12;";
                if (isVersaoAnterior) {
                    estiloBase += " -fx-opacity: 0.8;";
                }
                statusLabel.setStyle(estiloBase);
                // Esconde feedback se aprovado
                if (containerFeedback != null) {
                    containerFeedback.setVisible(false);
                    containerFeedback.setManaged(false);
                }
                if (btnExpand != null) {
                    btnExpand.setVisible(false);
                    btnExpand.setManaged(false);
                }
                // Mantém campo não editável se aprovado
                tornarCampoNaoEditavel(campo);
            } else if ("Revisar".equals(status)) {
                // Badge vermelho moderno (com indicador de versão anterior se aplicável)
                String estiloBase = "-fx-text-fill: #E74C3C; -fx-font-weight: bold; " +
                        "-fx-background-color: #FADBD8; -fx-background-radius: 12; " +
                        "-fx-padding: 4 12 4 12; -fx-border-color: #E74C3C; -fx-border-width: 1; -fx-border-radius: 12;";
                if (isVersaoAnterior) {
                    estiloBase += " -fx-opacity: 0.8;";
                }
                statusLabel.setStyle(estiloBase);
                // Mostra botão de expandir se houver feedback
                if (feedback != null && !feedback.trim().isEmpty() && btnExpand != null) {
                    btnExpand.setVisible(true);
                    btnExpand.setManaged(true);
                    btnExpand.setOnAction(e -> toggleFeedback(campo, containerFeedback, btnExpand));
                }
                // Torna campo editável se recusado (mesmo sendo versão anterior, pode editar)
                tornarCampoEditavel(campo);
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
            // Mantém campo não editável se sem avaliação
            tornarCampoNaoEditavel(campo);
        }
        
        if (textAreaFeedback != null) {
            if (feedback != null && !feedback.trim().isEmpty()) {
                textAreaFeedback.setText(feedback);
            } else {
                textAreaFeedback.setText("Nenhum feedback disponível para este campo.");
            }
        }
    }
    
    private String obterValorAtualCampo(String campo) {
        switch (campo) {
            case "empresa":
                return alunoEmpresa != null ? alunoEmpresa.getText() : "";
            case "descricao_empresa":
                return alunoDescricaoEmpresa != null ? alunoDescricaoEmpresa.getText() : "";
            case "link_repositorio":
            case "repositorio":
                if (alunoLinkRepositorioEditavel != null && alunoLinkRepositorioEditavel.isVisible()) {
                    return alunoLinkRepositorioEditavel.getText();
                }
                return linkRepositorio != null ? linkRepositorio.getText() : "";
            case "problema":
                return alunoProblema != null ? alunoProblema.getText() : "";
            case "solucao":
                return alunoSolucao != null ? alunoSolucao.getText() : "";
            case "tecnologias":
                return alunoTecnologias != null ? alunoTecnologias.getText() : "";
            case "contribuicoes":
                return alunoContribuicoes != null ? alunoContribuicoes.getText() : "";
            case "hard_skills":
                return alunoHardSkills != null ? alunoHardSkills.getText() : "";
            case "soft_skills":
                return alunoSoftSkills != null ? alunoSoftSkills.getText() : "";
            default:
                return "";
        }
    }
    
    private void tornarCampoEditavel(String campo) {
        switch (campo) {
            case "empresa":
                if (alunoEmpresa != null) {
                    alunoEmpresa.setEditable(true);
                    alunoEmpresa.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
                }
                break;
            case "descricao_empresa":
                if (alunoDescricaoEmpresa != null) {
                    alunoDescricaoEmpresa.setEditable(true);
                    alunoDescricaoEmpresa.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "repositorio":
                // Para repositório, mostra TextField editável e esconde Hyperlink
                if (linkRepositorio != null) {
                    linkRepositorio.setVisible(false);
                    linkRepositorio.setManaged(false);
                }
                if (alunoLinkRepositorioEditavel != null) {
                    alunoLinkRepositorioEditavel.setVisible(true);
                    alunoLinkRepositorioEditavel.setManaged(true);
                    alunoLinkRepositorioEditavel.setEditable(true);
                    alunoLinkRepositorioEditavel.setText(valoresOriginais.get("link_repositorio"));
                    alunoLinkRepositorioEditavel.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
                }
                break;
            case "problema":
                if (alunoProblema != null) {
                    alunoProblema.setEditable(true);
                    alunoProblema.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "solucao":
                if (alunoSolucao != null) {
                    alunoSolucao.setEditable(true);
                    alunoSolucao.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "tecnologias":
                if (alunoTecnologias != null) {
                    alunoTecnologias.setEditable(true);
                    alunoTecnologias.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "contribuicoes":
                if (alunoContribuicoes != null) {
                    alunoContribuicoes.setEditable(true);
                    alunoContribuicoes.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "hard_skills":
                if (alunoHardSkills != null) {
                    alunoHardSkills.setEditable(true);
                    alunoHardSkills.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "soft_skills":
                if (alunoSoftSkills != null) {
                    alunoSoftSkills.setEditable(true);
                    alunoSoftSkills.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
        }
    }
    
    private void tornarCampoNaoEditavel(String campo) {
        switch (campo) {
            case "empresa":
                if (alunoEmpresa != null) {
                    alunoEmpresa.setEditable(false);
                    alunoEmpresa.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
                }
                break;
            case "descricao_empresa":
                if (alunoDescricaoEmpresa != null) {
                    alunoDescricaoEmpresa.setEditable(false);
                    alunoDescricaoEmpresa.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "repositorio":
                if (linkRepositorio != null) {
                    linkRepositorio.setVisible(true);
                    linkRepositorio.setManaged(true);
                }
                if (alunoLinkRepositorioEditavel != null) {
                    alunoLinkRepositorioEditavel.setVisible(false);
                    alunoLinkRepositorioEditavel.setManaged(false);
                }
                break;
            case "problema":
                if (alunoProblema != null) {
                    alunoProblema.setEditable(false);
                    alunoProblema.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "solucao":
                if (alunoSolucao != null) {
                    alunoSolucao.setEditable(false);
                    alunoSolucao.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "tecnologias":
                if (alunoTecnologias != null) {
                    alunoTecnologias.setEditable(false);
                    alunoTecnologias.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "contribuicoes":
                if (alunoContribuicoes != null) {
                    alunoContribuicoes.setEditable(false);
                    alunoContribuicoes.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "hard_skills":
                if (alunoHardSkills != null) {
                    alunoHardSkills.setEditable(false);
                    alunoHardSkills.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "soft_skills":
                if (alunoSoftSkills != null) {
                    alunoSoftSkills.setEditable(false);
                    alunoSoftSkills.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
        }
    }
    
    private void configurarListenersModificacao() {
        // Listener para empresa
        if (alunoEmpresa != null) {
            alunoEmpresa.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("empresa").equals(newVal)) {
                    camposModificados.put("empresa", true);
                    aplicarBordaAzul(alunoEmpresa);
                    verificarModificacoes();
                } else {
                    camposModificados.put("empresa", false);
                    removerBordaAzul(alunoEmpresa);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para descricao_empresa
        if (alunoDescricaoEmpresa != null) {
            alunoDescricaoEmpresa.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("descricao_empresa").equals(newVal)) {
                    camposModificados.put("descricao_empresa", true);
                    aplicarBordaAzul(alunoDescricaoEmpresa);
                    verificarModificacoes();
                } else {
                    camposModificados.put("descricao_empresa", false);
                    removerBordaAzul(alunoDescricaoEmpresa);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para link_repositorio
        if (alunoLinkRepositorioEditavel != null) {
            alunoLinkRepositorioEditavel.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("link_repositorio").equals(newVal)) {
                    camposModificados.put("link_repositorio", true);
                    aplicarBordaAzul(alunoLinkRepositorioEditavel);
                    verificarModificacoes();
                } else {
                    camposModificados.put("link_repositorio", false);
                    removerBordaAzul(alunoLinkRepositorioEditavel);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para problema
        if (alunoProblema != null) {
            alunoProblema.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("problema").equals(newVal)) {
                    camposModificados.put("problema", true);
                    aplicarBordaAzul(alunoProblema);
                    verificarModificacoes();
                } else {
                    camposModificados.put("problema", false);
                    removerBordaAzul(alunoProblema);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para solucao
        if (alunoSolucao != null) {
            alunoSolucao.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("solucao").equals(newVal)) {
                    camposModificados.put("solucao", true);
                    aplicarBordaAzul(alunoSolucao);
                    verificarModificacoes();
                } else {
                    camposModificados.put("solucao", false);
                    removerBordaAzul(alunoSolucao);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para tecnologias
        if (alunoTecnologias != null) {
            alunoTecnologias.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("tecnologias").equals(newVal)) {
                    camposModificados.put("tecnologias", true);
                    aplicarBordaAzul(alunoTecnologias);
                    verificarModificacoes();
                } else {
                    camposModificados.put("tecnologias", false);
                    removerBordaAzul(alunoTecnologias);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para contribuicoes
        if (alunoContribuicoes != null) {
            alunoContribuicoes.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("contribuicoes").equals(newVal)) {
                    camposModificados.put("contribuicoes", true);
                    aplicarBordaAzul(alunoContribuicoes);
                    verificarModificacoes();
                } else {
                    camposModificados.put("contribuicoes", false);
                    removerBordaAzul(alunoContribuicoes);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para hard_skills
        if (alunoHardSkills != null) {
            alunoHardSkills.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("hard_skills").equals(newVal)) {
                    camposModificados.put("hard_skills", true);
                    aplicarBordaAzul(alunoHardSkills);
                    verificarModificacoes();
                } else {
                    camposModificados.put("hard_skills", false);
                    removerBordaAzul(alunoHardSkills);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para soft_skills
        if (alunoSoftSkills != null) {
            alunoSoftSkills.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("soft_skills").equals(newVal)) {
                    camposModificados.put("soft_skills", true);
                    aplicarBordaAzul(alunoSoftSkills);
                    verificarModificacoes();
                } else {
                    camposModificados.put("soft_skills", false);
                    removerBordaAzul(alunoSoftSkills);
                    verificarModificacoes();
                }
            });
        }
    }
    
    private void aplicarBordaAzul(javafx.scene.Node campo) {
        if (campo instanceof TextField) {
            TextField tf = (TextField) campo;
            tf.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3498DB; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
        } else if (campo instanceof TextArea) {
            TextArea ta = (TextArea) campo;
            ta.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3498DB; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        }
    }
    
    private void removerBordaAzul(javafx.scene.Node campo) {
        if (campo instanceof TextField) {
            TextField tf = (TextField) campo;
            tf.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
        } else if (campo instanceof TextArea) {
            TextArea ta = (TextArea) campo;
            ta.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        }
    }
    
    private void verificarModificacoes() {
        boolean temModificacoes = camposModificados.values().stream().anyMatch(modificado -> modificado);
        if (btnEnviarNovaVersao != null) {
            btnEnviarNovaVersao.setVisible(temModificacoes);
            btnEnviarNovaVersao.setManaged(temModificacoes);
        }
    }
    
    @FXML
    private void enviarNovaVersao(ActionEvent event) {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        
        // Busca a próxima versão disponível
        int proximaVersao = SecaoApi.getProximaVersao(
            secaoApi.getEmailAluno(), 
            secaoApi.getSemestreCurso(), 
            secaoApi.getAno(), 
            secaoApi.getSemestreAno()
        );
        
        // Coleta valores atuais (modificados ou não)
        String empresa = alunoEmpresa != null ? alunoEmpresa.getText() : valoresOriginais.get("empresa");
        String descricaoEmpresa = alunoDescricaoEmpresa != null ? alunoDescricaoEmpresa.getText() : valoresOriginais.get("descricao_empresa");
        String linkRepositorio = alunoLinkRepositorioEditavel != null && alunoLinkRepositorioEditavel.isVisible() 
            ? alunoLinkRepositorioEditavel.getText() 
            : valoresOriginais.get("link_repositorio");
        String problema = alunoProblema != null ? alunoProblema.getText() : valoresOriginais.get("problema");
        String solucao = alunoSolucao != null ? alunoSolucao.getText() : valoresOriginais.get("solucao");
        String tecnologias = alunoTecnologias != null ? alunoTecnologias.getText() : valoresOriginais.get("tecnologias");
        String contribuicoes = alunoContribuicoes != null ? alunoContribuicoes.getText() : valoresOriginais.get("contribuicoes");
        String hardSkills = alunoHardSkills != null ? alunoHardSkills.getText() : valoresOriginais.get("hard_skills");
        String softSkills = alunoSoftSkills != null ? alunoSoftSkills.getText() : valoresOriginais.get("soft_skills");
        
        // Cria e cadastra nova versão
        SecaoApi novaSecaoApi = new SecaoApi(
            secaoApi.getEmailAluno(),
            secaoApi.getSemestreCurso(),
            secaoApi.getAno(),
            secaoApi.getSemestreAno(),
            proximaVersao,
            empresa,
            descricaoEmpresa,
            problema,
            solucao,
            linkRepositorio,
            tecnologias,
            contribuicoes,
            hardSkills,
            softSkills
        );
        
        try {
            novaSecaoApi.cadastrar();
            
            // Mostra mensagem de sucesso
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Nova versão enviada com sucesso!");
            alert.showAndWait();
            
            // Recarrega a tela com a nova versão
            setIdentificadorSecao(
                secaoApi.getEmailAluno(),
                secaoApi.getSemestreCurso(),
                secaoApi.getAno(),
                secaoApi.getSemestreAno(),
                proximaVersao
            );
            
        } catch (Exception e) {
            mostrarErro("Erro ao enviar nova versão", e);
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
