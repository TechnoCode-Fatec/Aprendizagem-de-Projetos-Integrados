package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Services.NavigationService;

import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApi;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

public class OrientadorCorrigirApiController {

    // Identificador da seção usando classe modelo
    private SecaoApi secaoApi;

    // Status por campo (Aprovado | Revisar | null)
    private final Map<String, String> statusPorCampo = new HashMap<>();

    // TextAreas de conteúdo do aluno (coluna esquerda)
    @FXML private TextArea alunoEmpresa;
    @FXML private TextArea alunoDescricaoEmpresa;
    @FXML private TextArea alunoLinkRepositorio;
    @FXML private TextArea alunoProblema;
    @FXML private TextArea alunoSolucao;
    @FXML private TextArea alunoTecnologias;
    @FXML private TextArea alunoContribuicoes;
    @FXML private TextArea alunoHardSkills;
    @FXML private TextArea alunoSoftSkills;

    // TextAreas de feedback (coluna direita)
    @FXML private TextArea feedbackEmpresa;
    @FXML private TextArea feedbackDescricaoEmpresa;
    @FXML private TextArea feedbackProblema;
    @FXML private TextArea feedbackSolucao;
    @FXML private TextArea feedbackTecnologias;
    @FXML private TextArea feedbackContribuicoes;
    @FXML private TextArea feedbackHardSkills;
    @FXML private TextArea feedbackSoftSkills;

