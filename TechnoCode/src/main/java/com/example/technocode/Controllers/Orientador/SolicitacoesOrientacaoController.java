package com.example.technocode.Controllers.Orientador;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.SolicitacaoOrientacao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    @FXML
    private TableView<Map<String, String>> tabelaHistorico;

    @FXML
    private TableColumn<Map<String, String>, String> colHistoricoNomeAluno;

    @FXML
    private TableColumn<Map<String, String>, String> colHistoricoEmailAluno;

    @FXML
    private TableColumn<Map<String, String>, String> colHistoricoStatus;

    @FXML
    private TableColumn<Map<String, String>, String> colHistoricoDataSolicitacao;

    @FXML
    private TableColumn<Map<String, String>, String> colHistoricoMensagem;

    private String emailOrientador;

    @FXML
    public void initialize() {
        emailOrientador = LoginController.getEmailLogado();
        
        // Configura colunas da tabela de pendentes
        colNomeAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nome_aluno")));
        colNomeAluno.setCellFactory(col -> criarCellCentralizado());
        
        colEmailAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("aluno")));
        colEmailAluno.setCellFactory(col -> criarCellCentralizado());
        
        colDataSolicitacao.setCellValueFactory(data -> {
            String dataStr = formatarData(data.getValue().get("data_solicitacao"));
            return new SimpleStringProperty(dataStr);
        });
        colDataSolicitacao.setCellFactory(col -> criarCellCentralizado());

        tabelaSolicitacoes.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

        // Configura colunas da tabela de histórico
        colHistoricoNomeAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nome_aluno")));
        colHistoricoNomeAluno.setCellFactory(col -> criarCellCentralizado());
        
        colHistoricoEmailAluno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("aluno")));
        colHistoricoEmailAluno.setCellFactory(col -> criarCellCentralizado());
        
        colHistoricoStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("status")));
        
        colHistoricoDataSolicitacao.setCellValueFactory(data -> {
            String dataStr = formatarData(data.getValue().get("data_solicitacao"));
            return new SimpleStringProperty(dataStr);
        });
        colHistoricoDataSolicitacao.setCellFactory(col -> criarCellCentralizado());
        
        colHistoricoMensagem.setCellValueFactory(data -> {
            String msg = data.getValue().get("mensagem_orientador");
            return new SimpleStringProperty(msg != null ? msg : "");
        });
        colHistoricoMensagem.setCellFactory(col -> criarCellCentralizado());

        // Aplica estilo customizado na coluna de status do histórico com centralização
        colHistoricoStatus.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                setAlignment(Pos.CENTER);
                
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    
                    if ("Aceita".equals(status)) {
                        setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                    } else if ("Recusada".equals(status)) {
                        setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                    } else if ("Pendente".equals(status)) {
                        setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tabelaHistorico.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");

        addButtonToTable();
        carregarSolicitacoes();
        carregarHistorico();
    }

    private void carregarSolicitacoes() {
        List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPendentesPorOrientador(emailOrientador);
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(solicitacoes);
        tabelaSolicitacoes.setItems(items);
    }

    private void carregarHistorico() {
        List<Map<String, String>> historico = SolicitacaoOrientacao.buscarTodasPorOrientador(emailOrientador);
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(historico);
        tabelaHistorico.setItems(items);
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
                setAlignment(Pos.CENTER);
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
    
    /**
     * Formata a data para exibição amigável (ex: "22/11/2024 às 14:30")
     */
    private String formatarData(String dataStr) {
        if (dataStr == null || dataStr.isEmpty()) {
            return "";
        }
        
        try {
            // Tenta parsear como Timestamp (formato do banco: "2024-11-22 14:30:00")
            Timestamp timestamp = Timestamp.valueOf(dataStr);
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
            return formato.format(timestamp);
        } catch (Exception e) {
            // Se não conseguir parsear, retorna a string original
            return dataStr;
        }
    }

    public void recarregarSolicitacoes() {
        carregarSolicitacoes();
        carregarHistorico();
        tabelaSolicitacoes.refresh();
        tabelaHistorico.refresh();
    }
}

