package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller para o Dashboard do Aluno
 * Exibe estatísticas sobre as sessões do aluno
 */
public class DashboardAlunoController {

    @FXML
    private Label lblTotalSessoes;
    @FXML
    private Label lblSessoesRespondidas;
    @FXML
    private Label lblSessoesPendentes;
    @FXML
    private Label lblSessoesAprovadas;
    @FXML
    private Label lblSessoesRevisar;
    @FXML
    private Label lblPercentualProgresso;
    @FXML
    private PieChart pieChartStatus;
    @FXML
    private VBox containerEstatisticas;

    private String emailAluno;

    @FXML
    public void initialize() {
        emailAluno = LoginController.getEmailLogado();
        if (emailAluno != null && !emailAluno.isBlank()) {
            carregarEstatisticas();
        }
    }

    /**
     * Carrega todas as estatísticas do aluno
     */
    private void carregarEstatisticas() {
        try (Connection conn = new Connector().getConnection()) {
            // Total de sessões enviadas
            int totalSessoes = contarTotalSessoes(conn);
            lblTotalSessoes.setText(String.valueOf(totalSessoes));

            // Sessões respondidas (com feedback)
            int sessoesRespondidas = contarSessoesRespondidas(conn);
            lblSessoesRespondidas.setText(String.valueOf(sessoesRespondidas));

            // Sessões pendentes (sem feedback)
            int sessoesPendentes = totalSessoes - sessoesRespondidas;
            lblSessoesPendentes.setText(String.valueOf(sessoesPendentes));

            // Sessões aprovadas
            int sessoesAprovadas = contarSessoesAprovadas(conn);
            lblSessoesAprovadas.setText(String.valueOf(sessoesAprovadas));

            // Sessões para revisar
            int sessoesRevisar = contarSessoesRevisar(conn);
            lblSessoesRevisar.setText(String.valueOf(sessoesRevisar));

            // Percentual de progresso
            double percentual = totalSessoes > 0 ? 
                ((double) sessoesRespondidas / totalSessoes) * 100 : 0;
            lblPercentualProgresso.setText(String.format("%.1f%%", percentual));

            // Carrega gráfico
            carregarGraficoStatus(sessoesAprovadas, sessoesRevisar, sessoesPendentes);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar estatísticas: " + e.getMessage());
        }
    }

    /**
     * Conta o total de sessões do aluno (apresentação + API)
     */
    private int contarTotalSessoes(Connection conn) throws SQLException {
        // Conta apenas versões mais recentes
        String sqlApresentacao = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, MAX(versao) as versao_recente " +
                "FROM secao_apresentacao " +
                "WHERE aluno = ? " +
                "GROUP BY aluno " +
                ") AS versoes_recentes";
        
        String sqlApi = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api " +
                "WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes";

        int totalApresentacao = contarRegistros(sqlApresentacao, conn);
        int totalApi = contarRegistros(sqlApi, conn);
        
        return totalApresentacao + totalApi;
    }

    /**
     * Conta sessões que já receberam feedback
     */
    private int contarSessoesRespondidas(Connection conn) throws SQLException {
        int apresentacao = contarSessoesRespondidasApresentacao(conn);
        int api = contarSessoesRespondidasApi(conn);
        return apresentacao + api;
    }

