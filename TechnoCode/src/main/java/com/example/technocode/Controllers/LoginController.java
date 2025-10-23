package com.example.technocode.Controllers;

import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField txtEmail, txtSenha;

    private static String emailLogado;

    public static String getEmailLogado() {
        return emailLogado;
    }

    public void login(ActionEvent event) throws IOException {
        if (txtEmail.getText().isEmpty() || txtSenha.getText().isEmpty()) {
            mostrarAlertaErro("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return;
        }

        if (!validarEmail(txtEmail.getText())) {
            mostrarAlertaErro("Email inválido", "Por favor, insira um email válido.");
            return;
        }

        Connector connector = new Connector();
        String tipo = connector.login(txtEmail.getText(), txtSenha.getText());

        if (tipo == null || tipo.isEmpty()) {
            mostrarAlertaErro("Usuário não encontrado", "Email ou senha incorretos. Por favor, tente novamente.");
            return;
        }

        else {
            emailLogado = txtEmail.getText();
        }
        if (tipo.equals("Aluno")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-inicial-aluno.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            return;
        }
        if (tipo.equals("Orientador")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-inicial-orientador.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void cadastrarUsuario(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/cadastro.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}