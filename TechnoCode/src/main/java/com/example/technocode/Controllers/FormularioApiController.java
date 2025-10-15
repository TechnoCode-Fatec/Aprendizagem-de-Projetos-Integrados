package com.example.technocode.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class FormularioApiController {
    @FXML
    private ChoiceBox<String> choiceBoxSemestre,choiceBoxSemestreDoCurso;

    @FXML
    private void initialize(){
        choiceBoxSemestre.getItems().addAll("1","2");
        choiceBoxSemestreDoCurso.getItems().addAll("Primeiro semestre","Segundo semestre","Terceiro semestre","Quarto semestre","Quinto semestre","Sexto semestre");
    }
}
