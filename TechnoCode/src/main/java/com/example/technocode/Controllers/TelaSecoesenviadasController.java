package com.example.technocode.Controllers;


import com.example.technocode.Objetos.Seção;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TelaSecoesenviadasController {

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



    @FXML
    public void enviarFeedback(ActionEvent event) {
        // Coleta todos os feedbacks preenchidos
        Map<String, String> feedbacks = new HashMap<>();

        if (feedbackNome.isSelected() && !feedbackTextNome.getText().isEmpty()) {
            feedbacks.put("Nome Completo", feedbackTextNome.getText());
        }
        if (feedbackIdade.isSelected() && !feedbackTextIdade.getText().isEmpty()) {
            feedbacks.put("Idade", feedbackTextIdade.getText());
        }
        if (feedbackCurso.isSelected() && !feedbackTextCurso.getText().isEmpty()) {
            feedbacks.put("Curso", feedbackTextCurso.getText());
        }
        if (feedbackMotivacao.isSelected() && !feedbackTextMotivacao.getText().isEmpty()) {
            feedbacks.put("Motivação", feedbackTextMotivacao.getText());
        }
        if (feedbackHistorico.isSelected() && !feedbackTextHistorico.getText().isEmpty()) {
            feedbacks.put("Histórico", feedbackTextHistorico.getText());
        }
        if (feedbackGithub.isSelected() && !feedbackTextGithub.getText().isEmpty()) {
            feedbacks.put("GitHub", feedbackTextGithub.getText());
        }
        if (feedbackLinkedin.isSelected() && !feedbackTextLinkedin.getText().isEmpty()) {
            feedbacks.put("LinkedIn", feedbackTextLinkedin.getText());
        }
        if (feedbackConhecimentos.isSelected() && !feedbackTextConhecimentos.getText().isEmpty()) {
            feedbacks.put("Conhecimentos", feedbackTextConhecimentos.getText());
        }

        // Aqui você implementa a lógica para salvar os feedbacks
        for (Map.Entry<String, String> feedback : feedbacks.entrySet()) {
            System.out.println("Feedback para " + feedback.getKey() + ": " + feedback.getValue());
        }

        // Mostra mensagem de confirmação
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Feedbacks enviados com sucesso!");
        alert.showAndWait();
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

    public void setDadosDoAluno(Map<String, String> dados) {
        if (dados != null) {
            alunoTextNome.setText(dados.getOrDefault("nome", ""));
            alunoTextIdade.setText(dados.getOrDefault("idade", ""));
            alunoTextCurso.setText(dados.getOrDefault("curso", ""));
            alunoTextMotivacao.setText(dados.getOrDefault("motivacao", ""));
            alunoTextHistorico.setText(dados.getOrDefault("historico", ""));
            alunoTextGithub.setText(dados.getOrDefault("github", ""));
            alunoTextLinkedin.setText(dados.getOrDefault("linkedin", ""));
            alunoTextConhecimentos.setText(dados.getOrDefault("conhecimentos", ""));
        }
    }


}