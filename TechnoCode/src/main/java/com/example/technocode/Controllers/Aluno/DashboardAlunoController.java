package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.Orientador;
import com.example.technocode.model.SolicitacaoOrientacao;
import com.example.technocode.model.dao.Connector;
import com.example.technocode.Controllers.Aluno.AlunoFeedbackApiController;
import com.example.technocode.Controllers.Aluno.AlunoFeedbackApresentacaoController;
import com.example.technocode.Controllers.Aluno.PrincipalAlunoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private VBox vboxUltimosFeedbacks;
    @FXML
    private VBox vboxProgressoGeral;
    @FXML
    private Label labelDataDefesa;
    @FXML
    private Label labelHorarioDefesa;
    @FXML
    private Label labelSalaDefesa;

    private String emailAluno;
    private List<Map<String, Object>> ultimosFeedbacks = new ArrayList<>();

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
        carregarUltimosFeedbacks();
        carregarProgressoGeral();
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

            // Busca status da solicitação mais recente primeiro (para usar depois)
            List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPorAluno(emailAluno);
            Map<String, String> ultimaSolicitacao = null;
            String statusSolicitacao = null;
            
            if (!solicitacoes.isEmpty()) {
                ultimaSolicitacao = solicitacoes.get(0); // Já vem ordenado por data DESC
                statusSolicitacao = ultimaSolicitacao.get("status");
                
                // Atualiza o label de status
                if ("Pendente".equals(statusSolicitacao)) {
                    labelStatusSolicitacao.setText("Pendente");
                    labelStatusSolicitacao.setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;");
                } else if ("Aceita".equals(statusSolicitacao)) {
                    labelStatusSolicitacao.setText("Aceita");
                    labelStatusSolicitacao.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                } else if ("Recusada".equals(statusSolicitacao)) {
                    labelStatusSolicitacao.setText("Recusada");
                    labelStatusSolicitacao.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                }
            } else {
                labelStatusSolicitacao.setText("-");
            }

            // Busca orientador aceito
            try (Connection conn = new Connector().getConnection()) {
                String sql = "SELECT orientador FROM aluno WHERE email = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, emailAluno);
                ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    String emailOrientador = rs.getString("orientador");
                    if (emailOrientador != null && !emailOrientador.isEmpty()) {
                        // Aluno tem orientador aceito
                        Map<String, String> dadosOrientador = Orientador.buscarDadosPorEmail(emailOrientador);
                        labelNomeOrientador.setText(dadosOrientador.get("nome") != null ? dadosOrientador.get("nome") : "N/A");
                        labelNomeOrientador.setStyle("-fx-text-fill: #2C3E50;");
                    } else {
                        // Aluno não tem orientador aceito - verifica se há solicitação pendente
                        if (ultimaSolicitacao != null && "Pendente".equals(statusSolicitacao)) {
                            // Mostra o nome do orientador solicitado
                            String nomeOrientadorSolicitado = ultimaSolicitacao.get("nome_orientador");
                            if (nomeOrientadorSolicitado != null && !nomeOrientadorSolicitado.isEmpty()) {
                                labelNomeOrientador.setText(nomeOrientadorSolicitado);
                                labelNomeOrientador.setStyle("-fx-text-fill: #2C3E50;");
                            } else {
                                labelNomeOrientador.setText("Nenhum orientador ainda");
                                labelNomeOrientador.setStyle("-fx-text-fill: #2C3E50;");
                            }
                        } else {
                            labelNomeOrientador.setText("Nenhum orientador ainda");
                            labelNomeOrientador.setStyle("-fx-text-fill: #2C3E50;");
                        }
                    }
                } else {
                    // Caso não encontre o aluno (não deveria acontecer)
                    labelNomeOrientador.setText("Nenhum orientador ainda");
                    labelNomeOrientador.setStyle("-fx-text-fill: #F57C00;");
                }
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
            String sql = "SELECT data_defesa, horario, sala " +
                        "FROM agendamento_defesa_tg " +
                        "WHERE email_aluno = ? " +
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
     * Carrega os últimos 3 feedbacks (API e Apresentação combinados)
     */
    private void carregarUltimosFeedbacks() {
        ultimosFeedbacks.clear();
        
        try (Connection conn = new Connector().getConnection()) {
            // Busca últimos feedbacks de API (formatando horário diretamente no SQL para evitar problemas de timezone)
            String sqlApi = "SELECT sa.semestre_curso, sa.ano, sa.semestre_ano, sa.versao, " +
                    "DATE_FORMAT(sa.horario_feedback, '%d/%m/%Y às %H:%i') as horario_formatado, " +
                    "sa.empresa, sa.horario_feedback " +
                    "FROM secao_api sa " +
                    "WHERE sa.aluno = ? AND sa.horario_feedback IS NOT NULL " +
                    "AND (sa.status_empresa IS NOT NULL OR sa.status_problema IS NOT NULL) " +
                    "ORDER BY sa.horario_feedback DESC LIMIT 3";
            
            PreparedStatement pstApi = conn.prepareStatement(sqlApi);
            pstApi.setString(1, emailAluno);
            ResultSet rsApi = pstApi.executeQuery();
            
            while (rsApi.next()) {
                Map<String, Object> feedback = new java.util.HashMap<>();
                feedback.put("tipo", "api");
                feedback.put("semestre_curso", rsApi.getString("semestre_curso"));
                feedback.put("ano", rsApi.getInt("ano"));
                feedback.put("semestre_ano", rsApi.getString("semestre_ano"));
                feedback.put("versao", rsApi.getInt("versao"));
                feedback.put("empresa", rsApi.getString("empresa"));
                feedback.put("horario_feedback_formatado", rsApi.getString("horario_formatado"));
                feedback.put("horario_feedback_timestamp", rsApi.getTimestamp("horario_feedback")); // Para ordenação
                ultimosFeedbacks.add(feedback);
            }

            // Busca últimos feedbacks de Apresentação (formatando horário diretamente no SQL)
            String sqlApresentacao = "SELECT sa.versao, DATE_FORMAT(sa.horario_feedback, '%d/%m/%Y às %H:%i') as horario_formatado, " +
                    "sa.horario_feedback " +
                    "FROM secao_apresentacao sa " +
                    "WHERE sa.aluno = ? AND sa.horario_feedback IS NOT NULL " +
                    "AND (sa.status_nome IS NOT NULL OR sa.status_idade IS NOT NULL) " +
                    "ORDER BY sa.horario_feedback DESC LIMIT 3";
            
            PreparedStatement pstApresentacao = conn.prepareStatement(sqlApresentacao);
            pstApresentacao.setString(1, emailAluno);
            ResultSet rsApresentacao = pstApresentacao.executeQuery();
            
            while (rsApresentacao.next()) {
                Map<String, Object> feedback = new java.util.HashMap<>();
                feedback.put("tipo", "apresentacao");
                feedback.put("versao", rsApresentacao.getInt("versao"));
                feedback.put("horario_feedback_formatado", rsApresentacao.getString("horario_formatado"));
                feedback.put("horario_feedback_timestamp", rsApresentacao.getTimestamp("horario_feedback")); // Para ordenação
                ultimosFeedbacks.add(feedback);
            }

            // Ordena todos os feedbacks por data (mais recente primeiro) usando o timestamp original
            ultimosFeedbacks.sort((f1, f2) -> {
                Timestamp t1 = (Timestamp) f1.get("horario_feedback_timestamp");
                Timestamp t2 = (Timestamp) f2.get("horario_feedback_timestamp");
                if (t1 == null && t2 == null) return 0;
                if (t1 == null) return 1;
                if (t2 == null) return -1;
                return t2.compareTo(t1); // Ordem decrescente
            });
            
            // Limita aos 3 mais recentes
            if (ultimosFeedbacks.size() > 3) {
                ultimosFeedbacks = new ArrayList<>(ultimosFeedbacks.subList(0, 3));
            }
            
            if (ultimosFeedbacks.size() > 3) {
                ultimosFeedbacks = ultimosFeedbacks.subList(0, 3);
            }
            
            mostrarUltimosFeedbacks();

        } catch (Exception e) {
            e.printStackTrace();
            Label labelErro = new Label("Erro ao carregar feedbacks");
            labelErro.setTextFill(Color.web("#E74C3C"));
            vboxUltimosFeedbacks.getChildren().clear();
            vboxUltimosFeedbacks.getChildren().add(labelErro);
        }
    }

    private void mostrarUltimosFeedbacks() {
        vboxUltimosFeedbacks.getChildren().clear();
        
        if (ultimosFeedbacks.isEmpty()) {
            Label labelVazio = new Label("Nenhum feedback ainda");
            labelVazio.setTextFill(Color.web("#7F8C8D"));
            labelVazio.setFont(javafx.scene.text.Font.font(11.0));
            vboxUltimosFeedbacks.getChildren().add(labelVazio);
            return;
        }
        
        // Cria botões clicáveis para cada feedback
        for (int i = 0; i < Math.min(3, ultimosFeedbacks.size()); i++) {
            Map<String, Object> feedback = ultimosFeedbacks.get(i);
            String horarioFormatado = (String) feedback.get("horario_feedback_formatado");
            String tipo = (String) feedback.get("tipo");
            
            String identificador = "";
            
            if ("api".equals(tipo)) {
                String semestreCurso = (String) feedback.get("semestre_curso");
                String empresa = (String) feedback.get("empresa");
                if (semestreCurso != null && empresa != null) {
                    identificador = semestreCurso + " - " + empresa;
                } else if (semestreCurso != null) {
                    identificador = semestreCurso;
                } else {
                    identificador = "API";
                }
            } else {
                identificador = "Apresentação";
            }
            
            // Cria um botão estilizado para cada feedback
            Button btnFeedback = new Button();
            btnFeedback.setStyle("-fx-background-color: #E8F4F8; -fx-background-radius: 8; -fx-cursor: hand; " +
                    "-fx-border-color: #3498DB; -fx-border-radius: 8; -fx-border-width: 1; " +
                    "-fx-padding: 10 15; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.2), 3, 0, 0, 1);");
            btnFeedback.setMaxWidth(Double.MAX_VALUE);
            btnFeedback.setAlignment(Pos.CENTER_LEFT);
            
            // Cria HBox com identificador à esquerda e horário à direita
            HBox hboxFeedback = new HBox();
            hboxFeedback.setSpacing(10);
            hboxFeedback.setAlignment(Pos.CENTER_LEFT);
            
            Label labelIdentificador = new Label(identificador);
            labelIdentificador.setTextFill(Color.web("#7F8C8D"));
            labelIdentificador.setFont(javafx.scene.text.Font.font(11.0));

            // Region para empurrar o horário para a direita
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label labelHorario = new Label(horarioFormatado != null ? horarioFormatado : "");
            labelHorario.setTextFill(Color.web("#2C3E50"));
            labelHorario.setFont(javafx.scene.text.Font.font("System Bold", 11.0));
            
            hboxFeedback.getChildren().addAll(labelIdentificador, spacer, labelHorario);
            btnFeedback.setGraphic(hboxFeedback);
            
            // Adiciona ação de clique
            final Map<String, Object> feedbackFinal = feedback;
            final String tipoFinal = tipo;
            btnFeedback.setOnAction(e -> abrirFeedback(feedbackFinal, tipoFinal));
            
            // Efeito hover
            btnFeedback.setOnMouseEntered(e -> {
                btnFeedback.setStyle("-fx-background-color: #D6EAF8; -fx-background-radius: 8; -fx-cursor: hand; " +
                        "-fx-border-color: #3498DB; -fx-border-radius: 8; -fx-border-width: 1.5; " +
                        "-fx-padding: 10 15; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 5, 0, 0, 2);");
            });
            btnFeedback.setOnMouseExited(e -> {
                btnFeedback.setStyle("-fx-background-color: #E8F4F8; -fx-background-radius: 8; -fx-cursor: hand; " +
                        "-fx-border-color: #3498DB; -fx-border-radius: 8; -fx-border-width: 1; " +
                        "-fx-padding: 10 15; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.2), 3, 0, 0, 1);");
            });
            
            vboxUltimosFeedbacks.getChildren().add(btnFeedback);
        }
    }

    private void abrirFeedback(Map<String, Object> feedback, String tipo) {
        if ("api".equals(tipo)) {
            String semestreCurso = (String) feedback.get("semestre_curso");
            int ano = (Integer) feedback.get("ano");
            String semestreAno = (String) feedback.get("semestre_ano");
            int versao = (Integer) feedback.get("versao");
            
            PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Aluno/aluno-feedback-api.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApiController) {
                        ((AlunoFeedbackApiController) controller).setIdentificadorSecao(
                            emailAluno, semestreCurso, ano, semestreAno, versao
                        );
                    }
                }
            );
        } else {
            int versao = (Integer) feedback.get("versao");
            
            PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Aluno/aluno-feedback-apresentacao.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApresentacaoController) {
                        ((AlunoFeedbackApresentacaoController) controller).setIdentificadorSecao(
                            emailAluno, versao
                        );
                    }
                }
            );
        }
    }

    /**
     * Carrega progresso geral no formato X/9 com status de aprovação
     */
    private void carregarProgressoGeral() {
        vboxProgressoGeral.getChildren().clear();
        
        try (Connection conn = new Connector().getConnection()) {
            // Busca todas as seções API (última versão de cada semestre)
            String sqlApi = "SELECT sa.semestre_curso, sa.ano, sa.semestre_ano, sa.versao, sa.empresa " +
                    "FROM ( " +
                    "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                    "FROM secao_api WHERE aluno = ? " +
                    "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                    ") AS versoes_recentes " +
                    "INNER JOIN secao_api sa ON " +
                    "  versoes_recentes.aluno = sa.aluno AND " +
                    "  versoes_recentes.semestre_curso = sa.semestre_curso AND " +
                    "  versoes_recentes.ano = sa.ano AND " +
                    "  versoes_recentes.semestre_ano = sa.semestre_ano AND " +
                    "  versoes_recentes.versao_recente = sa.versao";
            
            PreparedStatement pstApi = conn.prepareStatement(sqlApi);
            pstApi.setString(1, emailAluno);
            ResultSet rsApi = pstApi.executeQuery();
            
            List<Map<String, Object>> secoesApi = new ArrayList<>();
            while (rsApi.next()) {
                Map<String, Object> secao = new java.util.HashMap<>();
                secao.put("tipo", "api");
                secao.put("semestre_curso", rsApi.getString("semestre_curso"));
                secao.put("ano", rsApi.getInt("ano"));
                secao.put("semestre_ano", rsApi.getString("semestre_ano"));
                secao.put("versao", rsApi.getInt("versao"));
                secao.put("empresa", rsApi.getString("empresa"));
                secoesApi.add(secao);
            }
            
            // Ordena as seções API pelo número do semestre do curso (1°, 2°, 3°, etc.)
            secoesApi.sort((s1, s2) -> {
                String sem1 = (String) s1.get("semestre_curso");
                String sem2 = (String) s2.get("semestre_curso");
                
                if (sem1 == null && sem2 == null) return 0;
                if (sem1 == null) return 1;
                if (sem2 == null) return -1;
                
                // Extrai o número do semestre (ex: "1°Semestre" -> 1)
                int num1 = extrairNumeroSemestre(sem1);
                int num2 = extrairNumeroSemestre(sem2);
                
                return Integer.compare(num1, num2);
            });
            
            // Busca última versão de apresentação
            String sqlApresentacao = "SELECT MAX(versao) as versao_maxima " +
                    "FROM secao_apresentacao WHERE aluno = ?";
            
            PreparedStatement pstApresentacao = conn.prepareStatement(sqlApresentacao);
            pstApresentacao.setString(1, emailAluno);
            ResultSet rsApresentacao = pstApresentacao.executeQuery();
            
            if (rsApresentacao.next() && rsApresentacao.getInt("versao_maxima") > 0) {
                Map<String, Object> apresentacao = new java.util.HashMap<>();
                apresentacao.put("tipo", "apresentacao");
                apresentacao.put("versao", rsApresentacao.getInt("versao_maxima"));
                
                // Adiciona apresentação primeiro
                adicionarItemProgresso(apresentacao, conn);
            }
            
            // Adiciona seções API
            for (Map<String, Object> secao : secoesApi) {
                adicionarItemProgresso(secao, conn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adicionarItemProgresso(Map<String, Object> secao, Connection conn) throws SQLException {
        String tipo = (String) secao.get("tipo");
        int aprovados = 0;
        boolean aprovada = false;
        String nome = "";
        
        if ("apresentacao".equals(tipo)) {
            int versao = (Integer) secao.get("versao");
            nome = "Apresentação";
            
            String sql = "SELECT status_nome, status_idade, status_curso, status_motivacao, " +
                        "status_historico, status_historico_profissional, status_github, " +
                        "status_linkedin, status_conhecimentos " +
                        "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            pst.setInt(2, versao);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                if ("Aprovado".equals(rs.getString("status_nome"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_idade"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_curso"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_motivacao"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_historico"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_historico_profissional"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_github"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_linkedin"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_conhecimentos"))) aprovados++;
                
                aprovada = aprovados == 9;
            }
                } else {
            String semestreCurso = (String) secao.get("semestre_curso");
            int ano = (Integer) secao.get("ano");
            String semestreAno = (String) secao.get("semestre_ano");
            int versao = (Integer) secao.get("versao");
            String empresa = (String) secao.get("empresa");
                        
            if (semestreCurso != null && empresa != null) {
                nome = semestreCurso + " - " + empresa;
            } else if (semestreCurso != null) {
                nome = semestreCurso;
            } else {
                nome = "API";
            }
            
            String sql = "SELECT status_empresa, status_descricao_empresa, status_repositorio, " +
                        "status_problema, status_solucao, status_tecnologias, status_contribuicoes, " +
                        "status_hard_skills, status_soft_skills " +
                        "FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? " +
                        "AND semestre_ano = ? AND versao = ?";
            
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailAluno);
            pst.setString(2, semestreCurso);
            pst.setInt(3, ano);
            pst.setString(4, semestreAno);
            pst.setInt(5, versao);
        ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                if ("Aprovado".equals(rs.getString("status_empresa"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_descricao_empresa"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_repositorio"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_problema"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_solucao"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_tecnologias"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_contribuicoes"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_hard_skills"))) aprovados++;
                if ("Aprovado".equals(rs.getString("status_soft_skills"))) aprovados++;
                
                aprovada = aprovados == 9;
            }
        }
        
        // Cria um botão clicável para o progresso (estilo similar aos feedbacks, mais estreito)
        Button btnProgresso = new Button();
        btnProgresso.setMaxWidth(Double.MAX_VALUE);
        btnProgresso.setAlignment(Pos.CENTER_LEFT);
        btnProgresso.setStyle("-fx-background-color: #E8F4F8; -fx-background-radius: 8; -fx-cursor: hand; " +
                "-fx-border-color: #27AE60; -fx-border-radius: 8; -fx-border-width: 1; " +
                "-fx-padding: 10 12; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.2), 3, 0, 0, 1);");
        
        HBox hboxConteudo = new HBox();
        hboxConteudo.setSpacing(10);
        hboxConteudo.setAlignment(Pos.CENTER_LEFT);
        
        Label labelNome = new Label(nome);
        labelNome.setTextFill(Color.web("#7F8C8D"));
        labelNome.setFont(javafx.scene.text.Font.font(11.0));
        
        // Region para empurrar o progresso para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        String textoProgresso = String.format("%d/9", aprovados);
        if (aprovada) {
            textoProgresso += " ✓ Aprovada";
        }
        
        Label labelProgresso = new Label(textoProgresso);
        labelProgresso.setTextFill(aprovada ? Color.web("#27AE60") : Color.web("#2C3E50"));
        labelProgresso.setFont(javafx.scene.text.Font.font("System Bold", 11.0));
        
        hboxConteudo.getChildren().addAll(labelNome, spacer, labelProgresso);
        btnProgresso.setGraphic(hboxConteudo);
        
        // Adiciona ação de clique
        final Map<String, Object> secaoFinal = secao;
        final String tipoFinal = tipo;
        btnProgresso.setOnAction(e -> abrirSecaoProgresso(secaoFinal, tipoFinal));
        
        // Efeito hover (ajustado para não sumir no scroll pane)
        btnProgresso.setOnMouseEntered(e -> {
            btnProgresso.setStyle("-fx-background-color: #D6EAF8; -fx-background-radius: 8; -fx-cursor: hand; " +
                    "-fx-border-color: #27AE60; -fx-border-radius: 8; -fx-border-width: 1.5; " +
                    "-fx-padding: 10 12; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.3), 4, 0, 0, 1.5);");
        });
        btnProgresso.setOnMouseExited(e -> {
            btnProgresso.setStyle("-fx-background-color: #E8F4F8; -fx-background-radius: 8; -fx-cursor: hand; " +
                    "-fx-border-color: #27AE60; -fx-border-radius: 8; -fx-border-width: 1; " +
                    "-fx-padding: 10 12; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.2), 3, 0, 0, 1);");
        });
        
        vboxProgressoGeral.getChildren().add(btnProgresso);
    }
    
    private void abrirSecaoProgresso(Map<String, Object> secao, String tipo) {
        if ("apresentacao".equals(tipo)) {
            int versao = (Integer) secao.get("versao");
        
        PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Aluno/aluno-feedback-apresentacao.fxml",
            controller -> {
                    if (controller instanceof AlunoFeedbackApresentacaoController) {
                    ((AlunoFeedbackApresentacaoController) controller).setIdentificadorSecao(
                            emailAluno, versao
                    );
                }
            }
        );
        } else {
            String semestreCurso = (String) secao.get("semestre_curso");
            int ano = (Integer) secao.get("ano");
            String semestreAno = (String) secao.get("semestre_ano");
            int versao = (Integer) secao.get("versao");
            
            PrincipalAlunoController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Aluno/aluno-feedback-api.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApiController) {
                        ((AlunoFeedbackApiController) controller).setIdentificadorSecao(
                            emailAluno, semestreCurso, ano, semestreAno, versao
                        );
                    }
                }
            );
        }
    }




    /**
     * Extrai o número do semestre do curso (ex: "1°Semestre" -> 1)
     */
    private int extrairNumeroSemestre(String semestreCurso) {
        if (semestreCurso == null || semestreCurso.isEmpty()) {
            return 0;
        }
        
        // Remove espaços e tenta encontrar o número
        String temp = semestreCurso.trim();
        // Procura por um número seguido de °
        int inicio = 0;
        while (inicio < temp.length() && !Character.isDigit(temp.charAt(inicio))) {
            inicio++;
        }
        
        if (inicio >= temp.length()) {
            return 0;
        }
        
        int fim = inicio;
        while (fim < temp.length() && Character.isDigit(temp.charAt(fim))) {
            fim++;
        }
        
        try {
            return Integer.parseInt(temp.substring(inicio, fim));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Método público para atualizar estatísticas
     */
    public void atualizarEstatisticas() {
        carregarDados();
    }
}
