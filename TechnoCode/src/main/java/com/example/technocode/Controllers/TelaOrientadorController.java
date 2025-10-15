package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
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

public class TelaOrientadorController {

    @FXML private TableView<Aluno> tabelaAlunos;
    @FXML private TableColumn<Aluno, String> colEmail;
    @FXML private TableColumn<Aluno, String> colEmailFatec;
    @FXML private TableColumn<Aluno, String> colNome;
    @FXML private TableColumn<Aluno, String> colRA;
    @FXML private TableColumn<Aluno, String> colMatriculado;
    @FXML private TableColumn<Aluno, Void> colAnalisar;

    @FXML
    private void voltarLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/login-orientador.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("Iniciando inicialização da tabela...");

            // Associa as colunas aos atributos da classe Aluno
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colEmailFatec.setCellValueFactory(new PropertyValueFactory<>("emailFatec"));
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colRA.setCellValueFactory(new PropertyValueFactory<>("ra"));
            colMatriculado.setCellValueFactory(new PropertyValueFactory<>("curso"));

            // Melhora a visualização da tabela
            tabelaAlunos.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

            // Adiciona os dados antes dos botões
            tabelaAlunos.getItems().addAll(
                    new Aluno("joao@gmail.com", "joao@fatec.sp.gov.br", "João Silva", "12345", "TG1"),
                    new Aluno("maria@gmail.com", "maria@fatec.sp.gov.br", "Maria Souza", "67890", "TG2"),
                    new Aluno("gabrielrocha@gmail.com", "gabriel.borges4@fatec.com.br", "Gabriel Rocha", "123456", "TG1 e TG2")
            );

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
        Callback<TableColumn<Aluno, Void>, TableCell<Aluno, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Aluno, Void> call(final TableColumn<Aluno, Void> param) {
                return new TableCell<>() {

                    private final Button btn = new Button("Analisar");

                    {
                        btn.setOnAction(event -> {
                            Aluno aluno = getTableView().getItems().get(getIndex());
                            abrirTelaAluno(aluno);
                        });

                        btn.setStyle("""
                            -fx-background-color: #5e5555;
                            -fx-text-fill: white;
                            -fx-font-weight: bold;
                            -fx-cursor: hand;
                            -fx-background-radius: 5;
                        """);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colAnalisar.setCellFactory(cellFactory);
    }

    private void abrirTelaAluno(Aluno aluno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-analisarAluno-orientador.fxml"));
            Parent root = loader.load();

            TelaAnalisarAlunoOrientador controller = loader.getController();
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
