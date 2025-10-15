package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
import com.example.technocode.Seção;
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

import java.io.IOException;

public class TelaEntregasDoAluno {

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
    private Label nomeSecao;
    @FXML
    private Label descricaoSecao;
    @FXML
    private Label statusSecao;


    @FXML private TableView<Seção> tabelaSecao;
    @FXML private TableColumn<Seção, String> colNomeSecao;
    @FXML private TableColumn<Seção, String> colDescricao;
    @FXML private TableColumn<Seção, String> colStatus;
    @FXML private TableColumn<Seção, Void> colAnalisar;

    @FXML
    private void voltarTelaOrientador(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-analisarAluno-orientador.fxml"));
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

    @FXML
    public void initialize() {
        try {
            System.out.println("Iniciando inicialização da tabela...");

            // Associa as colunas aos atributos da classe Aluno
            colNomeSecao.setCellValueFactory(new PropertyValueFactory<>("nomeSecao"));
            colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Melhora a visualização da tabela
            tabelaSecao.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

            // Adiciona os dados antes dos botões
            tabelaSecao.getItems().addAll(
                    new Seção("Seção 1", "Apresentação", "Concluido"),
                    new Seção("Seção 2", "API 1", "Em andamento"),
                    new Seção("Seção 3", "API 2", "Bloqueada")
            );

            // Debug para verificar se os dados foram adicionados
            System.out.println("Número de alunos na tabela: " + tabelaSecao.getItems().size());

            // Adiciona os botões de "Analisar"
            addButtonToTable();

            // Força a atualização da tabela
            tabelaSecao.refresh();

            System.out.println("Inicialização concluída com sucesso");
        } catch (Exception e) {
            System.err.println("Erro durante a inicialização: " + e.getMessage());
            e.printStackTrace();
        }
    }

        private void addButtonToTable() {
            Callback<TableColumn<Seção, Void>, TableCell<Seção, Void>> cellFactory = new Callback<>() {
                @Override
                public TableCell<Seção, Void> call(final TableColumn<Seção, Void> param) {
                    return new TableCell<>() {

                        private final Button btn = new Button("Analisar");

                        {
                            btn.setOnAction(event -> {
                                Seção secao = getTableView().getItems().get(getIndex());
                                abrirSecao(secao);
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


        private void abrirSecao(Seção secao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-analisarSecao.fxml"));
            Parent root = loader.load();

            TelaEntregasDoAluno controller = loader.getController();
            controller.setDadosSecao(secao);

            Stage stage = (Stage) tabelaSecao.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de análise da seção: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setDadosSecao(Seção secao) {
        if (secao != null) {
            nomeSecao.setText(secao.getNomeSecao());
            descricaoSecao.setText(secao.getDescricao());
            statusSecao.setText(secao.getStatus());
        }
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
}
