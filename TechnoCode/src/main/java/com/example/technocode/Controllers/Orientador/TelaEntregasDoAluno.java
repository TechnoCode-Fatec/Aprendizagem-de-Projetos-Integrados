package com.example.technocode.Controllers.Orientador;

import com.example.technocode.dao.Connector;
import javafx.beans.property.SimpleStringProperty;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

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
    @FXML private TableColumn<Map<String, String>, String> colStatusFeedback; // status do feedback
    @FXML private TableColumn<Map<String, String>, Void> colAnalisar;

    @FXML
    public void initialize() {
        try {
            colNomeSecao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("id")));
            colDescricao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("empresa")));
            colStatusFeedback.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("status_feedback")));

            // Aplica estilo customizado na coluna de status
            colStatusFeedback.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);
                        
                        // Aplica cor baseada no status
                        if ("Respondida".equals(status)) {
                            setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;"); // Verde escuro
                        } else if ("Á responder".equals(status)) {
                            setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;"); // Vermelho escuro
                        } else {
                            setStyle(""); // Padrão
                        }
                    }
                }
            });

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
        
        // Carrega seções API
        List<Map<String,String>> secoesApi = connector.secoesApi(emailAlunoParaConsulta);
        
        // Carrega seções de apresentação
        List<Map<String,String>> secoesApresentacao = connector.secoesApresentacao(emailAlunoParaConsulta);
        
        // Combina as duas listas
        List<Map<String,String>> todasSecoes = new ArrayList<>();
        todasSecoes.addAll(secoesApi);
        todasSecoes.addAll(secoesApresentacao);
        
        // Adiciona status de feedback para cada seção
        for (Map<String, String> secao : todasSecoes) {
            String statusFeedback = determinarStatusFeedback(secao, connector);
            secao.put("status_feedback", statusFeedback);
        }
        
        tabelaSecao.getItems().setAll(todasSecoes);
    }

    private String determinarStatusFeedback(Map<String, String> secao, Connector connector) {
        try {
            String tipo = secao.getOrDefault("tipo", "api");
            String emailAluno = emailAlunoParaConsulta;
            
            if ("apresentacao".equals(tipo)) {
                // Para seções de apresentação, verifica se existe feedback
                String versao = secao.getOrDefault("versao", null);
                if (versao != null) {
                    boolean temFeedback = connector.verificarFeedbackApresentacao(emailAluno, Integer.parseInt(versao));
                    return temFeedback ? "Respondida" : "Á responder";
                }
            } else {
                // Para seções de API, verifica se existe feedback
                String semestreCurso = secao.getOrDefault("semestre_curso", null);
                String ano = secao.getOrDefault("ano", null);
                String semestreAno = secao.getOrDefault("semestre_ano", null);
                String versao = secao.getOrDefault("versao", null);
                
                if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                    // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                    String anoExtraido = ano.split("-")[0];
                    boolean temFeedback = connector.verificarFeedbackApi(emailAluno, semestreCurso, Integer.parseInt(anoExtraido), semestreAno, Integer.parseInt(versao));
                    return temFeedback ? "Respondida" : "Á responder";
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar status do feedback: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Em caso de erro ou dados incompletos, assume que não tem feedback
        return "Á responder";
    }

    private void addButtonToTable() {
        colAnalisar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("analisar");
            {
                btn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
                btn.setOnAction(event -> {
                    Map<String, String> item = getTableView().getItems().get(getIndex());
                    try {
                        String tipo = item.getOrDefault("tipo", "api");
                        
                        if ("apresentacao".equals(tipo)) {
                            // Abre tela de apresentação
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-secoesenviadas.fxml"));
                            Parent root = loader.load();

                            TelaSecoesenviadasController controller = loader.getController();
                            
                            String versao = item.getOrDefault("versao", null);
                            if (versao != null) {
                                controller.setIdentificadorSecao(
                                    emailAlunoParaConsulta,  // email do aluno
                                    Integer.parseInt(versao) // versao
                                );
                            }
                            
                            Stage stage = (Stage) tabelaSecao.getScene().getWindow();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        } else {
                            // Abre tela de API (comportamento original)
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-secoesenviadasAPI.fxml"));
                            Parent root = loader.load();

                            TelaSecoesenviadasAPIController controller = loader.getController();
                            
                            String semestreCurso = item.getOrDefault("semestre_curso", null);
                            String ano = item.getOrDefault("ano", null);
                            String semestreAno = item.getOrDefault("semestre_ano", null);
                            String versao = item.getOrDefault("versao", null);
                            
                            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                                // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
                                String anoExtraido = ano.split("-")[0];
                                
                                controller.setIdentificadorSecao(
                                    emailAlunoParaConsulta,  // email do aluno
                                    semestreCurso,          // semestre_curso
                                    Integer.parseInt(anoExtraido),   // ano extraído da data
                                    semestreAno,            // semestre_ano
                                    Integer.parseInt(versao) // versao
                                );
                            }
                            
                            Stage stage = (Stage) tabelaSecao.getScene().getWindow();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-inicial-orientador.fxml"));
            Parent root = loader.load();
            
            // Obtém o controlador da tela de destino
            TelaOrientadorController controller = loader.getController();
            
            // Recarrega a tabela de alunos com os dados atualizados
            controller.recarregarTabelaAlunos();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar para tela do orientador: " + e.getMessage());
            throw e;
        }

    }

    /**
     * Abre a tela de histórico de versões do aluno
     */
    @FXML
    private void abrirHistorico(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/technocode/Orientador/tela-historico-orientador.fxml"));
            Parent root = loader.load();
            
            // Obtém o controller e passa o email do aluno
            TelaHistoricoOrientadorController controller = loader.getController();
            controller.setEmailAluno(emailAlunoParaConsulta);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir histórico: " + e.getMessage());
            throw e;
        }
    }




    public void setDadosAluno(String emailAluno) {
        if (emailAluno != null && !emailAluno.isBlank()) {
            Connector connector = new Connector();
            Map<String, String> dadosAluno = connector.buscarDadosAluno(emailAluno);
            
            if (!dadosAluno.isEmpty()) {
                nomeAluno.setText(dadosAluno.get("nome"));
                this.emailAluno.setText(dadosAluno.get("email"));
                cursoAluno.setText(dadosAluno.get("curso"));
            }
        }
    }

    // Método público para recarregar os dados da tabela
    public void recarregarDados() {
        if (emailAlunoParaConsulta != null && !emailAlunoParaConsulta.isBlank()) {
            carregarSecoesDoAluno();
            tabelaSecao.refresh();
        }
    }

}