    @FXML
    public void initialize() {
        // Todos feedbacks começam ocultos
        if (feedbackEmpresa != null) feedbackEmpresa.setVisible(false);
        if (feedbackDescricaoEmpresa != null) feedbackDescricaoEmpresa.setVisible(false);
        if (feedbackProblema != null) feedbackProblema.setVisible(false);
        if (feedbackSolucao != null) feedbackSolucao.setVisible(false);
        if (feedbackTecnologias != null) feedbackTecnologias.setVisible(false);
        if (feedbackContribuicoes != null) feedbackContribuicoes.setVisible(false);
        if (feedbackHardSkills != null) feedbackHardSkills.setVisible(false);
        if (feedbackSoftSkills != null) feedbackSoftSkills.setVisible(false);
    }

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, String semestreCurso, int ano, String semestreAno, int versao) {
        // Cria objeto SecaoApi para identificar a seção
        this.secaoApi = new SecaoApi(aluno, semestreCurso, ano, semestreAno, versao);
        carregarSecaoAluno();
    }

    // 1) Carrega dados da secao_api
    public void carregarSecaoAluno() {
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
                    String empresa = rs.getString("empresa");
                    String descricaoEmpresa = rs.getString("descricao_empresa");
                    String linkRepo = rs.getString("link_repositorio");
                    String problema = rs.getString("problema");
                    String solucao = rs.getString("solucao");
                    String tecnologias = rs.getString("tecnologias");
                    String contribuicoes = rs.getString("contribuicoes");
                    String hardSkills = rs.getString("hard_skills");
                    String softSkills = rs.getString("soft_skills");
                    
                    if (alunoEmpresa != null) alunoEmpresa.setText(empresa != null ? empresa : "");
                    if (alunoDescricaoEmpresa != null) alunoDescricaoEmpresa.setText(descricaoEmpresa != null ? descricaoEmpresa : "");
                    if (alunoLinkRepositorio != null) alunoLinkRepositorio.setText(linkRepo != null ? linkRepo : "");
                    if (alunoProblema != null) alunoProblema.setText(problema != null ? problema : "");
                    if (alunoSolucao != null) alunoSolucao.setText(solucao != null ? solucao : "");
                    if (alunoTecnologias != null) alunoTecnologias.setText(tecnologias != null ? tecnologias : "");
                    if (alunoContribuicoes != null) alunoContribuicoes.setText(contribuicoes != null ? contribuicoes : "");
                    if (alunoHardSkills != null) alunoHardSkills.setText(hardSkills != null ? hardSkills : "");
                    if (alunoSoftSkills != null) alunoSoftSkills.setText(softSkills != null ? softSkills : "");
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
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        String sql = "SELECT status_empresa, feedback_empresa, " +
                "status_descricao_empresa, feedback_descricao_empresa, " +
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
                    try {
                        carregarCampoFeedbackExistente("empresa", rs, feedbackEmpresa);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback empresa: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("descricao_empresa", rs, feedbackDescricaoEmpresa);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback descricao_empresa: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("problema", rs, feedbackProblema);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback problema: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("solucao", rs, feedbackSolucao);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback solucao: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("tecnologias", rs, feedbackTecnologias);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback tecnologias: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("contribuicoes", rs, feedbackContribuicoes);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback contribuicoes: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("hard_skills", rs, feedbackHardSkills);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback hard_skills: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("soft_skills", rs, feedbackSoftSkills);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback soft_skills: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            // Não é erro crítico se não houver feedback existente, mas loga o erro
            System.err.println("Erro ao carregar feedback existente: " + e.getMessage());
        }
    }
    
    private void carregarCampoFeedbackExistente(String campo, ResultSet rs, TextArea feedbackArea) throws SQLException {
        // Busca o nome correto da coluna no banco (com underscore se necessário)
        String colunaStatus = "status_" + campo;
        String colunaFeedback = "feedback_" + campo;
        
        String status = rs.getString(colunaStatus);
        String feedback = rs.getString(colunaFeedback);
        
        if (status != null) {
            statusPorCampo.put(campo, status);
            
            if (feedbackArea != null) {
                if ("Revisar".equals(status)) {
                    // Se foi marcado para revisar, mostra o campo de feedback
                    feedbackArea.setVisible(true);
                    if (feedback != null && !feedback.trim().isEmpty()) {
                        feedbackArea.setText(feedback);
                    }
                    feedbackArea.setPrefHeight(100);
                    feedbackArea.setWrapText(true);
                } else if ("Aprovado".equals(status)) {
                    // Se foi aprovado, esconde o campo de feedback
                    feedbackArea.setVisible(false);
                    feedbackArea.clear();
                }
            }
            
            // Atualiza as cores dos botões baseado no status carregado
            // Usa Platform.runLater para garantir que a cena esteja carregada
            Platform.runLater(() -> {
                try {
                    atualizarCorBotoes(campo, status);
                } catch (Exception e) {
                    // Ignora erros ao atualizar botões (pode ser que a cena ainda não esteja totalmente carregada)
                    System.err.println("Erro ao atualizar botões para campo " + campo + ": " + e.getMessage());
                }
            });
        }
    }

    // 2) Aprovar/Revisar por campo (handlers dos botões)
    @FXML private void aprovarEmpresa(ActionEvent e) { aprovarCampo("empresa", feedbackEmpresa); }
    @FXML private void revisarEmpresa(ActionEvent e) { revisarCampo("empresa", feedbackEmpresa); }
    @FXML private void aprovarDescricaoEmpresa(ActionEvent e) { aprovarCampo("descricao_empresa", feedbackDescricaoEmpresa); }
    @FXML private void revisarDescricaoEmpresa(ActionEvent e) { revisarCampo("descricao_empresa", feedbackDescricaoEmpresa); }
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
        atualizarCorBotoes(campo, "Aprovado");
    }

    private void revisarCampo(String campo, TextArea areaFeedback) {
        statusPorCampo.put(campo, "Revisar");
        if (areaFeedback != null) {
            areaFeedback.setVisible(true);
            areaFeedback.setPromptText("Digite seu feedback aqui...");
            areaFeedback.setPrefHeight(100);
            areaFeedback.setWrapText(true);
        }
        atualizarCorBotoes(campo, "Revisar");
    }
    
    private void atualizarCorBotoes(String campo, String status) {
        // Mapeia campos para seus respectivos botões usando lookup direto na cena
        Button btnAprovar = null;
        Button btnRevisar = null;
        
        // Obtém a cena de qualquer componente disponível
        javafx.scene.Node node = null;
        switch (campo) {
            case "empresa":
                node = alunoEmpresa;
                break;
            case "descricao_empresa":
                node = alunoDescricaoEmpresa;
                break;
            case "problema":
                node = alunoProblema;
                break;
            case "solucao":
                node = alunoSolucao;
                break;
            case "tecnologias":
                node = alunoTecnologias;
                break;
            case "contribuicoes":
                node = alunoContribuicoes;
                break;
            case "hard_skills":
                node = alunoHardSkills;
                break;
            case "soft_skills":
                node = alunoSoftSkills;
                break;
        }
        
        if (node == null || node.getScene() == null) {
            return; // Cena ainda não está pronta
        }
        
        // Busca os botões pelo ID
        switch (campo) {
            case "empresa":
                btnAprovar = (Button) node.getScene().lookup("#aprovarEmpresa");
                btnRevisar = (Button) node.getScene().lookup("#revisarEmpresa");
                break;
            case "descricao_empresa":
                btnAprovar = (Button) node.getScene().lookup("#aprovarDescricaoEmpresa");
                btnRevisar = (Button) node.getScene().lookup("#revisarDescricaoEmpresa");
                break;
            case "problema":
                btnAprovar = (Button) node.getScene().lookup("#aprovarProblema");
                btnRevisar = (Button) node.getScene().lookup("#revisarProblema");
                break;
            case "solucao":
                btnAprovar = (Button) node.getScene().lookup("#aprovarSolucao");
                btnRevisar = (Button) node.getScene().lookup("#revisarSolucao");
                break;
            case "tecnologias":
                btnAprovar = (Button) node.getScene().lookup("#aprovarTecnologias");
                btnRevisar = (Button) node.getScene().lookup("#revisarTecnologias");
                break;
            case "contribuicoes":
                btnAprovar = (Button) node.getScene().lookup("#aprovarContribuicoes");
                btnRevisar = (Button) node.getScene().lookup("#revisarContribuicoes");
                break;
            case "hard_skills":
                btnAprovar = (Button) node.getScene().lookup("#aprovarHardSkills");
                btnRevisar = (Button) node.getScene().lookup("#revisarHardSkills");
                break;
            case "soft_skills":
                btnAprovar = (Button) node.getScene().lookup("#aprovarSoftSkills");
                btnRevisar = (Button) node.getScene().lookup("#revisarSoftSkills");
                break;
        }
        
        if (btnAprovar != null && btnRevisar != null) {
            if ("Aprovado".equals(status)) {
                // Botão aprovar selecionado (destacado)
                btnAprovar.setStyle("-fx-background-color: #229954; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: #1E8449; -fx-border-width: 3px; -fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.5), 8, 0, 0, 2);");
                // Botão revisar não selecionado (normal)
                btnRevisar.setStyle("-fx-background-color: #E74C3C; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
            } else if ("Revisar".equals(status)) {
                // Botão aprovar não selecionado (normal)
                btnAprovar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
                // Botão revisar selecionado (destacado)
                btnRevisar.setStyle("-fx-background-color: #C0392B; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: #A93226; -fx-border-width: 3px; -fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 8, 0, 0, 2);");
            } else {
                // Nenhum selecionado (estado inicial)
                btnAprovar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
                btnRevisar.setStyle("-fx-background-color: #E74C3C; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-width: 0px; -fx-effect: null;");
            }
        }
    }

    // 3) Enviar feedbacks (UPDATE na tabela secao_api)
    @FXML
    public void enviarFeedback(ActionEvent event) {
        // No novo esquema, sempre faz UPDATE na tabela secao_api
        // pois a seção já existe quando o feedback é dado
        String sql = "UPDATE secao_api SET " +
                    "status_empresa = ?, feedback_empresa = ?, " +
                    "status_descricao_empresa = ?, feedback_descricao_empresa = ?, " +
                    "status_problema = ?, feedback_problema = ?, " +
                    "status_solucao = ?, feedback_solucao = ?, " +
                    "status_tecnologias = ?, feedback_tecnologias = ?, " +
                    "status_contribuicoes = ?, feedback_contribuicoes = ?, " +
                    "status_hard_skills = ?, feedback_hard_skills = ?, " +
                    "status_soft_skills = ?, feedback_soft_skills = ?, " +
                    "horario_feedback = CURRENT_TIMESTAMP " +
                    "WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            setNullableString(pst, 1, statusPorCampo.get("empresa"));
            setNullableString(pst, 2, textOrNull(feedbackEmpresa));
            setNullableString(pst, 3, statusPorCampo.get("descricao_empresa"));
            setNullableString(pst, 4, textOrNull(feedbackDescricaoEmpresa));
            setNullableString(pst, 5, statusPorCampo.get("problema"));
            setNullableString(pst, 6, textOrNull(feedbackProblema));
            setNullableString(pst, 7, statusPorCampo.get("solucao"));
            setNullableString(pst, 8, textOrNull(feedbackSolucao));
            setNullableString(pst, 9, statusPorCampo.get("tecnologias"));
            setNullableString(pst, 10, textOrNull(feedbackTecnologias));
            setNullableString(pst, 11, statusPorCampo.get("contribuicoes"));
            setNullableString(pst, 12, textOrNull(feedbackContribuicoes));
            setNullableString(pst, 13, statusPorCampo.get("hard_skills"));
            setNullableString(pst, 14, textOrNull(feedbackHardSkills));
            setNullableString(pst, 15, statusPorCampo.get("soft_skills"));
            setNullableString(pst, 16, textOrNull(feedbackSoftSkills));

            // Para UPDATE, adiciona WHERE
            pst.setString(17, secaoApi.getEmailAluno());
            pst.setString(18, secaoApi.getSemestreCurso());
            pst.setInt(19, secaoApi.getAno());
            pst.setString(20, secaoApi.getSemestreAno());
            pst.setInt(21, secaoApi.getVersao());

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
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return false;
        String sql = "SELECT COUNT(*) FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? " +
                     "AND (status_empresa IS NOT NULL OR status_descricao_empresa IS NOT NULL OR status_problema IS NOT NULL OR status_solucao IS NOT NULL OR status_tecnologias IS NOT NULL " +
                     "OR status_contribuicoes IS NOT NULL OR status_hard_skills IS NOT NULL OR status_soft_skills IS NOT NULL)";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApi.getEmailAluno());
            pst.setString(2, secaoApi.getSemestreCurso());
            pst.setInt(3, secaoApi.getAno());
            pst.setString(4, secaoApi.getSemestreAno());
            pst.setInt(5, secaoApi.getVersao());
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

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        final String emailAluno = secaoApi != null ? secaoApi.getEmailAluno() : null;
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

    private void mostrarErro(String titulo, Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
        }
    }