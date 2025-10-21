package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CadastroController {

    @FXML
    private RadioButton radioAluno, radioOrientador;
    @FXML
    private TextField txtNome, txtEmail, txtSenha;
    @FXML
    HBox hBoxOrientador;
    @FXML
    ComboBox<String> comboBoxOrientador;

    private ToggleGroup grupoUsuario;

    @FXML
    private void initialize() {
        grupoUsuario = new ToggleGroup();
        radioAluno.setToggleGroup(grupoUsuario);
        radioAluno.setUserData("Aluno");
        radioOrientador.setToggleGroup(grupoUsuario);
        radioOrientador.setUserData("Orientador");
        hBoxOrientador.setVisible(false);
        Connector con =  new Connector();
        List<String> orientadores = con.orientadores();
        comboBoxOrientador.getItems().addAll(orientadores);

    }
    public void voltar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public String getTipoUsuario(){
        if (grupoUsuario.getSelectedToggle() != null) {
            return grupoUsuario.getSelectedToggle().getUserData().toString();
        }
        return null;
    }
    @FXML
    private void cadastrarUsuario(ActionEvent event) throws IOException {
        String tipo  = getTipoUsuario();
        Connector conn = new Connector();
        if (tipo.equals("Aluno")){
            conn.cadastrarAluno(txtNome.getText(), txtEmail.getText(), txtSenha.getText(), comboBoxOrientador.getValue());
        }else if (tipo.equals("Orientador")){
            conn.cadastrarOrientador(txtNome.getText(), txtEmail.getText(), txtSenha.getText());
        }

        System.out.println("Usuario cadastrado com sucesso!");
    }
    @FXML
    private void toggleHboxOrientador(ActionEvent event) {
        RadioButton selected = (RadioButton) event.getSource();
        hBoxOrientador.setVisible(selected == radioAluno);
    }
}
