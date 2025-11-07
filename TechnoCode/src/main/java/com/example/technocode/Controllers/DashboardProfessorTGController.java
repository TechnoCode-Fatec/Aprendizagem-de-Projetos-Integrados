package com.example.technocode.Controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Controller para o Dashboard do Professor de TG
 * Gerencia visualização de estatísticas gerais do sistema
 */
public class DashboardProfessorTGController {

    // Cards de estatísticas
    @FXML
    private Label lblTotalAlunos;
    @FXML
    private Label lblTotalOrientadores;
    @FXML
    private Label lblTotalSecoes;
    @FXML
    private Label lblSecoesAprovadas;
    @FXML
    private Label lblSecoesReprovadas;
    @FXML
    private Label lblPercentualProgresso;
    @FXML
    private Label lblSecoesPendentes;

    // Gráficos
    @FXML
    private PieChart pieChartSecoes;
    @FXML
    private BarChart<String, Number> barChartProgresso;

    // Tabelas
    @FXML
    private TableView<AlunoProgresso> tabelaAlunos;
    @FXML
    private TableColumn<AlunoProgresso, String> colNomeAluno;
    @FXML
    private TableColumn<AlunoProgresso, String> colOrientador;
    @FXML
    private TableColumn<AlunoProgresso, Integer> colTotalSecoes;
    @FXML
    private TableColumn<AlunoProgresso, Integer> colSecoesAprovadas;
    @FXML
    private TableColumn<AlunoProgresso, String> colProgresso;

    @FXML
    private TableView<OrientadorInfo> tabelaOrientadores;
    @FXML
    private TableColumn<OrientadorInfo, String> colNomeOrientador;
    @FXML
    private TableColumn<OrientadorInfo, String> colEmailOrientador;
    @FXML
    private TableColumn<OrientadorInfo, Integer> colQtdAlunos;

    // Campos de busca e filtro
    @FXML
    private TextField txtBuscaAluno;
    @FXML
    private ComboBox<String> comboFiltroOrientador;

    @FXML
    private Button btnAtualizar;

    @FXML
    public void initialize() {
        configurarTabelas();
        carregarFiltros();
        atualizarDashboard();
    }

    /**
     * Configura as colunas das tabelas
     */
    private void configurarTabelas() {
        // Tabela de Alunos
        colNomeAluno.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colOrientador.setCellValueFactory(new PropertyValueFactory<>("orientador"));
        colTotalSecoes.setCellValueFactory(new PropertyValueFactory<>("totalSecoes"));
        colSecoesAprovadas.setCellValueFactory(new PropertyValueFactory<>("secoesAprovadas"));
        colProgresso.setCellValueFactory(new PropertyValueFactory<>("progresso"));

        // Tabela de Orientadores
        colNomeOrientador.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmailOrientador.setCellValueFactory(new PropertyValueFactory<>("email"));
        colQtdAlunos.setCellValueFactory(new PropertyValueFactory<>("qtdAlunos"));
    }

