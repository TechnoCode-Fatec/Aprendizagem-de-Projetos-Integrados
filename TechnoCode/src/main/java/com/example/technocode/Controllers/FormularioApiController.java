package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
        connector.cadastrarSessaoApi("@email", choiceBoxSemestreDoCurso.getValue(), Integer.parseInt(txtAno.getText()), choiceBoxSemestre.getValue(), 1,
                txtEmpresa.getText(), txtProblema.getText(), txtSolucao.getText(),txtLinkRepositorio.getText(),txtTecnologias.getText(),
                txtContribuicoes.getText(),txtHardSkills.getText(),txtSoftSkills.getText());

    }
}
