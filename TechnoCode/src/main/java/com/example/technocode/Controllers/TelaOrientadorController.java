package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
import com.example.technocode.dao.Connector;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TelaOrientadorController {

    @FXML
    private TableView<Map<String, String>> tabelaAlunos;
    @FXML
    private TableColumn<Map<String, String>, String> colNome;
    @FXML
    private TableColumn<Map<String, String>, String> colEmail;
    @FXML
    private TableColumn<Map<String, String>, String> colMatriculado;
    @FXML
    private TableColumn<Map<String, String>, Void> colAnalisar;

    @FXML
    private void voltarLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        try {
            colNome.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get("nome"))
            );
            colEmail.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get("email"))
            );
            colMatriculado.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get("curso"))
            );
            Connector con = new Connector();
            List<Map<String, String>> alunos = con.alunos(LoginController.getEmailLogado());

            tabelaAlunos.getItems().setAll(alunos);

            // Melhora a visualização da tabela
            tabelaAlunos.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");


            // Debug para verificar se os dados foram adicionados
            System.out.println("Número de alunos na tabela: " + tabelaAlunos.getItems().size());

            // Adiciona os botões de "Analisar"
            addButtonToTable();

            // Força a atualização da tabela
            tabelaAlunos.refresh();

            System.out.println("Inicialização concluída com sucesso");
        } catch (Exception e) {
            System.err.println("Erro durante a inicialização: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void addButtonToTable() {
        Callback<TableColumn<Map<String, String>, Void>, TableCell<Map<String, String>, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Map<String, String>, Void> call(final TableColumn<Map<String, String>, Void> param) {
                        return new TableCell<>() {

                            private final Button btn = new Button("analisar");

                            {
                                btn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
                                btn.setOnAction(event -> {
                                    Map<String, String> item = getTableView().getItems().get(getIndex());
                                    // Constrói um Aluno a partir do Map atual da linha
                                    Aluno aluno = new Aluno(
                                            item.getOrDefault("email", null),      // email
                                            null,                                   // emailFatec (não disponível no Map)
                                            item.getOrDefault("nome", null),       // nome
                                            null,                                   // ra (não disponível no Map)
                                            item.getOrDefault("curso", null)       // curso
                                    );
                                    abrirTelaAluno(aluno);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(empty ? null : btn);
                            }
                        };
                    }
                };
        colAnalisar.setCellFactory(cellFactory);
    }
    
    private void abrirTelaAluno(Aluno aluno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-entregasDoAluno.fxml"));
            Parent root = loader.load();

            TelaEntregasDoAluno controller = loader.getController();
            controller.setDadosAluno(aluno);
            // PASSA O E-MAIL DO ALUNO PARA A PRÓXIMA TELA
            controller.setEmailAlunoParaConsulta(aluno.getEmail());

            Stage stage = (Stage) tabelaAlunos.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de análise do aluno: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