    /**
     * Carrega os filtros disponíveis
     */
    private void carregarFiltros() {
        try (Connection conn = new Connector().getConnection()) {
            ObservableList<String> orientadores = FXCollections.observableArrayList();
            orientadores.add("Todos");
            
            String sql = "SELECT DISTINCT o.nome FROM orientador o " +
                        "INNER JOIN aluno a ON o.email = a.orientador " +
                        "ORDER BY o.nome";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                orientadores.add(rs.getString("nome"));
            }
            
            comboFiltroOrientador.setItems(orientadores);
            comboFiltroOrientador.setValue("Todos");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza todos os dados do dashboard
     */
    @FXML
    private void atualizarDashboard() {
        carregarDadosGerais();
        carregarGraficos();
        carregarTabelaAlunos();
        carregarTabelaOrientadores();
    }

    /**
     * Carrega os dados gerais (cards de estatísticas)
     */
    private void carregarDadosGerais() {
        try (Connection conn = new Connector().getConnection()) {
            // Total de alunos
            int totalAlunos = contarRegistros("SELECT COUNT(*) FROM aluno", conn);
            lblTotalAlunos.setText(String.valueOf(totalAlunos));

            // Total de orientadores
            int totalOrientadores = contarRegistros("SELECT COUNT(*) FROM orientador", conn);
            lblTotalOrientadores.setText(String.valueOf(totalOrientadores));

            // Total de seções (API + Apresentação)
            int totalSecoesApi = contarRegistros("SELECT COUNT(*) FROM secao_api", conn);
            int totalSecoesApresentacao = contarRegistros("SELECT COUNT(*) FROM secao_apresentacao", conn);
            int totalSecoes = totalSecoesApi + totalSecoesApresentacao;
            lblTotalSecoes.setText(String.valueOf(totalSecoes));

            // Seções aprovadas (baseado em feedbacks com status 'aprovado')
            int secoesAprovadasApi = contarSecoesAprovadas("api", conn);
            int secoesAprovadasApresentacao = contarSecoesAprovadas("apresentacao", conn);
            int totalAprovadas = secoesAprovadasApi + secoesAprovadasApresentacao;
            lblSecoesAprovadas.setText(String.valueOf(totalAprovadas));

            // Seções reprovadas
            int secoesReprovadasApi = contarSecoesReprovadas("api", conn);
            int secoesReprovadasApresentacao = contarSecoesReprovadas("apresentacao", conn);
            int totalReprovadas = secoesReprovadasApi + secoesReprovadasApresentacao;
            lblSecoesReprovadas.setText(String.valueOf(totalReprovadas));

            // Seções pendentes (sem feedback ou com status 'pendente')
            int secoesPendentes = totalSecoes - totalAprovadas - totalReprovadas;
            lblSecoesPendentes.setText(String.valueOf(secoesPendentes));

            // Percentual de progresso geral
            double percentual = totalSecoes > 0 ? (double) totalAprovadas / totalSecoes * 100 : 0;
            lblPercentualProgresso.setText(String.format("%.1f%%", percentual));

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar dados gerais", e.getMessage());
        }
    }

    /**
     * Conta registros de uma consulta SQL
     */
    private int contarRegistros(String sql, Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Conta seções aprovadas baseado nos status dos feedbacks
     * Uma seção é considerada aprovada quando TODOS os campos estão com status "Aprovado"
     */
    private int contarSecoesAprovadas(String tipo, Connection conn) throws SQLException {
        String sql;
        if ("api".equals(tipo)) {
            sql = "SELECT COUNT(DISTINCT CONCAT(aluno, '-', semestre_curso, '-', ano, '-', semestre_ano, '-', versao)) " +
                  "FROM feedback_api " +
                  "WHERE status_problema = 'Aprovado' AND status_solucao = 'Aprovado' " +
                  "AND status_tecnologias = 'Aprovado' AND status_contribuicoes = 'Aprovado' " +
                  "AND status_hard_skills = 'Aprovado' AND status_soft_skills = 'Aprovado'";
        } else {
            sql = "SELECT COUNT(DISTINCT CONCAT(aluno, '-', versao)) " +
                  "FROM feedback_apresentacao " +
                  "WHERE status_nome = 'Aprovado' AND status_idade = 'Aprovado' " +
                  "AND status_curso = 'Aprovado' AND status_motivacao = 'Aprovado' " +
                  "AND status_historico = 'Aprovado' AND status_github = 'Aprovado' " +
                  "AND status_linkedin = 'Aprovado' AND status_conhecimentos = 'Aprovado'";
        }
        return contarRegistros(sql, conn);
    }

    /**
     * Conta seções reprovadas baseado nos status dos feedbacks
     * Uma seção é considerada reprovada quando QUALQUER campo está com status "Revisar"
     */
    private int contarSecoesReprovadas(String tipo, Connection conn) throws SQLException {
        String sql;
        if ("api".equals(tipo)) {
            sql = "SELECT COUNT(DISTINCT CONCAT(aluno, '-', semestre_curso, '-', ano, '-', semestre_ano, '-', versao)) " +
                  "FROM feedback_api " +
                  "WHERE status_problema = 'Revisar' OR status_solucao = 'Revisar' " +
                  "OR status_tecnologias = 'Revisar' OR status_contribuicoes = 'Revisar' " +
                  "OR status_hard_skills = 'Revisar' OR status_soft_skills = 'Revisar'";
        } else {
            sql = "SELECT COUNT(DISTINCT CONCAT(aluno, '-', versao)) " +
                  "FROM feedback_apresentacao " +
                  "WHERE status_nome = 'Revisar' OR status_idade = 'Revisar' " +
                  "OR status_curso = 'Revisar' OR status_motivacao = 'Revisar' " +
                  "OR status_historico = 'Revisar' OR status_github = 'Revisar' " +
                  "OR status_linkedin = 'Revisar' OR status_conhecimentos = 'Revisar'";
        }
        return contarRegistros(sql, conn);
    }

    /**
     * Carrega os gráficos (PieChart e BarChart)
     */
    private void carregarGraficos() {
        carregarPieChartSecoes();
        carregarBarChartProgresso();
    }

    /**
     * Carrega o gráfico de pizza com distribuição de seções
     */
    private void carregarPieChartSecoes() {
        try (Connection conn = new Connector().getConnection()) {

            int aprovadas = Integer.parseInt(lblSecoesAprovadas.getText());
            int reprovadas = Integer.parseInt(lblSecoesReprovadas.getText());
            int pendentes = Integer.parseInt(lblSecoesPendentes.getText());

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Aprovadas", aprovadas),
                    new PieChart.Data("Reprovadas", reprovadas),
                    new PieChart.Data("Pendentes", pendentes)
            );

            pieChartSecoes.setData(pieChartData);
            pieChartSecoes.setTitle("Distribuição de Seções");

            Platform.runLater(() -> {

                // 1) Aplica a cor nas FATIAS (continua igual)
                for (PieChart.Data fatia : pieChartData) {
                    String cor = switch (fatia.getName()) {
                        case "Aprovadas" -> "#4CAF50"; // Verde
                        case "Reprovadas" -> "#F44336"; // Vermelho
                        case "Pendentes" -> "#FFD600";  // Amarelo
                        default -> "#000000";
                    };
                    fatia.getNode().setStyle("-fx-pie-color: " + cor + ";");

                    Tooltip tooltip = new Tooltip(fatia.getName() + ": " + (int) fatia.getPieValue());
                    Tooltip.install(fatia.getNode(), tooltip);
                }

                // 2) Aplica cor no SÍMBOLO da legenda (sem CSS)
                Set<Node> legendItems = pieChartSecoes.lookupAll(".chart-legend-item");
                for (Node legendItem : legendItems) {

                    Label label = (Label) legendItem.lookup(".label");
                    Region symbol = (Region) legendItem.lookup(".chart-legend-item-symbol");

                    if (label == null || symbol == null) continue;

                    // Texto sempre preto
                    label.setTextFill(Color.BLACK);

                    // Descobre a cor correta baseada no texto
                    Color cor = switch (label.getText()) {
                        case "Aprovadas" -> Color.web("#4CAF50");
                        case "Reprovadas" -> Color.web("#F44336");
                        case "Pendentes" -> Color.web("#FFD600");
                        default -> Color.BLACK;
                    };

                    // Define a cor no símbolo SEM CSS
                    symbol.setBackground(new Background(
                            new BackgroundFill(cor, CornerRadii.EMPTY, Insets.EMPTY)
                    ));

                    // Opcional: borda no símbolo
                    symbol.setBorder(new Border(new BorderStroke(
                            Color.GRAY,
                            BorderStrokeStyle.SOLID,
                            CornerRadii.EMPTY,
                            BorderWidths.DEFAULT
                    )));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /**
     * Carrega o gráfico de barras com progresso dos alunos
     */
    private void carregarBarChartProgresso() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT a.nome, " +
                    "COUNT(DISTINCT CONCAT(sa.aluno, '-', sa.versao)) + " +
                    "COUNT(DISTINCT CONCAT(sapi.aluno, '-', sapi.semestre_curso, '-', sapi.ano, '-', sapi.semestre_ano, '-', sapi.versao)) as total_secoes, " +
                    "COUNT(DISTINCT CASE WHEN fa.status_nome = 'Aprovado' AND fa.status_idade = 'Aprovado' AND fa.status_curso = 'Aprovado' " +
                    "AND fa.status_motivacao = 'Aprovado' AND fa.status_historico = 'Aprovado' " +
                    "AND fa.status_github = 'Aprovado' AND fa.status_linkedin = 'Aprovado' " +
                    "AND fa.status_conhecimentos = 'Aprovado' THEN CONCAT(fa.aluno, '-', fa.versao) END) + " +
                    "COUNT(DISTINCT CASE WHEN fapi.status_problema = 'Aprovado' AND fapi.status_solucao = 'Aprovado' " +
                    "AND fapi.status_tecnologias = 'Aprovado' AND fapi.status_contribuicoes = 'Aprovado' " +
                    "AND fapi.status_hard_skills = 'Aprovado' AND fapi.status_soft_skills = 'Aprovado' " +
                    "THEN CONCAT(fapi.aluno, '-', fapi.semestre_curso, '-', fapi.ano, '-', fapi.semestre_ano, '-', fapi.versao) END) as aprovadas " +
                    "FROM aluno a " +
                    "LEFT JOIN secao_apresentacao sa ON a.email = sa.aluno " +
                    "LEFT JOIN secao_api sapi ON a.email = sapi.aluno " +
                    "LEFT JOIN feedback_apresentacao fa ON a.email = fa.aluno AND sa.versao = fa.versao " +
                    "LEFT JOIN feedback_api fapi ON a.email = fapi.aluno " +
                    "GROUP BY a.email, a.nome " +
                    "HAVING total_secoes > 0 " +
                    "ORDER BY aprovadas DESC " +
                    "LIMIT 10";

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Seções Aprovadas");

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                int aprovadas = rs.getInt("aprovadas");
                series.getData().add(new XYChart.Data<>(
                        nome.length() > 15 ? nome.substring(0, 15) + "..." : nome,
                        aprovadas
                ));
            }

            barChartProgresso.getData().clear();
            barChartProgresso.getData().add(series);
            barChartProgresso.setTitle("Top 10 Alunos - Seções Aprovadas");

            // 🔥 Aplica cor às barras e à legenda
            Platform.runLater(() -> {
                // Cor das barras
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Node barra = data.getNode();
                    if (barra != null) {
                        barra.setStyle("-fx-bar-fill: #4CAF50;"); // Verde
                    }
                }

                // Cor do quadrado da legenda
                for (Node legend : barChartProgresso.lookupAll(".chart-legend-item")) {
                    if (legend instanceof Label l) {
                        if (l.getText().equals(series.getName())) {
                            Node symbol = l.lookup(".chart-legend-item-symbol");
                            if (symbol != null) {
                                symbol.setStyle("-fx-background-color: white, #4CAF50;"); // Verde igual às barras
                            }
                        }
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega a tabela de alunos com progresso
     */
    private void carregarTabelaAlunos() {
        try (Connection conn = new Connector().getConnection()) {
            String filtroOrientador = comboFiltroOrientador.getValue();
            String buscaAluno = txtBuscaAluno.getText().trim().toLowerCase();

            String sql = "SELECT a.nome, a.email, o.nome as orientador_nome, " +
                        "COUNT(DISTINCT CONCAT(sa.aluno, '-', sa.versao)) + " +
                        "COUNT(DISTINCT CONCAT(sapi.aluno, '-', sapi.semestre_curso, '-', sapi.ano, '-', sapi.semestre_ano, '-', sapi.versao)) as total_secoes, " +
                        "COUNT(DISTINCT CASE WHEN fa.status_nome = 'Aprovado' AND fa.status_idade = 'Aprovado' AND fa.status_curso = 'Aprovado' " +
                        "AND fa.status_motivacao = 'Aprovado' AND fa.status_historico = 'Aprovado' " +
                        "AND fa.status_github = 'Aprovado' AND fa.status_linkedin = 'Aprovado' " +
                        "AND fa.status_conhecimentos = 'Aprovado' THEN CONCAT(fa.aluno, '-', fa.versao) END) + " +
                        "COUNT(DISTINCT CASE WHEN fapi.status_problema = 'Aprovado' AND fapi.status_solucao = 'Aprovado' " +
                        "AND fapi.status_tecnologias = 'Aprovado' AND fapi.status_contribuicoes = 'Aprovado' " +
                        "AND fapi.status_hard_skills = 'Aprovado' AND fapi.status_soft_skills = 'Aprovado' " +
                        "THEN CONCAT(fapi.aluno, '-', fapi.semestre_curso, '-', fapi.ano, '-', fapi.semestre_ano, '-', fapi.versao) END) as aprovadas " +
                        "FROM aluno a " +
                        "LEFT JOIN orientador o ON a.orientador = o.email " +
                        "LEFT JOIN secao_apresentacao sa ON a.email = sa.aluno " +
                        "LEFT JOIN secao_api sapi ON a.email = sapi.aluno " +
                        "LEFT JOIN feedback_apresentacao fa ON a.email = fa.aluno AND sa.versao = fa.versao " +
                        "LEFT JOIN feedback_api fapi ON a.email = fapi.aluno " +
                        "WHERE 1=1 ";

            if (filtroOrientador != null && !filtroOrientador.equals("Todos")) {
                sql += "AND o.nome = ? ";
            }
            
            sql += "GROUP BY a.email, a.nome, o.nome " +
                   "ORDER BY a.nome";

            PreparedStatement pst = conn.prepareStatement(sql);
            
            int paramIndex = 1;
            if (filtroOrientador != null && !filtroOrientador.equals("Todos")) {
                pst.setString(paramIndex++, filtroOrientador);
            }

            ResultSet rs = pst.executeQuery();
            ObservableList<AlunoProgresso> alunos = FXCollections.observableArrayList();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String orientador = rs.getString("orientador_nome") != null ? rs.getString("orientador_nome") : "Sem orientador";
                int totalSecoes = rs.getInt("total_secoes");
                int secoesAprovadas = rs.getInt("aprovadas");
                
                // Aplica filtro de busca
                if (buscaAluno.isEmpty() || nome.toLowerCase().contains(buscaAluno)) {
                    double percentual = totalSecoes > 0 ? (double) secoesAprovadas / totalSecoes * 100 : 0;
                    String progresso = String.format("%.1f%%", percentual);
                    
                    alunos.add(new AlunoProgresso(nome, orientador, totalSecoes, secoesAprovadas, progresso));
                }
            }

            tabelaAlunos.setItems(alunos);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar tabela de alunos", e.getMessage());
        }
    }

    /**
     * Carrega a tabela de orientadores
     */
    private void carregarTabelaOrientadores() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT o.nome, o.email, COUNT(a.email) as qtd_alunos " +
                        "FROM orientador o " +
                        "LEFT JOIN aluno a ON o.email = a.orientador " +
                        "GROUP BY o.email, o.nome " +
                        "ORDER BY qtd_alunos DESC, o.nome";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            ObservableList<OrientadorInfo> orientadores = FXCollections.observableArrayList();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                int qtdAlunos = rs.getInt("qtd_alunos");
                
                orientadores.add(new OrientadorInfo(nome, email, qtdAlunos));
            }

            tabelaOrientadores.setItems(orientadores);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar tabela de orientadores", e.getMessage());
        }
    }

    /**
     * Filtra a tabela de alunos ao digitar
     */
    @FXML
    private void filtrarAlunos() {
        carregarTabelaAlunos();
    }

    /**
     * Filtra por orientador
     */
    @FXML
    private void filtrarPorOrientador() {
        carregarTabelaAlunos();
    }

    /**
     * Volta para a tela de login
     */
    @FXML
    private void voltarLogin(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/login.fxml", null);
    }

    /**
     * Mostra mensagem de erro
     */
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Classe auxiliar para representar progresso de aluno na tabela
     */
    public static class AlunoProgresso {
        private final String nome;
        private final String orientador;
        private final Integer totalSecoes;
        private final Integer secoesAprovadas;
        private final String progresso;

        public AlunoProgresso(String nome, String orientador, Integer totalSecoes, Integer secoesAprovadas, String progresso) {
            this.nome = nome;
            this.orientador = orientador;
            this.totalSecoes = totalSecoes;
            this.secoesAprovadas = secoesAprovadas;
            this.progresso = progresso;
        }

        public String getNome() { return nome; }
        public String getOrientador() { return orientador; }
        public Integer getTotalSecoes() { return totalSecoes; }
        public Integer getSecoesAprovadas() { return secoesAprovadas; }
        public String getProgresso() { return progresso; }
    }

    /**
     * Classe auxiliar para representar informações do orientador na tabela
     */
    public static class OrientadorInfo {
        private final String nome;
        private final String email;
        private final Integer qtdAlunos;

        public OrientadorInfo(String nome, String email, Integer qtdAlunos) {
            this.nome = nome;
            this.email = email;
            this.qtdAlunos = qtdAlunos;
        }

        public String getNome() { return nome; }
        public String getEmail() { return email; }
        public Integer getQtdAlunos() { return qtdAlunos; }
    }
}

