package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    private void enviarApresentacao(ActionEvent event) {
        Connector connector = new Connector();
        connector.cadastrarApresentacao("joao.silva@fatec.sp.gov.br", txtNome.getText(), Date.valueOf(datePickerIdade.getValue()), choiceBoxCurso.getValue(),1, txtMotivacao.getText(),txtHistorico.getText(), txtGithub.getText(), txtLinkedin.getText(),txtPrincipaisConhecimentos.getText());
        System.out.println("Cadastrado com sucesso");
    }
}
