package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AlunosOrientadosController {

    @FXML
    private TableView<Map<String, String>> tabelaAlunos;
    @FXML
    private TableColumn<Map<String, String>, String> colNome;
    @FXML
    private TableColumn<Map<String, String>, String> colEmail;
    @FXML
    private TableColumn<Map<String, String>, String> colDisciplina;
    @FXML
    private TableColumn<Map<String, String>, String> colSecoesAprovadas;
    @FXML
    private TableColumn<Map<String, String>, Void> colAnalisar;

    @FXML
    public void initialize() {
        carregarTabela();
    }

    public void recarregarTabela() {
        carregarTabela();
    }

    private void carregarTabela() {
        try {
            colNome.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get("nome"))
            );
            colEmail.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get("email"))
            );
            colDisciplina.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get("professor_tg"))
            );
            colSecoesAprovadas.setCellValueFactory(data -> {
                String emailAluno = data.getValue().get("email");
                Map<String, Integer> secoes = Aluno.contarSecoesPorAluno(emailAluno);
                int aprovadas = secoes.get("aprovadas");
                int enviadas = secoes.get("enviadas");
                return new SimpleStringProperty(aprovadas + "/" + enviadas);
            });

            List<Map<String, String>> alunos = Aluno.buscarPorOrientador(LoginController.getEmailLogado());

            // Adiciona informações de seções aprovadas para cada aluno
            for (Map<String, String> aluno : alunos) {
                String emailAluno = aluno.get("email");
                Map<String, Integer> secoes = Aluno.contarSecoesPorAluno(emailAluno);
                aluno.put("secoes_aprovadas", String.valueOf(secoes.get("aprovadas")));
                aluno.put("secoes_enviadas", String.valueOf(secoes.get("enviadas")));
            }

            tabelaAlunos.getItems().setAll(alunos);

            // Configura células centralizadas para Nome, Email e Disciplina
            colNome.setCellFactory(col -> criarCellCentralizado());
            colEmail.setCellFactory(col -> criarCellCentralizado());
            colDisciplina.setCellFactory(col -> criarCellCentralizado());

            // Estiliza coluna de seções aprovadas com centralização
            colSecoesAprovadas.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER);
                        // Extrai números para colorir
                        String[] partes = item.split("/");
                        if (partes.length == 2) {
                            try {
                                int aprovadas = Integer.parseInt(partes[0]);
                                int enviadas = Integer.parseInt(partes[1]);
                                if (aprovadas == enviadas && enviadas > 0) {
                                    setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                                } else if (aprovadas > 0) {
                                    setStyle("-fx-text-fill: #3498DB; -fx-font-weight: bold;");
                                } else {
                                    setStyle("-fx-text-fill: #95A5A6;");
                                }
                            } catch (NumberFormatException e) {
                                setStyle("");
                            }
                        }
                    }
                }
            });

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
                                setAlignment(Pos.CENTER);
                            }
                        };
                    }
                };
        colAnalisar.setCellFactory(cellFactory);
    }
    
    /**
     * Cria uma célula centralizada para as colunas da tabela
     */
    private TableCell<Map<String, String>, String> criarCellCentralizado() {
        TableCell<Map<String, String>, String> cell = new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
                setAlignment(Pos.CENTER);
            }
        };
        return cell;
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
}

