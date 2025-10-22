package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
        String tipo = getTipoUsuario();
        if (tipo == null) {
            mostrarAlertaErro("Tipo de usuário", "Por favor, selecione um tipo de usuário (Aluno ou Orientador).");
            return;
        }

        if (txtNome.getText().isEmpty() || txtEmail.getText().isEmpty() || txtSenha.getText().isEmpty()) {
            mostrarAlertaErro("Campos obrigatórios", "Por favor, preencha todos os campos: Nome, Email e Senha.");
            return;
        }

        if (!validarEmail(txtEmail.getText())) {
            mostrarAlertaErro("Email inválido", "Por favor, insira um email válido.");
            return;
        }

        if (tipo.equals("Aluno")) {
            if (comboBoxOrientador.getValue() == null || comboBoxOrientador.getValue().isEmpty()) {
                mostrarAlertaErro("Orientador obrigatório", "Para cadastro de aluno, é necessário selecionar um orientador.");
                return;
            }
        }

        if (txtSenha.getText().length() < 6) {
            mostrarAlertaErro("Senha fraca", "A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        Connector conn = new Connector();
        if (tipo.equals("Aluno")){
            conn.cadastrarAluno(txtNome.getText(), txtEmail.getText(), txtSenha.getText(), comboBoxOrientador.getValue());
        }else if (tipo.equals("Orientador")){
            conn.cadastrarOrientador(txtNome.getText(), txtEmail.getText(), txtSenha.getText());
        }

        mostrarAlertaSucesso("Cadastro realizado", "Usuário cadastrado com sucesso!");

        System.out.println("Usuario cadastrado com sucesso!");
    }

    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Cadastro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlertaSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cadastro Realizado");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void toggleHboxOrientador(ActionEvent event) {
        RadioButton selected = (RadioButton) event.getSource();
        hBoxOrientador.setVisible(selected == radioAluno);
    }
}