    private int contarSessoesRespondidasApresentacao(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT sa.aluno, MAX(sa.versao) as versao_recente " +
                "FROM secao_apresentacao sa " +
                "WHERE sa.aluno = ? " +
                "GROUP BY sa.aluno " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_apresentacao fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE (fa.status_nome IS NOT NULL OR fa.status_idade IS NOT NULL " +
                "OR fa.status_curso IS NOT NULL OR fa.status_motivacao IS NOT NULL " +
                "OR fa.status_historico IS NOT NULL OR fa.status_github IS NOT NULL " +
                "OR fa.status_linkedin IS NOT NULL OR fa.status_conhecimentos IS NOT NULL)";
        
        return contarRegistros(sql, conn);
    }

    private int contarSessoesRespondidasApi(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api " +
                "WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_api fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.semestre_curso = fa.semestre_curso AND " +
                "  versoes_recentes.ano = fa.ano AND " +
                "  versoes_recentes.semestre_ano = fa.semestre_ano AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE (fa.status_empresa IS NOT NULL OR fa.status_descricao_empresa IS NOT NULL " +
                "OR fa.status_problema IS NOT NULL OR fa.status_solucao IS NOT NULL " +
                "OR fa.status_tecnologias IS NOT NULL OR fa.status_contribuicoes IS NOT NULL " +
                "OR fa.status_hard_skills IS NOT NULL OR fa.status_soft_skills IS NOT NULL)";
        
        return contarRegistros(sql, conn);
    }

    /**
     * Conta sessões totalmente aprovadas
     */
    private int contarSessoesAprovadas(Connection conn) throws SQLException {
        int apresentacao = contarSessoesAprovadasApresentacao(conn);
        int api = contarSessoesAprovadasApi(conn);
        return apresentacao + api;
    }

    private int contarSessoesAprovadasApresentacao(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT sa.aluno, MAX(sa.versao) as versao_recente " +
                "FROM secao_apresentacao sa " +
                "WHERE sa.aluno = ? " +
                "GROUP BY sa.aluno " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_apresentacao fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE fa.status_nome = 'Aprovado' AND fa.status_idade = 'Aprovado' " +
                "AND fa.status_curso = 'Aprovado' AND fa.status_motivacao = 'Aprovado' " +
                "AND fa.status_historico = 'Aprovado' AND fa.status_github = 'Aprovado' " +
                "AND fa.status_linkedin = 'Aprovado' AND fa.status_conhecimentos = 'Aprovado'";
        
        return contarRegistros(sql, conn);
    }

    private int contarSessoesAprovadasApi(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api " +
                "WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_api fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.semestre_curso = fa.semestre_curso AND " +
                "  versoes_recentes.ano = fa.ano AND " +
                "  versoes_recentes.semestre_ano = fa.semestre_ano AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE fa.status_empresa = 'Aprovado' AND fa.status_descricao_empresa = 'Aprovado' " +
                "AND fa.status_problema = 'Aprovado' AND fa.status_solucao = 'Aprovado' " +
                "AND fa.status_tecnologias = 'Aprovado' AND fa.status_contribuicoes = 'Aprovado' " +
                "AND fa.status_hard_skills = 'Aprovado' AND fa.status_soft_skills = 'Aprovado'";
        
        return contarRegistros(sql, conn);
    }

    /**
     * Conta sessões que precisam de revisão
     */
    private int contarSessoesRevisar(Connection conn) throws SQLException {
        int apresentacao = contarSessoesRevisarApresentacao(conn);
        int api = contarSessoesRevisarApi(conn);
        return apresentacao + api;
    }

    private int contarSessoesRevisarApresentacao(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT sa.aluno, MAX(sa.versao) as versao_recente " +
                "FROM secao_apresentacao sa " +
                "WHERE sa.aluno = ? " +
                "GROUP BY sa.aluno " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_apresentacao fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE fa.status_nome = 'Revisar' OR fa.status_idade = 'Revisar' " +
                "OR fa.status_curso = 'Revisar' OR fa.status_motivacao = 'Revisar' " +
                "OR fa.status_historico = 'Revisar' OR fa.status_github = 'Revisar' " +
                "OR fa.status_linkedin = 'Revisar' OR fa.status_conhecimentos = 'Revisar'";
        
        return contarRegistros(sql, conn);
    }

    private int contarSessoesRevisarApi(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api " +
                "WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes " +
                "INNER JOIN secao_api fa ON " +
                "  versoes_recentes.aluno = fa.aluno AND " +
                "  versoes_recentes.semestre_curso = fa.semestre_curso AND " +
                "  versoes_recentes.ano = fa.ano AND " +
                "  versoes_recentes.semestre_ano = fa.semestre_ano AND " +
                "  versoes_recentes.versao_recente = fa.versao " +
                "WHERE fa.status_empresa = 'Revisar' OR fa.status_descricao_empresa = 'Revisar' " +
                "OR fa.status_problema = 'Revisar' OR fa.status_solucao = 'Revisar' " +
                "OR fa.status_tecnologias = 'Revisar' OR fa.status_contribuicoes = 'Revisar' " +
                "OR fa.status_hard_skills = 'Revisar' OR fa.status_soft_skills = 'Revisar'";
        
        return contarRegistros(sql, conn);
    }

    /**
     * Conta registros de uma consulta SQL
     */
    private int contarRegistros(String sql, Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, emailAluno);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Carrega o gráfico de pizza com status das sessões
     */
    private void carregarGraficoStatus(int aprovadas, int revisar, int pendentes) {
        pieChartStatus.getData().clear();
        
        if (aprovadas > 0) {
            PieChart.Data dataAprovadas = new PieChart.Data("Aprovadas", aprovadas);
            pieChartStatus.getData().add(dataAprovadas);
            // Usa listener para aplicar estilo quando o nó for criado
            dataAprovadas.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: #27AE60;");
                }
            });
        }
        
        if (revisar > 0) {
            PieChart.Data dataRevisar = new PieChart.Data("Para Revisar", revisar);
            pieChartStatus.getData().add(dataRevisar);
            // Usa listener para aplicar estilo quando o nó for criado
            dataRevisar.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: #E74C3C;");
                }
            });
        }
        
        if (pendentes > 0) {
            PieChart.Data dataPendentes = new PieChart.Data("Pendentes", pendentes);
            pieChartStatus.getData().add(dataPendentes);
            // Usa listener para aplicar estilo quando o nó for criado
            dataPendentes.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: #F39C12;");
                }
            });
        }
    }

    /**
     * Botão para atualizar estatísticas
     */
    @FXML
    public void atualizarDashboard(ActionEvent event) {
        carregarEstatisticas();
    }
    
    /**
     * Método público para atualizar estatísticas (pode ser chamado sem ActionEvent)
     */
    public void atualizarEstatisticas() {
        carregarEstatisticas();
    }

    /**
     * Botão para ver sessões atuais
     */
    @FXML
    private void verSessoesAtuais(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/sessoes-atuais.fxml",
            controller -> {
                if (controller instanceof SessoesAtuaisAlunoController) {
                    ((SessoesAtuaisAlunoController) controller).recarregarSecoes();
                }
            });
    }
}

