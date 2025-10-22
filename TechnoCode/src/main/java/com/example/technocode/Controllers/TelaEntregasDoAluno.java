package com.example.technocode.Controllers;

import com.example.technocode.Objetos.Aluno;
import com.example.technocode.Objetos.Seção;
import com.example.technocode.dao.Connector;
import javafx.beans.property.SimpleStringProperty;

import java.util.Map;
import java.util.List;
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

public class TelaEntregasDoAluno {

    @FXML
    private Label nomeAluno;
    @FXML
    private Label emailAluno;
    @FXML
    private Label cursoAluno;
    @FXML
    private Label nomeSecao;
    @FXML
    private Label descricaoSecao;
    @FXML
    private Label statusSecao;

    // GUARDA O E-MAIL DO ALUNO SELECIONADO
    private String emailAlunoParaConsulta;

    public void setEmailAlunoParaConsulta(String email) {
        this.emailAlunoParaConsulta = email;
        // se initialize já rodou, podemos carregar os dados agora
        carregarSecoesDoAluno();
    }

    @FXML private TableView<Map<String, String>> tabelaSecao;
    @FXML private TableColumn<Map<String, String>, String> colNomeSecao; // "id"
    @FXML private TableColumn<Map<String, String>, String> colDescricao; // "empresa"
    @FXML private TableColumn<Map<String, String>, Void> colAnalisar;

    @FXML
    public void initialize() {
        try {
            colNomeSecao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("id")));
            colDescricao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("empresa")));

            tabelaSecao.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

            addButtonToTable();

            // só carrega dados se já tiver o e-mail (pode ser setado após o load)
            carregarSecoesDoAluno();

            tabelaSecao.refresh();
        } catch (Exception e) {
            System.err.println("Erro durante a inicialização: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarSecoesDoAluno() {
        if (emailAlunoParaConsulta == null || emailAlunoParaConsulta.isBlank()) {
            // ainda não foi informado pelo controller anterior
            return;
        }
        Connector connector = new Connector();
        List<Map<String,String>> secoesApi = connector.secoesApi(emailAlunoParaConsulta);
        System.out.println("Carregando seções para: " + emailAlunoParaConsulta + " -> " + secoesApi.size() + " itens");
        tabelaSecao.getItems().setAll(secoesApi);
    }

    private void addButtonToTable() {
        colAnalisar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("analisar");
            {
                btn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
                btn.setOnAction(event -> {
                    Map<String, String> item = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/tela-secoesenviadasAPI.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) tabelaSecao.getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
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




    public void setDadosAluno(Aluno aluno) {
        if (aluno != null) {
            nomeAluno.setText(aluno.getNome());
            emailAluno.setText(aluno.getEmail());
            cursoAluno.setText(aluno.getCurso());
        }
    }

}
