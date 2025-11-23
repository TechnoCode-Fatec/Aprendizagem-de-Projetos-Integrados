package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.Orientador;
import com.example.technocode.model.SolicitacaoOrientacao;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardOrientadorController {

    @FXML
    private Label labelNomeOrientador;
    @FXML
    private Label labelAlunosOrientados;
    @FXML
    private Label labelAlunosOrientadosCard;
    @FXML
    private Label labelAlunosCompletos;
    @FXML
    private Label labelSecoesPendentes;
    @FXML
    private Label labelSolicitacoesPendentes;
    @FXML
    private VBox vboxSolicitacoesPendentes;
    @FXML
    private VBox cardSecoesPendentes;
    @FXML
    private VBox vboxTabelaSecoesPendentes;
    @FXML
    private TableView<Map<String, String>> tabelaSecoesPendentes;
    @FXML
    private TableColumn<Map<String, String>, String> colNomeAluno;
    @FXML
    private TableColumn<Map<String, String>, String> colEmailAluno;
    @FXML
    private TableColumn<Map<String, String>, String> colTipoSecao;
    @FXML
    private TableColumn<Map<String, String>, String> colIdentificador;
    @FXML
    private TableColumn<Map<String, String>, String> colAcao;

    private String emailOrientador;
    private boolean tabelaSecoesPendentesVisivel = false;

    @FXML
    public void initialize() {
        emailOrientador = LoginController.getEmailLogado();
        carregarInformacoesOrientador();
        carregarEstatisticas();
        carregarSolicitacoesPendentes();
        configurarTabelaSecoesPendentes();
    }

    private void carregarInformacoesOrientador() {
        try {
            Map<String, String> dadosOrientador = Orientador.buscarDadosPorEmail(emailOrientador);
            String nome = dadosOrientador.get("nome");
            if (nome != null && !nome.isBlank()) {
                labelNomeOrientador.setText(nome);
            } else {
                labelNomeOrientador.setText("N/A");
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelNomeOrientador.setText("Erro ao carregar");
        }
    }

    private void carregarEstatisticas() {
        try (Connection conn = new Connector().getConnection()) {
            // Quantidade de alunos orientados
            int totalAlunos = contarAlunosOrientados(conn);
            labelAlunosOrientados.setText(String.valueOf(totalAlunos));
            labelAlunosOrientadosCard.setText(String.valueOf(totalAlunos));

            // Alunos com seções completas (tanto apresentação quanto pelo menos uma API aprovadas)
            int alunosCompletos = contarAlunosCompletos(conn);
            labelAlunosCompletos.setText(String.valueOf(alunosCompletos));

            // Seções que faltam responder (seções enviadas sem feedback)
            int secoesPendentes = contarSecoesPendentes(conn);
            labelSecoesPendentes.setText(String.valueOf(secoesPendentes));

            // Solicitações pendentes
            int solicitacoesPendentes = contarSolicitacoesPendentes(conn);
            labelSolicitacoesPendentes.setText(String.valueOf(solicitacoesPendentes));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int contarAlunosOrientados(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM aluno WHERE orientador = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailOrientador);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt("total") : 0;
    }

    private int contarAlunosCompletos(Connection conn) throws SQLException {
        // Alunos que têm apresentação aprovada E pelo menos uma API aprovada
        String sql = "SELECT COUNT(DISTINCT a.email) as total " +
                    "FROM aluno a " +
                    "WHERE a.orientador = ? " +
                    "AND EXISTS ( " +
                    "  SELECT 1 FROM ( " +
                    "    SELECT aluno, MAX(versao) as versao_recente " +
                    "    FROM secao_apresentacao WHERE aluno = a.email " +
                    "    GROUP BY aluno " +
                    "  ) AS v_ap " +
                    "  INNER JOIN secao_apresentacao sa ON " +
                    "    v_ap.aluno = sa.aluno AND v_ap.versao_recente = sa.versao " +
                    "  WHERE sa.status_nome = 'Aprovado' AND sa.status_idade = 'Aprovado' " +
                    "  AND sa.status_curso = 'Aprovado' AND sa.status_motivacao = 'Aprovado' " +
                    "  AND sa.status_historico = 'Aprovado' AND sa.status_historico_profissional = 'Aprovado' " +
                    "  AND sa.status_github = 'Aprovado' AND sa.status_linkedin = 'Aprovado' " +
                    "  AND sa.status_conhecimentos = 'Aprovado' " +
                    ") " +
                    "AND EXISTS ( " +
                    "  SELECT 1 FROM ( " +
                    "    SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao_recente " +
                    "    FROM secao_api WHERE aluno = a.email " +
                    "    GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                    "  ) AS v_api " +
                    "  INNER JOIN secao_api sapi ON " +
                    "    v_api.aluno = sapi.aluno AND " +
                    "    v_api.semestre_curso = sapi.semestre_curso AND " +
                    "    v_api.ano = sapi.ano AND " +
                    "    v_api.semestre_ano = sapi.semestre_ano AND " +
                    "    v_api.versao_recente = sapi.versao " +
                    "  WHERE sapi.status_empresa = 'Aprovado' AND sapi.status_descricao_empresa = 'Aprovado' " +
                    "  AND sapi.status_repositorio = 'Aprovado' AND sapi.status_problema = 'Aprovado' " +
                    "  AND sapi.status_solucao = 'Aprovado' AND sapi.status_tecnologias = 'Aprovado' " +
                    "  AND sapi.status_contribuicoes = 'Aprovado' AND sapi.status_hard_skills = 'Aprovado' " +
                    "  AND sapi.status_soft_skills = 'Aprovado' " +
                    ")";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailOrientador);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt("total") : 0;
    }

    private int contarSecoesPendentes(Connection conn) throws SQLException {
        // Conta seções que foram enviadas mas não têm feedback (nenhum status preenchido)
        // Considera apenas versões mais recentes
        int pendentesApresentacao = 0;
        int pendentesApi = 0;
        
        // Conta apresentações pendentes
        String sqlApresentacao = "SELECT COUNT(*) as total FROM ( " +
                    "  SELECT sa.aluno, sa.versao FROM ( " +
                    "    SELECT aluno, MAX(versao) as versao " +
                    "    FROM secao_apresentacao " +
                    "    WHERE aluno IN (SELECT email FROM aluno WHERE orientador = ?) " +
                    "    GROUP BY aluno " +
                    "  ) AS versoes_recentes " +
                    "  INNER JOIN secao_apresentacao sa ON " +
                    "    versoes_recentes.aluno = sa.aluno AND versoes_recentes.versao = sa.versao " +
                    "  WHERE (sa.status_nome IS NULL AND sa.status_idade IS NULL AND sa.status_curso IS NULL " +
                    "    AND sa.status_motivacao IS NULL AND sa.status_historico IS NULL " +
                    "    AND sa.status_historico_profissional IS NULL AND sa.status_github IS NULL " +
                    "    AND sa.status_linkedin IS NULL AND sa.status_conhecimentos IS NULL) " +
                    ") AS secoes_pendentes";
        PreparedStatement pst1 = conn.prepareStatement(sqlApresentacao);
        pst1.setString(1, emailOrientador);
        ResultSet rs1 = pst1.executeQuery();
        if (rs1.next()) {
            pendentesApresentacao = rs1.getInt("total");
        }
        
        // Conta APIs pendentes
        String sqlApi = "SELECT COUNT(*) as total FROM ( " +
                    "  SELECT sa2.aluno FROM ( " +
                    "    SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao " +
                    "    FROM secao_api " +
                    "    WHERE aluno IN (SELECT email FROM aluno WHERE orientador = ?) " +
                    "    GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                    "  ) AS vr " +
                    "  INNER JOIN secao_api sa2 ON " +
                    "    vr.aluno = sa2.aluno AND vr.semestre_curso = sa2.semestre_curso " +
                    "    AND vr.ano = sa2.ano AND vr.semestre_ano = sa2.semestre_ano AND vr.versao = sa2.versao " +
                    "  WHERE (sa2.status_empresa IS NULL AND sa2.status_descricao_empresa IS NULL " +
                    "    AND sa2.status_repositorio IS NULL AND sa2.status_problema IS NULL " +
                    "    AND sa2.status_solucao IS NULL AND sa2.status_tecnologias IS NULL " +
                    "    AND sa2.status_contribuicoes IS NULL AND sa2.status_hard_skills IS NULL " +
                    "    AND sa2.status_soft_skills IS NULL) " +
                    ") AS secoes_pendentes";
        PreparedStatement pst2 = conn.prepareStatement(sqlApi);
        pst2.setString(1, emailOrientador);
        ResultSet rs2 = pst2.executeQuery();
        if (rs2.next()) {
            pendentesApi = rs2.getInt("total");
        }
        
        return pendentesApresentacao + pendentesApi;
    }

    private int contarSolicitacoesPendentes(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM solicitacao_orientacao " +
                    "WHERE orientador = ? AND status = 'Pendente'";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, emailOrientador);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt("total") : 0;
    }

    @FXML
    private void navegarParaAlunosOrientados(ActionEvent event) {
        if (OrientadorPrincipalController.getInstance() != null) {
            OrientadorPrincipalController.getInstance().navegarParaTela(
                "/com/example/technocode/Orientador/alunos-orientados.fxml",
                controller -> {
                    if (controller instanceof AlunosOrientadosController) {
                        ((AlunosOrientadosController) controller).recarregarTabela();
                    }
                }
            );
        }
    }

    @FXML
    private void navegarParaSolicitacoes(ActionEvent event) {
        navegarParaSolicitacoes();
    }
    
    private void navegarParaSolicitacoes() {
        if (OrientadorPrincipalController.getInstance() != null) {
            OrientadorPrincipalController.getInstance().navegarParaTela(
                "/com/example/technocode/Orientador/solicitacoes-orientacao.fxml",
                null
            );
        }
    }

    @FXML
    private void clicarCardSecoesPendentes(MouseEvent event) {
        // Alterna a visibilidade da tabela
        tabelaSecoesPendentesVisivel = !tabelaSecoesPendentesVisivel;
        
        if (tabelaSecoesPendentesVisivel) {
            vboxTabelaSecoesPendentes.setVisible(true);
            vboxTabelaSecoesPendentes.setManaged(true);
            carregarTabelaSecoesPendentes();
        } else {
            vboxTabelaSecoesPendentes.setVisible(false);
            vboxTabelaSecoesPendentes.setManaged(false);
        }
    }
    
    private void configurarTabelaSecoesPendentes() {
        // Configura colunas da tabela
        colNomeAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nome_aluno")));
        colNomeAluno.setCellFactory(col -> criarCellCentralizado());
        
        colEmailAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("email_aluno")));
        colEmailAluno.setCellFactory(col -> criarCellCentralizado());
        
        colTipoSecao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("tipo_secao")));
        colTipoSecao.setCellFactory(col -> criarCellCentralizado());
        
        colIdentificador.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("identificador")));
        colIdentificador.setCellFactory(col -> criarCellCentralizado());
        
        // Coluna de ação com botão para abrir a seção
        colAcao.setCellValueFactory(data -> new SimpleStringProperty(""));
        colAcao.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    Map<String, String> secao = getTableView().getItems().get(getIndex());
                    Button btnAbrir = new Button("Abrir");
                    btnAbrir.setStyle("-fx-background-color: #3498DB; -fx-background-radius: 6; -fx-cursor: hand; -fx-text-fill: white; -fx-padding: 5 15; -fx-font-size: 12px;");
                    btnAbrir.setOnAction(e -> abrirSecao(secao));
                    setGraphic(btnAbrir);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    
    private TableCell<Map<String, String>, String> criarCellCentralizado() {
        TableCell<Map<String, String>, String> cell = new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
                setAlignment(Pos.CENTER);
            }
        };
        return cell;
    }
    
    private void carregarTabelaSecoesPendentes() {
        try (Connection conn = new Connector().getConnection()) {
            ObservableList<Map<String, String>> secoes = FXCollections.observableArrayList();
            
            // Busca apresentações pendentes
            String sqlApresentacao = "SELECT a.nome as nome_aluno, a.email as email_aluno, " +
                    "'Apresentação' as tipo_secao, " +
                    "CONCAT('Versão ', sa.versao) as identificador, " +
                    "sa.aluno, sa.versao " +
                    "FROM ( " +
                    "  SELECT aluno, MAX(versao) as versao " +
                    "  FROM secao_apresentacao " +
                    "  WHERE aluno IN (SELECT email FROM aluno WHERE orientador = ?) " +
                    "  GROUP BY aluno " +
                    ") AS versoes_recentes " +
                    "INNER JOIN secao_apresentacao sa ON " +
                    "  versoes_recentes.aluno = sa.aluno AND versoes_recentes.versao = sa.versao " +
                    "INNER JOIN aluno a ON sa.aluno = a.email " +
                    "WHERE (sa.status_nome IS NULL AND sa.status_idade IS NULL AND sa.status_curso IS NULL " +
                    "  AND sa.status_motivacao IS NULL AND sa.status_historico IS NULL " +
                    "  AND sa.status_historico_profissional IS NULL AND sa.status_github IS NULL " +
                    "  AND sa.status_linkedin IS NULL AND sa.status_conhecimentos IS NULL)";
            
            PreparedStatement pst1 = conn.prepareStatement(sqlApresentacao);
            pst1.setString(1, emailOrientador);
            ResultSet rs1 = pst1.executeQuery();
            
            while (rs1.next()) {
                Map<String, String> secao = new java.util.HashMap<>();
                secao.put("nome_aluno", rs1.getString("nome_aluno"));
                secao.put("email_aluno", rs1.getString("email_aluno"));
                secao.put("tipo_secao", rs1.getString("tipo_secao"));
                secao.put("identificador", rs1.getString("identificador"));
                secao.put("tipo", "apresentacao");
                secao.put("aluno", rs1.getString("aluno"));
                secao.put("versao", String.valueOf(rs1.getInt("versao")));
                secoes.add(secao);
            }
            
            // Busca seções API pendentes
            String sqlApi = "SELECT a.nome as nome_aluno, a.email as email_aluno, " +
                    "'API' as tipo_secao, " +
                    "CONCAT(sa.semestre_curso, ' (', sa.ano, '-', sa.semestre_ano, ') - Versão ', sa.versao) as identificador, " +
                    "sa.aluno, sa.semestre_curso, sa.ano, sa.semestre_ano, sa.versao " +
                    "FROM ( " +
                    "  SELECT aluno, semestre_curso, ano, semestre_ano, MAX(versao) as versao " +
                    "  FROM secao_api " +
                    "  WHERE aluno IN (SELECT email FROM aluno WHERE orientador = ?) " +
                    "  GROUP BY aluno, semestre_curso, ano, semestre_ano " +
                    ") AS vr " +
                    "INNER JOIN secao_api sa ON " +
                    "  vr.aluno = sa.aluno AND vr.semestre_curso = sa.semestre_curso " +
                    "  AND vr.ano = sa.ano AND vr.semestre_ano = sa.semestre_ano AND vr.versao = sa.versao " +
                    "INNER JOIN aluno a ON sa.aluno = a.email " +
                    "WHERE (sa.status_empresa IS NULL AND sa.status_descricao_empresa IS NULL " +
                    "  AND sa.status_repositorio IS NULL AND sa.status_problema IS NULL " +
                    "  AND sa.status_solucao IS NULL AND sa.status_tecnologias IS NULL " +
                    "  AND sa.status_contribuicoes IS NULL AND sa.status_hard_skills IS NULL " +
                    "  AND sa.status_soft_skills IS NULL)";
            
            PreparedStatement pst2 = conn.prepareStatement(sqlApi);
            pst2.setString(1, emailOrientador);
            ResultSet rs2 = pst2.executeQuery();
            
            while (rs2.next()) {
                Map<String, String> secao = new java.util.HashMap<>();
                secao.put("nome_aluno", rs2.getString("nome_aluno"));
                secao.put("email_aluno", rs2.getString("email_aluno"));
                secao.put("tipo_secao", rs2.getString("tipo_secao"));
                secao.put("identificador", rs2.getString("identificador"));
                secao.put("tipo", "api");
                secao.put("aluno", rs2.getString("aluno"));
                secao.put("semestre_curso", rs2.getString("semestre_curso"));
                secao.put("ano", String.valueOf(rs2.getInt("ano")));
                secao.put("semestre_ano", rs2.getString("semestre_ano"));
                secao.put("versao", String.valueOf(rs2.getInt("versao")));
                secoes.add(secao);
            }
            
            tabelaSecoesPendentes.setItems(secoes);
            
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro", "Erro ao carregar seções pendentes: " + e.getMessage());
        }
    }
    
    private void abrirSecao(Map<String, String> secao) {
        String tipo = secao.get("tipo");
        String emailAluno = secao.get("aluno");
        
        if (OrientadorPrincipalController.getInstance() != null) {
            if ("apresentacao".equals(tipo)) {
                int versao = Integer.parseInt(secao.get("versao"));
                OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
                    "/com/example/technocode/Orientador/orientador-corrigir-apresentacao.fxml",
                    controller -> {
                        if (controller instanceof OrientadorCorrigirApresentacaoController) {
                            ((OrientadorCorrigirApresentacaoController) controller).setIdentificadorSecao(emailAluno, versao);
                        }
                    }
                );
            } else if ("api".equals(tipo)) {
                String semestreCurso = secao.get("semestre_curso");
                int ano = Integer.parseInt(secao.get("ano"));
                String semestreAno = secao.get("semestre_ano");
                int versao = Integer.parseInt(secao.get("versao"));
                OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
                    "/com/example/technocode/Orientador/orientador-corrigir-api.fxml",
                    controller -> {
                        if (controller instanceof OrientadorCorrigirApiController) {
                            ((OrientadorCorrigirApiController) controller).setIdentificadorSecao(emailAluno, semestreCurso, ano, semestreAno, versao);
                        }
                    }
                );
            }
        }
    }

    private void carregarSolicitacoesPendentes() {
        try {
            List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPendentesPorOrientador(emailOrientador);
            vboxSolicitacoesPendentes.getChildren().clear();

            if (solicitacoes.isEmpty()) {
                Label labelSemSolicitacoes = new Label("Nenhuma solicitação pendente");
                labelSemSolicitacoes.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 12px;");
                vboxSolicitacoesPendentes.getChildren().add(labelSemSolicitacoes);
            } else {
                for (Map<String, String> solicitacao : solicitacoes) {
                    String nomeAluno = solicitacao.get("nome_aluno");
                    int solicitacaoId = Integer.parseInt(solicitacao.get("id"));
                    
                    // Criar HBox para cada solicitação
                    HBox hboxSolicitacao = new HBox(10);
                    hboxSolicitacao.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 10; -fx-background-color: #F8F9FA; -fx-background-radius: 6;");
                    hboxSolicitacao.setPrefWidth(Double.MAX_VALUE);
                    
                    // Label com nome do aluno
                    Label labelNome = new Label(nomeAluno);
                    labelNome.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");
                    
                    // Region para empurrar os botões para a direita
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    // Botão Aceitar com ícone
                    Button btnAceitar = new Button("✓ Aceitar");
                    btnAceitar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 6; -fx-text-fill: WHITE; -fx-cursor: hand; -fx-padding: 8 15; -fx-font-size: 13px; -fx-font-weight: bold;");
                    btnAceitar.setOnAction(e -> aceitarSolicitacao(solicitacaoId));
                    
                    // Botão Recusar com ícone
                    Button btnRecusar = new Button("✗ Recusar");
                    btnRecusar.setStyle("-fx-background-color: #E74C3C; -fx-background-radius: 6; -fx-text-fill: WHITE; -fx-cursor: hand; -fx-padding: 8 15; -fx-font-size: 13px; -fx-font-weight: bold;");
                    btnRecusar.setOnAction(e -> recusarSolicitacao(solicitacaoId));
                    
                    hboxSolicitacao.getChildren().addAll(labelNome, spacer, btnAceitar, btnRecusar);
                    vboxSolicitacoesPendentes.getChildren().add(hboxSolicitacao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label labelErro = new Label("Erro ao carregar solicitações");
            labelErro.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 12px;");
            vboxSolicitacoesPendentes.getChildren().clear();
            vboxSolicitacoesPendentes.getChildren().add(labelErro);
        }
    }

    private void aceitarSolicitacao(int solicitacaoId) {
        try {
            SolicitacaoOrientacao solicitacao = SolicitacaoOrientacao.buscarPorId(solicitacaoId);
            if (solicitacao != null) {
                solicitacao.aceitar();
                mostrarAlertaSucesso("Sucesso", "Solicitação aceita com sucesso! O aluno foi vinculado ao seu perfil.");
                
                // Recarrega as estatísticas e a lista de solicitações
                carregarEstatisticas();
                carregarSolicitacoesPendentes();
            } else {
                mostrarAlertaErro("Erro", "Solicitação não encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro", "Não foi possível aceitar a solicitação. Erro: " + e.getMessage());
        }
    }

    private void recusarSolicitacao(int solicitacaoId) {
        // Navega para a tela de responder solicitação já com o card de justificativa visível
        if (OrientadorPrincipalController.getInstance() != null) {
            OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Orientador/responder-solicitacao.fxml",
                c -> {
                    if (c instanceof ResponderSolicitacaoController) {
                        ((ResponderSolicitacaoController) c).setSolicitacaoIdComRecusa(solicitacaoId);
                    }
                }
            );
        }
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlertaSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

