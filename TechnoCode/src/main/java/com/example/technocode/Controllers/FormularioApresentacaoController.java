package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class FormularioApresentacaoController {

    @FXML
    private TextField txtNome, txtGithub, txtLinkedin;
    @FXML
    private TextArea txtMotivacao, txtHistorico, txtPrincipaisConhecimentos;
    @FXML
    private DatePicker datePickerIdade;
    @FXML
    private ChoiceBox<String> choiceBoxCurso;

    @FXML
    private void initialize(){
        choiceBoxCurso.getItems().addAll("ADS","BD");
    }

    @FXML
    private void enviarApresentacao(ActionEvent event) {

        if (txtNome.getText().isEmpty() ||
                datePickerIdade.getValue() == null ||
                choiceBoxCurso.getValue() == null ||
                txtMotivacao.getText().isEmpty() ||
                txtHistorico.getText().isEmpty() ||
                txtGithub.getText().isEmpty() ||
                txtLinkedin.getText().isEmpty() ||
                txtPrincipaisConhecimentos.getText().isEmpty()) {

            mostrarAlerta("Campos obrigatórios", "Por favor, preencha todos os campos antes de enviar.");
            return;
        }

        Connector connector = new Connector();
        String emailAluno = LoginController.getEmailLogado();
        connector.cadastrarApresentacao(emailAluno, txtNome.getText(), Date.valueOf(datePickerIdade.getValue()), choiceBoxCurso.getValue(),1, txtMotivacao.getText(),txtHistorico.getText(), txtGithub.getText(), txtLinkedin.getText(),txtPrincipaisConhecimentos.getText());
        System.out.println("Cadastrado com sucesso");
    }

    public void voltar (ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-inicial-aluno.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }
    public void mostrarAlerta (String titulo, String mensagem){
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensagem);
            alerta.showAndWait();
        }

}

