package com.example.technocode.Controllers.Orientador;

import com.example.technocode.model.dao.Connector;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.SecaoApresentacao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URI;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class OrientadorCorrigirApresentacaoController {

    // Identificador da seção usando classe modelo
    private SecaoApresentacao secaoApresentacao;

    // Status por campo (Aprovado | Revisar | null)
    private final Map<String, String> statusPorCampo = new HashMap<>();

    @FXML private TextArea alunoTextNome;
    @FXML private TextArea alunoTextIdade;
    @FXML private TextArea alunoTextCurso;
    @FXML private TextArea alunoTextMotivacao;
    @FXML private TextArea alunoTextHistorico;
    @FXML private TextArea alunoTextHistoricoProfissional;
    @FXML private Hyperlink alunoTextGithub;
    @FXML private Hyperlink alunoTextLinkedin;
    @FXML private TextArea alunoTextConhecimentos;


    @FXML private TextArea feedbackTextNome;
    @FXML private TextArea feedbackTextIdade;
    @FXML private TextArea feedbackTextCurso;
    @FXML private TextArea feedbackTextMotivacao;
    @FXML private TextArea feedbackTextHistorico;
    @FXML private TextArea feedbackTextHistoricoProfissional;
    @FXML private TextArea feedbackTextGithub;
    @FXML private TextArea feedbackTextLinkedin;
    @FXML private TextArea feedbackTextConhecimentos;
    
    // Containers para feedback
    @FXML private javafx.scene.layout.VBox containerFeedbackNome;
    @FXML private javafx.scene.layout.VBox containerFeedbackIdade;
    @FXML private javafx.scene.layout.VBox containerFeedbackCurso;
    @FXML private javafx.scene.layout.VBox containerFeedbackMotivacao;
    @FXML private javafx.scene.layout.VBox containerFeedbackHistorico;
    @FXML private javafx.scene.layout.VBox containerFeedbackHistoricoProfissional;
    @FXML private javafx.scene.layout.VBox containerFeedbackGithub;
    @FXML private javafx.scene.layout.VBox containerFeedbackLinkedin;
    @FXML private javafx.scene.layout.VBox containerFeedbackConhecimentos;
    
    // Labels para indicar versão do feedback
    @FXML private javafx.scene.control.Label labelVersaoNome;
    @FXML private javafx.scene.control.Label labelVersaoIdade;
    @FXML private javafx.scene.control.Label labelVersaoCurso;
    @FXML private javafx.scene.control.Label labelVersaoMotivacao;
    @FXML private javafx.scene.control.Label labelVersaoHistorico;
    @FXML private javafx.scene.control.Label labelVersaoHistoricoProfissional;
    @FXML private javafx.scene.control.Label labelVersaoGithub;
    @FXML private javafx.scene.control.Label labelVersaoLinkedin;
    @FXML private javafx.scene.control.Label labelVersaoConhecimentos;

    @FXML
    public void initialize() {
        // Inicializa todas as TextAreas como invisíveis
        if (feedbackTextNome != null) feedbackTextNome.setVisible(false);
        if (feedbackTextIdade != null) feedbackTextIdade.setVisible(false);
        if (feedbackTextCurso != null) feedbackTextCurso.setVisible(false);
        if (feedbackTextMotivacao != null) feedbackTextMotivacao.setVisible(false);
        if (feedbackTextHistorico != null) feedbackTextHistorico.setVisible(false);
        if (feedbackTextHistoricoProfissional != null) feedbackTextHistoricoProfissional.setVisible(false);
        if (feedbackTextGithub != null) feedbackTextGithub.setVisible(false);
        if (feedbackTextLinkedin != null) feedbackTextLinkedin.setVisible(false);
        if (feedbackTextConhecimentos != null) feedbackTextConhecimentos.setVisible(false);
    }

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        // Cria objeto SecaoApresentacao para identificar a seção
        this.secaoApresentacao = new SecaoApresentacao(aluno, versao);
        carregarSecaoAluno();
    }

    // 1) Carrega dados da secao_apresentacao
    public void carregarSecaoAluno() {
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;
        String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, secaoApresentacao.getVersao());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String dataNascimentoStr = rs.getString("idade");
                    String curso = rs.getString("curso");
                    String motivacao = rs.getString("motivacao");
                    String historico = rs.getString("historico");
                    String historicoProfissional = rs.getString("historico_profissional");
                    String github = rs.getString("link_github");
                    String linkedin = rs.getString("link_linkedin");
                    String conhecimentos = rs.getString("principais_conhecimentos");
                    
                    if (alunoTextNome != null) alunoTextNome.setText(nome != null ? nome : "");
                    if (alunoTextIdade != null) {
                        if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                            try {
                                LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);
                                LocalDate hoje = LocalDate.now();
                                int idade = Period.between(dataNascimento, hoje).getYears();
                                alunoTextIdade.setText(idade + " anos");
                            } catch (Exception e) {
                                alunoTextIdade.setText(dataNascimentoStr);
                            }
                        } else {
                            alunoTextIdade.setText("");
                        }
                    }
                    if (alunoTextCurso != null) alunoTextCurso.setText(curso != null ? curso : "");
                    if (alunoTextMotivacao != null) alunoTextMotivacao.setText(motivacao != null ? motivacao : "");
                    if (alunoTextHistorico != null) alunoTextHistorico.setText(historico != null ? historico : "");
                    if (alunoTextHistoricoProfissional != null) alunoTextHistoricoProfissional.setText(historicoProfissional != null ? historicoProfissional : "");
                    if (alunoTextGithub != null) {
                        String githubUrl = github != null ? github : "";
                        if (githubUrl.isEmpty()) {
                            alunoTextGithub.setText("N/A");
                            alunoTextGithub.setDisable(true);
                            alunoTextGithub.setStyle("-fx-text-fill: #95A5A6; -fx-underline: false;");
                        } else {
                            alunoTextGithub.setText(githubUrl);
                            alunoTextGithub.setDisable(false);
                            alunoTextGithub.setStyle("-fx-text-fill: #3498DB; -fx-underline: true;");
                            alunoTextGithub.setOnAction(e -> abrirLink(githubUrl));
                        }
                    }
                    if (alunoTextLinkedin != null) {
                        String linkedinUrl = linkedin != null ? linkedin : "";
                        if (linkedinUrl.isEmpty()) {
                            alunoTextLinkedin.setText("N/A");
                            alunoTextLinkedin.setDisable(true);
                            alunoTextLinkedin.setStyle("-fx-text-fill: #95A5A6; -fx-underline: false;");
                        } else {
                            alunoTextLinkedin.setText(linkedinUrl);
                            alunoTextLinkedin.setDisable(false);
                            alunoTextLinkedin.setStyle("-fx-text-fill: #3498DB; -fx-underline: true;");
                            alunoTextLinkedin.setOnAction(e -> abrirLink(linkedinUrl));
                        }
                    }
                    if (alunoTextConhecimentos != null) alunoTextConhecimentos.setText(conhecimentos != null ? conhecimentos : "");
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
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
                "status_github, feedback_github, " +
                "status_linkedin, feedback_linkedin, " +
                "status_conhecimentos, feedback_conhecimentos, " +
                "status_historico_profissional, feedback_historico_profissional " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, secaoApresentacao.getVersao());
            try (ResultSet rs = pst.executeQuery()) {
                boolean temFeedbackAtual = false;
                if (rs.next()) {
                    // Verifica se há algum feedback na versão atual
                    String[] statusFields = {"status_nome", "status_idade", "status_curso", "status_motivacao", 
                                             "status_historico", "status_historico_profissional", 
                                             "status_github", "status_linkedin", "status_conhecimentos"};
                    for (String field : statusFields) {
                        if (rs.getString(field) != null) {
                            temFeedbackAtual = true;
                            break;
                        }
                    }
                    
                    if (temFeedbackAtual) {
                        // Carrega feedback da versão atual
                        try {
                            carregarCampoFeedbackExistente("nome", rs, feedbackTextNome, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback nome: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("idade", rs, feedbackTextIdade, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback idade: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("curso", rs, feedbackTextCurso, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback curso: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("motivacao", rs, feedbackTextMotivacao, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback motivacao: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("historico", rs, feedbackTextHistorico, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback historico: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("historico_profissional", rs, feedbackTextHistoricoProfissional, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback historico_profissional: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("github", rs, feedbackTextGithub, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback github: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("linkedin", rs, feedbackTextLinkedin, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback linkedin: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("conhecimentos", rs, feedbackTextConhecimentos, false, secaoApresentacao.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback conhecimentos: " + e.getMessage());
                        }
                    }
                }
                
                // Se não há feedback na versão atual, carrega da versão anterior
                if (!temFeedbackAtual && secaoApresentacao.getVersao() > 1) {
                    carregarFeedbackVersaoAnteriorParaReuso();
                }
            }
        } catch (SQLException e) {
            // Não é erro crítico se não houver feedback existente, mas loga o erro
            System.err.println("Erro ao carregar feedback existente: " + e.getMessage());
        }
    }
    
    // Carrega feedback da versão anterior para reutilização quando não há feedback na versão atual
    private void carregarFeedbackVersaoAnteriorParaReuso() {
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null || secaoApresentacao.getVersao() <= 1) {
            return;
        }
        
        int versaoAnterior = secaoApresentacao.getVersao() - 1;
        // Indica que estamos carregando feedback da versão anterior
        boolean isVersaoAnterior = true;
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
                "status_github, feedback_github, " +
                "status_linkedin, feedback_linkedin, " +
                "status_conhecimentos, feedback_conhecimentos, " +
                "status_historico_profissional, feedback_historico_profissional " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, versaoAnterior);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Carrega feedback da versão anterior
                    try {
                        carregarCampoFeedbackExistente("nome", rs, feedbackTextNome, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback nome da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("idade", rs, feedbackTextIdade, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback idade da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("curso", rs, feedbackTextCurso, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback curso da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("motivacao", rs, feedbackTextMotivacao, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback motivacao da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("historico", rs, feedbackTextHistorico, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback historico da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("historico_profissional", rs, feedbackTextHistoricoProfissional, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback historico_profissional da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("github", rs, feedbackTextGithub, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback github da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("linkedin", rs, feedbackTextLinkedin, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback linkedin da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("conhecimentos", rs, feedbackTextConhecimentos, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback conhecimentos da versão anterior: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar feedback da versão anterior: " + e.getMessage());
        }
    }
    
    private void carregarCampoFeedbackExistente(String campo, ResultSet rs, TextArea feedbackArea) throws SQLException {
        carregarCampoFeedbackExistente(campo, rs, feedbackArea, false, secaoApresentacao != null ? secaoApresentacao.getVersao() : 1);
    }
    
    private void carregarCampoFeedbackExistente(String campo, ResultSet rs, TextArea feedbackArea, boolean isVersaoAnterior, int versao) throws SQLException {
        // Busca o nome correto da coluna no banco (com underscore se necessário)
        String colunaStatus = "status_" + campo;
        String colunaFeedback = "feedback_" + campo;
        
        String status = rs.getString(colunaStatus);
        String feedback = rs.getString(colunaFeedback);
        
        if (status != null) {
            statusPorCampo.put(campo, status);
            
            javafx.scene.layout.VBox container = obterContainerFeedback(campo);
            javafx.scene.control.Label labelVersao = obterLabelVersao(campo);
            
            // Atualiza label de versão se for da versão anterior
            if (isVersaoAnterior && labelVersao != null) {
                labelVersao.setText("(v." + versao + ")");
                labelVersao.setVisible(true);
                labelVersao.setManaged(true);
            } else if (labelVersao != null) {
                labelVersao.setVisible(false);
                labelVersao.setManaged(false);
            }
            
            if (feedbackArea != null) {
                if ("Revisar".equals(status)) {
                    // Se foi marcado para revisar, mostra o campo de feedback
                    if (container != null) {
                        container.setVisible(true);
                        container.setManaged(true);
                    }
                    feedbackArea.setVisible(true);
                    feedbackArea.setManaged(true);
                    if (feedback != null && !feedback.trim().isEmpty()) {
                        feedbackArea.setText(feedback);
                    }
                    // Para GitHub e LinkedIn, usar altura menor já que estão lado a lado
                    if ("github".equals(campo) || "linkedin".equals(campo)) {
                        feedbackArea.setPrefHeight(60);
                    } else {
                        feedbackArea.setPrefHeight(80);
                    }
                    feedbackArea.setWrapText(true);
                } else if ("Aprovado".equals(status)) {
                    // Se foi aprovado, esconde o campo de feedback
                    if (container != null) {
                        container.setVisible(false);
                        container.setManaged(false);
                    }
                    feedbackArea.setVisible(false);
                    feedbackArea.setManaged(false);
                    feedbackArea.clear();
                }
            }
            
            // Atualiza as cores dos botões e bordas baseado no status carregado
            // Usa Platform.runLater para garantir que a cena esteja carregada
            Platform.runLater(() -> {
                try {
                    atualizarCorBotoes(campo, status);
                    if ("Aprovado".equals(status)) {
                        aplicarBordaVerdeCampo(campo);
                    } else if ("Revisar".equals(status)) {
                        aplicarBordaVermelhaCampo(campo);
                    } else {
                        removerBordaCampo(campo);
                    }
                } catch (Exception e) {
                    // Ignora erros ao atualizar botões (pode ser que a cena ainda não esteja totalmente carregada)
                    System.err.println("Erro ao atualizar botões para campo " + campo + ": " + e.getMessage());
                }
            });
        }
    }
    
    private javafx.scene.control.Label obterLabelVersao(String campo) {
        switch (campo) {
            case "nome":
                return labelVersaoNome;
            case "idade":
                return labelVersaoIdade;
            case "curso":
                return labelVersaoCurso;
            case "motivacao":
                return labelVersaoMotivacao;
            case "historico":
                return labelVersaoHistorico;
            case "historico_profissional":
                return labelVersaoHistoricoProfissional;
            case "github":
                return labelVersaoGithub;
            case "linkedin":
                return labelVersaoLinkedin;
            case "conhecimentos":
                return labelVersaoConhecimentos;
            default:
                return null;
        }
    }

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        final String emailAluno = secaoApresentacao != null ? secaoApresentacao.getEmailAluno() : null;
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

    // 2) Aprovar/Revisar por campo (handlers dos botões)
    @FXML private void aprovarNome(ActionEvent e) { aprovarCampo("nome", feedbackTextNome); }
    @FXML private void revisarNome(ActionEvent e) { revisarCampo("nome", feedbackTextNome); }
    @FXML private void aprovarIdade(ActionEvent e) { aprovarCampo("idade", feedbackTextIdade); }
    @FXML private void revisarIdade(ActionEvent e) { revisarCampo("idade", feedbackTextIdade); }
    @FXML private void aprovarCurso(ActionEvent e) { aprovarCampo("curso", feedbackTextCurso); }
    @FXML private void revisarCurso(ActionEvent e) { revisarCampo("curso", feedbackTextCurso); }
    @FXML private void aprovarMotivacao(ActionEvent e) { aprovarCampo("motivacao", feedbackTextMotivacao); }
    @FXML private void revisarMotivacao(ActionEvent e) { revisarCampo("motivacao", feedbackTextMotivacao); }
    @FXML private void aprovarHistorico(ActionEvent e) { aprovarCampo("historico", feedbackTextHistorico); }
    @FXML private void revisarHistorico(ActionEvent e) { revisarCampo("historico", feedbackTextHistorico); }
    @FXML private void aprovarHistoricoProfissional(ActionEvent e) { aprovarCampo("historico_profissional", feedbackTextHistoricoProfissional); }
    @FXML private void revisarHistoricoProfissional(ActionEvent e) { revisarCampo("historico_profissional", feedbackTextHistoricoProfissional); }
    @FXML private void aprovarGithub(ActionEvent e) { aprovarCampo("github", feedbackTextGithub); }
    @FXML private void revisarGithub(ActionEvent e) { revisarCampo("github", feedbackTextGithub); }
    @FXML private void aprovarLinkedin(ActionEvent e) { aprovarCampo("linkedin", feedbackTextLinkedin); }
    @FXML private void revisarLinkedin(ActionEvent e) { revisarCampo("linkedin", feedbackTextLinkedin); }
    @FXML private void aprovarConhecimentos(ActionEvent e) { aprovarCampo("conhecimentos", feedbackTextConhecimentos); }
    @FXML private void revisarConhecimentos(ActionEvent e) { revisarCampo("conhecimentos", feedbackTextConhecimentos); }

    private void aprovarCampo(String campo, TextArea areaFeedback) {
        statusPorCampo.put(campo, "Aprovado");
        javafx.scene.layout.VBox container = obterContainerFeedback(campo);
        javafx.scene.control.Label labelVersao = obterLabelVersao(campo);
        
        // Oculta label de versão quando aprovar (agora é feedback da versão atual)
        if (labelVersao != null) {
            labelVersao.setVisible(false);
            labelVersao.setManaged(false);
        }
        
        if (container != null) {
            container.setVisible(false);
            container.setManaged(false);
        }
        if (areaFeedback != null) {
            areaFeedback.setVisible(false);
            areaFeedback.setManaged(false);
            areaFeedback.clear();
        }
        atualizarCorBotoes(campo, "Aprovado");
        aplicarBordaVerdeCampo(campo);
    }

    private void revisarCampo(String campo, TextArea areaFeedback) {
        statusPorCampo.put(campo, "Revisar");
        javafx.scene.layout.VBox container = obterContainerFeedback(campo);
        javafx.scene.control.Label labelVersao = obterLabelVersao(campo);
        
        // Oculta label de versão quando revisar (agora é feedback da versão atual)
        if (labelVersao != null) {
            labelVersao.setVisible(false);
            labelVersao.setManaged(false);
        }
        
        if (container != null) {
            container.setVisible(true);
            container.setManaged(true);
        }
        if (areaFeedback != null) {
            areaFeedback.setVisible(true);
            areaFeedback.setManaged(true);
            areaFeedback.setPromptText("Digite seu feedback aqui...");
            // Para GitHub e LinkedIn, usar altura menor já que estão lado a lado
            if ("github".equals(campo) || "linkedin".equals(campo)) {
                areaFeedback.setPrefHeight(60);
            } else {
                areaFeedback.setPrefHeight(80);
            }
            areaFeedback.setWrapText(true);
        }
        atualizarCorBotoes(campo, "Revisar");
        aplicarBordaVermelhaCampo(campo);
    }
    
    private void aplicarBordaVerdeCampo(String campo) {
        if ("github".equals(campo) || "linkedin".equals(campo)) {
            Hyperlink hyperlink = obterHyperlinkAluno(campo);
            if (hyperlink != null && hyperlink.getParent() != null) {
                ((javafx.scene.layout.HBox) hyperlink.getParent()).setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #27AE60; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
            }
        } else {
            TextArea textArea = obterTextAreaAluno(campo);
            if (textArea != null) {
                textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #27AE60; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
            }
        }
    }
    
    private void aplicarBordaVermelhaCampo(String campo) {
        if ("github".equals(campo) || "linkedin".equals(campo)) {
            Hyperlink hyperlink = obterHyperlinkAluno(campo);
            if (hyperlink != null && hyperlink.getParent() != null) {
                ((javafx.scene.layout.HBox) hyperlink.getParent()).setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E74C3C; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
            }
        } else {
            TextArea textArea = obterTextAreaAluno(campo);
            if (textArea != null) {
                textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E74C3C; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
            }
        }
    }
    
    private void removerBordaCampo(String campo) {
        if ("github".equals(campo) || "linkedin".equals(campo)) {
            Hyperlink hyperlink = obterHyperlinkAluno(campo);
            if (hyperlink != null && hyperlink.getParent() != null) {
                ((javafx.scene.layout.HBox) hyperlink.getParent()).setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
            }
        } else {
            TextArea textArea = obterTextAreaAluno(campo);
            if (textArea != null) {
                textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
            }
        }
    }
    
    private TextArea obterTextAreaAluno(String campo) {
        switch (campo) {
            case "nome":
                return alunoTextNome;
            case "idade":
                return alunoTextIdade;
            case "curso":
                return alunoTextCurso;
            case "motivacao":
                return alunoTextMotivacao;
            case "historico":
                return alunoTextHistorico;
            case "historico_profissional":
                return alunoTextHistoricoProfissional;
            case "conhecimentos":
                return alunoTextConhecimentos;
            default:
                return null;
        }
    }
    
    private Hyperlink obterHyperlinkAluno(String campo) {
        switch (campo) {
            case "github":
                return alunoTextGithub;
            case "linkedin":
                return alunoTextLinkedin;
            default:
                return null;
        }
    }
    
    /**
     * Abre um link no navegador padrão
     */
    private void abrirLink(String url) {
        if (url == null || url.isEmpty() || url.equals("N/A")) {
            return;
        }
        
        try {
            // Garante que a URL tenha protocolo
            String urlCompleta = url;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                urlCompleta = "https://" + url;
            }
            
            // Tenta usar Desktop via reflection (funciona mesmo sem módulo java.desktop)
            try {
                Class<?> desktopClass = Class.forName("java.awt.Desktop");
                Object desktop = desktopClass.getMethod("getDesktop").invoke(null);
                Boolean isSupported = (Boolean) desktopClass.getMethod("isDesktopSupported").invoke(desktop);
                if (isSupported != null && isSupported) {
                    desktopClass.getMethod("browse", URI.class).invoke(desktop, new URI(urlCompleta));
                    return;
                }
            } catch (Exception e) {
                // Ignora erros de reflection
            }
            // Se Desktop não funcionar, mostra mensagem
            System.err.println("Não foi possível abrir a URL automaticamente: " + urlCompleta);
            System.out.println("Por favor, copie e cole a URL no navegador: " + urlCompleta);
        } catch (Exception e) {
            System.err.println("Erro ao abrir link: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private javafx.scene.layout.VBox obterContainerFeedback(String campo) {
        switch (campo) {
            case "nome":
                return containerFeedbackNome;
            case "idade":
                return containerFeedbackIdade;
            case "curso":
                return containerFeedbackCurso;
            case "motivacao":
                return containerFeedbackMotivacao;
            case "historico":
                return containerFeedbackHistorico;
            case "historico_profissional":
                return containerFeedbackHistoricoProfissional;
            case "github":
                return containerFeedbackGithub;
            case "linkedin":
                return containerFeedbackLinkedin;
            case "conhecimentos":
                return containerFeedbackConhecimentos;
            default:
                return null;
        }
    }
    
    private void atualizarCorBotoes(String campo, String status) {
        // Mapeia campos para seus respectivos botões usando lookup direto na cena
        Button btnAprovar = null;
        Button btnRevisar = null;
        
        // Obtém a cena de qualquer componente disponível
        javafx.scene.Node node = null;
        switch (campo) {
            case "nome":
                node = alunoTextNome;
                break;
            case "idade":
                node = alunoTextIdade;
                break;
            case "curso":
                node = alunoTextCurso;
                break;
            case "motivacao":
                node = alunoTextMotivacao;
                break;
            case "historico":
                node = alunoTextHistorico;
                break;
            case "historico_profissional":
                node = alunoTextHistoricoProfissional;
                break;
            case "github":
                node = alunoTextGithub;
                break;
            case "linkedin":
                node = alunoTextLinkedin;
                break;
            case "conhecimentos":
                node = alunoTextConhecimentos;
                break;
        }
        
        if (node == null || node.getScene() == null) {
            return; // Cena ainda não está pronta
        }
        
        // Busca os botões pelo ID
        switch (campo) {
            case "nome":
                btnAprovar = (Button) node.getScene().lookup("#aprovarNome");
                btnRevisar = (Button) node.getScene().lookup("#revisarNome");
                break;
            case "idade":
                btnAprovar = (Button) node.getScene().lookup("#aprovarIdade");
                btnRevisar = (Button) node.getScene().lookup("#revisarIdade");
                break;
            case "curso":
                btnAprovar = (Button) node.getScene().lookup("#aprovarCurso");
                btnRevisar = (Button) node.getScene().lookup("#revisarCurso");
                break;
            case "motivacao":
                btnAprovar = (Button) node.getScene().lookup("#aprovarMotivacao");
                btnRevisar = (Button) node.getScene().lookup("#revisarMotivacao");
                break;
            case "historico":
                btnAprovar = (Button) node.getScene().lookup("#aprovarHistorico");
                btnRevisar = (Button) node.getScene().lookup("#revisarHistorico");
                break;
            case "historico_profissional":
                btnAprovar = (Button) node.getScene().lookup("#aprovarHistoricoProfissional");
                btnRevisar = (Button) node.getScene().lookup("#revisarHistoricoProfissional");
                break;
            case "github":
                btnAprovar = (Button) node.getScene().lookup("#aprovarGithub");
                btnRevisar = (Button) node.getScene().lookup("#revisarGithub");
                break;
            case "linkedin":
                btnAprovar = (Button) node.getScene().lookup("#aprovarLinkedin");
                btnRevisar = (Button) node.getScene().lookup("#revisarLinkedin");
                break;
            case "conhecimentos":
                btnAprovar = (Button) node.getScene().lookup("#aprovarConhecimentos");
                btnRevisar = (Button) node.getScene().lookup("#revisarConhecimentos");
                break;
        }
        
        if (btnAprovar != null && btnRevisar != null) {
            if ("Aprovado".equals(status)) {
                // Botão aprovar selecionado (destacado)
                btnAprovar.setMinWidth(50);
                btnAprovar.setPrefWidth(50);
                btnAprovar.setMinHeight(34);
                btnAprovar.setPrefHeight(34);
                btnAprovar.setStyle("-fx-background-color: #229954; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: #1E8449; -fx-border-width: 3px; -fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.5), 8, 0, 0, 2); -fx-font-size: 10px; -fx-padding: 2px 4px;");
                // Botão revisar não selecionado (normal)
                btnRevisar.setMinWidth(45);
                btnRevisar.setPrefWidth(45);
                btnRevisar.setMinHeight(30);
                btnRevisar.setPrefHeight(30);
                btnRevisar.setStyle("-fx-background-color: #E74C3C; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
            } else if ("Revisar".equals(status)) {
                // Botão aprovar não selecionado (normal)
                btnAprovar.setMinWidth(45);
                btnAprovar.setPrefWidth(45);
                btnAprovar.setMinHeight(30);
                btnAprovar.setPrefHeight(30);
                btnAprovar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
                // Botão revisar selecionado (destacado)
                btnRevisar.setMinWidth(50);
                btnRevisar.setPrefWidth(50);
                btnRevisar.setMinHeight(34);
                btnRevisar.setPrefHeight(34);
                btnRevisar.setStyle("-fx-background-color: #C0392B; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: #A93226; -fx-border-width: 3px; -fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 8, 0, 0, 2); -fx-font-size: 10px; -fx-padding: 2px 4px;");
            } else {
                // Nenhum selecionado (estado inicial)
                btnAprovar.setMinWidth(45);
                btnAprovar.setPrefWidth(45);
                btnAprovar.setMinHeight(30);
                btnAprovar.setPrefHeight(30);
                btnAprovar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
                btnRevisar.setMinWidth(45);
                btnRevisar.setPrefWidth(45);
                btnRevisar.setMinHeight(30);
                btnRevisar.setPrefHeight(30);
                btnRevisar.setStyle("-fx-background-color: #E74C3C; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
            }
        }
    }

    // 3) Enviar feedbacks (UPDATE na tabela secao_apresentacao)
    @FXML
    public void enviarFeedback(ActionEvent event) {
        // No novo esquema, sempre faz UPDATE na tabela secao_apresentacao
        // pois a seção já existe quando o feedback é dado
        String sql = "UPDATE secao_apresentacao SET " +
                    "status_nome = ?, feedback_nome = ?, " +
                    "status_idade = ?, feedback_idade = ?, " +
                    "status_curso = ?, feedback_curso = ?, " +
                    "status_motivacao = ?, feedback_motivacao = ?, " +
                    "status_historico = ?, feedback_historico = ?, " +
                    "status_historico_profissional = ?, feedback_historico_profissional = ?, " +
                    "status_github = ?, feedback_github = ?, " +
                    "status_linkedin = ?, feedback_linkedin = ?, " +
                    "status_conhecimentos = ?, feedback_conhecimentos = ?, " +
                    "horario_feedback = CURRENT_TIMESTAMP " +
                    "WHERE aluno = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            setNullableString(pst, 1, statusPorCampo.get("nome"));
            setNullableString(pst, 2, textOrNull(feedbackTextNome));
            setNullableString(pst, 3, statusPorCampo.get("idade"));
            setNullableString(pst, 4, textOrNull(feedbackTextIdade));
            setNullableString(pst, 5, statusPorCampo.get("curso"));
            setNullableString(pst, 6, textOrNull(feedbackTextCurso));
            setNullableString(pst, 7, statusPorCampo.get("motivacao"));
            setNullableString(pst, 8, textOrNull(feedbackTextMotivacao));
            setNullableString(pst, 9, statusPorCampo.get("historico"));
            setNullableString(pst, 10, textOrNull(feedbackTextHistorico));
            setNullableString(pst, 11, statusPorCampo.get("historico_profissional"));
            setNullableString(pst, 12, textOrNull(feedbackTextHistoricoProfissional));
            setNullableString(pst, 13, statusPorCampo.get("github"));
            setNullableString(pst, 14, textOrNull(feedbackTextGithub));
            setNullableString(pst, 15, statusPorCampo.get("linkedin"));
            setNullableString(pst, 16, textOrNull(feedbackTextLinkedin));
            setNullableString(pst, 17, statusPorCampo.get("conhecimentos"));
            setNullableString(pst, 18, textOrNull(feedbackTextConhecimentos));

            // Para UPDATE, adiciona WHERE
            pst.setString(19, secaoApresentacao.getEmailAluno());
            pst.setInt(20, secaoApresentacao.getVersao());

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
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return false;
        String sql = "SELECT COUNT(*) FROM secao_apresentacao WHERE aluno = ? AND versao = ? " +
                     "AND (status_nome IS NOT NULL OR status_idade IS NOT NULL OR status_curso IS NOT NULL " +
                     "OR status_motivacao IS NOT NULL OR status_historico IS NOT NULL OR status_github IS NOT NULL " +
                     "OR status_linkedin IS NOT NULL OR status_conhecimentos IS NOT NULL OR status_historico_profissional IS NOT NULL)";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, secaoApresentacao.getVersao());
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

    private void mostrarErro(String titulo, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}