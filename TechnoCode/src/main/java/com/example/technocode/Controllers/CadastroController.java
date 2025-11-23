package com.example.technocode.Controllers;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.Orientador;
import com.example.technocode.model.ProfessorTG;
import com.example.technocode.model.SolicitacaoOrientacao;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Map;

public class CadastroController {

    @FXML
    private RadioButton radioAluno, radioOrientador, radioProfessorTG;
    @FXML
    private TextField txtNome, txtEmail, txtSenha;
    @FXML
    private HBox hBoxOrientador, hBoxCurso, hBoxDisciplina, hBoxDisciplinaToggle;
    @FXML
    private ComboBox<String> comboBoxOrientador, comboBoxCurso, comboBoxDisciplina;
    @FXML
    private ChoiceBox<String> choiceBoxProfessorTG;
    @FXML
    private RadioButton radioTG1, radioTG2, radioTG1TG2;
    @FXML
    private Button btnCadastrar;

    private ToggleGroup grupoUsuario;
    private ToggleGroup grupoDisciplina;
    private Map<String, String> orientadoresMap;
    private Map<String, String> professoresTGMap; // Map com nome -> email

    @FXML
    private void initialize() {
        // Agrupamento de tipo de usuário
        grupoUsuario = new ToggleGroup();
        radioAluno.setToggleGroup(grupoUsuario);
        radioOrientador.setToggleGroup(grupoUsuario);
        radioProfessorTG.setToggleGroup(grupoUsuario);
        radioAluno.setUserData("Aluno");
        radioOrientador.setUserData("Orientador");
        radioProfessorTG.setUserData("ProfessorTG");

        // Inicialmente invisíveis
        hBoxOrientador.setVisible(false);
        hBoxOrientador.setManaged(false);
        hBoxCurso.setVisible(false);
        hBoxCurso.setManaged(false);
        hBoxDisciplina.setVisible(false);
        hBoxDisciplina.setManaged(false);
        if (hBoxDisciplinaToggle != null) {
            hBoxDisciplinaToggle.setVisible(false);
            hBoxDisciplinaToggle.setManaged(false);
        }

        // Vincula a visibilidade diretamente à seleção do RadioButton "Aluno"
        hBoxOrientador.visibleProperty().bind(radioAluno.selectedProperty());
        hBoxOrientador.managedProperty().bind(radioAluno.selectedProperty());
        hBoxCurso.visibleProperty().bind(radioAluno.selectedProperty());
        hBoxCurso.managedProperty().bind(radioAluno.selectedProperty());
        // hBoxDisciplinaToggle será controlado dinamicamente pelo método verificarDisciplinaProfessor()

        // Vincula a visibilidade do campo Disciplina à seleção do RadioButton "ProfessorTG"
        hBoxDisciplina.visibleProperty().bind(radioProfessorTG.selectedProperty());
        hBoxDisciplina.managedProperty().bind(radioProfessorTG.selectedProperty());

        // Configura toggle group para disciplina
        grupoDisciplina = new ToggleGroup();
        if (radioTG1 != null) {
            radioTG1.setToggleGroup(grupoDisciplina);
            radioTG1.setUserData("TG1");
        }
        if (radioTG2 != null) {
            radioTG2.setToggleGroup(grupoDisciplina);
            radioTG2.setUserData("TG2");
        }
        if (radioTG1TG2 != null) {
            radioTG1TG2.setToggleGroup(grupoDisciplina);
            radioTG1TG2.setUserData("TG1/TG2");
        }

        // Carrega orientadores
        orientadoresMap = Orientador.buscarTodos();
        comboBoxOrientador.getItems().addAll(orientadoresMap.keySet());
        
        // Carrega professores de TG para o ChoiceBox do aluno
        professoresTGMap = ProfessorTG.buscarTodosProfessores();
        if (choiceBoxProfessorTG != null) {
            choiceBoxProfessorTG.getItems().addAll(professoresTGMap.keySet());
            choiceBoxProfessorTG.setOnAction(e -> verificarDisciplinaProfessor());
        }
        
        // Carrega disciplinas simples para o ComboBox do professor de TG
        comboBoxDisciplina.getItems().addAll("TG1", "TG2", "TG1/TG2");

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
            mostrarAlertaErro("Tipo de usuário", "Por favor, selecione um tipo de usuário (Aluno, Orientador ou Professor de TG).");
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
            if (choiceBoxProfessorTG == null || choiceBoxProfessorTG.getValue() == null || choiceBoxProfessorTG.getValue().isEmpty()) {
                mostrarAlertaErro("Professor obrigatório", "Selecione um professor de TG para o aluno.");
                return;
            }

            // Orientador é opcional - verifica se foi selecionado
            String emailOrientador = null;
            String nomeOrientadorSelecionado = comboBoxOrientador.getValue();
            
            if (nomeOrientadorSelecionado != null && !nomeOrientadorSelecionado.isEmpty()) {
                emailOrientador = Orientador.buscarEmailPorNome(nomeOrientadorSelecionado);

            if (emailOrientador == null) {
                mostrarAlertaErro("Orientador inválido", "Não foi possível encontrar o email do orientador selecionado.");
                return;
            }
            }

            // Busca o email do professor selecionado
            String nomeProfessor = choiceBoxProfessorTG.getValue();
            String emailProfessorTG = professoresTGMap.get(nomeProfessor);

            if (emailProfessorTG == null) {
                mostrarAlertaErro("Professor inválido", "Não foi possível encontrar o email do professor selecionado.");
                return;
            }

            // Busca a disciplina do professor
            String disciplinaProfessor = ProfessorTG.buscarDisciplinaPorEmail(emailProfessorTG);
            String disciplinaTG;

            // Se o professor for TG1/TG2, verifica se o aluno escolheu uma disciplina específica
            if ("TG1/TG2".equals(disciplinaProfessor)) {
                if (grupoDisciplina.getSelectedToggle() == null) {
                    mostrarAlertaErro("Disciplina obrigatória", "Selecione uma disciplina (TG1, TG2 ou TG1/TG2).");
                    return;
                }
                disciplinaTG = grupoDisciplina.getSelectedToggle().getUserData().toString();
            } else {
                // Se o professor não for TG1/TG2, usa a disciplina do professor
                disciplinaTG = disciplinaProfessor;
            }

            // Cadastra o aluno (com ou sem orientador, dependendo da escolha)
            Aluno aluno = new Aluno(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText(),
                    emailOrientador, // pode ser null se não foi selecionado
                    emailProfessorTG,
                    disciplinaTG,
                    null  // curso removido do banco de dados
            );
            aluno.cadastrar();
            
            // Cria uma solicitação de orientação apenas se um orientador foi selecionado
            if (emailOrientador != null) {
            SolicitacaoOrientacao solicitacao = new SolicitacaoOrientacao(
                    txtEmail.getText(),
                    emailOrientador
            );
            solicitacao.criar();
            }
        } else if (tipo.equals("Orientador")) {
            Orientador orientador = new Orientador(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText()
            );
            orientador.cadastrar();

            // Atualiza lista de orientadores após novo cadastro
            comboBoxOrientador.getItems().setAll(Orientador.buscarTodos().keySet());
        } else if (tipo.equals("ProfessorTG")) {
            if (comboBoxDisciplina.getValue() == null || comboBoxDisciplina.getValue().isEmpty()) {
                mostrarAlertaErro("Disciplina obrigatória", "Selecione uma disciplina (TG1, TG2 ou TG1/TG2).");
                return;
            }

            ProfessorTG professorTG = new ProfessorTG(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText(),
                    comboBoxDisciplina.getValue()
            );
            professorTG.cadastrar();
        }

