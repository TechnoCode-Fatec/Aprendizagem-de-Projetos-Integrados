package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.dao.Connector;
import com.example.technocode.model.SecaoApresentacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;

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
    private void initialize(){
        choiceBoxCurso.getItems().addAll("ADS","BD");
    }

    /**
     * Preenche os campos do formulário com os dados de uma versão anterior
     * para criar uma nova versão baseada na anterior
     */
    public void setDadosVersaoAnterior(String nome, String dataNascimento, String curso, 
                                        String motivacao, String historico, 
                                        String github, String linkedin, String conhecimentos) {
        // Preenche o TextField de nome
        if (nome != null && !nome.isEmpty()) {
            txtNome.setText(nome);
        }
        
        // Preenche o DatePicker com a data de nascimento
        if (dataNascimento != null && !dataNascimento.isEmpty()) {
            try {
                java.time.LocalDate data = java.time.LocalDate.parse(dataNascimento);
                datePickerIdade.setValue(data);
            } catch (Exception e) {
                System.err.println("Erro ao converter data: " + e.getMessage());
            }
        }
        
        // Preenche o ChoiceBox de curso
        if (curso != null && !curso.isEmpty()) {
            choiceBoxCurso.setValue(curso);
        }
        
        // Preenche os TextFields de links
        if (github != null && !github.isEmpty()) {
            txtGithub.setText(github);
        }
        if (linkedin != null && !linkedin.isEmpty()) {
            txtLinkedin.setText(linkedin);
        }
        
        // Preenche os TextAreas
        if (motivacao != null && !motivacao.isEmpty()) {
            txtMotivacao.setText(motivacao);
        }
        if (historico != null && !historico.isEmpty()) {
            txtHistorico.setText(historico);
        }
        if (conhecimentos != null && !conhecimentos.isEmpty()) {
            txtPrincipaisConhecimentos.setText(conhecimentos);
        }
    }

    @FXML
    private void enviarApresentacao(ActionEvent event) {

        if (txtNome.getText().isEmpty() ||
                datePickerIdade.getValue() == null ||
                choiceBoxCurso.getValue() == null ||
                txtMotivacao.getText().isEmpty() ||
                txtHistorico.getText().isEmpty() ||
                txtGithub.getText().isEmpty() ||
                txtLinkedin.getText().isEmpty() ||
                txtPrincipaisConhecimentos.getText().isEmpty()) {

            mostrarAlerta("Campos obrigatórios", "Por favor, preencha todos os campos antes de enviar.");
            return;
        }

        Connector connector = new Connector();
        String emailAluno = LoginController.getEmailLogado();
        
        // Busca a próxima versão disponível automaticamente
        int proximaVersao = connector.getProximaVersaoApresentacao(emailAluno);
        
        // Cria objeto SecaoApresentacao
        SecaoApresentacao secaoApresentacao = new SecaoApresentacao(
                emailAluno,
                txtNome.getText(),
                Date.valueOf(datePickerIdade.getValue()),
                choiceBoxCurso.getValue(),
                proximaVersao,
                txtMotivacao.getText(),
                txtHistorico.getText(),
                txtGithub.getText(),
                txtLinkedin.getText(),
                txtPrincipaisConhecimentos.getText()
        );
        
        // Mantém compatibilidade com o método existente
        connector.cadastrarApresentacao(
                secaoApresentacao.getEmailAluno(),
                secaoApresentacao.getNome(),
                secaoApresentacao.getIdade(),
                secaoApresentacao.getCurso(),
                secaoApresentacao.getVersao(),
                secaoApresentacao.getMotivacao(),
                secaoApresentacao.getHistorico(),
                secaoApresentacao.getLinkGithub(),
                secaoApresentacao.getLinkLinkedin(),
                secaoApresentacao.getPrincipaisConhecimentos()
        );
        
        // Volta para a tela inicial e recarrega as seções
        try {
            voltar(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void voltar (ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Aluno/tela-inicial-aluno.fxml"));
            Parent root = loader.load();
            
            TelaInicialAlunoController controller = loader.getController();
            controller.recarregarSecoes();

            Stage stage;
            if (event != null && event.getSource() != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // Se chamado programaticamente, pega a janela atual
                stage = (Stage) txtNome.getScene().getWindow();
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }
    public void mostrarAlerta (String titulo, String mensagem){
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensagem);
            alerta.showAndWait();
        }

}

