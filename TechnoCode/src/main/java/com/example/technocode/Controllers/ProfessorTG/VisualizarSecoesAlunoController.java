package com.example.technocode.Controllers.ProfessorTG;

import com.example.technocode.model.Aluno;
import com.example.technocode.model.SecaoApi;
import com.example.technocode.model.SecaoApresentacao;
import com.example.technocode.model.SolicitacaoOrientacao;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller para visualizar as seções enviadas por um aluno
 * Permite apenas visualização, sem capacidade de dar feedback
 */
public class VisualizarSecoesAlunoController {

    @FXML
    private Label nomeAluno;
    @FXML
    private Label emailAluno;
    @FXML
    private Label cursoAluno;

    // GUARDA O E-MAIL DO ALUNO SELECIONADO
    private String emailAlunoParaConsulta;

    @FXML 
    private TableView<Map<String, String>> tabelaSecao;
    @FXML 
    private TableColumn<Map<String, String>, String> colNomeSecao;
    @FXML 
    private TableColumn<Map<String, String>, String> colDescricao;
    @FXML 
    private TableColumn<Map<String, String>, String> colItensAprovados;
    @FXML 
    private TableColumn<Map<String, String>, String> colStatusFeedback;
    @FXML 
    private TableColumn<Map<String, String>, Void> colVisualizar;

    // Tabela de solicitações de orientação
    @FXML 
    private TableView<Map<String, String>> tabelaSolicitacoes;
    @FXML 
    private TableColumn<Map<String, String>, String> colOrientadorSolicitacao;
    @FXML 
    private TableColumn<Map<String, String>, String> colStatusSolicitacao;
    @FXML 
    private TableColumn<Map<String, String>, String> colDataSolicitacao;
    @FXML 
    private TableColumn<Map<String, String>, String> colDataResposta;
    @FXML 
    private TableColumn<Map<String, String>, String> colMensagemOrientador;

    public void setEmailAlunoParaConsulta(String email) {
        this.emailAlunoParaConsulta = email;
        // se initialize já rodou, podemos carregar os dados agora
        carregarSecoesDoAluno();
        carregarSolicitacoesOrientacao();
    }

