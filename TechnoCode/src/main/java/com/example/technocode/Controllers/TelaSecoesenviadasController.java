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

        // Mostra ou esconde a área de feedback
        if (feedbackArea != null) {
            feedbackArea.setVisible(checkBox.isSelected());
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
}