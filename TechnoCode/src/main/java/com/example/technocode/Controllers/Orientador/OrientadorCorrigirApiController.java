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
    @FXML private TextArea feedbackRepositorio;
    @FXML private TextArea feedbackProblema;
    @FXML private TextArea feedbackSolucao;
    @FXML private TextArea feedbackTecnologias;
    @FXML private TextArea feedbackContribuicoes;
    @FXML private TextArea feedbackHardSkills;
    @FXML private TextArea feedbackSoftSkills;
    
    // TextAreas de feedback da versão anterior
    @FXML private TextArea feedbackAnteriorEmpresa;
    @FXML private TextArea feedbackAnteriorDescricaoEmpresa;
    @FXML private TextArea feedbackAnteriorRepositorio;
    @FXML private TextArea feedbackAnteriorProblema;
    @FXML private TextArea feedbackAnteriorSolucao;
    @FXML private TextArea feedbackAnteriorTecnologias;
    @FXML private TextArea feedbackAnteriorContribuicoes;
    @FXML private TextArea feedbackAnteriorHardSkills;
    @FXML private TextArea feedbackAnteriorSoftSkills;
    
    // Containers para feedback anterior
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorEmpresa;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorDescricaoEmpresa;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorRepositorio;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorProblema;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorSolucao;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorTecnologias;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorContribuicoes;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorHardSkills;
    @FXML private javafx.scene.layout.VBox containerFeedbackAnteriorSoftSkills;
    
    // Containers para feedback
    @FXML private javafx.scene.layout.VBox containerFeedbackEmpresa;
    @FXML private javafx.scene.layout.VBox containerFeedbackDescricaoEmpresa;
    @FXML private javafx.scene.layout.VBox containerFeedbackRepositorio;
    @FXML private javafx.scene.layout.VBox containerFeedbackProblema;
    @FXML private javafx.scene.layout.VBox containerFeedbackSolucao;
    @FXML private javafx.scene.layout.VBox containerFeedbackTecnologias;
    @FXML private javafx.scene.layout.VBox containerFeedbackContribuicoes;
    @FXML private javafx.scene.layout.VBox containerFeedbackHardSkills;
    @FXML private javafx.scene.layout.VBox containerFeedbackSoftSkills;
    
    // Labels para indicar versão do feedback
    @FXML private javafx.scene.control.Label labelVersaoEmpresa;
    @FXML private javafx.scene.control.Label labelVersaoDescricaoEmpresa;
    @FXML private javafx.scene.control.Label labelVersaoRepositorio;
    @FXML private javafx.scene.control.Label labelVersaoProblema;
    @FXML private javafx.scene.control.Label labelVersaoSolucao;
    @FXML private javafx.scene.control.Label labelVersaoTecnologias;
    @FXML private javafx.scene.control.Label labelVersaoContribuicoes;
    @FXML private javafx.scene.control.Label labelVersaoHardSkills;
    @FXML private javafx.scene.control.Label labelVersaoSoftSkills;

    @FXML
    public void initialize() {
        // Todos feedbacks começam ocultos e desabilitados (não ocupam espaço)
        if (feedbackEmpresa != null) {
            feedbackEmpresa.setVisible(false);
            feedbackEmpresa.setManaged(false);
        }
        if (feedbackDescricaoEmpresa != null) {
            feedbackDescricaoEmpresa.setVisible(false);
            feedbackDescricaoEmpresa.setManaged(false);
        }
        if (feedbackRepositorio != null) {
            feedbackRepositorio.setVisible(false);
            feedbackRepositorio.setManaged(false);
        }
        if (feedbackProblema != null) {
            feedbackProblema.setVisible(false);
            feedbackProblema.setManaged(false);
        }
        if (feedbackSolucao != null) {
            feedbackSolucao.setVisible(false);
            feedbackSolucao.setManaged(false);
        }
        if (feedbackTecnologias != null) {
            feedbackTecnologias.setVisible(false);
            feedbackTecnologias.setManaged(false);
        }
        if (feedbackContribuicoes != null) {
            feedbackContribuicoes.setVisible(false);
            feedbackContribuicoes.setManaged(false);
        }
        if (feedbackHardSkills != null) {
            feedbackHardSkills.setVisible(false);
            feedbackHardSkills.setManaged(false);
        }
        if (feedbackSoftSkills != null) {
            feedbackSoftSkills.setVisible(false);
            feedbackSoftSkills.setManaged(false);
        }
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
        
        // Se não for a primeira versão, carrega feedback da versão anterior
        if (secaoApi.getVersao() > 1) {
            carregarFeedbackVersaoAnterior();
        }
    }
    
    // Carrega feedback existente do orientador
    private void carregarFeedbackExistente() {
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
                boolean temFeedbackAtual = false;
                if (rs.next()) {
                    // Verifica se há algum feedback na versão atual
                    String[] statusFields = {"status_empresa", "status_descricao_empresa", "status_repositorio", 
                                             "status_problema", "status_solucao", "status_tecnologias", 
                                             "status_contribuicoes", "status_hard_skills", "status_soft_skills"};
                    for (String field : statusFields) {
                        if (rs.getString(field) != null) {
                            temFeedbackAtual = true;
                            break;
                        }
                    }
                    
                    if (temFeedbackAtual) {
                        // Carrega feedback da versão atual
                        try {
                            carregarCampoFeedbackExistente("empresa", rs, feedbackEmpresa, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback empresa: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("descricao_empresa", rs, feedbackDescricaoEmpresa, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback descricao_empresa: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("repositorio", rs, feedbackRepositorio, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback repositorio: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("problema", rs, feedbackProblema, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback problema: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("solucao", rs, feedbackSolucao, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback solucao: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("tecnologias", rs, feedbackTecnologias, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback tecnologias: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("contribuicoes", rs, feedbackContribuicoes, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback contribuicoes: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("hard_skills", rs, feedbackHardSkills, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback hard_skills: " + e.getMessage());
                        }
                        try {
                            carregarCampoFeedbackExistente("soft_skills", rs, feedbackSoftSkills, false, secaoApi.getVersao());
                        } catch (Exception e) {
                            System.err.println("Erro ao carregar feedback soft_skills: " + e.getMessage());
                        }
                    }
                }
                
                // Se não há feedback na versão atual, carrega da versão anterior
                if (!temFeedbackAtual && secaoApi.getVersao() > 1) {
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
        if (secaoApi == null || secaoApi.getEmailAluno() == null || secaoApi.getVersao() <= 1) {
            return;
        }
        
        int versaoAnterior = secaoApi.getVersao() - 1;
        // Indica que estamos carregando feedback da versão anterior
        boolean isVersaoAnterior = true;
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
                    // Carrega feedback da versão anterior
                    try {
                        carregarCampoFeedbackExistente("empresa", rs, feedbackEmpresa, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback empresa da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("descricao_empresa", rs, feedbackDescricaoEmpresa, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback descricao_empresa da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("repositorio", rs, feedbackRepositorio, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback repositorio da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("problema", rs, feedbackProblema, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback problema da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("solucao", rs, feedbackSolucao, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback solucao da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("tecnologias", rs, feedbackTecnologias, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback tecnologias da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("contribuicoes", rs, feedbackContribuicoes, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback contribuicoes da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("hard_skills", rs, feedbackHardSkills, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback hard_skills da versão anterior: " + e.getMessage());
                    }
                    try {
                        carregarCampoFeedbackExistente("soft_skills", rs, feedbackSoftSkills, isVersaoAnterior, versaoAnterior);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar feedback soft_skills da versão anterior: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar feedback da versão anterior: " + e.getMessage());
        }
    }
    
    private void carregarCampoFeedbackExistente(String campo, ResultSet rs, TextArea feedbackArea) throws SQLException {
        carregarCampoFeedbackExistente(campo, rs, feedbackArea, false, secaoApi != null ? secaoApi.getVersao() : 1);
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
                    feedbackArea.setPrefHeight(80);
                    feedbackArea.setWrapText(true);
                } else if ("Aprovado".equals(status)) {
                    // Se foi aprovado, esconde o campo de feedback e desabilita (não ocupa espaço)
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
                        removerBordaVerdeCampo(campo);
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
            case "empresa":
                return labelVersaoEmpresa;
            case "descricao_empresa":
                return labelVersaoDescricaoEmpresa;
            case "repositorio":
                return labelVersaoRepositorio;
            case "problema":
                return labelVersaoProblema;
            case "solucao":
                return labelVersaoSolucao;
            case "tecnologias":
                return labelVersaoTecnologias;
            case "contribuicoes":
                return labelVersaoContribuicoes;
            case "hard_skills":
                return labelVersaoHardSkills;
            case "soft_skills":
                return labelVersaoSoftSkills;
            default:
                return null;
        }
    }

    // 2) Aprovar/Revisar por campo (handlers dos botões)
    @FXML private void aprovarEmpresa(ActionEvent e) { aprovarCampo("empresa", feedbackEmpresa); }
    @FXML private void revisarEmpresa(ActionEvent e) { revisarCampo("empresa", feedbackEmpresa); }
    @FXML private void aprovarDescricaoEmpresa(ActionEvent e) { aprovarCampo("descricao_empresa", feedbackDescricaoEmpresa); }
    @FXML private void revisarDescricaoEmpresa(ActionEvent e) { revisarCampo("descricao_empresa", feedbackDescricaoEmpresa); }
    @FXML private void aprovarRepositorio(ActionEvent e) { aprovarCampo("repositorio", feedbackRepositorio); }
    @FXML private void revisarRepositorio(ActionEvent e) { revisarCampo("repositorio", feedbackRepositorio); }
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
            areaFeedback.setManaged(false); // Não ocupa espaço quando invisível
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
            areaFeedback.setManaged(true); // Ocupa espaço quando visível
            areaFeedback.setPromptText("Digite seu feedback aqui...");
            areaFeedback.setPrefHeight(80);
            areaFeedback.setWrapText(true);
        }
        atualizarCorBotoes(campo, "Revisar");
        aplicarBordaVermelhaCampo(campo);
    }
    
    private void aplicarBordaVermelhaCampo(String campo) {
        TextArea textArea = obterTextAreaAluno(campo);
        if (textArea != null) {
            textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E74C3C; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        }
    }
    
    private javafx.scene.layout.VBox obterContainerFeedback(String campo) {
        switch (campo) {
            case "empresa":
                return containerFeedbackEmpresa;
            case "descricao_empresa":
                return containerFeedbackDescricaoEmpresa;
            case "repositorio":
                return containerFeedbackRepositorio;
            case "problema":
                return containerFeedbackProblema;
            case "solucao":
                return containerFeedbackSolucao;
            case "tecnologias":
                return containerFeedbackTecnologias;
            case "contribuicoes":
                return containerFeedbackContribuicoes;
            case "hard_skills":
                return containerFeedbackHardSkills;
            case "soft_skills":
                return containerFeedbackSoftSkills;
            default:
                return null;
        }
    }
    
    private void aplicarBordaVerdeCampo(String campo) {
        TextArea textArea = obterTextAreaAluno(campo);
        if (textArea != null) {
            textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #27AE60; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        }
    }
    
    private void removerBordaVerdeCampo(String campo) {
        TextArea textArea = obterTextAreaAluno(campo);
        if (textArea != null) {
            textArea.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-wrap-text: true; -fx-text-fill: #2C3E50;");
        }
    }
    
    private TextArea obterTextAreaAluno(String campo) {
        switch (campo) {
            case "empresa":
                return alunoEmpresa;
            case "descricao_empresa":
                return alunoDescricaoEmpresa;
            case "repositorio":
                return alunoLinkRepositorio;
            case "problema":
                return alunoProblema;
            case "solucao":
                return alunoSolucao;
            case "tecnologias":
                return alunoTecnologias;
            case "contribuicoes":
                return alunoContribuicoes;
            case "hard_skills":
                return alunoHardSkills;
            case "soft_skills":
                return alunoSoftSkills;
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
            case "empresa":
                node = alunoEmpresa;
                break;
            case "descricao_empresa":
                node = alunoDescricaoEmpresa;
                break;
            case "repositorio":
                node = alunoLinkRepositorio;
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
            case "repositorio":
                btnAprovar = (Button) node.getScene().lookup("#aprovarRepositorio");
                btnRevisar = (Button) node.getScene().lookup("#revisarRepositorio");
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
                    "status_repositorio = ?, feedback_repositorio = ?, " +
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
            setNullableString(pst, 5, statusPorCampo.get("repositorio"));
            setNullableString(pst, 6, textOrNull(feedbackRepositorio));
            setNullableString(pst, 7, statusPorCampo.get("problema"));
            setNullableString(pst, 8, textOrNull(feedbackProblema));
            setNullableString(pst, 9, statusPorCampo.get("solucao"));
            setNullableString(pst, 10, textOrNull(feedbackSolucao));
            setNullableString(pst, 11, statusPorCampo.get("tecnologias"));
            setNullableString(pst, 12, textOrNull(feedbackTecnologias));
            setNullableString(pst, 13, statusPorCampo.get("contribuicoes"));
            setNullableString(pst, 14, textOrNull(feedbackContribuicoes));
            setNullableString(pst, 15, statusPorCampo.get("hard_skills"));
            setNullableString(pst, 16, textOrNull(feedbackHardSkills));
            setNullableString(pst, 17, statusPorCampo.get("soft_skills"));
            setNullableString(pst, 18, textOrNull(feedbackSoftSkills));

            // Para UPDATE, adiciona WHERE
            pst.setString(19, secaoApi.getEmailAluno());
            pst.setString(20, secaoApi.getSemestreCurso());
            pst.setInt(21, secaoApi.getAno());
            pst.setString(22, secaoApi.getSemestreAno());
            pst.setInt(23, secaoApi.getVersao());

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
    
    // Carrega feedback da versão anterior (versao - 1)
    private void carregarFeedbackVersaoAnterior() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return;
        
        int versaoAnterior = secaoApi.getVersao() - 1;
        if (versaoAnterior < 1) return; // Não há versão anterior
        
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
                    // Carrega feedback e status de cada campo
                    carregarFeedbackAnteriorCampo("empresa", rs.getString("status_empresa"), rs.getString("feedback_empresa"), 
                            alunoEmpresa, feedbackAnteriorEmpresa, containerFeedbackAnteriorEmpresa);
                    carregarFeedbackAnteriorCampo("descricao_empresa", rs.getString("status_descricao_empresa"), rs.getString("feedback_descricao_empresa"), 
                            alunoDescricaoEmpresa, feedbackAnteriorDescricaoEmpresa, containerFeedbackAnteriorDescricaoEmpresa);
                    carregarFeedbackAnteriorCampo("repositorio", rs.getString("status_repositorio"), rs.getString("feedback_repositorio"), 
                            alunoLinkRepositorio, feedbackAnteriorRepositorio, containerFeedbackAnteriorRepositorio);
                    carregarFeedbackAnteriorCampo("problema", rs.getString("status_problema"), rs.getString("feedback_problema"), 
                            alunoProblema, feedbackAnteriorProblema, containerFeedbackAnteriorProblema);
                    carregarFeedbackAnteriorCampo("solucao", rs.getString("status_solucao"), rs.getString("feedback_solucao"), 
                            alunoSolucao, feedbackAnteriorSolucao, containerFeedbackAnteriorSolucao);
                    carregarFeedbackAnteriorCampo("tecnologias", rs.getString("status_tecnologias"), rs.getString("feedback_tecnologias"), 
                            alunoTecnologias, feedbackAnteriorTecnologias, containerFeedbackAnteriorTecnologias);
                    carregarFeedbackAnteriorCampo("contribuicoes", rs.getString("status_contribuicoes"), rs.getString("feedback_contribuicoes"), 
                            alunoContribuicoes, feedbackAnteriorContribuicoes, containerFeedbackAnteriorContribuicoes);
                    carregarFeedbackAnteriorCampo("hard_skills", rs.getString("status_hard_skills"), rs.getString("feedback_hard_skills"), 
                            alunoHardSkills, feedbackAnteriorHardSkills, containerFeedbackAnteriorHardSkills);
                    carregarFeedbackAnteriorCampo("soft_skills", rs.getString("status_soft_skills"), rs.getString("feedback_soft_skills"), 
                            alunoSoftSkills, feedbackAnteriorSoftSkills, containerFeedbackAnteriorSoftSkills);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar feedback da versão anterior: " + e.getMessage());
        }
    }
    
    private void carregarFeedbackAnteriorCampo(String campo, String status, String feedback, 
            TextArea textAreaAluno, TextArea textAreaFeedback, javafx.scene.layout.VBox container) {
        if (status == null) return;
        
        // Se foi aprovado, aplica borda verde e seleciona botão
        if ("Aprovado".equals(status)) {
            // Atualiza status e botões
            statusPorCampo.put(campo, "Aprovado");
            Platform.runLater(() -> {
                try {
                    atualizarCorBotoes(campo, "Aprovado");
                    aplicarBordaVerdeCampo(campo);
                } catch (Exception e) {
                    System.err.println("Erro ao atualizar botões para campo " + campo + ": " + e.getMessage());
                }
            });
        }
        
        // Se tem feedback, exibe o container de feedback anterior
        if (feedback != null && !feedback.trim().isEmpty() && textAreaFeedback != null && container != null) {
            textAreaFeedback.setText(feedback);
            container.setVisible(true);
            container.setManaged(true);
        }
    }
    
    private boolean verificarFeedbackExistente() {
        if (secaoApi == null || secaoApi.getEmailAluno() == null) return false;
        String sql = "SELECT COUNT(*) FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? " +
                     "AND (status_empresa IS NOT NULL OR status_descricao_empresa IS NOT NULL OR status_repositorio IS NOT NULL OR status_problema IS NOT NULL OR status_solucao IS NOT NULL OR status_tecnologias IS NOT NULL " +
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