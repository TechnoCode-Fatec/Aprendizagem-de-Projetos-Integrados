package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLOutput;

public class FormularioApiController {
    @FXML
    private ChoiceBox<String> choiceBoxSemestre,choiceBoxSemestreDoCurso;
    @FXML
    private TextField txtAno, txtEmpresa, txtLinkRepositorio;
    @FXML
    private TextArea txtProblema, txtSolucao, txtTecnologias, txtContribuicoes, txtHardSkills, txtSoftSkills;

    @FXML
    private void initialize(){
        choiceBoxSemestre.getItems().addAll("1","2");
        choiceBoxSemestreDoCurso.getItems().addAll("Primeiro semestre","Segundo semestre","Terceiro semestre","Quarto semestre","Quinto semestre","Sexto semestre");
    }
    @FXML
    private void enviarSecaoApi(ActionEvent event) {
        Connector connector = new Connector();
        String emailAluno = LoginController.getEmailLogado();
        connector.cadastrarSessaoApi(emailAluno, choiceBoxSemestreDoCurso.getValue(), Integer.parseInt(txtAno.getText()), choiceBoxSemestre.getValue(), 1,
                txtEmpresa.getText(), txtProblema.getText(), txtSolucao.getText(),txtLinkRepositorio.getText(),txtTecnologias.getText(),
                txtContribuicoes.getText(),txtHardSkills.getText(),txtSoftSkills.getText());
        System.out.println("Cadastrado com sucesso");
    }
    public void voltar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-inicial-aluno.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
