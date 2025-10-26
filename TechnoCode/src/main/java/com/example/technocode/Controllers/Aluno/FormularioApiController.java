package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class FormularioApiController {
    @FXML
    private ChoiceBox<String> choiceBoxSemestre,choiceBoxSemestreDoCurso;
    @FXML
    private TextField txtAno, txtEmpresa, txtLinkRepositorio;
    @FXML
    private TextArea txtProblema, txtSolucao, txtTecnologias, txtContribuicoes, txtHardSkills, txtSoftSkills;

    @FXML
    private void initialize(){
        choiceBoxSemestre.getItems().addAll("1","2");
        choiceBoxSemestreDoCurso.getItems().addAll("1º Semestre","2º Semestre","3º Semestre","4º Semestre","5º Semestre","6º Semestre");
    }

    /**
     * Preenche os campos do formulário com os dados de uma versão anterior
     * para criar uma nova versão baseada na anterior
     */
    public void setDadosVersaoAnterior(String semestreCurso, String ano, String semestre, 
                                        String empresa, String linkRepositorio, 
                                        String problema, String solucao, 
                                        String tecnologias, String contribuicoes, 
                                        String hardSkills, String softSkills) {
        // Preenche os ChoiceBoxes
        if (semestreCurso != null && !semestreCurso.isEmpty()) {
            choiceBoxSemestreDoCurso.setValue(semestreCurso);
        }
        if (semestre != null && !semestre.isEmpty()) {
            choiceBoxSemestre.setValue(semestre);
        }
        
        // Preenche os TextFields
        if (ano != null && !ano.isEmpty()) {
            txtAno.setText(ano);
        }
        if (empresa != null && !empresa.isEmpty()) {
            txtEmpresa.setText(empresa);
        }
        if (linkRepositorio != null && !linkRepositorio.isEmpty()) {
            txtLinkRepositorio.setText(linkRepositorio);
        }
        
        // Preenche os TextAreas
        if (problema != null && !problema.isEmpty()) {
            txtProblema.setText(problema);
        }
        if (solucao != null && !solucao.isEmpty()) {
            txtSolucao.setText(solucao);
        }
        if (tecnologias != null && !tecnologias.isEmpty()) {
            txtTecnologias.setText(tecnologias);
        }
        if (contribuicoes != null && !contribuicoes.isEmpty()) {
            txtContribuicoes.setText(contribuicoes);
        }
        if (hardSkills != null && !hardSkills.isEmpty()) {
            txtHardSkills.setText(hardSkills);
        }
        if (softSkills != null && !softSkills.isEmpty()) {
            txtSoftSkills.setText(softSkills);
        }
    }
    @FXML
    private void enviarSecaoApi(ActionEvent event) {
        if ((choiceBoxSemestre.getValue() == null) ||
                (choiceBoxSemestreDoCurso.getValue() == null) ||
                txtAno.getText().isEmpty() ||
                txtEmpresa.getText().isEmpty() ||
                txtLinkRepositorio.getText().isEmpty() ||
                txtProblema.getText().isEmpty() ||
                txtSolucao.getText().isEmpty() ||
                txtTecnologias.getText().isEmpty() ||
                txtContribuicoes.getText().isEmpty() ||
                txtHardSkills.getText().isEmpty() ||
                txtSoftSkills.getText().isEmpty()) {

            mostrarAlerta("Campos obrigatórios", "Por favor, preencha todos os campos antes de enviar.");
            return;
        }
        Connector connector = new Connector();
        String emailAluno = LoginController.getEmailLogado();
        
        // Busca a próxima versão disponível automaticamente
        int proximaVersao = connector.getProximaVersaoApi(emailAluno, choiceBoxSemestreDoCurso.getValue(), Integer.parseInt(txtAno.getText()), choiceBoxSemestre.getValue());
        
        connector.cadastrarSessaoApi(emailAluno, choiceBoxSemestreDoCurso.getValue(), Integer.parseInt(txtAno.getText()), choiceBoxSemestre.getValue(), proximaVersao,
                txtEmpresa.getText(), txtProblema.getText(), txtSolucao.getText(),txtLinkRepositorio.getText(),txtTecnologias.getText(),
                txtContribuicoes.getText(),txtHardSkills.getText(),txtSoftSkills.getText());
        System.out.println("Cadastrado com sucesso - Versão: " + proximaVersao);
        
        // Volta para a tela inicial e recarrega as seções
        try {
            voltar(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarAlerta (String titulo, String mensagem){
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();    }

    public void voltar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-inicial-aluno.fxml"));
        Parent root = loader.load();
        
        TelaInicialAlunoController controller = loader.getController();
        controller.recarregarSecoes();

        Stage stage;
        if (event != null && event.getSource() != null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            // Se chamado programaticamente, pega a janela atual
            stage = (Stage) txtEmpresa.getScene().getWindow();
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
