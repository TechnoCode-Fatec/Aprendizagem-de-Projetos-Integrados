package com.example.technocode.Controllers.Orientador;

import com.example.technocode.model.SolicitacaoOrientacao;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResponderSolicitacaoController {

    @FXML
    private Label labelNomeAluno;

    @FXML
    private Label labelEmailAluno;

    @FXML
    private Label labelDataSolicitacao;
    
    @FXML
    private Label labelDisciplina;

    @FXML
    private TextArea textAreaMensagem;

    @FXML
    private Button btnAceitar;

    @FXML
    private Button btnRecusar;
    
    @FXML
    private VBox cardJustificativa;

    private int solicitacaoId;
    private SolicitacaoOrientacao solicitacao;

    public void setSolicitacaoId(int id) {
        this.solicitacaoId = id;
        carregarDadosSolicitacao();
    }
    
    public void setSolicitacaoIdComRecusa(int id) {
        this.solicitacaoId = id;
        carregarDadosSolicitacao();
        // Mostra o card de justificativa imediatamente
        mostrarCardJustificativa();
    }

    private void carregarDadosSolicitacao() {
        solicitacao = SolicitacaoOrientacao.buscarPorId(solicitacaoId);
        
        if (solicitacao != null) {
            // Busca dados do aluno
            java.util.Map<String, String> dadosAluno = com.example.technocode.model.Aluno.buscarDadosPorEmail(solicitacao.getAluno());
            
            labelNomeAluno.setText("Nome: " + dadosAluno.get("nome"));
            labelEmailAluno.setText("Email: " + solicitacao.getAluno());
            
            // Busca disciplina do aluno
            try (Connection conn = new Connector().getConnection()) {
                String sql = "SELECT disciplina_tg FROM aluno WHERE email = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, solicitacao.getAluno());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    String disciplina = rs.getString("disciplina_tg");
                    // Formata a disciplina para exibição (TG1 -> TG 1, TG2 -> TG 2, TG1/TG2 -> TG 1/TG 2)
                    String disciplinaFormatada = disciplina != null ? disciplina.replace("TG1", "TG 1").replace("TG2", "TG 2") : "N/A";
                    labelDisciplina.setText("Disciplina: " + disciplinaFormatada);
                } else {
                    labelDisciplina.setText("Disciplina: N/A");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                labelDisciplina.setText("Disciplina: N/A");
            }
            
            if (solicitacao.getDataSolicitacao() != null) {
                String dataStr = solicitacao.getDataSolicitacao().toString();
                if (dataStr.length() > 19) {
                    dataStr = dataStr.substring(0, 19);
                }
                labelDataSolicitacao.setText("Data da Solicitação: " + dataStr);
            }
        }
    }

    @FXML
    private void aceitarSolicitacao(ActionEvent event) {
        if (solicitacao == null) {
            mostrarAlertaErro("Erro", "Solicitação não encontrada.");
            return;
        }

        try {
            solicitacao.aceitar();
            mostrarAlertaSucesso("Sucesso", "Solicitação aceita com sucesso! O aluno foi vinculado ao seu perfil.");
            
            // Volta para a lista de solicitações
            OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Orientador/solicitacoes-orientacao.fxml",
                c -> {
                    if (c instanceof SolicitacoesOrientacaoController) {
                        ((SolicitacoesOrientacaoController) c).recarregarSolicitacoes();
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro", "Não foi possível aceitar a solicitação. Erro: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarCardJustificativa(ActionEvent event) {
        mostrarCardJustificativa();
    }
    
    private void mostrarCardJustificativa() {
        // Mostra o card de justificativa
        if (cardJustificativa != null) {
            cardJustificativa.setVisible(true);
            cardJustificativa.setManaged(true);
            // Foca no TextArea
            textAreaMensagem.requestFocus();
        }
    }
    
    @FXML
    private void cancelarRecusa(ActionEvent event) {
        // Oculta o card de justificativa e limpa o texto
        if (cardJustificativa != null) {
            cardJustificativa.setVisible(false);
            cardJustificativa.setManaged(false);
            textAreaMensagem.clear();
        }
    }

    @FXML
    private void recusarSolicitacao(ActionEvent event) {
        if (solicitacao == null) {
            mostrarAlertaErro("Erro", "Solicitação não encontrada.");
            return;
        }

        String mensagem = textAreaMensagem.getText().trim();
        
        if (mensagem.isEmpty()) {
            mostrarAlertaErro("Mensagem obrigatória", "Por favor, escreva uma mensagem justificando a recusa.");
            return;
        }

        try {
            solicitacao.recusar(mensagem);
            mostrarAlertaSucesso("Sucesso", "Solicitação recusada com sucesso.");
            
            // Volta para a lista de solicitações
            OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
                "/com/example/technocode/Orientador/solicitacoes-orientacao.fxml",
                c -> {
                    if (c instanceof SolicitacoesOrientacaoController) {
                        ((SolicitacoesOrientacaoController) c).recarregarSolicitacoes();
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro", "Não foi possível recusar a solicitação. Erro: " + e.getMessage());
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        OrientadorPrincipalController.getInstance().navegarParaTelaDoCenter(
            "/com/example/technocode/Orientador/solicitacoes-orientacao.fxml",
            c -> {
                if (c instanceof SolicitacoesOrientacaoController) {
                    ((SolicitacoesOrientacaoController) c).recarregarSolicitacoes();
                }
            }
        );
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
}

