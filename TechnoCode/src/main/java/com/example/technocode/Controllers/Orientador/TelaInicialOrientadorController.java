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

            // A tabela será estilizada via CSS (estilo.css)
            // Configura alinhamento das colunas
            colNome.setStyle("-fx-alignment: CENTER-LEFT;");
            colEmail.setStyle("-fx-alignment: CENTER-LEFT;");
            colMatriculado.setStyle("-fx-alignment: CENTER-LEFT;");
            colAnalisar.setStyle("-fx-alignment: CENTER;");

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

                            private final Button btn = new Button("Analisar");

                            {
                                btn.setStyle(
                                    "-fx-background-color: #3498DB; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-font-size: 12px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-background-radius: 6; " +
                                    "-fx-padding: 6 15 6 15; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);"
                                );
                                
                                // Efeito hover
                                btn.setOnMouseEntered(e -> btn.setStyle(
                                    "-fx-background-color: #2980B9; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-font-size: 12px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-background-radius: 6; " +
                                    "-fx-padding: 6 15 6 15; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(41,128,185,0.4), 6, 0, 0, 3);"
                                ));
                                btn.setOnMouseExited(e -> btn.setStyle(
                                    "-fx-background-color: #3498DB; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-font-size: 12px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-background-radius: 6; " +
                                    "-fx-padding: 6 15 6 15; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);"
                                ));
                                
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
