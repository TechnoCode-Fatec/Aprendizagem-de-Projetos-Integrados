package com.example.technocode.Controllers.Orientador;

import com.example.technocode.model.SolicitacaoOrientacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;

public class ResponderSolicitacaoController {

    @FXML
    private Label labelNomeAluno;

    @FXML
    private Label labelEmailAluno;

    @FXML
    private Label labelDataSolicitacao;

    @FXML
    private TextArea textAreaMensagem;

    @FXML
    private Button btnAceitar;

    @FXML
    private Button btnRecusar;

    private int solicitacaoId;
    private SolicitacaoOrientacao solicitacao;

    public void setSolicitacaoId(int id) {
        this.solicitacaoId = id;
        carregarDadosSolicitacao();
    }

    private void carregarDadosSolicitacao() {
        solicitacao = SolicitacaoOrientacao.buscarPorId(solicitacaoId);
        
        if (solicitacao != null) {
            // Busca dados do aluno
            java.util.Map<String, String> dadosAluno = com.example.technocode.model.Aluno.buscarDadosPorEmail(solicitacao.getAluno());
            
            labelNomeAluno.setText("Nome: " + dadosAluno.get("nome"));
            labelEmailAluno.setText("Email: " + solicitacao.getAluno());
            
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

