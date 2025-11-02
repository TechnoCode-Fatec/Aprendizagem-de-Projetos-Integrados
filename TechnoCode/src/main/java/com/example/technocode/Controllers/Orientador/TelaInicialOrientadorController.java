package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TelaInicialOrientadorController {

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
        NavigationService.navegarPara(event, "/com/example/technocode/login.fxml");
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
            List<Map<String, String>> alunos = Aluno.buscarPorOrientador(LoginController.getEmailLogado());

            tabelaAlunos.getItems().setAll(alunos);

            // Melhora a visualização da tabela
            tabelaAlunos.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

            // Adiciona os botões de "Analisar"
            addButtonToTable();

            // Força a atualização da tabela
            tabelaAlunos.refresh();
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
                                    String emailAluno = item.getOrDefault("email", null);
                                    abrirTelaAluno(emailAluno);
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
    
    private void abrirTelaAluno(String emailAluno) {
        try {
            final String emailFinal = emailAluno;
            NavigationService.navegarPara(tabelaAlunos, "/com/example/technocode/Orientador/entregas-do-aluno.fxml",
                controller -> {
                    if (controller instanceof EntregasDoAlunoController) {
                        EntregasDoAlunoController entregasController = (EntregasDoAlunoController) controller;
                        entregasController.setDadosAluno(emailFinal);
                        entregasController.setEmailAlunoParaConsulta(emailFinal);
                    }
                });
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de análise do aluno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método público para recarregar a tabela de alunos
    public void recarregarTabelaAlunos() {
        try {
            List<Map<String, String>> alunos = Aluno.buscarPorOrientador(LoginController.getEmailLogado());

            tabelaAlunos.getItems().setAll(alunos);
            tabelaAlunos.refresh();
        } catch (Exception e) {
            System.err.println("Erro ao recarregar tabela de alunos: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
