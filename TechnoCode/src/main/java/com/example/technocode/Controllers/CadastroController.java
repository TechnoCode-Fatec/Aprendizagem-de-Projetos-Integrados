package com.example.technocode.Controllers;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.Orientador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;

public class CadastroController {

    @FXML
    private RadioButton radioAluno, radioOrientador;
    @FXML
    private TextField txtNome, txtEmail, txtSenha;
    @FXML
    HBox hBoxOrientador, hBoxCurso;
    @FXML
    ComboBox<String> comboBoxOrientador, comboBoxCurso;

    private ToggleGroup grupoUsuario;
    private Map<String, String> orientadoresMap;

    @FXML
    private void initialize() {
        grupoUsuario = new ToggleGroup();
        radioAluno.setToggleGroup(grupoUsuario);
        radioAluno.setUserData("Aluno");
        radioOrientador.setToggleGroup(grupoUsuario);
        radioOrientador.setUserData("Orientador");
        hBoxOrientador.setVisible(false);
        hBoxCurso.setVisible(false);
        orientadoresMap = Orientador.buscarTodos();
        comboBoxOrientador.getItems().addAll(orientadoresMap.keySet());
        comboBoxCurso.getItems().addAll("TG1", "TG2", "TG1/TG2");

    }
    public void voltar(ActionEvent event) throws IOException {
        NavigationService.navegarPara(event, "/com/example/technocode/login.fxml");
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

        if (tipo.equals("Aluno")) {
            // Busca o email do orientador a partir do nome selecionado
            String nomeSelecionado = comboBoxOrientador.getValue();
            String emailOrientador = Orientador.buscarEmailPorNome(nomeSelecionado);

            if (emailOrientador == null) {
                mostrarAlertaErro("Orientador inválido", "Não foi possível encontrar o email do orientador selecionado.");
                return;
            }

            // Cria e cadastra objeto Aluno
            Aluno aluno = new Aluno(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText(),
                    emailOrientador,
                    comboBoxCurso.getValue()
            );
            aluno.cadastrar();

        } else if (tipo.equals("Orientador")) {
            // Cria e cadastra objeto Orientador
            Orientador orientador = new Orientador(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText()
            );
            orientador.cadastrar();

            // Recarrega a lista de orientadores após cadastrar um novo
            try {
                comboBoxOrientador.getItems().clear();
                comboBoxOrientador.getItems().addAll(Orientador.buscarTodos().keySet());
            } catch (Exception e) {
                System.err.println("Erro ao recarregar comboBoxOrientador: " + e.getMessage());
            }
        }

        mostrarAlertaSucesso("Cadastro realizado", "Usuário cadastrado com sucesso!");
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
        hBoxCurso.setVisible(selected == radioAluno);
    }
}
