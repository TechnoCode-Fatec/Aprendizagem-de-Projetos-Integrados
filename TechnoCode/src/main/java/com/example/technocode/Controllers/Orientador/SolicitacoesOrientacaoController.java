package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.SolicitacaoOrientacao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

public class SolicitacoesOrientacaoController {

    @FXML
    private TableView<Map<String, String>> tabelaSolicitacoes;

    @FXML
    private TableColumn<Map<String, String>, String> colNomeAluno;

    @FXML
    private TableColumn<Map<String, String>, String> colEmailAluno;

    @FXML
    private TableColumn<Map<String, String>, String> colDataSolicitacao;

    @FXML
    private TableColumn<Map<String, String>, Void> colAcoes;

    private String emailOrientador;

    @FXML
    public void initialize() {
        emailOrientador = LoginController.getEmailLogado();
        
        // Configura colunas da tabela
        colNomeAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nome_aluno")));
        colEmailAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("aluno")));
        colDataSolicitacao.setCellValueFactory(data -> {
            String dataStr = data.getValue().get("data_solicitacao");
            if (dataStr != null && dataStr.length() > 19) {
                return new SimpleStringProperty(dataStr.substring(0, 19));
            }
            return new SimpleStringProperty(dataStr != null ? dataStr : "");
        });

        tabelaSolicitacoes.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

        addButtonToTable();
        carregarSolicitacoes();
    }

    private void carregarSolicitacoes() {
        List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPendentesPorOrientador(emailOrientador);
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(solicitacoes);
        tabelaSolicitacoes.setItems(items);
    }

    private void addButtonToTable() {
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnResponder = new Button("Responder");
            {
                btnResponder.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
                btnResponder.setOnAction(event -> {
                    Map<String, String> item = getTableView().getItems().get(getIndex());
                    int idSolicitacao = Integer.parseInt(item.get("id"));
                    abrirTelaResponder(idSolicitacao);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnResponder);
            }
        });
    }

    private void abrirTelaResponder(int idSolicitacao) {
        // Navega usando o OrientadorPrincipalController
        OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
            "/com/example/technocode/Orientador/responder-solicitacao.fxml",
            c -> {
                if (c instanceof ResponderSolicitacaoController) {
                    ((ResponderSolicitacaoController) c).setSolicitacaoId(idSolicitacao);
                }
            }
        );
    }

    @FXML
    private void voltar(ActionEvent event) {
        OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
            "/com/example/technocode/Orientador/tela-inicial-orientador.fxml",
            c -> {
                if (c instanceof TelaInicialOrientadorController) {
                    ((TelaInicialOrientadorController) c).recarregarTabelaAlunos();
                }
            }
        );
    }

    public void recarregarSolicitacoes() {
        carregarSolicitacoes();
        tabelaSolicitacoes.refresh();
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

