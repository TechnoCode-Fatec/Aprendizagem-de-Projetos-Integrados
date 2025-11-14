package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.Orientador;
import com.example.technocode.model.SolicitacaoOrientacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public class SolicitarOrientacaoController {

    @FXML
    private ComboBox<String> comboBoxOrientadores;

    @FXML
    private TableView<Map<String, String>> tabelaSolicitacoes;

    @FXML
    private TableColumn<Map<String, String>, String> colOrientador;

    @FXML
    private TableColumn<Map<String, String>, String> colStatus;

    @FXML
    private TableColumn<Map<String, String>, String> colDataSolicitacao;

    @FXML
    private TableColumn<Map<String, String>, String> colMensagem;

    @FXML
    private Label labelStatusAtual;

    @FXML
    private Label labelMensagemInfo;

    @FXML
    private javafx.scene.control.Button btnSolicitar;

    private String emailAluno;
    private boolean temOrientador = false;

    @FXML
    public void initialize() {
        emailAluno = LoginController.getEmailLogado();
        
        // Configura colunas da tabela
        colOrientador.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nome_orientador")));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("status")));
        colDataSolicitacao.setCellValueFactory(data -> {
            String dataStr = data.getValue().get("data_solicitacao");
            if (dataStr != null && dataStr.length() > 19) {
                return new SimpleStringProperty(dataStr.substring(0, 19));
            }
            return new SimpleStringProperty(dataStr != null ? dataStr : "");
        });
        colMensagem.setCellValueFactory(data -> {
            String msg = data.getValue().get("mensagem_orientador");
            return new SimpleStringProperty(msg != null ? msg : "");
        });

        // Aplica estilo customizado na coluna de status
        colStatus.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                
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

        carregarOrientadores();
        carregarSolicitacoes();
        atualizarStatusAtual();
    }

    private void carregarOrientadores() {
        Map<String, String> orientadores = Orientador.buscarTodos();
        comboBoxOrientadores.getItems().clear();
        comboBoxOrientadores.getItems().addAll(orientadores.keySet());
    }

    private void carregarSolicitacoes() {
        List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPorAluno(emailAluno);
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(solicitacoes);
        tabelaSolicitacoes.setItems(items);
    }

    private void atualizarStatusAtual() {
        // Busca o orientador atual do aluno (se tiver)
        // Precisamos buscar diretamente do banco pois buscarDadosPorEmail não retorna orientador
        try {
            java.sql.Connection conn = new com.example.technocode.model.dao.Connector().getConnection();
            String sql = "SELECT orientador FROM aluno WHERE email = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            java.sql.ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String orientadorEmail = rs.getString("orientador");
                if (orientadorEmail != null && !orientadorEmail.isEmpty()) {
                    temOrientador = true;
                    Map<String, String> dadosOrientador = Orientador.buscarDadosPorEmail(orientadorEmail);
                    String nomeOrientador = dadosOrientador.get("nome");
                    labelStatusAtual.setText("Orientador atual: " + nomeOrientador);
                    labelStatusAtual.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                    
                    // Mostra mensagem informativa
                    if (labelMensagemInfo != null) {
                        labelMensagemInfo.setText("Você já possui um orientador atribuído. Não é possível criar novas solicitações.");
                        labelMensagemInfo.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    }
                    
                    // Desabilita o comboBox e botão se já tiver orientador
                    comboBoxOrientadores.setDisable(true);
                    if (btnSolicitar != null) {
                        btnSolicitar.setDisable(true);
                        btnSolicitar.setStyle("-fx-background-color: #95A5A6; -fx-background-radius: 8; -fx-cursor: default;");
                    }
                } else {
                    temOrientador = false;
                    labelStatusAtual.setText("Nenhum orientador atribuído");
                    labelStatusAtual.setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;");
                    
                    // Limpa mensagem informativa
                    if (labelMensagemInfo != null) {
                        labelMensagemInfo.setText("");
                    }
                    
                    // Habilita o comboBox e botão se não tiver orientador
                    comboBoxOrientadores.setDisable(false);
                    if (btnSolicitar != null) {
                        btnSolicitar.setDisable(false);
                        btnSolicitar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.3), 5, 0, 0, 2);");
                    }
                }
            } else {
                temOrientador = false;
                labelStatusAtual.setText("Nenhum orientador atribuído");
                labelStatusAtual.setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;");
                
                // Limpa mensagem informativa
                if (labelMensagemInfo != null) {
                    labelMensagemInfo.setText("");
                }
                
                // Habilita o comboBox e botão se não tiver orientador
                comboBoxOrientadores.setDisable(false);
                if (btnSolicitar != null) {
                    btnSolicitar.setDisable(false);
                    btnSolicitar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.3), 5, 0, 0, 2);");
                }
            }
            conn.close();
        } catch (Exception e) {
            temOrientador = false;
            labelStatusAtual.setText("Erro ao carregar status");
            labelStatusAtual.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void solicitarOrientacao(ActionEvent event) {
        // Verifica se o aluno já tem orientador definido
        if (temOrientador) {
            mostrarAlertaErro("Orientador já atribuído", "Você já possui um orientador atribuído. Não é possível criar novas solicitações.");
            return;
        }

        if (comboBoxOrientadores.getValue() == null || comboBoxOrientadores.getValue().isEmpty()) {
            mostrarAlertaErro("Selecione um orientador", "Por favor, selecione um orientador da lista.");
            return;
        }

        String nomeOrientador = comboBoxOrientadores.getValue();
        String emailOrientador = Orientador.buscarEmailPorNome(nomeOrientador);

        if (emailOrientador == null) {
            mostrarAlertaErro("Erro", "Não foi possível encontrar o email do orientador selecionado.");
            return;
        }

        // Verifica se já existe uma solicitação pendente
        if (SolicitacaoOrientacao.existeSolicitacaoPendente(emailAluno, emailOrientador)) {
            mostrarAlertaErro("Solicitação já existe", "Você já possui uma solicitação pendente para este orientador.");
            return;
        }

        // Cria a solicitação
        SolicitacaoOrientacao solicitacao = new SolicitacaoOrientacao(emailAluno, emailOrientador);
        solicitacao.criar();

        mostrarAlertaSucesso("Solicitação enviada", "Sua solicitação foi enviada com sucesso! Aguarde a resposta do orientador.");
        
        // Recarrega os dados
        carregarSolicitacoes();
        comboBoxOrientadores.getSelectionModel().clearSelection();
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlertaSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void recarregarDados() {
        carregarSolicitacoes();
        atualizarStatusAtual();
        // Atualiza o estado do comboBox e botão após recarregar
        if (temOrientador) {
            if (labelMensagemInfo != null) {
                labelMensagemInfo.setText("Você já possui um orientador atribuído. Não é possível criar novas solicitações.");
                labelMensagemInfo.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            }
            comboBoxOrientadores.setDisable(true);
            if (btnSolicitar != null) {
                btnSolicitar.setDisable(true);
                btnSolicitar.setStyle("-fx-background-color: #95A5A6; -fx-background-radius: 8; -fx-cursor: default;");
            }
        } else {
            if (labelMensagemInfo != null) {
                labelMensagemInfo.setText("");
            }
            comboBoxOrientadores.setDisable(false);
            if (btnSolicitar != null) {
                btnSolicitar.setDisable(false);
                btnSolicitar.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.3), 5, 0, 0, 2);");
            }
        }
    }
}

