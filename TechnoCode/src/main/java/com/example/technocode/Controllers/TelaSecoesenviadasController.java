package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class TelaSecoesenviadasController {

    // Identificador da seção
    private String alunoId;
    private int versaoId;

    // Status por campo (Aprovado | Revisar | null)
    private final Map<String, String> statusPorCampo = new HashMap<>();

    private Map<String, Boolean> validacoes = new HashMap<>();

    @FXML private CheckBox validarNome;
    @FXML private CheckBox validarIdade;
    @FXML private CheckBox validarCurso;
    @FXML private CheckBox validarMotivacao;
    @FXML private CheckBox validarHistorico;
    @FXML private CheckBox validarGithub;
    @FXML private CheckBox validarLinkedin;
    @FXML private CheckBox validarConhecimentos;

    @FXML private TextArea alunoTextNome;
    @FXML private TextArea alunoTextIdade;
    @FXML private TextArea alunoTextCurso;
    @FXML private TextArea alunoTextMotivacao;
    @FXML private TextArea alunoTextHistorico;
    @FXML private TextArea alunoTextGithub;
    @FXML private TextArea alunoTextLinkedin;
    @FXML private TextArea alunoTextConhecimentos;


    @FXML private TextArea feedbackTextNome;
    @FXML private TextArea feedbackTextIdade;
    @FXML private TextArea feedbackTextCurso;
    @FXML private TextArea feedbackTextMotivacao;
    @FXML private TextArea feedbackTextHistorico;
    @FXML private TextArea feedbackTextGithub;
    @FXML private TextArea feedbackTextLinkedin;
    @FXML private TextArea feedbackTextConhecimentos;


    @FXML private CheckBox feedbackNome;
    @FXML private CheckBox feedbackIdade;
    @FXML private CheckBox feedbackCurso;
    @FXML private CheckBox feedbackMotivacao;
    @FXML private CheckBox feedbackHistorico;
    @FXML private CheckBox feedbackGithub;
    @FXML private CheckBox feedbackLinkedin;
    @FXML private CheckBox feedbackConhecimentos;

    @FXML
    public void initialize() {
        // Inicializa todas as TextAreas como invisíveis
        feedbackTextNome.setVisible(false);
        feedbackTextIdade.setVisible(false);
        feedbackTextCurso.setVisible(false);
        feedbackTextMotivacao.setVisible(false);
        feedbackTextHistorico.setVisible(false);
        feedbackTextGithub.setVisible(false);
        feedbackTextLinkedin.setVisible(false);
        feedbackTextConhecimentos.setVisible(false);
    }

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        this.alunoId = aluno;
        this.versaoId = versao;
        carregarSecaoAluno();
    }

    // 1) Carrega dados da secao_apresentacao
    public void carregarSecaoAluno() {
        if (alunoId == null) return;
        String sql = "SELECT nome, idade, curso, motivacao, historico, link_github, link_linkedin, principais_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setInt(2, versaoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    if (alunoTextNome != null) alunoTextNome.setText(rs.getString("nome"));
                    if (alunoTextIdade != null) alunoTextIdade.setText(rs.getString("idade"));
                    if (alunoTextCurso != null) alunoTextCurso.setText(rs.getString("curso"));
                    if (alunoTextMotivacao != null) alunoTextMotivacao.setText(rs.getString("motivacao"));
                    if (alunoTextHistorico != null) alunoTextHistorico.setText(rs.getString("historico"));
                    if (alunoTextGithub != null) alunoTextGithub.setText(rs.getString("link_github"));
                    if (alunoTextLinkedin != null) alunoTextLinkedin.setText(rs.getString("link_linkedin"));
                    if (alunoTextConhecimentos != null) alunoTextConhecimentos.setText(rs.getString("principais_conhecimentos"));
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
        if (alunoId == null) return;
        String sql = "SELECT status_nome, feedback_nome, " +
                "status_idade, feedback_idade, " +
                "status_curso, feedback_curso, " +
                "status_motivacao, feedback_motivacao, " +
                "status_historico, feedback_historico, " +
                "status_github, feedback_github, " +
                "status_linkedin, feedback_linkedin, " +
                "status_conhecimentos, feedback_conhecimentos " +
                "FROM feedback_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setInt(2, versaoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    carregarCampoFeedbackExistente("nome", rs, feedbackTextNome, feedbackNome);
                    carregarCampoFeedbackExistente("idade", rs, feedbackTextIdade, feedbackIdade);
                    carregarCampoFeedbackExistente("curso", rs, feedbackTextCurso, feedbackCurso);
                    carregarCampoFeedbackExistente("motivacao", rs, feedbackTextMotivacao, feedbackMotivacao);
                    carregarCampoFeedbackExistente("historico", rs, feedbackTextHistorico, feedbackHistorico);
                    carregarCampoFeedbackExistente("github", rs, feedbackTextGithub, feedbackGithub);
                    carregarCampoFeedbackExistente("linkedin", rs, feedbackTextLinkedin, feedbackLinkedin);
                    carregarCampoFeedbackExistente("conhecimentos", rs, feedbackTextConhecimentos, feedbackConhecimentos);
                }
            }
        } catch (SQLException e) {
            // Não é erro crítico se não houver feedback existente
            System.out.println("Nenhum feedback existente encontrado para esta seção");
        }
    }
    
    private void carregarCampoFeedbackExistente(String campo, ResultSet rs, TextArea feedbackArea, CheckBox feedbackCheckBox) throws SQLException {
        String status = rs.getString("status_" + campo);
        String feedback = rs.getString("feedback_" + campo);
        
        if (status != null) {
            statusPorCampo.put(campo, status);
            
            if ("Revisar".equals(status) && feedback != null && !feedback.trim().isEmpty()) {
                // Se foi marcado para revisar e tem feedback, mostra o campo
                if (feedbackCheckBox != null) {
                    feedbackCheckBox.setSelected(true);
                }
                if (feedbackArea != null) {
                    feedbackArea.setVisible(true);
                    feedbackArea.setText(feedback);
                    feedbackArea.setPrefHeight(100);
                    feedbackArea.setWrapText(true);
                }
            }
            
            // Atualiza as cores dos botões baseado no status carregado
            // Usa Platform.runLater para garantir que a cena esteja carregada
            Platform.runLater(() -> atualizarCorBotoes(campo, status));
        }
    }

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-entregasDoAluno.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela do orientador: " + e.getMessage());
            throw e;
        }
    }

    @FXML
    public void feedbackSecao(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        TextArea feedbackArea = null;

        // Determina qual TextArea deve ser mostrada/escondida
        if (checkBox == feedbackNome) {
            feedbackArea = feedbackTextNome;
        } else if (checkBox == feedbackIdade) {
            feedbackArea = feedbackTextIdade;
        } else if (checkBox == feedbackCurso) {
            feedbackArea = feedbackTextCurso;
        } else if (checkBox == feedbackMotivacao) {
            feedbackArea = feedbackTextMotivacao;
        } else if (checkBox == feedbackHistorico) {
            feedbackArea = feedbackTextHistorico;
        } else if (checkBox == feedbackGithub) {
            feedbackArea = feedbackTextGithub;
        } else if (checkBox == feedbackLinkedin) {
            feedbackArea = feedbackTextLinkedin;
        } else if (checkBox == feedbackConhecimentos) {
            feedbackArea = feedbackTextConhecimentos;
        }

        // Mostra ou esconde a área de feedback e limpa o texto quando esconde
        if (feedbackArea != null) {
            feedbackArea.setVisible(checkBox.isSelected());
            if (!checkBox.isSelected()) {
                feedbackArea.clear(); // Limpa o texto quando esconde
            } else {
                // Configura um texto padrão ou placeholder quando mostra
                feedbackArea.setPromptText("Digite seu feedback aqui...");

                // Ajusta o tamanho para um campo menor
                feedbackArea.setPrefHeight(100); // Altura menor
                feedbackArea.setWrapText(true); // Permite quebra de linha automática
            }
        }
    }



    // 2) Aprovar/Revisar por campo (handlers dos botões)
    @FXML private void aprovarNome(ActionEvent e) { aprovarCampo("nome", feedbackTextNome); }
    @FXML private void revisarNome(ActionEvent e) { revisarCampo("nome", feedbackTextNome); }
    @FXML private void aprovarIdade(ActionEvent e) { aprovarCampo("idade", feedbackTextIdade); }
    @FXML private void revisarIdade(ActionEvent e) { revisarCampo("idade", feedbackTextIdade); }
    @FXML private void aprovarCurso(ActionEvent e) { aprovarCampo("curso", feedbackTextCurso); }
    @FXML private void revisarCurso(ActionEvent e) { revisarCampo("curso", feedbackTextCurso); }
    @FXML private void aprovarMotivacao(ActionEvent e) { aprovarCampo("motivacao", feedbackTextMotivacao); }
    @FXML private void revisarMotivacao(ActionEvent e) { revisarCampo("motivacao", feedbackTextMotivacao); }
    @FXML private void aprovarHistorico(ActionEvent e) { aprovarCampo("historico", feedbackTextHistorico); }
    @FXML private void revisarHistorico(ActionEvent e) { revisarCampo("historico", feedbackTextHistorico); }
    @FXML private void aprovarGithub(ActionEvent e) { aprovarCampo("github", feedbackTextGithub); }
    @FXML private void revisarGithub(ActionEvent e) { revisarCampo("github", feedbackTextGithub); }
    @FXML private void aprovarLinkedin(ActionEvent e) { aprovarCampo("linkedin", feedbackTextLinkedin); }
    @FXML private void revisarLinkedin(ActionEvent e) { revisarCampo("linkedin", feedbackTextLinkedin); }
    @FXML private void aprovarConhecimentos(ActionEvent e) { aprovarCampo("conhecimentos", feedbackTextConhecimentos); }
    @FXML private void revisarConhecimentos(ActionEvent e) { revisarCampo("conhecimentos", feedbackTextConhecimentos); }

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
        // Mapeia campos para seus respectivos botões
        Button btnAprovar = null;
        Button btnRevisar = null;
        
        switch (campo) {
            case "nome":
                btnAprovar = (Button) alunoTextNome.getScene().lookup("#aprovarNome");
                btnRevisar = (Button) alunoTextNome.getScene().lookup("#revisarNome");
                break;
            case "idade":
                btnAprovar = (Button) alunoTextIdade.getScene().lookup("#aprovarIdade");
                btnRevisar = (Button) alunoTextIdade.getScene().lookup("#revisarIdade");
                break;
            case "curso":
                btnAprovar = (Button) alunoTextCurso.getScene().lookup("#aprovarCurso");
                btnRevisar = (Button) alunoTextCurso.getScene().lookup("#revisarCurso");
                break;
            case "motivacao":
                btnAprovar = (Button) alunoTextMotivacao.getScene().lookup("#aprovarMotivacao");
                btnRevisar = (Button) alunoTextMotivacao.getScene().lookup("#revisarMotivacao");
                break;
            case "historico":
                btnAprovar = (Button) alunoTextHistorico.getScene().lookup("#aprovarHistorico");
                btnRevisar = (Button) alunoTextHistorico.getScene().lookup("#revisarHistorico");
                break;
            case "github":
                btnAprovar = (Button) alunoTextGithub.getScene().lookup("#aprovarGithub");
                btnRevisar = (Button) alunoTextGithub.getScene().lookup("#revisarGithub");
                break;
            case "linkedin":
                btnAprovar = (Button) alunoTextLinkedin.getScene().lookup("#aprovarLinkedin");
                btnRevisar = (Button) alunoTextLinkedin.getScene().lookup("#revisarLinkedin");
                break;
            case "conhecimentos":
                btnAprovar = (Button) alunoTextConhecimentos.getScene().lookup("#aprovarConhecimentos");
                btnRevisar = (Button) alunoTextConhecimentos.getScene().lookup("#revisarConhecimentos");
                break;
        }
        
        if (btnAprovar != null && btnRevisar != null) {
            if ("Aprovado".equals(status)) {
                btnAprovar.setStyle("-fx-background-color: #00AA00; -fx-text-fill: white;");
                btnRevisar.setStyle("-fx-background-color: #5E5555; -fx-text-fill: white;");
            } else if ("Revisar".equals(status)) {
                btnAprovar.setStyle("-fx-background-color: #5E5555; -fx-text-fill: white;");
                btnRevisar.setStyle("-fx-background-color: #AA0000; -fx-text-fill: white;");
            }
        }
    }

    // 3) Enviar feedbacks (INSERT ou UPDATE)
    @FXML
    public void enviarFeedback(ActionEvent event) {
        // Verifica se já existe feedback para esta seção
        boolean feedbackExiste = verificarFeedbackExistente();
        
        String sql;
        if (feedbackExiste) {
            // UPDATE se já existe
            sql = "UPDATE feedback_apresentacao SET " +
                    "status_nome = ?, feedback_nome = ?, " +
                    "status_idade = ?, feedback_idade = ?, " +
                    "status_curso = ?, feedback_curso = ?, " +
                    "status_motivacao = ?, feedback_motivacao = ?, " +
                    "status_historico = ?, feedback_historico = ?, " +
                    "status_github = ?, feedback_github = ?, " +
                    "status_linkedin = ?, feedback_linkedin = ?, " +
                    "status_conhecimentos = ?, feedback_conhecimentos = ? " +
                    "WHERE aluno = ? AND versao = ?";
        } else {
            // INSERT se não existe
            sql = "INSERT INTO feedback_apresentacao (" +
                    "status_nome, feedback_nome, " +
                    "status_idade, feedback_idade, " +
                    "status_curso, feedback_curso, " +
                    "status_motivacao, feedback_motivacao, " +
                    "status_historico, feedback_historico, " +
                    "status_github, feedback_github, " +
                    "status_linkedin, feedback_linkedin, " +
                    "status_conhecimentos, feedback_conhecimentos, " +
                    "aluno, versao) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        }

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            setNullableString(pst, 1, statusPorCampo.get("nome"));
            setNullableString(pst, 2, textOrNull(feedbackTextNome));
            setNullableString(pst, 3, statusPorCampo.get("idade"));
            setNullableString(pst, 4, textOrNull(feedbackTextIdade));
            setNullableString(pst, 5, statusPorCampo.get("curso"));
            setNullableString(pst, 6, textOrNull(feedbackTextCurso));
            setNullableString(pst, 7, statusPorCampo.get("motivacao"));
            setNullableString(pst, 8, textOrNull(feedbackTextMotivacao));
            setNullableString(pst, 9, statusPorCampo.get("historico"));
            setNullableString(pst, 10, textOrNull(feedbackTextHistorico));
            setNullableString(pst, 11, statusPorCampo.get("github"));
            setNullableString(pst, 12, textOrNull(feedbackTextGithub));
            setNullableString(pst, 13, statusPorCampo.get("linkedin"));
            setNullableString(pst, 14, textOrNull(feedbackTextLinkedin));
            setNullableString(pst, 15, statusPorCampo.get("conhecimentos"));
            setNullableString(pst, 16, textOrNull(feedbackTextConhecimentos));

            if (feedbackExiste) {
                // Para UPDATE, adiciona WHERE
                pst.setString(17, alunoId);
                pst.setInt(18, versaoId);
            } else {
                // Para INSERT, adiciona aluno e versao
                pst.setString(17, alunoId);
                pst.setInt(18, versaoId);
            }

            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText(feedbackExiste ? "Feedbacks atualizados com sucesso!" : "Feedbacks enviados com sucesso!");
            alert.showAndWait();
        } catch (SQLException e) {
            mostrarErro("Erro ao enviar feedback", e);
        }
    }
    
    private boolean verificarFeedbackExistente() {
        String sql = "SELECT COUNT(*) FROM feedback_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, alunoId);
            pst.setInt(2, versaoId);
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
    public void validarSecao(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        boolean isValidado = checkBox.isSelected();

        String secaoValidada = "";
        String mensagemAdicional = "";

        // Identifica qual seção está sendo validada
        if (checkBox == validarNome) {
            secaoValidada = "Nome";
            validacoes.put("Nome", isValidado);
            mensagemAdicional = isValidado ? "Nome do aluno foi aprovado" : "Nome do aluno precisa de revisão";
        } else if (checkBox == validarIdade) {
            secaoValidada = "Idade";
            validacoes.put("Idade", isValidado);
            mensagemAdicional = isValidado ? "Idade foi confirmada" : "Idade precisa ser verificada";
        } else if (checkBox == validarCurso) {
            secaoValidada = "Curso";
            validacoes.put("Curso", isValidado);
            mensagemAdicional = isValidado ? "Informações do curso foram confirmadas" : "Informações do curso precisam ser revisadas";
        } else if (checkBox == validarMotivacao) {
            secaoValidada = "Motivação";
            validacoes.put("Motivação", isValidado);
            mensagemAdicional = isValidado ? "Motivação foi aprovada" : "Motivação precisa ser melhorada";
        } else if (checkBox == validarHistorico) {
            secaoValidada = "Histórico";
            validacoes.put("Histórico", isValidado);
            mensagemAdicional = isValidado ? "Histórico foi aprovado" : "Histórico precisa ser complementado";
        } else if (checkBox == validarGithub) {
            secaoValidada = "GitHub";
            validacoes.put("GitHub", isValidado);
            mensagemAdicional = isValidado ? "Perfil do GitHub foi aprovado" : "Perfil do GitHub precisa ser atualizado";
        } else if (checkBox == validarLinkedin) {
            secaoValidada = "LinkedIn";
            validacoes.put("LinkedIn", isValidado);
            mensagemAdicional = isValidado ? "Perfil do LinkedIn foi aprovado" : "Perfil do LinkedIn precisa ser atualizado";
        } else if (checkBox == validarConhecimentos) {
            secaoValidada = "Conhecimentos";
            validacoes.put("Conhecimentos", isValidado);
            mensagemAdicional = isValidado ? "Conhecimentos foram aprovados" : "Conhecimentos precisam ser revisados";
        }

        // Exibe mensagem de confirmação com detalhes
        if (!secaoValidada.isEmpty()) {
            String status = isValidado ? "validada" : "invalidada";
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Status da Validação");
            alert.setHeaderText("Seção: " + secaoValidada);
            alert.setContentText(mensagemAdicional);
            alert.showAndWait();

            // Atualiza o status da seção no banco de dados ou sistema
            atualizarStatusSecao(secaoValidada, isValidado);
        }
    }

    private void atualizarStatusSecao(String secao, boolean status) {
        try {
            // TODO: Implementar a lógica de atualização no banco de dados
            System.out.println("Atualizando status da seção " + secao + ": " + status);

            // Aqui você pode adicionar a lógica para salvar no banco de dados
            // Exemplo de como poderia ser:
            // String sql = "UPDATE secao_apresentacao SET status = ? WHERE secao = ? AND aluno = ?";
            // PreparedStatement pst = connection.prepareStatement(sql);
            // pst.setBoolean(1, status);
            // pst.setString(2, secao);
            // pst.setString(3, alunoAtual);
            // pst.executeUpdate();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao atualizar status");
            alert.setContentText("Não foi possível atualizar o status da seção no banco de dados.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    // Método para verificar se todas as seções foram validadas
    public boolean todasSecoesValidadas() {
        for (Boolean validado : validacoes.values()) {
            if (!validado) {
                return false;
            }
        }
        return !validacoes.isEmpty();
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