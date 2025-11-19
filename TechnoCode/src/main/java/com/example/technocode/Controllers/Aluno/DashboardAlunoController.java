package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.Orientador;
import com.example.technocode.model.SolicitacaoOrientacao;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Controller para o Dashboard do Aluno
 * Exibe informações resumidas e atalhos rápidos
 */
public class DashboardAlunoController {

    @FXML
    private Label labelNomeAluno;
    @FXML
    private Label labelNomeOrientador;
    @FXML
    private Label labelStatusSolicitacao;
    @FXML
    private Button btnEnviarApi;
    @FXML
    private Button btnEnviarApresentacao;
    @FXML
    private Label labelDataUltimoFeedback;
    @FXML
    private Button btnVerDetalhesFeedback;
    @FXML
    private Label labelSecoesApiEnviadas;
    @FXML
    private Label labelSecoesApiAvaliadas;
    @FXML
    private Label labelPendenciasApi;
    @FXML
    private Label labelPendenciasApresentacao;
    @FXML
    private Label labelUltimaSecaoEnviada;
    @FXML
    private Label labelUltimaVersaoApi;
    @FXML
    private Label labelDataHoraUltimaSecao;
    @FXML
    private Label labelStatusApresentacao;
    @FXML
    private Label labelPendenciasApresentacaoDetalhe;
    @FXML
    private Label labelVersaoAtualApresentacao;
    @FXML
    private Label labelDataDefesa;
    @FXML
    private Label labelHorarioDefesa;
    @FXML
    private Label labelSalaDefesa;

    private String emailAluno;
    private String ultimoFeedbackTipo; // "api" ou "apresentacao"
    private Map<String, String> ultimoFeedbackDados;

    @FXML
    public void initialize() {
        emailAluno = LoginController.getEmailLogado();
        if (emailAluno != null && !emailAluno.isBlank()) {
            carregarDados();
        }
    }

    /**
     * Carrega todos os dados do dashboard
     */
    private void carregarDados() {
        carregarInformacoesAluno();
        carregarUltimoFeedback();
        carregarProgressoGeral();
        carregarMinhasSecoes();
        carregarMinhaApresentacao();
        carregarAgendamentoDefesa();
    }

