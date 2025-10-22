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
            List<Map<String, String>> alunos = con.alunos("mineda@");

            tabelaAlunos.getItems().setAll(alunos);

            // Melhora a visualização da tabela
            tabelaAlunos.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");


            // Debug para verificar se os dados foram adicionados
            System.out.println("Número de alunos na tabela: " + tabelaAlunos.getItems().size());

            // Adiciona os botões de "Analisar"
//            addButtonToTable();

            // Força a atualização da tabela
            tabelaAlunos.refresh();

            System.out.println("Inicialização concluída com sucesso");
        } catch (Exception e) {
            System.err.println("Erro durante a inicialização: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    private void addButtonToTable() {
//        Callback<TableColumn<Aluno, Void>, TableCell<Aluno, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Aluno, Void> call(final TableColumn<Aluno, Void> param) {
//                return new TableCell<>() {
//
//                    private final Button btn = new Button("Analisar");
//
//                    {
//                        btn.setOnAction(event -> {
//                            Aluno aluno = getTableView().getItems().get(getIndex());
//                            abrirTelaAluno(aluno);
//                        });
//
//                        btn.setStyle("""
//                            -fx-background-color: #5e5555;
//                            -fx-text-fill: white;
//                            -fx-font-weight: bold;
//                            -fx-cursor: hand;
//                            -fx-background-radius: 5;
//                        """);
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            setGraphic(btn);
//                        }
//                    }
//                };
//            }
//        };
//        colAnalisar.setCellFactory(cellFactory);
//    }

    private void abrirTelaAluno(Aluno aluno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-entregasDoAluno.fxml"));
            Parent root = loader.load();

            TelaEntregasDoAluno controller = loader.getController();
            controller.setDadosAluno(aluno);

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