        // Faz login automático após cadastro bem-sucedido
        fazerLoginAutomatico(event, txtEmail.getText(), txtSenha.getText(), tipo);
    }
    
    /**
     * Realiza login automático após cadastro e navega para a tela apropriada
     */
    private void fazerLoginAutomatico(ActionEvent event, String email, String senha, String tipo) throws IOException {
        try {
            // Realiza login usando o Connector
            Connector connector = new Connector();
            String tipoUsuario = connector.login(email, senha);
            
            if (tipoUsuario == null || tipoUsuario.isEmpty()) {
                mostrarAlertaErro("Erro no login", "Cadastro realizado, mas não foi possível fazer login automático. Por favor, faça login manualmente.");
                return;
            }
            
            // Define o email logado no LoginController
            LoginController.setEmailLogado(email);
            
            // Navega para a tela apropriada
            switch (tipoUsuario) {
                case "Aluno":
                    NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/Aluno/aluno-principal.fxml", null);
                    break;
                case "Orientador":
                    NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/Orientador/orientador-principal.fxml", null);
                    break;
                case "ProfessorTG":
                    NavigationService.navegarParaTelaCheia(event, "/com/example/technocode/ProfessorTG/professor-tg-principal.fxml", null);
                    break;
                default:
                    mostrarAlertaErro("Erro no login", "Tipo de usuário não reconhecido.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro no login", "Cadastro realizado, mas ocorreu um erro ao fazer login automático. Por favor, faça login manualmente.");
        }
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

    public void toggleHboxOrientador(ActionEvent actionEvent) {
    }
    
    /**
     * Verifica a disciplina do professor selecionado e mostra/oculta o toggle de disciplina
     */
    private void verificarDisciplinaProfessor() {
        if (choiceBoxProfessorTG == null || choiceBoxProfessorTG.getValue() == null) {
            if (hBoxDisciplinaToggle != null) {
                hBoxDisciplinaToggle.setVisible(false);
                hBoxDisciplinaToggle.setManaged(false);
            }
            return;
        }

        String nomeProfessor = choiceBoxProfessorTG.getValue();
        String emailProfessor = professoresTGMap.get(nomeProfessor);
        
        if (emailProfessor != null) {
            String disciplinaProfessor = ProfessorTG.buscarDisciplinaPorEmail(emailProfessor);
            
            // Se o professor for TG1/TG2, mostra o toggle para escolher disciplina
            if ("TG1/TG2".equals(disciplinaProfessor) && hBoxDisciplinaToggle != null) {
                hBoxDisciplinaToggle.setVisible(true);
                hBoxDisciplinaToggle.setManaged(true);
                // Limpa seleção anterior
                grupoDisciplina.selectToggle(null);
            } else {
                // Se não for TG1/TG2, oculta o toggle
                if (hBoxDisciplinaToggle != null) {
                    hBoxDisciplinaToggle.setVisible(false);
                    hBoxDisciplinaToggle.setManaged(false);
                }
            }
        }
    }
}
