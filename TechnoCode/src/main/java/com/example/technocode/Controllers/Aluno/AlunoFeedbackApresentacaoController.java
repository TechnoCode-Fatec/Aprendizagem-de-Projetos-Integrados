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
    @FXML private TextArea alunoTextConhecimentos;

    // TextAreas com feedbacks
    @FXML private TextArea feedbackNome;
    @FXML private TextArea feedbackIdade;
    @FXML private TextArea feedbackCurso;
    @FXML private TextArea feedbackMotivacao;
    @FXML private TextArea feedbackHistorico;
    @FXML private TextArea feedbackGithub;
    @FXML private TextArea feedbackLinkedin;
    @FXML private TextArea feedbackConhecimentos;

    // Labels de status
    @FXML private Label statusNome;
    @FXML private Label statusIdade;
    @FXML private Label statusCurso;
    @FXML private Label statusMotivacao;
    @FXML private Label statusHistorico;
    @FXML private Label statusGithub;
    @FXML private Label statusLinkedin;
    @FXML private Label statusConhecimentos;

    // Containers de feedback expansíveis
    @FXML private VBox containerFeedbackNome;
    @FXML private VBox containerFeedbackIdade;
    @FXML private VBox containerFeedbackCurso;
    @FXML private VBox containerFeedbackMotivacao;
    @FXML private VBox containerFeedbackHistorico;
    @FXML private VBox containerFeedbackGithub;
    @FXML private VBox containerFeedbackLinkedin;
    @FXML private VBox containerFeedbackConhecimentos;

    // Botões de expandir
    @FXML private Button btnExpandNome;
    @FXML private Button btnExpandIdade;
    @FXML private Button btnExpandCurso;
    @FXML private Button btnExpandMotivacao;
    @FXML private Button btnExpandHistorico;
    @FXML private Button btnExpandGithub;
    @FXML private Button btnExpandLinkedin;
    @FXML private Button btnExpandConhecimentos;

    @FXML private Button btnNovaVersao;

    // Mapa para controlar estado de expansão
    private Map<String, Boolean> estadosExpansao = new HashMap<>();

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        // Cria objeto SecaoApresentacao para identificar a seção
        this.secaoApresentacao = new SecaoApresentacao(aluno, versao);
        carregarDadosAluno();
        carregarFeedback();
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
                    if (alunoTextNome != null) alunoTextNome.setText(rs.getString("nome") != null ? rs.getString("nome") : "");
                    if (alunoTextIdade != null) {
                        String dataNascimentoStr = rs.getString("idade");
                        if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                            try {
                                LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);
                                LocalDate hoje = LocalDate.now();
                                int idade = Period.between(dataNascimento, hoje).getYears();
                                alunoTextIdade.setText(String.valueOf(idade));
                            } catch (Exception e) {
                                alunoTextIdade.setText(dataNascimentoStr);
                            }
                        }
                    }
                    if (alunoTextCurso != null) alunoTextCurso.setText(rs.getString("curso") != null ? rs.getString("curso") : "");
                    if (alunoTextMotivacao != null) alunoTextMotivacao.setText(rs.getString("motivacao") != null ? rs.getString("motivacao") : "");
                    if (alunoTextHistorico != null) alunoTextHistorico.setText(rs.getString("historico") != null ? rs.getString("historico") : "");
                    if (alunoTextHistoricoProfissional != null) alunoTextHistoricoProfissional.setText(rs.getString("historico_profissional") != null ? rs.getString("historico_profissional") : "");
                    
                    // Configura links do GitHub e LinkedIn
                    String linkGithubStr = rs.getString("link_github");
                    if (linkGithub != null && linkGithubStr != null && !linkGithubStr.trim().isEmpty()) {
                        linkGithub.setText(linkGithubStr);
                        final String linkGithubFinal = linkGithubStr;
                        linkGithub.setOnAction(e -> abrirURL(linkGithubFinal, linkGithub));
                    } else if (linkGithub != null) {
                        linkGithub.setText("Nenhum link disponível");
                        linkGithub.setDisable(true);
                    }
                    
                    String linkLinkedinStr = rs.getString("link_linkedin");
                    if (linkLinkedin != null && linkLinkedinStr != null && !linkLinkedinStr.trim().isEmpty()) {
                        linkLinkedin.setText(linkLinkedinStr);
                        final String linkLinkedinFinal = linkLinkedinStr;
                        linkLinkedin.setOnAction(e -> abrirURL(linkLinkedinFinal, linkLinkedin));
                    } else if (linkLinkedin != null) {
                        linkLinkedin.setText("Nenhum link disponível");
                        linkLinkedin.setDisable(true);
                    }
                    
                    if (alunoTextConhecimentos != null) alunoTextConhecimentos.setText(rs.getString("principais_conhecimentos") != null ? rs.getString("principais_conhecimentos") : "");
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
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
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
                    carregarCampoFeedback("nome", rs, feedbackNome, statusNome, containerFeedbackNome, btnExpandNome);
                    carregarCampoFeedback("idade", rs, feedbackIdade, statusIdade, containerFeedbackIdade, btnExpandIdade);
                    carregarCampoFeedback("curso", rs, feedbackCurso, statusCurso, containerFeedbackCurso, btnExpandCurso);
                    carregarCampoFeedback("motivacao", rs, feedbackMotivacao, statusMotivacao, containerFeedbackMotivacao, btnExpandMotivacao);
                    carregarCampoFeedback("historico", rs, feedbackHistorico, statusHistorico, containerFeedbackHistorico, btnExpandHistorico);
                    carregarCampoFeedback("github", rs, feedbackGithub, statusGithub, containerFeedbackGithub, btnExpandGithub);
                    carregarCampoFeedback("linkedin", rs, feedbackLinkedin, statusLinkedin, containerFeedbackLinkedin, btnExpandLinkedin);
                    carregarCampoFeedback("conhecimentos", rs, feedbackConhecimentos, statusConhecimentos, containerFeedbackConhecimentos, btnExpandConhecimentos);
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
        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/tela-inicial-aluno.fxml",
            controller -> {
                if (controller instanceof TelaInicialAlunoController) {
                    ((TelaInicialAlunoController) controller).recarregarSecoes();
                }
            });
    }

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }
}
