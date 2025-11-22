package com.example.technocode.Controllers.ProfessorTG;

import com.example.technocode.model.ProfessorTG;
import com.example.technocode.model.dao.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Controller para o Dashboard do Professor de TG
 * Exibe informações específicas do professor logado e sua disciplina
 */
public class DashboardProfessorTGController {

    // Labels de informações do professor
    @FXML
    private Label labelNomeProfessor;
    @FXML
    private Label labelDisciplina;
    @FXML
    private Label labelQtdAlunosOrientados;

    // VBoxes para listas dinâmicas
    @FXML
    private VBox vboxOrientadores;
    @FXML
    private VBox vboxAgendamentos;

    // Tabela de alunos
    @FXML
    private TableView<AlunoProgresso> tabelaAlunos;
    @FXML
    private TableColumn<AlunoProgresso, String> colNomeAluno;
    @FXML
    private TableColumn<AlunoProgresso, String> colEmailAluno;
    @FXML
    private TableColumn<AlunoProgresso, String> colOrientador;
    @FXML
    private TableColumn<AlunoProgresso, String> colProgresso;
    @FXML
    private TableColumn<AlunoProgresso, String> colAcao;

    // Campo de busca
    @FXML
    private TextField txtBuscaAluno;

    private String emailProfessorLogado;

    @FXML
    public void initialize() {
        // Obtém o email do professor logado
        emailProfessorLogado = com.example.technocode.Controllers.LoginController.getEmailLogado();
        
        if (emailProfessorLogado == null || emailProfessorLogado.isBlank()) {
            mostrarErro("Erro", "Não foi possível identificar o professor logado.");
            return;
        }

        configurarTabelas();
        carregarDados();
    }