    @FXML
    public void initialize() {
        try {
            colNomeSecao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("id")));
            colDescricao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("empresa")));
            colItensAprovados.setCellValueFactory(data -> {
                Map<String, String> secao = data.getValue();
                String tipo = secao.getOrDefault("tipo", "api");
                String emailAluno = emailAlunoParaConsulta;
                
                // Se o email ainda não foi definido, retorna "-"
                if (emailAluno == null || emailAluno.isBlank()) {
                    return new SimpleStringProperty("-");
                }
                
                try {
                    Map<String, Integer> itensAprovados = null;
                    if ("apresentacao".equals(tipo)) {
                        String versao = secao.getOrDefault("versao", null);
                        if (versao != null) {
                            itensAprovados = SecaoApresentacao.contarItensAprovados(emailAluno, Integer.parseInt(versao));
                        }
                    } else {
                        String semestreCurso = secao.getOrDefault("semestre_curso", null);
                        String ano = secao.getOrDefault("ano", null);
                        String semestreAno = secao.getOrDefault("semestre_ano", null);
                        String versao = secao.getOrDefault("versao", null);
                        
                        if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                            String anoExtraido = ano.split("-")[0];
                            itensAprovados = SecaoApi.contarItensAprovados(emailAluno, semestreCurso, Integer.parseInt(anoExtraido), semestreAno, Integer.parseInt(versao));
                        }
                    }
                    
                    if (itensAprovados != null) {
                        int aprovados = itensAprovados.get("aprovados");
                        int total = itensAprovados.get("total");
                        if (aprovados == total) {
                            return new SimpleStringProperty("Seção aprovada!");
                        } else {
                            return new SimpleStringProperty(aprovados + "/" + total + " itens aprovados");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao contar itens aprovados: " + e.getMessage());
                }
                
                return new SimpleStringProperty("-");
            });
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
            
            // Configura tabela de solicitações de orientação
            configurarTabelaSolicitacoes();

            // só carrega dados se já tiver o e-mail (pode ser setado após o load)
            if (emailAlunoParaConsulta != null && !emailAlunoParaConsulta.isBlank()) {
            carregarSecoesDoAluno();
                carregarSolicitacoesOrientacao();
            tabelaSecao.refresh();
            }
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
        // Carrega seções API
        List<Map<String,String>> secoesApi = SecaoApi.buscarSecoesPorAluno(emailAlunoParaConsulta);
        
        // Carrega seções de apresentação
        List<Map<String,String>> secoesApresentacao = SecaoApresentacao.buscarSecoesPorAluno(emailAlunoParaConsulta);
        
        // Combina as duas listas
        List<Map<String,String>> todasSecoes = new ArrayList<>();
        todasSecoes.addAll(secoesApi);
        todasSecoes.addAll(secoesApresentacao);
        
        // Adiciona status de feedback para cada seção
        for (Map<String, String> secao : todasSecoes) {
            String statusFeedback = determinarStatusFeedback(secao);
            secao.put("status_feedback", statusFeedback);
        }
        
        tabelaSecao.getItems().setAll(todasSecoes);
    }
    
    /**
     * Configura as colunas da tabela de solicitações de orientação
     */
    private void configurarTabelaSolicitacoes() {
        if (tabelaSolicitacoes == null || colOrientadorSolicitacao == null || colStatusSolicitacao == null) {
            System.err.println("Tabela de solicitações não inicializada corretamente");
            return;
        }
        
        colOrientadorSolicitacao.setCellValueFactory(data -> {
            String nome = data.getValue().getOrDefault("nome_orientador", null);
            return new SimpleStringProperty(nome != null && !nome.isEmpty() ? nome : "-");
        });
        colOrientadorSolicitacao.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        
        colStatusSolicitacao.setCellValueFactory(data -> {
            String status = data.getValue().getOrDefault("status", null);
            return new SimpleStringProperty(status != null && !status.isEmpty() ? status : "-");
        });
        colStatusSolicitacao.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setAlignment(Pos.CENTER);
                    
                    // Aplica cor baseada no status
                    if ("Aceita".equals(status)) {
                        setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;"); // Verde escuro
                    } else if ("Pendente".equals(status)) {
                        setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;"); // Laranja
                    } else if ("Recusada".equals(status)) {
                        setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;"); // Vermelho escuro
                    } else {
                        setStyle(""); // Padrão
                    }
                }
            }
        });
        
        colDataSolicitacao.setCellValueFactory(data -> {
            String dataStr = data.getValue().getOrDefault("data_solicitacao", null);
            if (dataStr != null && !dataStr.isEmpty() && !dataStr.equals("null")) {
                try {
                    // Remove milissegundos se houver (formato: 2024-01-15 10:30:00.0)
                    if (dataStr.contains(".")) {
                        dataStr = dataStr.substring(0, dataStr.indexOf("."));
                    }
                    // Tenta parsear como Timestamp
                    java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(dataStr);
                    java.time.LocalDateTime localDateTime = timestamp.toLocalDateTime();
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    return new SimpleStringProperty(localDateTime.format(formatter));
                } catch (Exception e) {
                    System.err.println("Erro ao formatar data_solicitacao: " + dataStr + " - " + e.getMessage());
                    return new SimpleStringProperty(dataStr);
                }
            }
            return new SimpleStringProperty("-");
        });
        colDataSolicitacao.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        
        colDataResposta.setCellValueFactory(data -> {
            String dataStr = data.getValue().getOrDefault("data_resposta", null);
            if (dataStr != null && !dataStr.isEmpty() && !dataStr.equals("null")) {
                try {
                    // Remove milissegundos se houver (formato: 2024-01-15 10:30:00.0)
                    if (dataStr.contains(".")) {
                        dataStr = dataStr.substring(0, dataStr.indexOf("."));
                    }
                    // Tenta parsear como Timestamp
                    java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(dataStr);
                    java.time.LocalDateTime localDateTime = timestamp.toLocalDateTime();
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    return new SimpleStringProperty(localDateTime.format(formatter));
                } catch (Exception e) {
                    System.err.println("Erro ao formatar data_resposta: " + dataStr + " - " + e.getMessage());
                    return new SimpleStringProperty(dataStr);
                }
            }
            return new SimpleStringProperty("-");
        });
        colDataResposta.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        
        colMensagemOrientador.setCellValueFactory(data -> {
            String mensagem = data.getValue().getOrDefault("mensagem_orientador", null);
            return new SimpleStringProperty(mensagem != null && !mensagem.isEmpty() ? mensagem : "-");
        });
        colMensagemOrientador.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    setWrapText(true);
                }
            }
        });
        
        tabelaSolicitacoes.setStyle("-fx-control-inner-background: #ffffff; -fx-text-background-color: black;");
    }
    
    /**
     * Carrega as solicitações de orientação do aluno
     */
    private void carregarSolicitacoesOrientacao() {
        if (emailAlunoParaConsulta == null || emailAlunoParaConsulta.isBlank()) {
            return;
        }
        
        if (tabelaSolicitacoes == null) {
            System.err.println("Tabela de solicitações não inicializada");
            return;
        }
        
        try {
            List<Map<String, String>> solicitacoes = SolicitacaoOrientacao.buscarPorAluno(emailAlunoParaConsulta);
            
            // Limpa a tabela antes de adicionar novos itens
            tabelaSolicitacoes.getItems().clear();
            
            if (!solicitacoes.isEmpty()) {
                tabelaSolicitacoes.getItems().addAll(solicitacoes);
            }
            
            tabelaSolicitacoes.refresh();
        } catch (Exception e) {
            System.err.println("Erro ao carregar solicitações de orientação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String determinarStatusFeedback(Map<String, String> secao) {
        try {
            String tipo = secao.getOrDefault("tipo", "api");
            String emailAluno = emailAlunoParaConsulta;
            
            if ("apresentacao".equals(tipo)) {
                // Para seções de apresentação, verifica se existe feedback
                String versao = secao.getOrDefault("versao", null);
                if (versao != null) {
                    boolean temFeedback = SecaoApresentacao.verificarFeedback(emailAluno, Integer.parseInt(versao));
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
                    boolean temFeedback = SecaoApi.verificarFeedback(emailAluno, semestreCurso, Integer.parseInt(anoExtraido), semestreAno, Integer.parseInt(versao));
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
        colVisualizar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Visualizar");
            {
                btn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
                btn.setOnAction(event -> {
                    Map<String, String> item = getTableView().getItems().get(getIndex());
                    try {
                        String tipo = item.getOrDefault("tipo", "api");
                        
                        ProfessorTGPrincipalController principalController = ProfessorTGPrincipalController.getInstance();
                        if (principalController == null) {
                            mostrarErro("Erro", "Não foi possível acessar o controlador principal.");
                            return;
                        }
                        
                        if ("apresentacao".equals(tipo)) {
                            // Abre tela de visualização de apresentação (somente leitura)
                            String versao = item.getOrDefault("versao", null);
                            if (versao != null) {
                                final int versaoFinal = Integer.parseInt(versao);
                                principalController.navegarParaTela(
                                    "/com/example/technocode/Aluno/aluno-visualizar-apresentacao.fxml",
                                    controller -> {
                                        if (controller instanceof com.example.technocode.Controllers.Aluno.AlunoVisualizarApresentacaoController) {
                                            com.example.technocode.Controllers.Aluno.AlunoVisualizarApresentacaoController visualizarController = 
                                                (com.example.technocode.Controllers.Aluno.AlunoVisualizarApresentacaoController) controller;
                                            visualizarController.setIdentificadorSecao(emailAlunoParaConsulta, versaoFinal);
                                            visualizarController.esconderBotaoFeedback();
                                        }
                                    });
                            } else {
                                mostrarErro("Erro", "Versão da seção não encontrada.");
                            }
                        } else {
                            // Abre tela de visualização de API (somente leitura)
                            String semestreCurso = item.getOrDefault("semestre_curso", null);
                            String ano = item.getOrDefault("ano", null);
                            String semestreAno = item.getOrDefault("semestre_ano", null);
                            String versao = item.getOrDefault("versao", null);
                            
                            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                                String anoExtraido = ano.split("-")[0];
                                final int anoFinal = Integer.parseInt(anoExtraido);
                                final int versaoFinal = Integer.parseInt(versao);
                                
                                principalController.navegarParaTela(
                                    "/com/example/technocode/Aluno/aluno-visualizar-api.fxml",
                                    controller -> {
                                        if (controller instanceof com.example.technocode.Controllers.Aluno.AlunoVisualizarApiController) {
                                            com.example.technocode.Controllers.Aluno.AlunoVisualizarApiController visualizarController = 
                                                (com.example.technocode.Controllers.Aluno.AlunoVisualizarApiController) controller;
                                            visualizarController.setIdentificadorSecao(
                                                emailAlunoParaConsulta, semestreCurso, anoFinal, semestreAno, versaoFinal);
                                            visualizarController.esconderBotaoFeedback();
                                        }
                                    });
                            } else {
                                mostrarErro("Erro", "Dados da seção incompletos.");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        mostrarErro("Erro", "Não foi possível abrir a visualização da seção: " + ex.getMessage());
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
    private void voltarTelaDashboard(ActionEvent event) throws IOException {
        if (ProfessorTGPrincipalController.getInstance() != null) {
            ProfessorTGPrincipalController.getInstance().navegarParaTela(
                "/com/example/technocode/ProfessorTG/dashboard-professor-tg-content.fxml",
                controller -> {
                    if (controller instanceof DashboardProfessorTGController) {
                        ((DashboardProfessorTGController) controller).atualizarDashboard();
                    }
                }
            );
        }
    }

    public void setDadosAluno(String emailAluno) {
        if (emailAluno != null && !emailAluno.isBlank()) {
            Map<String, String> dadosAluno = Aluno.buscarDadosPorEmail(emailAluno);
            
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
            carregarSolicitacoesOrientacao();
            tabelaSecao.refresh();
            tabelaSolicitacoes.refresh();
        }
    }

    /**
     * Mostra mensagem de erro
     */
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

