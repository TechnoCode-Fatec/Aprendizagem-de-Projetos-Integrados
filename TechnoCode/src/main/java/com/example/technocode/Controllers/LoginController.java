package com.example.technocode.Controllers;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private ComboBox<String> cbEmail; // ⬅️ substitui o TextField

    @FXML
    private PasswordField txtSenha;

    @FXML
    private CheckBox lembreDeMim;

    @FXML
    private TextField txtSenhaVisivel;

    @FXML
    private Button btnToggleSenha;

    @FXML
    private Button btnEntrar;

    private static String emailLogado;

    // 🔹 Preferências do sistema
    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);

    private static final String KEY_EMAILS = "emails_salvos";
    private static final String KEY_SENHA = "senha_salva";
    private static final String KEY_LEMBRAR = "lembrar";

    public static String getEmailLogado() {
        return emailLogado;
    }

    // =========================================================
    //                      LOGIN
    // =========================================================
    // =========================================================
//                      LOGIN
// =========================================================
    public void login(ActionEvent event) throws IOException {

        String email = cbEmail.getEditor().getText();
        String senha = obterSenhaAtual();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlertaErro("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return;
        }

        if (!validarEmail(email)) {
            mostrarAlertaErro("Email inválido", "Por favor, insira um email válido.");
            return;
        }

        Connector connector = new Connector();
        String tipo = connector.login(email, senha);

        if (tipo == null || tipo.isEmpty()) {
            mostrarAlertaErro("Usuário não encontrado", "Email ou senha incorretos. Por favor, tente novamente.");
            return;
        }

        // ✔ E-mail do usuário logado
        emailLogado = email;

        // ✔ Salva email apenas se lembrar-me estiver marcado
        if (lembreDeMim.isSelected()) {
            salvarEmail(email);
        }

        // ❌ Não salvar senha
        prefs.remove(KEY_SENHA);
        prefs.putBoolean(KEY_LEMBRAR, lembreDeMim.isSelected());

        // 🔹 Redirecionar usuário
        switch (tipo) {
            case "Aluno":
                NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/Aluno/aluno-principal.fxml", null);
                return;
            case "Orientador":
                NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/Orientador/orientador-principal.fxml", null);
                return;
            case "ProfessorTG":
                NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/ProfessorTG/professor-tg-principal.fxml", null);
                return;
        }
    }


    // =========================================================
//                  HISTÓRICO DE EMAILS
// =========================================================
    private void carregarEmailsSalvos() {
        String emailsStr = prefs.get(KEY_EMAILS, "");

        if (!emailsStr.isEmpty()) {
            List<String> emails = Arrays.asList(emailsStr.split(";"));
            cbEmail.getItems().addAll(emails);
        }
    }

    private void salvarEmail(String email) {
        String emailsStr = prefs.get(KEY_EMAILS, "");

        Set<String> lista = new LinkedHashSet<>();
        if (!emailsStr.isEmpty()) {
            lista.addAll(Arrays.asList(emailsStr.split(";")));
        }

        lista.add(email); // evita duplicados
        prefs.put(KEY_EMAILS, String.join(";", lista));
    }


    // =========================================================
    //                SENHA VISÍVEL / INVISÍVEL
    // =========================================================

    private boolean senhaVisivel = false;

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

    private String obterSenhaAtual() {
        return senhaVisivel ? txtSenhaVisivel.getText() : txtSenha.getText();
    }

    // =========================================================
    //               LEMBRAR-ME (CARREGA AO ABRIR)
    // =========================================================
    private void carregarLembranca() {
        boolean lembrar = prefs.getBoolean(KEY_LEMBRAR, false);
        lembreDeMim.setSelected(lembrar);

        if (!lembrar) return;

        // Preenche o e-mail salvo no ComboBox
        String emailsStr = prefs.get(KEY_EMAILS, "");
        if (!emailsStr.isEmpty()) {
            List<String> emails = Arrays.asList(emailsStr.split(";"));
            cbEmail.getItems().setAll(emails);
            cbEmail.getEditor().setText(emails.get(emails.size() - 1)); // último usado
        }

        // ⚠ NÃO CARREGA A SENHA
        txtSenha.clear();
    }


    // =========================================================
    //                OUTROS MÉTODOS
    // =========================================================

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

    // =========================================================
    //                    INITIALIZE
    // =========================================================
    @FXML
    private void initialize() {

        // Ícones do olho
        iconMostrar.setFitWidth(16);
        iconMostrar.setFitHeight(16);
        iconOcultar.setFitWidth(16);
        iconOcultar.setFitHeight(16);
        btnToggleSenha.setGraphic(iconMostrar);

        // Enter faz login apenas ao DIGITAR
        cbEmail.getEditor().setOnAction(e -> btnEntrar.fire());
        txtSenha.setOnAction(e -> btnEntrar.fire());
        txtSenhaVisivel.setOnAction(e -> btnEntrar.fire());

        // 🔹 Carregar histórico
        carregarEmailsSalvos();

        // ⚠ LEMBRAR-ME só depois da tela pronta
        javafx.application.Platform.runLater(this::carregarLembranca);
    }


}
