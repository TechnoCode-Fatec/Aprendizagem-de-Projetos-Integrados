package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApresentacao;
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
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class AlunoFeedbackApresentacaoController {

    // Identificador da seção usando classe modelo
    private SecaoApresentacao secaoApresentacao;

    // TextAreas com dados do aluno
    @FXML private TextArea alunoTextNome;
    @FXML private TextArea alunoTextIdade;
    @FXML private TextArea alunoTextCurso;
    @FXML private TextArea alunoTextMotivacao;
    @FXML private TextArea alunoTextHistorico;
    @FXML private TextArea alunoTextHistoricoProfissional;
    @FXML private Hyperlink linkGithub;
    @FXML private Hyperlink linkLinkedin;
    @FXML private TextField alunoLinkGithubEditavel; // Campo editável quando recusado
    @FXML private TextField alunoLinkLinkedinEditavel; // Campo editável quando recusado
    @FXML private TextArea alunoTextConhecimentos;
    
    // Valores originais para detectar modificações
    private Map<String, String> valoresOriginais = new HashMap<>();
    
    // Valores da versão anterior para comparação quando for nova versão
    private Map<String, String> valoresVersaoAnterior = new HashMap<>();
    
    // Campos que foram modificados
    private Map<String, Boolean> camposModificados = new HashMap<>();

    // TextAreas com feedbacks
    @FXML private TextArea feedbackNome;
    @FXML private TextArea feedbackIdade;
    @FXML private TextArea feedbackCurso;
    @FXML private TextArea feedbackMotivacao;
    @FXML private TextArea feedbackHistorico;
    @FXML private TextArea feedbackHistoricoProfissional;
    @FXML private TextArea feedbackGithub;
    @FXML private TextArea feedbackLinkedin;
    @FXML private TextArea feedbackConhecimentos;

    // Labels de status
    @FXML private Label statusNome;
    @FXML private Label statusIdade;
    @FXML private Label statusCurso;
    @FXML private Label statusMotivacao;
    @FXML private Label statusHistorico;
    @FXML private Label statusHistoricoProfissional;
    @FXML private Label statusGithub;
    @FXML private Label statusLinkedin;
    @FXML private Label statusConhecimentos;

    // Containers de feedback expansíveis
    @FXML private VBox containerFeedbackNome;
    @FXML private VBox containerFeedbackIdade;
    @FXML private VBox containerFeedbackCurso;
    @FXML private VBox containerFeedbackMotivacao;
    @FXML private VBox containerFeedbackHistorico;
    @FXML private VBox containerFeedbackHistoricoProfissional;
    @FXML private VBox containerFeedbackGithub;
    @FXML private VBox containerFeedbackLinkedin;
    @FXML private VBox containerFeedbackConhecimentos;

    // Botões de expandir
    @FXML private Button btnExpandNome;
    @FXML private Button btnExpandIdade;
    @FXML private Button btnExpandCurso;
    @FXML private Button btnExpandMotivacao;
    @FXML private Button btnExpandHistorico;
    @FXML private Button btnExpandHistoricoProfissional;
    @FXML private Button btnExpandGithub;
    @FXML private Button btnExpandLinkedin;
    @FXML private Button btnExpandConhecimentos;

    @FXML private Button btnNovaVersao;
    @FXML private Button btnEnviarNovaVersao;

    // Mapa para controlar estado de expansão
    private Map<String, Boolean> estadosExpansao = new HashMap<>();

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        // Cria objeto SecaoApresentacao para identificar a seção
        this.secaoApresentacao = new SecaoApresentacao(aluno, versao);
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
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;
        String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, secaoApresentacao.getVersao());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome") != null ? rs.getString("nome") : "";
                    String dataNascimentoStr = rs.getString("idade");
                    String idadeStr = "";
                    if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                        try {
                            LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);
                            LocalDate hoje = LocalDate.now();
                            int idade = Period.between(dataNascimento, hoje).getYears();
                            idadeStr = String.valueOf(idade);
                        } catch (Exception e) {
                            idadeStr = dataNascimentoStr;
                        }
                    }
                    String curso = rs.getString("curso") != null ? rs.getString("curso") : "";
                    String motivacao = rs.getString("motivacao") != null ? rs.getString("motivacao") : "";
                    String historico = rs.getString("historico") != null ? rs.getString("historico") : "";
                    String historicoProfissional = rs.getString("historico_profissional") != null ? rs.getString("historico_profissional") : "";
                    String linkGithubStr = rs.getString("link_github") != null ? rs.getString("link_github") : "";
                    String linkLinkedinStr = rs.getString("link_linkedin") != null ? rs.getString("link_linkedin") : "";
                    String conhecimentos = rs.getString("principais_conhecimentos") != null ? rs.getString("principais_conhecimentos") : "";
                    
                    // Armazena valores originais
                    valoresOriginais.put("nome", nome);
                    valoresOriginais.put("idade", idadeStr);
                    valoresOriginais.put("curso", curso);
                    valoresOriginais.put("motivacao", motivacao);
                    valoresOriginais.put("historico", historico);
                    valoresOriginais.put("historico_profissional", historicoProfissional);
                    valoresOriginais.put("link_github", linkGithubStr);
                    valoresOriginais.put("link_linkedin", linkLinkedinStr);
                    valoresOriginais.put("conhecimentos", conhecimentos);
                    
                    if (alunoTextNome != null) alunoTextNome.setText(nome);
                    if (alunoTextIdade != null) alunoTextIdade.setText(idadeStr);
                    if (alunoTextCurso != null) alunoTextCurso.setText(curso);
                    if (alunoTextMotivacao != null) alunoTextMotivacao.setText(motivacao);
                    if (alunoTextHistorico != null) alunoTextHistorico.setText(historico);
                    if (alunoTextHistoricoProfissional != null) alunoTextHistoricoProfissional.setText(historicoProfissional);
                    
                    // Configura links do GitHub e LinkedIn (será configurado depois baseado no status)
                    if (linkGithub != null && linkGithubStr != null && !linkGithubStr.trim().isEmpty()) {
                        linkGithub.setText(linkGithubStr);
                        final String linkGithubFinal = linkGithubStr;
                        linkGithub.setOnAction(e -> abrirURL(linkGithubFinal, linkGithub));
                    } else if (linkGithub != null) {
                        linkGithub.setText("Nenhum link disponível");
                        linkGithub.setDisable(true);
                    }
                    
                    if (linkLinkedin != null && linkLinkedinStr != null && !linkLinkedinStr.trim().isEmpty()) {
                        linkLinkedin.setText(linkLinkedinStr);
                        final String linkLinkedinFinal = linkLinkedinStr;
                        linkLinkedin.setOnAction(e -> abrirURL(linkLinkedinFinal, linkLinkedin));
                    } else if (linkLinkedin != null) {
                        linkLinkedin.setText("Nenhum link disponível");
                        linkLinkedin.setDisable(true);
                    }
                    
                    // Inicializa TextFields editáveis (serão mostrados apenas se recusados)
                    if (alunoLinkGithubEditavel != null) {
                        alunoLinkGithubEditavel.setText(linkGithubStr);
                        alunoLinkGithubEditavel.setVisible(false);
                        alunoLinkGithubEditavel.setManaged(false);
                    }
                    if (alunoLinkLinkedinEditavel != null) {
                        alunoLinkLinkedinEditavel.setText(linkLinkedinStr);
                        alunoLinkLinkedinEditavel.setVisible(false);
                        alunoLinkLinkedinEditavel.setManaged(false);
                    }
                    
                    if (alunoTextConhecimentos != null) alunoTextConhecimentos.setText(conhecimentos);
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

    // Carrega dados do feedback da secao_apresentacao
    private void carregarFeedback() {
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;
        
        // Primeiro tenta carregar feedback da versão atual
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
                "status_historico_profissional, feedback_historico_profissional, " +
                "status_github, feedback_github, " +
                "status_linkedin, feedback_linkedin, " +
                "status_conhecimentos, feedback_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, secaoApresentacao.getVersao());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Verifica se há algum feedback na versão atual
                    boolean temFeedback = false;
                    for (String campo : new String[]{"nome", "idade", "curso", "motivacao", "historico", 
                            "historico_profissional", "github", "linkedin", "conhecimentos"}) {
                        if (rs.getString("status_" + campo) != null) {
                            temFeedback = true;
                            break;
                        }
                    }
                    
                    if (temFeedback) {
                        // Carrega feedback da versão atual
                        carregarCampoFeedback("nome", rs, feedbackNome, statusNome, containerFeedbackNome, btnExpandNome, false);
                        carregarCampoFeedback("idade", rs, feedbackIdade, statusIdade, containerFeedbackIdade, btnExpandIdade, false);
                        carregarCampoFeedback("curso", rs, feedbackCurso, statusCurso, containerFeedbackCurso, btnExpandCurso, false);
                        carregarCampoFeedback("motivacao", rs, feedbackMotivacao, statusMotivacao, containerFeedbackMotivacao, btnExpandMotivacao, false);
                        carregarCampoFeedback("historico", rs, feedbackHistorico, statusHistorico, containerFeedbackHistorico, btnExpandHistorico, false);
                        carregarCampoFeedback("historico_profissional", rs, feedbackHistoricoProfissional, statusHistoricoProfissional, containerFeedbackHistoricoProfissional, btnExpandHistoricoProfissional, false);
                        carregarCampoFeedback("github", rs, feedbackGithub, statusGithub, containerFeedbackGithub, btnExpandGithub, false);
                        carregarCampoFeedback("linkedin", rs, feedbackLinkedin, statusLinkedin, containerFeedbackLinkedin, btnExpandLinkedin, false);
                        carregarCampoFeedback("conhecimentos", rs, feedbackConhecimentos, statusConhecimentos, containerFeedbackConhecimentos, btnExpandConhecimentos, false);
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
        if (secaoApresentacao == null || secaoApresentacao.getVersao() <= 1) {
            // Não há versão anterior, todos os campos ficam sem avaliação
            inicializarCamposSemAvaliacao();
            return;
        }
        
        int versaoAnterior = secaoApresentacao.getVersao() - 1;
        
        // Primeiro carrega os valores da versão anterior para comparação
        String sqlValores = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sqlValores)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, versaoAnterior);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String dataNascimentoStr = rs.getString("idade");
                    String idadeStr = "";
                    if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                        try {
                            LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);
                            LocalDate hoje = LocalDate.now();
                            int idade = Period.between(dataNascimento, hoje).getYears();
                            idadeStr = String.valueOf(idade);
                        } catch (Exception e) {
                            idadeStr = dataNascimentoStr;
                        }
                    }
                    
                    valoresVersaoAnterior.put("nome", rs.getString("nome") != null ? rs.getString("nome") : "");
                    valoresVersaoAnterior.put("idade", idadeStr);
                    valoresVersaoAnterior.put("curso", rs.getString("curso") != null ? rs.getString("curso") : "");
                    valoresVersaoAnterior.put("motivacao", rs.getString("motivacao") != null ? rs.getString("motivacao") : "");
                    valoresVersaoAnterior.put("historico", rs.getString("historico") != null ? rs.getString("historico") : "");
                    valoresVersaoAnterior.put("historico_profissional", rs.getString("historico_profissional") != null ? rs.getString("historico_profissional") : "");
                    valoresVersaoAnterior.put("github", rs.getString("link_github") != null ? rs.getString("link_github") : "");
                    valoresVersaoAnterior.put("linkedin", rs.getString("link_linkedin") != null ? rs.getString("link_linkedin") : "");
                    valoresVersaoAnterior.put("conhecimentos", rs.getString("principais_conhecimentos") != null ? rs.getString("principais_conhecimentos") : "");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar valores da versão anterior: " + e.getMessage());
        }
        
        // Agora carrega os feedbacks da versão anterior
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
                "status_historico_profissional, feedback_historico_profissional, " +
                "status_github, feedback_github, " +
                "status_linkedin, feedback_linkedin, " +
                "status_conhecimentos, feedback_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, versaoAnterior);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Carrega feedback da versão anterior, mas verifica se o campo foi modificado
                    carregarCampoFeedback("nome", rs, feedbackNome, statusNome, containerFeedbackNome, btnExpandNome, true);
                    carregarCampoFeedback("idade", rs, feedbackIdade, statusIdade, containerFeedbackIdade, btnExpandIdade, true);
                    carregarCampoFeedback("curso", rs, feedbackCurso, statusCurso, containerFeedbackCurso, btnExpandCurso, true);
                    carregarCampoFeedback("motivacao", rs, feedbackMotivacao, statusMotivacao, containerFeedbackMotivacao, btnExpandMotivacao, true);
                    carregarCampoFeedback("historico", rs, feedbackHistorico, statusHistorico, containerFeedbackHistorico, btnExpandHistorico, true);
                    carregarCampoFeedback("historico_profissional", rs, feedbackHistoricoProfissional, statusHistoricoProfissional, containerFeedbackHistoricoProfissional, btnExpandHistoricoProfissional, true);
                    carregarCampoFeedback("github", rs, feedbackGithub, statusGithub, containerFeedbackGithub, btnExpandGithub, true);
                    carregarCampoFeedback("linkedin", rs, feedbackLinkedin, statusLinkedin, containerFeedbackLinkedin, btnExpandLinkedin, true);
                    carregarCampoFeedback("conhecimentos", rs, feedbackConhecimentos, statusConhecimentos, containerFeedbackConhecimentos, btnExpandConhecimentos, true);
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
        String[] campos = {"nome", "idade", "curso", "motivacao", "historico", 
                          "historico_profissional", "github", "linkedin", "conhecimentos"};
        for (String campo : campos) {
            estadosExpansao.put(campo, false);
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
            return;
        }
        
        // Inicializa estado de expansão
        estadosExpansao.put(campo, false);
        
        if (status != null) {
            // Se é versão anterior, adiciona indicador visual
            String textoStatus = isVersaoAnterior ? status + " (v." + (secaoApresentacao.getVersao() - 1) + ")" : status;
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
                // Torna campo editável se recusado
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
            case "nome":
                return alunoTextNome != null ? alunoTextNome.getText() : "";
            case "idade":
                return alunoTextIdade != null ? alunoTextIdade.getText() : "";
            case "curso":
                return alunoTextCurso != null ? alunoTextCurso.getText() : "";
            case "motivacao":
                return alunoTextMotivacao != null ? alunoTextMotivacao.getText() : "";
            case "historico":
                return alunoTextHistorico != null ? alunoTextHistorico.getText() : "";
            case "historico_profissional":
                return alunoTextHistoricoProfissional != null ? alunoTextHistoricoProfissional.getText() : "";
            case "github":
                if (alunoLinkGithubEditavel != null && alunoLinkGithubEditavel.isVisible()) {
                    return alunoLinkGithubEditavel.getText();
                }
                return linkGithub != null ? linkGithub.getText() : "";
            case "linkedin":
                if (alunoLinkLinkedinEditavel != null && alunoLinkLinkedinEditavel.isVisible()) {
                    return alunoLinkLinkedinEditavel.getText();
                }
                return linkLinkedin != null ? linkLinkedin.getText() : "";
            case "conhecimentos":
                return alunoTextConhecimentos != null ? alunoTextConhecimentos.getText() : "";
            default:
                return "";
        }
    }
    
    private void tornarCampoEditavel(String campo) {
        switch (campo) {
            case "nome":
                if (alunoTextNome != null) {
                    alunoTextNome.setEditable(true);
                    alunoTextNome.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "idade":
                if (alunoTextIdade != null) {
                    alunoTextIdade.setEditable(true);
                    alunoTextIdade.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "curso":
                if (alunoTextCurso != null) {
                    alunoTextCurso.setEditable(true);
                    alunoTextCurso.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "motivacao":
                if (alunoTextMotivacao != null) {
                    alunoTextMotivacao.setEditable(true);
                    alunoTextMotivacao.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "historico":
                if (alunoTextHistorico != null) {
                    alunoTextHistorico.setEditable(true);
                    alunoTextHistorico.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "historico_profissional":
                if (alunoTextHistoricoProfissional != null) {
                    alunoTextHistoricoProfissional.setEditable(true);
                    alunoTextHistoricoProfissional.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
            case "github":
                // Para GitHub, mostra TextField editável e esconde Hyperlink
                if (linkGithub != null) {
                    linkGithub.setVisible(false);
                    linkGithub.setManaged(false);
                }
                if (alunoLinkGithubEditavel != null) {
                    alunoLinkGithubEditavel.setVisible(true);
                    alunoLinkGithubEditavel.setManaged(true);
                    alunoLinkGithubEditavel.setEditable(true);
                    alunoLinkGithubEditavel.setText(valoresOriginais.get("link_github"));
                    alunoLinkGithubEditavel.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
                }
                break;
            case "linkedin":
                // Para LinkedIn, mostra TextField editável e esconde Hyperlink
                if (linkLinkedin != null) {
                    linkLinkedin.setVisible(false);
                    linkLinkedin.setManaged(false);
                }
                if (alunoLinkLinkedinEditavel != null) {
                    alunoLinkLinkedinEditavel.setVisible(true);
                    alunoLinkLinkedinEditavel.setManaged(true);
                    alunoLinkLinkedinEditavel.setEditable(true);
                    alunoLinkLinkedinEditavel.setText(valoresOriginais.get("link_linkedin"));
                    alunoLinkLinkedinEditavel.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
                }
                break;
            case "conhecimentos":
                if (alunoTextConhecimentos != null) {
                    alunoTextConhecimentos.setEditable(true);
                    alunoTextConhecimentos.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
                }
                break;
        }
    }
    
    private void configurarListenersModificacao() {
        // Listener para nome
        if (alunoTextNome != null) {
            alunoTextNome.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("nome").equals(newVal)) {
                    camposModificados.put("nome", true);
                    aplicarBordaAzul(alunoTextNome);
                    verificarModificacoes();
                } else {
                    camposModificados.put("nome", false);
                    removerBordaAzul(alunoTextNome);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para idade
        if (alunoTextIdade != null) {
            alunoTextIdade.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("idade").equals(newVal)) {
                    camposModificados.put("idade", true);
                    aplicarBordaAzul(alunoTextIdade);
                    verificarModificacoes();
                } else {
                    camposModificados.put("idade", false);
                    removerBordaAzul(alunoTextIdade);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para curso
        if (alunoTextCurso != null) {
            alunoTextCurso.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("curso").equals(newVal)) {
                    camposModificados.put("curso", true);
                    aplicarBordaAzul(alunoTextCurso);
                    verificarModificacoes();
                } else {
                    camposModificados.put("curso", false);
                    removerBordaAzul(alunoTextCurso);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para motivacao
        if (alunoTextMotivacao != null) {
            alunoTextMotivacao.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("motivacao").equals(newVal)) {
                    camposModificados.put("motivacao", true);
                    aplicarBordaAzul(alunoTextMotivacao);
                    verificarModificacoes();
                } else {
                    camposModificados.put("motivacao", false);
                    removerBordaAzul(alunoTextMotivacao);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para historico
        if (alunoTextHistorico != null) {
            alunoTextHistorico.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("historico").equals(newVal)) {
                    camposModificados.put("historico", true);
                    aplicarBordaAzul(alunoTextHistorico);
                    verificarModificacoes();
                } else {
                    camposModificados.put("historico", false);
                    removerBordaAzul(alunoTextHistorico);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para historico_profissional
        if (alunoTextHistoricoProfissional != null) {
            alunoTextHistoricoProfissional.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("historico_profissional").equals(newVal)) {
                    camposModificados.put("historico_profissional", true);
                    aplicarBordaAzul(alunoTextHistoricoProfissional);
                    verificarModificacoes();
                } else {
                    camposModificados.put("historico_profissional", false);
                    removerBordaAzul(alunoTextHistoricoProfissional);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para link_github
        if (alunoLinkGithubEditavel != null) {
            alunoLinkGithubEditavel.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("link_github").equals(newVal)) {
                    camposModificados.put("link_github", true);
                    aplicarBordaAzul(alunoLinkGithubEditavel);
                    verificarModificacoes();
                } else {
                    camposModificados.put("link_github", false);
                    removerBordaAzul(alunoLinkGithubEditavel);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para link_linkedin
        if (alunoLinkLinkedinEditavel != null) {
            alunoLinkLinkedinEditavel.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("link_linkedin").equals(newVal)) {
                    camposModificados.put("link_linkedin", true);
                    aplicarBordaAzul(alunoLinkLinkedinEditavel);
                    verificarModificacoes();
                } else {
                    camposModificados.put("link_linkedin", false);
                    removerBordaAzul(alunoLinkLinkedinEditavel);
                    verificarModificacoes();
                }
            });
        }
        
        // Listener para conhecimentos
        if (alunoTextConhecimentos != null) {
            alunoTextConhecimentos.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!valoresOriginais.get("conhecimentos").equals(newVal)) {
                    camposModificados.put("conhecimentos", true);
                    aplicarBordaAzul(alunoTextConhecimentos);
                    verificarModificacoes();
                } else {
                    camposModificados.put("conhecimentos", false);
                    removerBordaAzul(alunoTextConhecimentos);
                    verificarModificacoes();
                }
            });
        }
    }
    
    private void aplicarBordaAzul(javafx.scene.Node campo) {
        if (campo instanceof javafx.scene.control.TextField) {
            javafx.scene.control.TextField tf = (javafx.scene.control.TextField) campo;
            tf.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3498DB; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-text-fill: #2C3E50;");
        } else if (campo instanceof TextArea) {
            TextArea ta = (TextArea) campo;
            ta.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3498DB; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        }
    }
    
    private void removerBordaAzul(javafx.scene.Node campo) {
        if (campo instanceof javafx.scene.control.TextField) {
            javafx.scene.control.TextField tf = (javafx.scene.control.TextField) campo;
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
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;
        
        // Busca a próxima versão disponível
        int proximaVersao = SecaoApresentacao.getProximaVersao(secaoApresentacao.getEmailAluno());
        
        // Coleta valores atuais (modificados ou não)
        String nome = alunoTextNome != null ? alunoTextNome.getText() : valoresOriginais.get("nome");
        String idade = alunoTextIdade != null ? alunoTextIdade.getText() : valoresOriginais.get("idade");
        String curso = alunoTextCurso != null ? alunoTextCurso.getText() : valoresOriginais.get("curso");
        String motivacao = alunoTextMotivacao != null ? alunoTextMotivacao.getText() : valoresOriginais.get("motivacao");
        String historico = alunoTextHistorico != null ? alunoTextHistorico.getText() : valoresOriginais.get("historico");
        String historicoProfissional = alunoTextHistoricoProfissional != null ? alunoTextHistoricoProfissional.getText() : valoresOriginais.get("historico_profissional");
        String github = alunoLinkGithubEditavel != null && alunoLinkGithubEditavel.isVisible() 
            ? alunoLinkGithubEditavel.getText() 
            : valoresOriginais.get("link_github");
        String linkedin = alunoLinkLinkedinEditavel != null && alunoLinkLinkedinEditavel.isVisible() 
            ? alunoLinkLinkedinEditavel.getText() 
            : valoresOriginais.get("link_linkedin");
        String conhecimentos = alunoTextConhecimentos != null ? alunoTextConhecimentos.getText() : valoresOriginais.get("conhecimentos");
        
        // Converte idade de volta para data de nascimento se necessário
        java.sql.Date dataNascimento;
        try {
            // Se idade é um número, calcula data de nascimento
            int idadeInt = Integer.parseInt(idade);
            LocalDate hoje = LocalDate.now();
            LocalDate dataNasc = hoje.minusYears(idadeInt);
            dataNascimento = java.sql.Date.valueOf(dataNasc);
        } catch (NumberFormatException e) {
            // Se não for número, tenta parsear como data
            try {
                dataNascimento = java.sql.Date.valueOf(idade);
            } catch (Exception ex) {
                // Se falhar, usa data padrão (hoje menos 20 anos)
                LocalDate hoje = LocalDate.now();
                LocalDate dataNasc = hoje.minusYears(20);
                dataNascimento = java.sql.Date.valueOf(dataNasc);
            }
        }
        
        // Cria e cadastra nova versão
        SecaoApresentacao novaSecaoApresentacao = new SecaoApresentacao(
            secaoApresentacao.getEmailAluno(),
            nome,
            dataNascimento,
            curso,
            proximaVersao,
            motivacao,
            historico,
            historicoProfissional,
            github,
            linkedin,
            conhecimentos
        );
        
        try {
            novaSecaoApresentacao.cadastrar();
            
            // Mostra mensagem de sucesso
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Nova versão enviada com sucesso!");
            alert.showAndWait();
            
            // Recarrega a tela com a nova versão
            setIdentificadorSecao(
                secaoApresentacao.getEmailAluno(),
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
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;

        String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos " +
                     "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, secaoApresentacao.getEmailAluno());
            ps.setInt(2, secaoApresentacao.getVersao());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String dataNascimento = rs.getString("idade");
                    String curso = rs.getString("curso");
                    String motivacao = rs.getString("motivacao");
                    String historico = rs.getString("historico");
                    String historicoProfissional = rs.getString("historico_profissional");
                    String github = rs.getString("link_github");
                    String linkedin = rs.getString("link_linkedin");
                    String conhecimentos = rs.getString("principais_conhecimentos");
                    
                    NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/formulario-apresentacao.fxml",
                        controller -> {
                            if (controller instanceof FormularioApresentacaoController) {
                                ((FormularioApresentacaoController) controller).setDadosVersaoAnterior(
                                    nome, dataNascimento, curso, motivacao,
                                    historico, historicoProfissional, github, linkedin, conhecimentos
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
