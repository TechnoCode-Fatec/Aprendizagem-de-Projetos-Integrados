package com.example.technocode.Controllers;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.Orientador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Map;

public class CadastroController {

    @FXML
    private RadioButton radioAluno, radioOrientador;
    @FXML
    private TextField txtNome, txtEmail, txtSenha;
    @FXML
    private HBox hBoxOrientador, hBoxCurso;
    @FXML
    private ComboBox<String> comboBoxOrientador, comboBoxCurso;
    @FXML
    private Button btnCadastrar;

    private ToggleGroup grupoUsuario;
    private Map<String, String> orientadoresMap;

    @FXML
    private void initialize() {
        // Agrupamento de tipo de usuário
        grupoUsuario = new ToggleGroup();
        radioAluno.setToggleGroup(grupoUsuario);
        radioOrientador.setToggleGroup(grupoUsuario);
        radioAluno.setUserData("Aluno");
        radioOrientador.setUserData("Orientador");

        // Inicialmente invisíveis
        hBoxOrientador.setVisible(false);
        hBoxOrientador.setManaged(false);
        hBoxCurso.setVisible(false);
        hBoxCurso.setManaged(false);

        // Vincula a visibilidade diretamente à seleção do RadioButton "Aluno"
        hBoxOrientador.visibleProperty().bind(radioAluno.selectedProperty());
        hBoxOrientador.managedProperty().bind(radioAluno.selectedProperty());
        hBoxCurso.visibleProperty().bind(radioAluno.selectedProperty());
        hBoxCurso.managedProperty().bind(radioAluno.selectedProperty());

        // Carrega orientadores e cursos
        orientadoresMap = Orientador.buscarTodos();
        comboBoxOrientador.getItems().addAll(orientadoresMap.keySet());
        comboBoxCurso.getItems().addAll("TG1", "TG2", "TG1/TG2");

        // Atalho ENTER -> Cadastrar
        btnCadastrar.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            cadastrarUsuario(new ActionEvent());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void voltar(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/login.fxml", null);
    }

    public String getTipoUsuario() {
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

        if (txtSenha.getText().length() < 6) {
            mostrarAlertaErro("Senha fraca", "A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        if (tipo.equals("Aluno")) {
            if (comboBoxOrientador.getValue() == null || comboBoxOrientador.getValue().isEmpty()) {
                mostrarAlertaErro("Orientador obrigatório", "Selecione um orientador para o aluno.");
                return;
            }

            String nomeSelecionado = comboBoxOrientador.getValue();
            String emailOrientador = Orientador.buscarEmailPorNome(nomeSelecionado);

            if (emailOrientador == null) {
                mostrarAlertaErro("Orientador inválido", "Não foi possível encontrar o email do orientador selecionado.");
                return;
            }

            Aluno aluno = new Aluno(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText(),
                    emailOrientador,
                    null  // curso removido do banco de dados
            );
            aluno.cadastrar();
        } else {
            Orientador orientador = new Orientador(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText()
            );
            orientador.cadastrar();

            // Atualiza lista de orientadores após novo cadastro
            comboBoxOrientador.getItems().setAll(Orientador.buscarTodos().keySet());
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

    public void toggleHboxOrientador(ActionEvent actionEvent) {
    }
}