    /**
     * Carrega informações do aluno, orientador e status da solicitação
     */
    private void carregarInformacoesAluno() {
        try {
            // Busca dados do aluno
            Map<String, String> dadosAluno = Aluno.buscarDadosPorEmail(emailAluno);
            labelNomeAluno.setText(dadosAluno.get("nome") != null ? dadosAluno.get("nome") : "N/A");

            // Busca orientador
            try (Connection conn = new Connector().getConnection()) {
                String sql = "SELECT orientador FROM aluno WHERE email = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, emailAluno);
                ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    String emailOrientador = rs.getString("orientador");
                    if (emailOrientador != null && !emailOrientador.isEmpty()) {
                        Map<String, String> dadosOrientador = Orientador.buscarDadosPorEmail(emailOrientador);
                        labelNomeOrientador.setText(dadosOrientador.get("nome") != null ? dadosOrientador.get("nome") : "N/A");
                    } else {
                        labelNomeOrientador.setText("Nenhum orientador ainda");
                        labelNomeOrientador.setStyle("-fx-text-fill: #F57C00;");
                    }
                } else {
                    labelNomeOrientador.setText("Nenhum orientador ainda");
                    labelNomeOrientador.setStyle("-fx-text-fill: #F57C00;");
                }
            }

            // Busca status da solicitação mais recente
            List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPorAluno(emailAluno);
            if (!solicitacoes.isEmpty()) {
                Map<String, String> ultimaSolicitacao = solicitacoes.get(0); // Já vem ordenado por data DESC
                String status = ultimaSolicitacao.get("status");
                if ("Pendente".equals(status)) {
                    labelStatusSolicitacao.setText("Pendente");
                    labelStatusSolicitacao.setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;");
                } else if ("Aceita".equals(status)) {
                    labelStatusSolicitacao.setText("Aceita");
                    labelStatusSolicitacao.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                } else if ("Recusada".equals(status)) {
                    labelStatusSolicitacao.setText("Recusada");
                    labelStatusSolicitacao.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                }
            } else {
                labelStatusSolicitacao.setText("-");
            }

        } catch (Exception e) {
            e.printStackTrace();
            labelNomeAluno.setText("Erro ao carregar");
        }
    }

    /**
     * Carrega informações do agendamento de defesa do TG
     */
    private void carregarAgendamentoDefesa() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT data_defesa, horario, sala, status " +
                        "FROM agendamento_defesa_tg " +
                        "WHERE email_aluno = ? AND status = 'Agendado' " +
                        "ORDER BY data_defesa, horario LIMIT 1";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                java.sql.Date dataDefesa = rs.getDate("data_defesa");
                String horario = rs.getString("horario");
                String sala = rs.getString("sala");
                
                // Formata a data
                SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
                labelDataDefesa.setText(sdfData.format(dataDefesa));
                
                // Formata o horário (remove segundos se houver)
                if (horario != null && horario.length() > 5) {
                    horario = horario.substring(0, 5);
                }
                labelHorarioDefesa.setText(horario != null ? horario : "-");
                labelSalaDefesa.setText(sala != null ? sala : "-");
            } else {
                labelDataDefesa.setText("Não agendado");
                labelHorarioDefesa.setText("-");
                labelSalaDefesa.setText("-");
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelDataDefesa.setText("Erro ao carregar");
            labelHorarioDefesa.setText("-");
            labelSalaDefesa.setText("-");
        }
    }
    
    /**
     * Navega para a tela de enviar API
     */
    @FXML
    private void navegarEnviarApi(ActionEvent event) {
        PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
            "/com/example/technocode/Aluno/formulario-api.fxml", null);
    }
    
    /**
     * Navega para a tela de enviar Apresentação
     */
    @FXML
    private void navegarEnviarApresentacao(ActionEvent event) {
        PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
            "/com/example/technocode/Aluno/formulario-apresentacao.fxml", null);
    }

    /**
     * Carrega informações do último feedback
     */
    private void carregarUltimoFeedback() {
        try (Connection conn = new Connector().getConnection()) {
            // Busca último feedback de API (ordenado por horario_feedback)
            String sqlApi = "SELECT sa.aluno, sa.semestre_curso, sa.ano, sa.semestre_ano, sa.versao, sa.horario_feedback " +
                    "FROM secao_api sa " +
                    "WHERE sa.aluno = ? AND sa.horario_feedback IS NOT NULL " +
                    "AND (sa.status_empresa IS NOT NULL OR sa.status_problema IS NOT NULL) " +
                    "ORDER BY sa.horario_feedback DESC LIMIT 1";
            
            Timestamp dataUltimoFeedbackApi = null;
            Map<String, String> dadosFeedbackApi = null;
            
            PreparedStatement pstApi = conn.prepareStatement(sqlApi);
            pstApi.setString(1, emailAluno);
            ResultSet rsApi = pstApi.executeQuery();
            if (rsApi.next()) {
                dataUltimoFeedbackApi = rsApi.getTimestamp("horario_feedback");
                dadosFeedbackApi = new java.util.HashMap<>();
                dadosFeedbackApi.put("tipo", "api");
                dadosFeedbackApi.put("semestre_curso", rsApi.getString("semestre_curso"));
                dadosFeedbackApi.put("ano", String.valueOf(rsApi.getInt("ano")));
                dadosFeedbackApi.put("semestre_ano", rsApi.getString("semestre_ano"));
                dadosFeedbackApi.put("versao", String.valueOf(rsApi.getInt("versao")));
            }

            // Busca último feedback de Apresentação (ordenado por horario_feedback)
            String sqlApresentacao = "SELECT sa.aluno, sa.versao, sa.horario_feedback " +
                    "FROM secao_apresentacao sa " +
                    "WHERE sa.aluno = ? AND sa.horario_feedback IS NOT NULL " +
                    "AND (sa.status_nome IS NOT NULL OR sa.status_idade IS NOT NULL) " +
                    "ORDER BY sa.horario_feedback DESC LIMIT 1";
            
            Timestamp dataUltimoFeedbackApresentacao = null;
            Map<String, String> dadosFeedbackApresentacao = null;
            
            PreparedStatement pstApresentacao = conn.prepareStatement(sqlApresentacao);
            pstApresentacao.setString(1, emailAluno);
            ResultSet rsApresentacao = pstApresentacao.executeQuery();
            if (rsApresentacao.next()) {
                dataUltimoFeedbackApresentacao = rsApresentacao.getTimestamp("horario_feedback");
                dadosFeedbackApresentacao = new java.util.HashMap<>();
                dadosFeedbackApresentacao.put("tipo", "apresentacao");
                dadosFeedbackApresentacao.put("versao", String.valueOf(rsApresentacao.getInt("versao")));
            }

            // Compara qual é mais recente usando horario_feedback
            if (dataUltimoFeedbackApi != null && dataUltimoFeedbackApresentacao != null) {
                // Compara qual feedback é mais recente
                if (dataUltimoFeedbackApi.after(dataUltimoFeedbackApresentacao)) {
                    mostrarUltimoFeedback(dataUltimoFeedbackApi, dadosFeedbackApi);
                } else {
                    mostrarUltimoFeedback(dataUltimoFeedbackApresentacao, dadosFeedbackApresentacao);
                }
            } else if (dataUltimoFeedbackApi != null) {
                mostrarUltimoFeedback(dataUltimoFeedbackApi, dadosFeedbackApi);
            } else if (dataUltimoFeedbackApresentacao != null) {
                mostrarUltimoFeedback(dataUltimoFeedbackApresentacao, dadosFeedbackApresentacao);
            } else {
                labelDataUltimoFeedback.setText("Nenhum feedback ainda");
                btnVerDetalhesFeedback.setVisible(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            labelDataUltimoFeedback.setText("Erro ao carregar");
        }
    }

    private void mostrarUltimoFeedback(Timestamp data, Map<String, String> dados) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String tipoSecao = "api".equals(dados.get("tipo")) ? "API" : "Apresentação";
        labelDataUltimoFeedback.setText(sdf.format(data) + " - Seção " + tipoSecao);
        btnVerDetalhesFeedback.setVisible(true);
        ultimoFeedbackTipo = dados.get("tipo");
        ultimoFeedbackDados = dados;
    }

    /**
     * Carrega progresso geral
     */
    private void carregarProgressoGeral() {
        try (Connection conn = new Connector().getConnection()) {
            // Seções API enviadas
            int secoesApiEnviadas = contarSecoesApiEnviadas(conn);
            labelSecoesApiEnviadas.setText(String.valueOf(secoesApiEnviadas));

            // Seções API avaliadas
            int secoesApiAvaliadas = contarSecoesApiAvaliadas(conn);
            labelSecoesApiAvaliadas.setText(String.valueOf(secoesApiAvaliadas));

            // Pendências API
            int pendenciasApi = contarPendenciasApi(conn);
            labelPendenciasApi.setText(String.valueOf(pendenciasApi));

            // Pendências Apresentação
            int pendenciasApresentacao = contarPendenciasApresentacao(conn);
            labelPendenciasApresentacao.setText(String.valueOf(pendenciasApresentacao));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int contarSecoesApresentacaoEnviadas(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT versao) FROM secao_apresentacao WHERE aluno = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private int contarSecoesApresentacaoAvaliadas(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, MAX(versao) as versao_recente " +
                "FROM secao_apresentacao WHERE aluno = ? " +
                "GROUP BY aluno " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_apresentacao fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE (fa.status_nome IS NOT NULL OR fa.status_idade IS NOT NULL)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    /**
     * Carrega informações das últimas seções
     */
    private void carregarMinhasSecoes() {
        try (Connection conn = new Connector().getConnection()) {
            // Busca última seção API enviada (com data)
            String sqlUltimaApi = "SELECT semestre_curso, ano, semestre_ano, versao, horario_secao " +
                    "FROM secao_api WHERE aluno = ? " +
                    "ORDER BY horario_secao DESC LIMIT 1";
            
            PreparedStatement pstApi = conn.prepareStatement(sqlUltimaApi);
            pstApi.setString(1, emailAluno);
            ResultSet rsApi = pstApi.executeQuery();
            
            // Busca última versão de apresentação (sem data, ordenado por versão)
            String sqlUltimaApresentacao = "SELECT versao " +
                    "FROM secao_apresentacao WHERE aluno = ? " +
                    "ORDER BY versao DESC LIMIT 1";
            
            PreparedStatement pstApresentacao = conn.prepareStatement(sqlUltimaApresentacao);
            pstApresentacao.setString(1, emailAluno);
            ResultSet rsApresentacao = pstApresentacao.executeQuery();
            
            boolean temApi = rsApi.next();
            boolean temApresentacao = rsApresentacao.next();
            
            if (temApi) {
                // Prioriza mostrar API se existir, pois tem data
                labelUltimaSecaoEnviada.setText("Seção API");
                labelUltimaVersaoApi.setText("Versão " + rsApi.getInt("versao"));
                Timestamp horario = rsApi.getTimestamp("horario_secao");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                labelDataHoraUltimaSecao.setText(sdf.format(horario));
            } else if (temApresentacao) {
                // Se só tiver apresentação, mostra ela
                labelUltimaSecaoEnviada.setText("Seção de Apresentação");
                labelUltimaVersaoApi.setText("Versão " + rsApresentacao.getInt("versao"));
                labelDataHoraUltimaSecao.setText("Data não disponível");
            } else {
                labelUltimaSecaoEnviada.setText("Nenhuma seção ainda");
                labelUltimaVersaoApi.setText("-");
                labelDataHoraUltimaSecao.setText("-");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega informações da apresentação
     */
    private void carregarMinhaApresentacao() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT MAX(versao) as versao_maxima, COUNT(*) as total " +
                    "FROM secao_apresentacao WHERE aluno = ?";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next() && rs.getInt("total") > 0) {
                int versaoMaxima = rs.getInt("versao_maxima");
                labelVersaoAtualApresentacao.setText("Versão " + versaoMaxima);
                
                // Primeiro verifica se a apresentação foi avaliada (se algum status não é NULL)
                String sqlAvaliada = "SELECT COUNT(*) as avaliada " +
                        "FROM secao_apresentacao " +
                        "WHERE aluno = ? AND versao = ? " +
                        "AND (status_nome IS NOT NULL OR status_idade IS NOT NULL OR status_curso IS NOT NULL " +
                        "OR status_motivacao IS NOT NULL OR status_historico IS NOT NULL OR status_github IS NOT NULL " +
                        "OR status_linkedin IS NOT NULL OR status_conhecimentos IS NOT NULL)";
                
                PreparedStatement pstAvaliada = conn.prepareStatement(sqlAvaliada);
                pstAvaliada.setString(1, emailAluno);
                pstAvaliada.setInt(2, versaoMaxima);
                ResultSet rsAvaliada = pstAvaliada.executeQuery();
                
                boolean foiAvaliada = false;
                if (rsAvaliada.next()) {
                    foiAvaliada = rsAvaliada.getInt("avaliada") > 0;
                }
                
                if (!foiAvaliada) {
                    // Apresentação ainda não foi avaliada pelo orientador
                    labelStatusApresentacao.setText("Aguardando avaliação");
                    labelStatusApresentacao.setStyle("-fx-text-fill: #F57C00;");
                    labelPendenciasApresentacaoDetalhe.setText("0");
                } else {
                    // Verifica se há status "Revisar"
                    String sqlStatus = "SELECT COUNT(*) as pendentes " +
                            "FROM secao_apresentacao " +
                            "WHERE aluno = ? AND versao = ? " +
                            "AND (status_nome = 'Revisar' OR status_idade = 'Revisar' OR status_curso = 'Revisar' " +
                            "OR status_motivacao = 'Revisar' OR status_historico = 'Revisar' OR status_github = 'Revisar' " +
                            "OR status_linkedin = 'Revisar' OR status_conhecimentos = 'Revisar')";
                    
                    PreparedStatement pstStatus = conn.prepareStatement(sqlStatus);
                    pstStatus.setString(1, emailAluno);
                    pstStatus.setInt(2, versaoMaxima);
                    ResultSet rsStatus = pstStatus.executeQuery();
                    
                    if (rsStatus.next()) {
                        int pendentes = rsStatus.getInt("pendentes");
                        labelPendenciasApresentacaoDetalhe.setText(String.valueOf(pendentes));
                        
                        if (pendentes > 0) {
                            labelStatusApresentacao.setText("Revisão necessária");
                            labelStatusApresentacao.setStyle("-fx-text-fill: #E74C3C;");
                        } else {
                            labelStatusApresentacao.setText("Aprovada");
                            labelStatusApresentacao.setStyle("-fx-text-fill: #27AE60;");
                        }
                    }
                }
            } else {
                labelStatusApresentacao.setText("Nenhuma apresentação");
                labelVersaoAtualApresentacao.setText("-");
                labelPendenciasApresentacaoDetalhe.setText("0");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para contar estatísticas
    private int contarSecoesApiEnviadas(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT CONCAT(semestre_curso, '-', ano, '-', semestre_ano)) " +
                "FROM secao_api WHERE aluno = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private int contarSecoesApiAvaliadas(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_api fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.semestre_curso = fa.semestre_curso AND " +
                "  versoes_recentes.ano = fa.ano AND " +
                "  versoes_recentes.semestre_ano = fa.semestre_ano AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE (fa.status_empresa IS NOT NULL OR fa.status_problema IS NOT NULL)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private int contarPendenciasApi(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_api fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.semestre_curso = fa.semestre_curso AND " +
                "  versoes_recentes.ano = fa.ano AND " +
                "  versoes_recentes.semestre_ano = fa.semestre_ano AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE fa.status_empresa = 'Revisar' OR fa.status_problema = 'Revisar' " +
                "OR fa.status_solucao = 'Revisar' OR fa.status_tecnologias = 'Revisar'";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private int contarPendenciasApresentacao(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, MAX(versao) as versao_recente " +
                "FROM secao_apresentacao WHERE aluno = ? " +
                "GROUP BY aluno " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_apresentacao fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE fa.status_nome = 'Revisar' OR fa.status_idade = 'Revisar' " +
                "OR fa.status_curso = 'Revisar' OR fa.status_motivacao = 'Revisar'";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    // Métodos de navegação
    @FXML
    private void verDetalhesFeedback(ActionEvent event) throws IOException {
        if (ultimoFeedbackDados == null) return;
        
        PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
            "api".equals(ultimoFeedbackTipo) ? 
                "/com/example/technocode/Aluno/aluno-feedback-api.fxml" :
                "/com/example/technocode/Aluno/aluno-feedback-apresentacao.fxml",
            controller -> {
                if ("api".equals(ultimoFeedbackTipo) && controller instanceof AlunoFeedbackApiController) {
                    ((AlunoFeedbackApiController) controller).setIdentificadorSecao(
                        emailAluno,
                        ultimoFeedbackDados.get("semestre_curso"),
                        Integer.parseInt(ultimoFeedbackDados.get("ano")),
                        ultimoFeedbackDados.get("semestre_ano"),
                        Integer.parseInt(ultimoFeedbackDados.get("versao"))
                    );
                } else if ("apresentacao".equals(ultimoFeedbackTipo) && controller instanceof AlunoFeedbackApresentacaoController) {
                    ((AlunoFeedbackApresentacaoController) controller).setIdentificadorSecao(
                        emailAluno,
                        Integer.parseInt(ultimoFeedbackDados.get("versao"))
                    );
                }
            }
        );
    }


    /**
     * Método público para atualizar estatísticas
     */
    public void atualizarEstatisticas() {
        carregarDados();
    }
}