    /**
     * Configura as colunas da tabela de alunos
     */
    private void configurarTabelas() {
        colNomeAluno.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmailAluno.setCellValueFactory(new PropertyValueFactory<>("email"));
        colOrientador.setCellValueFactory(new PropertyValueFactory<>("orientador"));
        colProgresso.setCellValueFactory(new PropertyValueFactory<>("progresso"));
        
        // Coluna de ação com botão agendar
        colAcao.setCellValueFactory(new PropertyValueFactory<>("acao"));
        colAcao.setCellFactory(column -> new TableCell<AlunoProgresso, String>() {
            private final Button btnAgendar = new Button("Agendar");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    AlunoProgresso aluno = getTableView().getItems().get(getIndex());
                    btnAgendar.setDisable(!aluno.isPodeAgendar());
                    btnAgendar.setOnAction(e -> agendarDefesa(aluno));
                    // Aplica estilo CSS específico do dashboard do professor
                    btnAgendar.getStyleClass().add("button");
                    setGraphic(btnAgendar);
                }
            }
        });
        
        // Adiciona evento de clique duplo na linha da tabela para visualizar seções
        tabelaAlunos.setRowFactory(tv -> {
            TableRow<AlunoProgresso> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AlunoProgresso aluno = row.getItem();
                    visualizarSecoesAluno(aluno);
                }
            });
            return row;
        });
    }

    /**
     * Carrega todos os dados do dashboard
     */
    private void carregarDados() {
        carregarInformacoesProfessor();
        carregarQtdAlunosOrientados();
        carregarOrientadores();
        carregarAgendamentosProximos();
        carregarTabelaAlunos();
    }

    /**
     * Carrega informações do professor (nome e disciplina)
     */
    private void carregarInformacoesProfessor() {
        try {
            Map<String, String> dadosProfessor = ProfessorTG.buscarDadosPorEmail(emailProfessorLogado);
            String nome = dadosProfessor.get("nome");
            String disciplina = dadosProfessor.get("disciplina");

            labelNomeProfessor.setText(nome != null ? nome : "N/A");
            
            // Formata a disciplina (TG1 -> TG 1, TG2 -> TG 2)
            if (disciplina != null) {
                String disciplinaFormatada = disciplina.equals("TG1") ? "TG 1" : 
                                           disciplina.equals("TG2") ? "TG 2" : disciplina;
                labelDisciplina.setText(disciplinaFormatada);
            } else {
                labelDisciplina.setText("N/A");
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelNomeProfessor.setText("Erro ao carregar");
            labelDisciplina.setText("Erro ao carregar");
        }
    }

    /**
     * Carrega quantidade de alunos da disciplina sendo orientados
     */
    private void carregarQtdAlunosOrientados() {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT COUNT(DISTINCT a.email) as qtd " +
                        "FROM aluno a " +
                        "WHERE a.professor_tg = ? AND a.orientador IS NOT NULL";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int qtd = rs.getInt("qtd");
                labelQtdAlunosOrientados.setText(String.valueOf(qtd));
            } else {
                labelQtdAlunosOrientados.setText("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelQtdAlunosOrientados.setText("0");
        }
    }

    /**
     * Carrega lista de orientadores com quantidade de alunos
     */
    private void carregarOrientadores() {
        vboxOrientadores.getChildren().clear();
        
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT o.nome, COUNT(a.email) as qtd_alunos " +
                        "FROM orientador o " +
                        "LEFT JOIN aluno a ON o.email = a.orientador AND a.professor_tg = ? " +
                        "GROUP BY o.email, o.nome " +
                        "HAVING COUNT(a.email) > 0 " +
                        "ORDER BY qtd_alunos DESC, o.nome";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String nomeOrientador = rs.getString("nome");
                int qtdAlunos = rs.getInt("qtd_alunos");
                
                HBox hbox = new HBox(10);
                Label labelNome = new Label(nomeOrientador + ":");
                labelNome.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C3E50;");
                labelNome.setFont(Font.font(12));
                
                Label labelQtd = new Label(String.valueOf(qtdAlunos) + " aluno(s)");
                labelQtd.setStyle("-fx-text-fill: #7F8C8D;");
                labelQtd.setFont(Font.font(12));
                
                hbox.getChildren().addAll(labelNome, labelQtd);
                vboxOrientadores.getChildren().add(hbox);
            }
            
            if (vboxOrientadores.getChildren().isEmpty()) {
                Label labelVazio = new Label("Nenhum orientador encontrado");
                labelVazio.setStyle("-fx-text-fill: #7F8C8D;");
                vboxOrientadores.getChildren().add(labelVazio);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label labelErro = new Label("Erro ao carregar orientadores");
            labelErro.setStyle("-fx-text-fill: #E74C3C;");
            vboxOrientadores.getChildren().add(labelErro);
        }
    }

    /**
     * Carrega os 3 agendamentos mais próximos
     */
    private void carregarAgendamentosProximos() {
        vboxAgendamentos.getChildren().clear();
        
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT a.nome as nome_aluno, ad.data_defesa, ad.horario, ad.sala " +
                        "FROM agendamento_defesa_tg ad " +
                        "INNER JOIN aluno a ON ad.email_aluno = a.email " +
                        "WHERE ad.email_professor = ? " +
                        "AND ad.data_defesa >= CURDATE() " +
                        "ORDER BY ad.data_defesa ASC, ad.horario ASC " +
                        "LIMIT 3";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            ResultSet rs = pst.executeQuery();
            
            DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM");
            int count = 0;
            
            while (rs.next() && count < 3) {
                String nomeAluno = rs.getString("nome_aluno");
                LocalDate data = rs.getDate("data_defesa").toLocalDate();
                String horario = rs.getString("horario");
                String sala = rs.getString("sala");
                
                // Formata o horário (remove segundos se houver)
                if (horario != null && horario.length() > 5) {
                    horario = horario.substring(0, 5);
                }
                
                // Card compacto em uma linha horizontal
                HBox cardAgendamento = new HBox(4);
                cardAgendamento.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 6; -fx-padding: 8;");
                cardAgendamento.setAlignment(Pos.CENTER_LEFT);
                
                // Nome do aluno (negrito) - reduzido para dar espaço à sala
                Label labelAluno = new Label(nomeAluno);
                labelAluno.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C3E50;");
                labelAluno.setFont(Font.font(11));
                labelAluno.setPrefWidth(120);
                
                // Separador
                Label separador1 = new Label("•");
                separador1.setStyle("-fx-text-fill: #BDC3C7;");
                separador1.setFont(Font.font(9));
                
                // Data (apenas dia/mês)
                Label labelData = new Label(data.format(formatterData));
                labelData.setStyle("-fx-text-fill: #7F8C8D;");
                labelData.setFont(Font.font(10));
                labelData.setPrefWidth(50);
                
                // Separador
                Label separador2 = new Label("•");
                separador2.setStyle("-fx-text-fill: #BDC3C7;");
                separador2.setFont(Font.font(9));
                
                // Horário
                Label labelHorario = new Label(horario != null ? horario : "-");
                labelHorario.setStyle("-fx-text-fill: #7F8C8D;");
                labelHorario.setFont(Font.font(10));
                labelHorario.setPrefWidth(45);
                
                // Separador
                Label separador3 = new Label("•");
                separador3.setStyle("-fx-text-fill: #BDC3C7;");
                separador3.setFont(Font.font(9));
                
                // Sala
                Label labelSala = new Label(sala != null && !sala.isEmpty() ? sala : "-");
                labelSala.setStyle("-fx-text-fill: #7F8C8D;");
                labelSala.setFont(Font.font(10));
                labelSala.setPrefWidth(60);
                
                cardAgendamento.getChildren().addAll(labelAluno, separador1, labelData, separador2, labelHorario, separador3, labelSala);
                vboxAgendamentos.getChildren().add(cardAgendamento);
                
                count++;
            }
            
            if (vboxAgendamentos.getChildren().isEmpty()) {
                Label labelVazio = new Label("Nenhum agendamento próximo");
                labelVazio.setStyle("-fx-text-fill: #7F8C8D;");
                vboxAgendamentos.getChildren().add(labelVazio);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label labelErro = new Label("Erro ao carregar agendamentos");
            labelErro.setStyle("-fx-text-fill: #E74C3C;");
            vboxAgendamentos.getChildren().add(labelErro);
        }
    }

    /**
     * Carrega a tabela de alunos da disciplina do professor
     * Calcula o progresso apenas com as versões mais recentes de cada seção
     */
    private void carregarTabelaAlunos() {
        try (Connection conn = new Connector().getConnection()) {
            String buscaAluno = txtBuscaAluno.getText().trim().toLowerCase();

            // Query corrigida para considerar apenas versões mais recentes
            String sql = "SELECT a.nome, a.email, o.nome as orientador_nome, " +
                        // Total de seções (apenas versões mais recentes)
                        "(SELECT COUNT(*) FROM ( " +
                        "  SELECT aluno, MAX(versao) as versao_recente " +
                        "  FROM secao_apresentacao WHERE aluno = a.email " +
                        "  GROUP BY aluno " +
                        ") AS versoes_apresentacao) + " +
                        "(SELECT COUNT(*) FROM ( " +
                        "  SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                        "  FROM secao_api WHERE aluno = a.email " +
                        "  GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                        ") AS versoes_api) as total_secoes, " +
                        // Seções aprovadas (apenas versões mais recentes)
                        "(SELECT COUNT(*) FROM ( " +
                        "  SELECT sa.aluno, MAX(sa.versao) as versao_recente " +
                        "  FROM secao_apresentacao sa WHERE sa.aluno = a.email " +
                        "  GROUP BY sa.aluno " +
                        ") AS versoes_recentes " +
                        "INNER JOIN secao_apresentacao fa ON " +
                        "  versoes_recentes.aluno = fa.aluno AND " +
                        "  versoes_recentes.versao_recente = fa.versao " +
                        "WHERE fa.status_nome = 'Aprovado' AND fa.status_idade = 'Aprovado' " +
                        "AND fa.status_curso = 'Aprovado' AND fa.status_motivacao = 'Aprovado' " +
                        "AND fa.status_historico = 'Aprovado' AND fa.status_github = 'Aprovado' " +
                        "AND fa.status_linkedin = 'Aprovado' AND fa.status_conhecimentos = 'Aprovado') + " +
                        "(SELECT COUNT(*) FROM ( " +
                        "  SELECT sa.aluno, sa.semestre_curso, sa.ano, sa.semestre_ano, MAX(sa.versao) as versao_recente " +
                        "  FROM secao_api sa WHERE sa.aluno = a.email " +
                        "  GROUP BY sa.aluno, sa.semestre_curso, sa.ano, sa.semestre_ano " +
                        ") AS versoes_recentes " +
                        "INNER JOIN secao_api fapi ON " +
                        "  versoes_recentes.aluno = fapi.aluno AND " +
                        "  versoes_recentes.semestre_curso = fapi.semestre_curso AND " +
                        "  versoes_recentes.ano = fapi.ano AND " +
                        "  versoes_recentes.semestre_ano = fapi.semestre_ano AND " +
                        "  versoes_recentes.versao_recente = fapi.versao " +
                        "WHERE fapi.status_problema = 'Aprovado' AND fapi.status_solucao = 'Aprovado' " +
                        "AND fapi.status_tecnologias = 'Aprovado' AND fapi.status_contribuicoes = 'Aprovado' " +
                        "AND fapi.status_hard_skills = 'Aprovado' AND fapi.status_soft_skills = 'Aprovado') as aprovadas " +
                        "FROM aluno a " +
                        "LEFT JOIN orientador o ON a.orientador = o.email " +
                        "WHERE a.professor_tg = ? ";

            if (!buscaAluno.isEmpty()) {
                sql += "AND LOWER(a.nome) LIKE ? ";
            }

            sql += "ORDER BY a.nome";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailProfessorLogado);
            
            int paramIndex = 2;
            if (!buscaAluno.isEmpty()) {
                pst.setString(paramIndex++, "%" + buscaAluno + "%");
            }

            ResultSet rs = pst.executeQuery();
            ObservableList<AlunoProgresso> alunos = FXCollections.observableArrayList();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String orientador = rs.getString("orientador_nome") != null ? rs.getString("orientador_nome") : "Sem orientador";
                int totalSecoes = rs.getInt("total_secoes");
                int secoesAprovadas = rs.getInt("aprovadas");
                
                double percentual = totalSecoes > 0 ? (double) secoesAprovadas / totalSecoes * 100 : 0;
                String progresso = String.format("%.1f%%", percentual);
                
                // Verifica se pode agendar (todas as seções enviadas estão aprovadas)
                boolean podeAgendar = verificarPodeAgendar(email, conn);
                
                alunos.add(new AlunoProgresso(nome, email, orientador, progresso, podeAgendar));
            }

            tabelaAlunos.setItems(alunos);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar tabela de alunos", e.getMessage());
        }
    }

    /**
     * Verifica se o aluno pode agendar defesa
     * Retorna true apenas se todas as seções enviadas estiverem aprovadas
     */
    private boolean verificarPodeAgendar(String emailAluno, Connection conn) throws SQLException {
        // Verifica se há seções de apresentação enviadas
        String sqlApresentacao = "SELECT COUNT(*) as total " +
                "FROM secao_apresentacao " +
                "WHERE aluno = ?";
        
        PreparedStatement pstApresentacao = conn.prepareStatement(sqlApresentacao);
        pstApresentacao.setString(1, emailAluno);
        ResultSet rsApresentacao = pstApresentacao.executeQuery();
        
        boolean temApresentacao = false;
        boolean apresentacaoOk = true;
        if (rsApresentacao.next()) {
            int total = rsApresentacao.getInt("total");
            temApresentacao = total > 0;
            
            if (temApresentacao) {
                // Verifica se a última versão está totalmente aprovada
                String sqlUltimaVersao = "SELECT COUNT(*) as total, " +
                        "COUNT(CASE WHEN status_nome = 'Aprovado' AND status_idade = 'Aprovado' " +
                        "AND status_curso = 'Aprovado' AND status_motivacao = 'Aprovado' " +
                        "AND status_historico = 'Aprovado' AND status_github = 'Aprovado' " +
                        "AND status_linkedin = 'Aprovado' AND status_conhecimentos = 'Aprovado' " +
                        "THEN 1 END) as aprovadas " +
                        "FROM secao_apresentacao " +
                        "WHERE aluno = ? AND versao = (SELECT MAX(versao) FROM secao_apresentacao WHERE aluno = ?)";
                
                PreparedStatement pstUltima = conn.prepareStatement(sqlUltimaVersao);
                pstUltima.setString(1, emailAluno);
                pstUltima.setString(2, emailAluno);
                ResultSet rsUltima = pstUltima.executeQuery();
                
                if (rsUltima.next()) {
                    int totalUltima = rsUltima.getInt("total");
                    int aprovadas = rsUltima.getInt("aprovadas");
                    // Todas as seções da última versão devem estar aprovadas
                    apresentacaoOk = (totalUltima > 0 && aprovadas == totalUltima);
                }
            }
        }
        
        // Verifica se há seções de API enviadas
        String sqlApi = "SELECT COUNT(*) as total_secoes " +
                "FROM ( " +
                "SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                "FROM secao_api WHERE aluno = ? " +
                "GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                ") AS versoes_recentes";
        
        PreparedStatement pstApi = conn.prepareStatement(sqlApi);
        pstApi.setString(1, emailAluno);
        ResultSet rsApi = pstApi.executeQuery();
        
        boolean temApi = false;
        boolean apiOk = true;
        if (rsApi.next()) {
            int totalSecoes = rsApi.getInt("total_secoes");
            temApi = totalSecoes > 0;
            
            if (temApi) {
                // Verifica se todas as versões recentes estão totalmente aprovadas
                String sqlApiAprovadas = "SELECT COUNT(*) as total_secoes, " +
                        "COUNT(CASE WHEN sa.status_problema = 'Aprovado' AND sa.status_solucao = 'Aprovado' " +
                        "AND sa.status_tecnologias = 'Aprovado' AND sa.status_contribuicoes = 'Aprovado' " +
                        "AND sa.status_hard_skills = 'Aprovado' AND sa.status_soft_skills = 'Aprovado' " +
                        "THEN 1 END) as aprovadas " +
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
                
                PreparedStatement pstApiAprovadas = conn.prepareStatement(sqlApiAprovadas);
                pstApiAprovadas.setString(1, emailAluno);
                ResultSet rsApiAprovadas = pstApiAprovadas.executeQuery();
                
                if (rsApiAprovadas.next()) {
                    int totalSecoesAprovadas = rsApiAprovadas.getInt("total_secoes");
                    int aprovadas = rsApiAprovadas.getInt("aprovadas");
                    // Todas as seções devem estar aprovadas
                    apiOk = (totalSecoesAprovadas > 0 && aprovadas == totalSecoesAprovadas);
                }
            }
        }
        
        // Só pode agendar se:
        // 1. Tem pelo menos uma seção enviada (apresentação ou API)
        // 2. Todas as seções enviadas estão aprovadas
        boolean temSecoes = temApresentacao || temApi;
        return temSecoes && apresentacaoOk && apiOk;
    }

    /**
     * Navega para a tela de agendamento de defesa e pré-seleciona o aluno
     */
    private void agendarDefesa(AlunoProgresso aluno) {
        // Navega para a tela de agendamento
        try {
            String nomeAluno = aluno.getNome();
            com.example.technocode.Controllers.ProfessorTG.ProfessorTGPrincipalController.getInstance()
                .navegarParaTelaDoCenter("/com/example/technocode/ProfessorTG/AgendamentoDefesaTGView.fxml", 
                    controller -> {
                        // Pré-seleciona o aluno no ComboBox
                        if (controller instanceof com.example.technocode.Controllers.ProfessorTG.AgendamentoDefesaTGController) {
                            ((com.example.technocode.Controllers.ProfessorTG.AgendamentoDefesaTGController) controller)
                                .selecionarAluno(nomeAluno);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível abrir a tela de agendamento.");
        }
    }

    /**
     * Navega para a tela de visualização de seções do aluno
     */
    private void visualizarSecoesAluno(AlunoProgresso aluno) {
        try {
            String emailAluno = aluno.getEmail();
            com.example.technocode.Controllers.ProfessorTG.ProfessorTGPrincipalController.getInstance()
                .navegarParaTelaDoCenter("/com/example/technocode/ProfessorTG/visualizar-secoes-aluno.fxml", 
                    controller -> {
                        if (controller instanceof com.example.technocode.Controllers.ProfessorTG.VisualizarSecoesAlunoController) {
                            VisualizarSecoesAlunoController visualizarController = 
                                (VisualizarSecoesAlunoController) controller;
                            visualizarController.setDadosAluno(emailAluno);
                            visualizarController.setEmailAlunoParaConsulta(emailAluno);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível abrir a visualização de seções.");
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
     * Método público para atualizar o dashboard
     */
    @FXML
    public void atualizarDashboard() {
        carregarDados();
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
        private final String email;
        private final String orientador;
        private final String progresso;
        private final boolean podeAgendar;

        public AlunoProgresso(String nome, String email, String orientador, String progresso, boolean podeAgendar) {
            this.nome = nome;
            this.email = email;
            this.orientador = orientador;
            this.progresso = progresso;
            this.podeAgendar = podeAgendar;
        }

        public String getNome() { return nome; }
        public String getEmail() { return email; }
        public String getOrientador() { return orientador; }
        public String getProgresso() { return progresso; }
        public boolean isPodeAgendar() { return podeAgendar; }
        public String getAcao() { return podeAgendar ? "Agendar" : "Aguardar"; }
    }
}
