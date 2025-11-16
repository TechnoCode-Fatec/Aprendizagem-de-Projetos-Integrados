package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.*;
import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.SecaoApi;
import com.example.technocode.model.SecaoApresentacao;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;

public class SessoesAtuaisAlunoController {
    
    @FXML
    private VBox containerApresentacoes;
    
    @FXML
    private VBox containerApis;
    
    @FXML
    private Button btnAdicionarApresentacao;
    
    @FXML
    private Button btnAdicionarApi;
    
    
    @FXML
    public void initialize() {
        carregarSecoesDoAluno();
    }

    String emailAluno = LoginController.getEmailLogado();
    
    private void carregarSecoesDoAluno() {
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }
        
        // Carrega seções de apresentação
        List<Map<String, String>> secoesApresentacao = SecaoApresentacao.buscarSecoesPorAluno(emailAluno);
        exibirSecoesApresentacao(secoesApresentacao);
        
        // Carrega seções de API
        List<Map<String, String>> secoesApi = SecaoApi.buscarSecoesPorAluno(emailAluno);
        exibirSecoesApi(secoesApi);
    }
    
    private void exibirSecoesApresentacao(List<Map<String, String>> secoes) {
        containerApresentacoes.getChildren().clear();
        
        if (secoes.isEmpty()) {
            btnAdicionarApresentacao.setVisible(true);
        } else {
            btnAdicionarApresentacao.setVisible(false);
            btnAdicionarApresentacao.setManaged(false);
            
            for (Map<String, String> secao : secoes) {
                VBox secaoCard = criarCardSecao(secao, "apresentacao");
                containerApresentacoes.getChildren().add(secaoCard);
            }
        }
    }
    
    private void exibirSecoesApi(List<Map<String, String>> secoes) {
        containerApis.getChildren().clear();
        
        if (secoes.isEmpty()) {
            btnAdicionarApi.setVisible(true);
        } else {
            btnAdicionarApi.setVisible(false);
            btnAdicionarApi.setManaged(false);
            
            for (Map<String, String> secao : secoes) {
                VBox secaoCard = criarCardSecao(secao, "api");
                containerApis.getChildren().add(secaoCard);
            }
        }
    }
    
    private VBox criarCardSecao(Map<String, String> secao, String tipo) {
        // Container principal do card moderno
        VBox card = new VBox();
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-padding: 18; -fx-cursor: hand; " +
                     "-fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        
        // Hover effect será aplicado via código
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 10; -fx-padding: 18; -fx-cursor: hand; " +
                                                  "-fx-border-color: #B82E1A; -fx-border-width: 1.5; -fx-border-radius: 10; " +
                                                  "-fx-effect: dropshadow(gaussian, rgba(184,46,26,0.15), 8, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-padding: 18; -fx-cursor: hand; " +
                                                 "-fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 10; " +
                                                 "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);"));
        
        // HBox para organizar conteúdo horizontalmente
        HBox contentBox = new HBox();
        contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contentBox.setSpacing(15);
        
        // VBox para título e subtítulo (lado esquerdo)
        VBox textBox = new VBox();
        textBox.setSpacing(4);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);
        
        Label titulo = new Label();
        Label subtitulo = new Label();
        
        if ("apresentacao".equals(tipo)) {
            titulo.setText(secao.get("id")); // Nome da apresentação
            subtitulo.setText("Apresentação - Versão " + secao.get("versao"));
        } else {
            titulo.setText(secao.get("id")); // Semestre + Ano/Semestre
            subtitulo.setText(secao.get("empresa"));
        }
        
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D;");
        
        textBox.getChildren().addAll(titulo, subtitulo);
        
        // Busca e adiciona informação do horário do feedback (se existir)
        String horarioFeedback = null;
        if ("apresentacao".equals(tipo)) {
            int versao = Integer.parseInt(secao.get("versao"));
            horarioFeedback = SecaoApresentacao.buscarHorarioFeedback(emailAluno, versao);
        } else {
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            int versao = Integer.parseInt(secao.get("versao"));
            // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
            String anoExtraido = ano != null ? ano.split("-")[0] : ano;
            horarioFeedback = SecaoApi.buscarHorarioFeedback(emailAluno, semestreCurso, anoExtraido, semestreAno, versao);
        }
        
        // Se existe feedback, adiciona label com o horário
        if (horarioFeedback != null) {
            Label labelHorario = new Label("✓ Feedback recebido em: " + horarioFeedback);
            labelHorario.setStyle("-fx-font-size: 11px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
            textBox.getChildren().add(labelHorario);
        }
        
        // Botão de feedback (lado direito)
        Button btnFeedback = new Button();
        btnFeedback.setPrefHeight(36.0);
        btnFeedback.setPrefWidth(120.0);
        btnFeedback.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; " +
                            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);");
        btnFeedback.setText("Ver Feedback");
        btnFeedback.setFont(new javafx.scene.text.Font(12.0));

        // Desativa por padrão
        btnFeedback.setDisable(true);
        btnFeedback.setStyle("-fx-background-color: #BDC3C7; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: default;");

        // Verifica se existe feedback para esta seção
        if ("apresentacao".equals(tipo)) {
            int versao = Integer.parseInt(secao.get("versao"));
            if (SecaoApresentacao.verificarFeedback(emailAluno, versao)) {
                btnFeedback.setDisable(false);
                btnFeedback.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; " +
                                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);");
            }
        } else {
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            int versao = Integer.parseInt(secao.get("versao"));
            // Extrair apenas o ano da data (ex: "2024-01-01" -> "2024")
            String anoExtraido = ano.split("-")[0];
            if (SecaoApi.verificarFeedback(emailAluno, semestreCurso, Integer.parseInt(anoExtraido), semestreAno, versao)) {
                btnFeedback.setDisable(false);
                btnFeedback.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 6; " +
                                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 2);");
            }
        }

        // Evento do botão de feedback
        btnFeedback.setOnAction(event -> {
            try {
                if ("apresentacao".equals(tipo)) {
                    verFeedbackApresentacao(event, secao);
                } else {
                    verFeedbackApi(event, secao);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        // Adiciona componentes ao HBox
        contentBox.getChildren().addAll(textBox, btnFeedback);
        
        // Adiciona o HBox ao card
        card.getChildren().add(contentBox);
        
        // Adiciona evento de clique no card (exceto no botão)
        card.setOnMouseClicked(event -> {
            // Verifica se o clique não foi no botão
            if (!btnFeedback.getBoundsInParent().contains(event.getX(), event.getY())) {
                try {
                    abrirVisualizacaoSecao(secao, tipo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        return card;
    }

    private void abrirVisualizacaoSecao(Map<String, String> secao, String tipo) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        
        if ("apresentacao".equals(tipo)) {
            Node node = containerApresentacoes;
            NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/aluno-visualizar-apresentacao.fxml",
                controller -> {
                    if (controller instanceof AlunoVisualizarApresentacaoController) {
                        ((AlunoVisualizarApresentacaoController) controller).setIdentificadorSecao(
                            emailAluno, Integer.parseInt(secao.get("versao"))
                        );
                    }
                });
        } else {
            Node node = containerApis;
            String semestreCurso = secao.get("semestre_curso");
            String ano = secao.get("ano");
            String semestreAno = secao.get("semestre_ano");
            String versao = secao.get("versao");
            
            if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
                String anoExtraido = ano.split("-")[0];
                final int anoInt = Integer.parseInt(anoExtraido);
                final int versaoInt = Integer.parseInt(versao);
                
                NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/aluno-visualizar-api.fxml",
                    controller -> {
                        if (controller instanceof AlunoVisualizarApiController) {
                            ((AlunoVisualizarApiController) controller).setIdentificadorSecao(
                                emailAluno, semestreCurso, anoInt, semestreAno, versaoInt
                            );
                        }
                    });
            }
        }
    }
    
    @FXML
    private void voltarLogin(ActionEvent event) throws IOException {
        NavigationService.navegarPara(event, "/com/example/technocode/login.fxml");
    }

    @FXML
    private void adicionarApresentacao(ActionEvent event) throws IOException {
        Node node = (event != null && event.getSource() != null) 
            ? (Node) event.getSource() 
            : btnAdicionarApresentacao;
        NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/formulario-apresentacao.fxml");
    }

    @FXML
    private void adicionarApi(ActionEvent event) throws IOException {
        Node node = (event != null && event.getSource() != null) 
            ? (Node) event.getSource() 
            : btnAdicionarApi;
        NavigationService.navegarParaTelaInterna(node, "/com/example/technocode/Aluno/formulario-api.fxml");
    }
    
    // Método público para ser chamado quando retornar dos formulários
    public void recarregarSecoes() {
        carregarSecoesDoAluno();
    }
    
    private void verFeedbackApresentacao(ActionEvent event, Map<String, String> secao) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }

        int versao = Integer.parseInt(secao.get("versao"));
        final int versaoFinal = versao;

        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-feedback-apresentacao.fxml",
            controller -> {
                if (controller instanceof AlunoFeedbackApresentacaoController) {
                    ((AlunoFeedbackApresentacaoController) controller).setIdentificadorSecao(emailAluno, versaoFinal);
                }
            });
    }

    private void verFeedbackApi(ActionEvent event, Map<String, String> secao) throws IOException {
        String emailAluno = LoginController.getEmailLogado();
        if (emailAluno == null || emailAluno.isBlank()) {
            return;
        }

        String semestreCurso = secao.get("semestre_curso");
        String ano = secao.get("ano");
        String semestreAno = secao.get("semestre_ano");
        String versao = secao.get("versao");

        if (semestreCurso != null && ano != null && semestreAno != null && versao != null) {
            String anoExtraido = ano.split("-")[0];
            final int anoInt = Integer.parseInt(anoExtraido);
            final int versaoInt = Integer.parseInt(versao);
            
            NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-feedback-api.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApiController) {
                        ((AlunoFeedbackApiController) controller).setIdentificadorSecao(
                            emailAluno, semestreCurso, anoInt, semestreAno, versaoInt
                        );
                    }
                });
        }
    }

    @FXML
    private void mostrarOpcoesNovaSessao(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nova Sessão");
        alert.setHeaderText("Escolha o tipo de sessão");
        alert.setContentText("Que tipo de sessão você deseja criar?");
        
        ButtonType btnApresentacao = new ButtonType("Apresentação");
        ButtonType btnApi = new ButtonType("API");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnApresentacao, btnApi, btnCancelar);
        
        alert.showAndWait().ifPresent(buttonType -> {
            try {
                if (buttonType == btnApresentacao) {
                    adicionarApresentacao(null);
                } else if (buttonType == btnApi) {
                    adicionarApi(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void gerarArquivoMD(ActionEvent event) {
        try {
            // Busca dados do aluno
            Map<String, String> dadosAluno = Aluno.buscarDadosPorEmail(emailAluno);
            String nomeAluno = dadosAluno.get("nome");
            String emailAlunoDados = dadosAluno.get("email");
            
            if (nomeAluno == null || nomeAluno.isBlank()) {
                mostrarAlerta("Erro", "Não foi possível obter os dados do aluno.");
                return;
            }
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // 1. Escolhe onde criar a pasta
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Escolha onde criar a pasta do README");
            File diretorioPai = directoryChooser.showDialog(stage);
            
            if (diretorioPai == null) {
                return; // Usuário cancelou
            }
            
            // 2. Cria a pasta (usa nome do aluno ou "README")
            String nomePasta = sanitizeFileName(nomeAluno);
            if (nomePasta.isBlank()) {
                nomePasta = "README";
            }
            File pastaDestino = new File(diretorioPai, nomePasta);
            
            // Se a pasta já existir, adiciona número
            int contador = 1;
            File pastaFinal = pastaDestino;
            while (pastaFinal.exists()) {
                pastaFinal = new File(diretorioPai, nomePasta + "_" + contador);
                contador++;
            }
            pastaFinal.mkdirs();
            
            // 3. Escolhe a foto do aluno
            FileChooser fotoChooser = new FileChooser();
            fotoChooser.setTitle("Escolha a foto do aluno");
            fotoChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
            );
            File fotoOriginal = fotoChooser.showOpenDialog(stage);
            
            String nomeFoto = null;
            if (fotoOriginal != null) {
                // Copia a foto para a pasta criada
                String extensao = getFileExtension(fotoOriginal.getName());
                nomeFoto = "foto" + extensao;
                File fotoDestino = new File(pastaFinal, nomeFoto);
                
                try {
                    Files.copy(fotoOriginal.toPath(), fotoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    mostrarAlerta("Erro", "Erro ao copiar a foto: " + e.getMessage());
                    return;
                }
            }
            
            // 4. Busca apresentação mais recente
            Map<String, String> apresentacao = buscarApresentacaoMaisRecente();
            
            // 5. Busca todas as seções API (versões mais recentes)
            List<Map<String, String>> secoesApi = buscarTodasSecoesApiCompletas();
            
            // 6. Gera o conteúdo do arquivo MD (passa o nome da foto)
            String conteudoMD = gerarConteudoMD(nomeAluno, emailAlunoDados, apresentacao, secoesApi, nomeFoto);
            
            // 7. Salva o README.md na pasta criada
            File arquivoMD = new File(pastaFinal, "README.md");
            try (FileWriter writer = new FileWriter(arquivoMD)) {
                writer.write(conteudoMD);
                mostrarAlerta("Sucesso", "Pasta criada com sucesso!\n\n" +
                    "Localização: " + pastaFinal.getAbsolutePath() + "\n" +
                    "Arquivos criados:\n" +
                    "- README.md\n" +
                    (nomeFoto != null ? "- " + nomeFoto : ""));
            } catch (IOException e) {
                mostrarAlerta("Erro", "Erro ao salvar o arquivo: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao gerar arquivo: " + e.getMessage());
        }
    }
    
    private String sanitizeFileName(String nome) {
        if (nome == null) return "";
        // Remove caracteres inválidos para nome de arquivo/pasta
        return nome.replaceAll("[<>:\"/\\|?*]", "_").trim();
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return ".jpg"; // Extensão padrão se não encontrar
    }
    
    private Map<String, String> buscarApresentacaoMaisRecente() {
        Map<String, String> apresentacao = new java.util.HashMap<>();
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, " +
                        "link_github, link_linkedin, principais_conhecimentos " +
                        "FROM secao_apresentacao " +
                        "WHERE aluno = ? AND versao = (SELECT MAX(versao) FROM secao_apresentacao WHERE aluno = ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            pst.setString(2, emailAluno);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                apresentacao.put("nome", rs.getString("nome"));
                apresentacao.put("idade", rs.getString("idade"));
                apresentacao.put("curso", rs.getString("curso"));
                apresentacao.put("motivacao", rs.getString("motivacao"));
                apresentacao.put("historico", rs.getString("historico"));
                apresentacao.put("historico_profissional", rs.getString("historico_profissional"));
                apresentacao.put("link_github", rs.getString("link_github"));
                apresentacao.put("link_linkedin", rs.getString("link_linkedin"));
                apresentacao.put("principais_conhecimentos", rs.getString("principais_conhecimentos"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return apresentacao;
    }
    
    private List<Map<String, String>> buscarTodasSecoesApiCompletas() {
        List<Map<String, String>> secoesApi = new java.util.ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            // Busca versões mais recentes de cada seção API com todos os campos
            String sql = "SELECT sa.semestre_curso, sa.ano, sa.semestre_ano, sa.versao, sa.empresa, " +
                        "sa.descricao_empresa, sa.problema, sa.solucao, sa.link_repositorio, " +
                        "sa.tecnologias, sa.contribuicoes, sa.hard_skills, sa.soft_skills " +
                        "FROM secao_api sa " +
                        "WHERE sa.aluno = ? AND sa.versao = (" +
                        "    SELECT MAX(versao) " +
                        "    FROM secao_api s2 " +
                        "    WHERE s2.aluno = sa.aluno " +
                        "    AND s2.semestre_curso = sa.semestre_curso " +
                        "    AND s2.ano = sa.ano " +
                        "    AND s2.semestre_ano = sa.semestre_ano" +
                        ") " +
                        "ORDER BY sa.ano DESC, sa.semestre_ano DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Map<String, String> secao = new java.util.HashMap<>();
                secao.put("semestre_curso", rs.getString("semestre_curso"));
                secao.put("ano", String.valueOf(rs.getInt("ano")));
                secao.put("semestre_ano", rs.getString("semestre_ano"));
                secao.put("versao", String.valueOf(rs.getInt("versao")));
                secao.put("empresa", rs.getString("empresa"));
                secao.put("descricao_empresa", rs.getString("descricao_empresa"));
                secao.put("problema", rs.getString("problema"));
                secao.put("solucao", rs.getString("solucao"));
                secao.put("link_repositorio", rs.getString("link_repositorio"));
                secao.put("tecnologias", rs.getString("tecnologias"));
                secao.put("contribuicoes", rs.getString("contribuicoes"));
                secao.put("hard_skills", rs.getString("hard_skills"));
                secao.put("soft_skills", rs.getString("soft_skills"));
                secoesApi.add(secao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return secoesApi;
    }
    
    private String gerarConteudoMD(String nomeAluno, String emailAluno, Map<String, String> apresentacao, 
                                   List<Map<String, String>> secoesApi, String nomeFoto) {
        StringBuilder md = new StringBuilder();
        
        // Calcula idade
        String idadeStr = "N/A";
        if (apresentacao.get("idade") != null && !apresentacao.get("idade").isBlank()) {
            try {
                LocalDate dataNascimento = LocalDate.parse(apresentacao.get("idade"));
                int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
                idadeStr = String.valueOf(idade);
            } catch (Exception e) {
                idadeStr = apresentacao.get("idade");
            }
        }
        
        // Foto do aluno - usa o nome do arquivo se fornecido, senão placeholder
        String fotoAluno = nomeFoto != null ? nomeFoto : "(FOTO DO ALUNO)";
        
        // Header
        md.append("<div align=\"center\">\n\n");
        md.append("<h1>").append(escapeHtml(nomeAluno)).append("</h1>\n\n");
        md.append("    \n");
        md.append("<img src=\"").append(fotoAluno).append("\" alt=\"Foto\" width=\"300\" height=\"300\"/>\n\n");
        md.append("    \n");
        md.append("</div>\n\n");
        md.append("<hr>\n\n");
        
        // Apresentação pessoal
        md.append("Meu nome é ").append(escapeHtml(nomeAluno)).append(" e tenho ").append(idadeStr).append(".\n\n");
        md.append("Cursando ").append(escapeHtml(apresentacao.getOrDefault("curso", "N/A"))).append(" na FATEC-SJC\n\n");
        
        String historico = apresentacao.getOrDefault("historico", "");
        if (!historico.isBlank()) {
            md.append(historico).append("\n\n");
        }
        
        String motivacao = apresentacao.getOrDefault("motivacao", "");
        if (!motivacao.isBlank()) {
            md.append(motivacao).append("\n\n");
        }
        
        String historicoProfissional = apresentacao.getOrDefault("historico_profissional", "");
        if (!historicoProfissional.isBlank()) {
            md.append(historicoProfissional).append("\n\n");
        }
        
        // Contatos
        md.append("## 📞 Contatos\n\n");
        md.append("<ul style=\"display: flex; flex-direction: column; align-items: left;\">\n\n");
        
        String github = apresentacao.getOrDefault("link_github", "");
        if (!github.isBlank()) {
            md.append("    <li style=\"margin-bottom: 10px;\">\n");
            md.append("         <a href=\"").append(github).append("\"><img align=\"center\" alt=\"GitHub\" src = \"https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white\"/></a>\n");
            md.append("    </li>\n\n");
        }
        
        String linkedin = apresentacao.getOrDefault("link_linkedin", "");
        if (!linkedin.isBlank()) {
            md.append("    <li style=\"margin-bottom: 10px\">\n");
            md.append("        <a href=\"").append(linkedin).append("\" target=\"_blank\"><img align=\"center\" alt=\"Linkedin\" src=\"https://img.shields.io/badge/-LinkedIn-%230077B5?style=for-the-badge&logo=linkedin&logoColor=white\" target=\"_blank\"></a>\n");
            md.append("    </li>\n\n");
        }
        
        md.append("    <li style=\"margin-bottom: 10px\">\n");
        md.append("        <a href = \"mailto:").append(emailAluno).append("\"><img align=\"center\" alt=\"Gmail\" src=\"https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white\" target=\"_blank\"></a>\n");
        md.append("    </li>\n\n");
        md.append("</ul>\n\n");
        
        // Principais conhecimentos
        String conhecimentos = apresentacao.getOrDefault("principais_conhecimentos", "");
        if (!conhecimentos.isBlank()) {
            md.append("## 📚 Meus Principais Conhecimentos\n\n");
            md.append(conhecimentos).append("\n\n");
        }
        
        md.append("<hr>\n\n");
        
        // Projetos API
        if (!secoesApi.isEmpty()) {
            md.append("## 💼 Meus Projetos\n\n");
            
            for (Map<String, String> secao : secoesApi) {
                md.append("### ").append(escapeHtml(secao.get("semestre_curso")))
                  .append(" (").append(secao.get("ano")).append("-").append(secao.get("semestre_ano"))
                  .append(") | ").append(escapeHtml(secao.get("empresa"))).append("\n\n");
                
                md.append("O projeto desenvolvido no ").append(escapeHtml(secao.get("semestre_curso")))
                  .append(" do curso teve como cliente a ").append(escapeHtml(secao.get("empresa"))).append(", ");
                
                String descricaoEmpresa = secao.getOrDefault("descricao_empresa", "");
                if (!descricaoEmpresa.isBlank()) {
                    md.append(descricaoEmpresa).append("\n\n");
                } else {
                    md.append("\n\n");
                }
                
                String problema = secao.getOrDefault("problema", "");
                if (!problema.isBlank()) {
                    md.append(problema).append("\n\n");
                }
                
                String solucao = secao.getOrDefault("solucao", "");
                if (!solucao.isBlank()) {
                    md.append(solucao).append("\n\n");
                }
                
                md.append("<br>\n\n");
                md.append("<hr>\n\n");
                
                String linkRepositorio = secao.getOrDefault("link_repositorio", "");
                if (!linkRepositorio.isBlank()) {
                    md.append("#### ✍ Repositório do Projeto: [GitHub](").append(linkRepositorio).append(")\n\n");
                    md.append("<hr>\n\n");
                }
                
                md.append("#### 👨‍💻 Tecnologias Utilizadas\n\n");
                String tecnologias = secao.getOrDefault("tecnologias", "");
                if (!tecnologias.isBlank()) {
                    md.append(tecnologias).append("\n\n");
                }
                
                md.append("#### 👍 Contribuições Pessoais\n\n");
                String contribuicoes = secao.getOrDefault("contribuicoes", "");
                if (!contribuicoes.isBlank()) {
                    md.append(contribuicoes).append("\n\n");
                }
                
                md.append("#### 💪 Hard Skills\n\n");
                md.append("Exercitei as seguintes Hard Skills durante esse projeto:\n\n");
                String hardSkills = secao.getOrDefault("hard_skills", "");
                if (!hardSkills.isBlank()) {
                    md.append(hardSkills).append("\n\n");
                }
                
                md.append("#### 🍀 Soft Skills\n\n");
                String softSkills = secao.getOrDefault("soft_skills", "");
                if (!softSkills.isBlank()) {
                    md.append(softSkills).append("\n\n");
                }
                
                md.append("---\n\n");
            }
        }
        
        return md.toString();
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

}

