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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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

    private String emailOrientador;

    @FXML
    public void initialize() {
        emailOrientador = LoginController.getEmailLogado();
        carregarInformacoesOrientador();
        carregarEstatisticas();
        carregarSolicitacoesPendentes();
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
    private void clicarCardSecoesPendentes(ActionEvent event) {
        navegarParaAlunosOrientados(event);
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
        // Navega para a tela de responder solicitação
        if (OrientadorPrincipalController.getInstance() != null) {
            OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Orientador/responder-solicitacao.fxml",
                c -> {
                    if (c instanceof ResponderSolicitacaoController) {
                        ((ResponderSolicitacaoController) c).setSolicitacaoId(solicitacaoId);
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

