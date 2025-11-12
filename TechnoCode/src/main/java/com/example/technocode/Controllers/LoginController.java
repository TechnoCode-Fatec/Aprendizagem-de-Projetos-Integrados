package com.example.technocode.Controllers;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtSenhaVisivel;

    @FXML
    private Button btnToggleSenha;

    // 🔹 ADIÇÃO — referencie o botão "Entrar" do FXML (precisa ter fx:id="btnEntrar")
    @FXML
    private Button btnEntrar;

    private static String emailLogado;

    public static String getEmailLogado() {
        return emailLogado;
    }

    public void login(ActionEvent event) throws IOException {
        String senha = obterSenhaAtual();

        if (txtEmail.getText().isEmpty() || senha.isEmpty()) {
            mostrarAlertaErro("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return;
        }

        if (!validarEmail(txtEmail.getText())) {
            mostrarAlertaErro("Email inválido", "Por favor, insira um email válido.");
            return;
        }

        Connector connector = new Connector();
        String tipo = connector.login(txtEmail.getText(), senha);

        if (tipo == null || tipo.isEmpty()) {
            mostrarAlertaErro("Usuário não encontrado", "Email ou senha incorretos. Por favor, tente novamente.");
            return;
        } else {
            emailLogado = txtEmail.getText();
        }
        if (tipo.equals("Aluno")) {
            NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/Aluno/aluno-principal.fxml", null);
            return;
        }
        if (tipo.equals("Orientador")) {
            NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/Orientador/orientador-principal.fxml", null);
            return;
        }
        if (tipo.equals("ProfessorTG")) {
            // Verifica se existe tela para professor de TG, caso contrário usa dashboard
            NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/dashboard-professor-tg.fxml", null);
            return;
        }
    }

    private String obterSenhaAtual() {
        if (senhaVisivel) {
            return txtSenhaVisivel.getText();
        } else {
            return txtSenha.getText();
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
        NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/cadastro.fxml", null);
    }

    private boolean senhaVisivel = false;

    // Ícones
    private final ImageView iconMostrar = new ImageView(
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/technocode/imagens/Revelar.png")))
    );
    private final ImageView iconOcultar = new ImageView(
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/technocode/imagens/Ocultar.png")))
    );

    @FXML
    private void toggleSenha() {
        if (senhaVisivel) {
            txtSenha.setText(txtSenhaVisivel.getText());
            txtSenhaVisivel.setVisible(false);
            txtSenhaVisivel.setManaged(false);
            txtSenha.setVisible(true);
            txtSenha.setManaged(true);
            btnToggleSenha.setGraphic(iconMostrar);
        } else {
            txtSenhaVisivel.setText(txtSenha.getText());
            txtSenha.setVisible(false);
            txtSenha.setManaged(false);
            txtSenhaVisivel.setVisible(true);
            txtSenhaVisivel.setManaged(true);
            btnToggleSenha.setGraphic(iconOcultar);
        }
        senhaVisivel = !senhaVisivel;
    }

    @FXML
    private void initialize() {
        // Define tamanho fixo dos ícones
        iconMostrar.setFitWidth(16);
        iconMostrar.setFitHeight(16);
        iconOcultar.setFitWidth(16);
        iconOcultar.setFitHeight(16);

        iconMostrar.setPreserveRatio(true);
        iconOcultar.setPreserveRatio(true);
        iconMostrar.setSmooth(true);
        iconOcultar.setSmooth(true);

        btnToggleSenha.setGraphic(iconMostrar);

        // ADIÇÃO — faz a tecla Enter acionar o botão "Entrar"
        txtEmail.setOnAction(e -> btnEntrar.fire());
        txtSenha.setOnAction(e -> btnEntrar.fire());
        txtSenhaVisivel.setOnAction(e -> btnEntrar.fire());
    }
}
