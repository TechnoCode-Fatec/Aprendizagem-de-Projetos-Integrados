package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class TelaAnalisarAlunoOrientador {
    @FXML
    private Label nomeAluno;
    @FXML
    private Label raAluno;
    @FXML
    private Label emailAluno;
    @FXML
    private Label emailFatecAluno;
    @FXML
    private Label cursoAluno;
    @FXML
    private Label labelTitulo;
    @FXML
    private Button btnVerApresentacoes;
    @FXML
    private Button btnVerEntregas;

    @FXML
    public void initialize() {
        // Inicialização dos componentes, se necessário
//        btnVerApresentacoes.setOnAction(this::verApresentacoes);
    }

    public void setDadosAluno(Aluno aluno) {
        if (aluno != null) {
            nomeAluno.setText(aluno.getNome());
            raAluno.setText(aluno.getRa());
            emailAluno.setText(aluno.getEmail());
            emailFatecAluno.setText(aluno.getEmailFatec());
            cursoAluno.setText(aluno.getCurso());
        }
    }

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-inicial-orientador.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela do orientador: " + e.getMessage());
            throw e;
        }
    }

//    @FXML
//    private void verApresentacoes(ActionEvent event) {
//        try {
//            // TODO: Implementar a lógica para ver apresentações
//            System.out.println("Abrindo visualização de apresentações...");
//        } catch (Exception e) {
//            System.err.println("Erro ao abrir apresentações: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    //    @FXML
//    private void verEntregas(ActionEvent event) {
//        try {
//            // TODO: Implementar a lógica para ver APIs
//            System.out.println("Abrindo visualização de Entregas...");
//        } catch (Exception e) {
//            System.err.println("Erro ao abrir APIs: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    @FXML
    private void verEntregas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-entregasDoAluno.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de entregas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}