package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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

public class GerarReadmeController {
    
    @FXML
    private Button btnCriarPasta;
    
    @FXML
    private Button btnAdicionarFoto;
    
    @FXML
    private Button btnTabCode;
    
    @FXML
    private Button btnTabPreview;
    
    @FXML
    private Label labelStatusPasta;
    
    @FXML
    private TextArea textAreaMarkdown;
    
    @FXML
    private ScrollPane scrollPaneCode;
    
    @FXML
    private WebView webViewPreview;
    
    private String emailAluno;
    private File pastaAtual;
    private String nomeFoto;
    private String conteudoMD;
    
    @FXML
    public void initialize() {
        emailAluno = LoginController.getEmailLogado();
        
        // Configura estilo inicial das tabs
        atualizarEstiloTabs(true);
        
        // Gera o conteúdo MD automaticamente baseado nas seções atuais
        gerarConteudoMD(null);
    }
    
    @FXML
    private void selecionarPasta(ActionEvent event) {
        try {
            Stage stage = (Stage) btnCriarPasta.getScene().getWindow();
            
            // Permite selecionar uma pasta existente ou criar uma nova
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecione a pasta do README (ou crie uma nova)");
            
            // Se já tiver uma pasta selecionada, abre nesse diretório
            if (pastaAtual != null && pastaAtual.exists() && pastaAtual.getParentFile() != null) {
                directoryChooser.setInitialDirectory(pastaAtual.getParentFile());
            }
            
            File pastaSelecionada = directoryChooser.showDialog(stage);
            
            if (pastaSelecionada == null) {
                return;
            }
            
            // Se a pasta não existir, cria ela
            if (!pastaSelecionada.exists()) {
                if (!pastaSelecionada.mkdirs()) {
                    mostrarAlerta("Erro", "Não foi possível criar a pasta selecionada.");
                    return;
                }
            }
            
            pastaAtual = pastaSelecionada;
            btnAdicionarFoto.setDisable(false);
            labelStatusPasta.setText("Pasta: " + pastaSelecionada.getName());
            
            // Verifica se já existe uma foto na pasta selecionada
            String fotoEncontrada = buscarFotoNaPasta(pastaSelecionada);
            if (fotoEncontrada != null) {
                nomeFoto = fotoEncontrada;
                // Regenera o conteúdo MD com a foto encontrada
                gerarConteudoMD(nomeFoto);
                // Atualiza o preview se estiver visível
                if (webViewPreview.isVisible()) {
                    carregarPreview(conteudoMD);
                }
            } else {
                // Se não houver foto, limpa o nomeFoto e regenera sem foto
                nomeFoto = null;
                gerarConteudoMD(null);
            }
            
            // Salva o README.md na pasta
            if (conteudoMD != null) {
                File arquivoMD = new File(pastaSelecionada, "README.md");
                try (FileWriter writer = new FileWriter(arquivoMD)) {
                    writer.write(conteudoMD);
                } catch (IOException e) {
                    mostrarAlerta("Erro", "Erro ao salvar README.md: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao selecionar pasta: " + e.getMessage());
        }
    }
    
    @FXML
    private void adicionarFoto(ActionEvent event) {
        if (pastaAtual == null || !pastaAtual.exists()) {
            mostrarAlerta("Aviso", "Por favor, crie uma pasta primeiro.");
            return;
        }
        
        Stage stage = (Stage) btnAdicionarFoto.getScene().getWindow();
        
        // Escolhe a foto
        FileChooser fotoChooser = new FileChooser();
        fotoChooser.setTitle("Escolha a foto do aluno");
        fotoChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
        );
        File fotoOriginal = fotoChooser.showOpenDialog(stage);
        
        if (fotoOriginal != null) {
            try {
                // Copia a foto para a pasta criada
                String extensao = getFileExtension(fotoOriginal.getName());
                nomeFoto = "foto" + extensao;
                File fotoDestino = new File(pastaAtual, nomeFoto);
                
                Files.copy(fotoOriginal.toPath(), fotoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Regenera o conteúdo MD com a foto
                gerarConteudoMD(nomeFoto);
                
                // Atualiza o preview se estiver visível
                if (webViewPreview.isVisible()) {
                    carregarPreview(conteudoMD);
                }
                
                mostrarAlerta("Sucesso", "Foto adicionada com sucesso!");
            } catch (IOException e) {
                mostrarAlerta("Erro", "Erro ao copiar a foto: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void mostrarCode(ActionEvent event) {
        atualizarEstiloTabs(true);
        scrollPaneCode.setVisible(true);
        webViewPreview.setVisible(false);
    }
    
    @FXML
    private void mostrarPreview(ActionEvent event) {
        atualizarEstiloTabs(false);
        scrollPaneCode.setVisible(false);
        webViewPreview.setVisible(true);
        
        // Sempre atualiza o preview quando muda para a tab
        if (conteudoMD != null) {
            carregarPreview(conteudoMD);
        } else {
            // Se não houver conteúdo ainda, gera
            gerarConteudoMD(nomeFoto);
        }
    }
    
    private void atualizarEstiloTabs(boolean codeAtivo) {
        if (codeAtivo) {
            btnTabCode.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: #FD7E14; -fx-text-fill: #24292E; -fx-font-weight: bold; -fx-cursor: hand;");
            btnTabPreview.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #586069; -fx-cursor: hand;");
        } else {
            btnTabCode.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #586069; -fx-cursor: hand;");
            btnTabPreview.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: #FD7E14; -fx-text-fill: #24292E; -fx-font-weight: bold; -fx-cursor: hand;");
        }
    }
    
    private void gerarConteudoMD(String nomeFoto) {
        try {
            Map<String, String> dadosAluno = Aluno.buscarDadosPorEmail(emailAluno);
            String nomeAluno = dadosAluno.get("nome");
            String emailAlunoDados = dadosAluno.get("email");
            
            if (nomeAluno == null || nomeAluno.isBlank()) {
                conteudoMD = "# README.md\n\n*Carregando dados...*";
                textAreaMarkdown.setText(conteudoMD);
                carregarPreview(conteudoMD);
                return;
            }
            
            Map<String, String> apresentacao = buscarApresentacaoMaisRecente();
            List<Map<String, String>> secoesApi = buscarTodasSecoesApiCompletas();
            
            conteudoMD = gerarConteudoMD(nomeAluno, emailAlunoDados, apresentacao, secoesApi, nomeFoto);
            
            // Atualiza a text area
            textAreaMarkdown.setText(conteudoMD);
            
            // Atualiza o preview se estiver visível
            if (webViewPreview.isVisible()) {
                carregarPreview(conteudoMD);
            }
            
            // Salva o README.md na pasta se já foi criada
            if (pastaAtual != null && pastaAtual.exists()) {
                File arquivoMD = new File(pastaAtual, "README.md");
                try (FileWriter writer = new FileWriter(arquivoMD)) {
                    writer.write(conteudoMD);
                } catch (IOException e) {
                    mostrarAlerta("Erro", "Erro ao salvar README.md: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            conteudoMD = "# Erro\n\n*Erro ao carregar dados: " + e.getMessage() + "*";
            textAreaMarkdown.setText(conteudoMD);
            carregarPreview(conteudoMD);
        }
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
        
        // Foto do aluno
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
        md.append("## Contatos\n\n");
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
            md.append("## Meus Principais Conhecimentos\n\n");
            md.append(conhecimentos).append("\n\n");
        }
        
        md.append("<hr>\n\n");
        
        // Projetos API
        if (!secoesApi.isEmpty()) {
            md.append("## Meus Projetos\n\n");
            
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
                    md.append("#### Repositório do Projeto: [GitHub](").append(linkRepositorio).append(")\n\n");
                    md.append("<hr>\n\n");
                }
                
                md.append("#### Tecnologias Utilizadas\n\n");
                String tecnologias = secao.getOrDefault("tecnologias", "");
                if (!tecnologias.isBlank()) {
                    md.append(tecnologias).append("\n\n");
                }
                
                md.append("#### Contribuições Pessoais\n\n");
                String contribuicoes = secao.getOrDefault("contribuicoes", "");
                if (!contribuicoes.isBlank()) {
                    md.append(contribuicoes).append("\n\n");
                }
                
                md.append("#### Hard Skills\n\n");
                md.append("Exercitei as seguintes Hard Skills durante esse projeto:\n\n");
                String hardSkills = secao.getOrDefault("hard_skills", "");
                if (!hardSkills.isBlank()) {
                    md.append(hardSkills).append("\n\n");
                }
                
                md.append("#### Soft Skills\n\n");
                String softSkills = secao.getOrDefault("soft_skills", "");
                if (!softSkills.isBlank()) {
                    md.append(softSkills).append("\n\n");
                }
                
                md.append("---\n\n");
            }
        }
        
        return md.toString();
    }
    
    private void carregarPreview(String markdown) {
        WebEngine engine = webViewPreview.getEngine();
        
        // Se há uma foto na pasta, precisa converter para data URI ou usar caminho local
        String fotoDataUri = "";
        if (nomeFoto != null && pastaAtual != null && pastaAtual.exists()) {
            File fotoFile = new File(pastaAtual, nomeFoto);
            if (fotoFile.exists()) {
                try {
                    // Converte a imagem para data URI
                    byte[] fotoBytes = Files.readAllBytes(fotoFile.toPath());
                    String extensao = getFileExtension(nomeFoto).substring(1).toLowerCase();
                    String mimeType = "image/jpeg";
                    if (extensao.equals("png")) mimeType = "image/png";
                    else if (extensao.equals("gif")) mimeType = "image/gif";
                    else if (extensao.equals("bmp")) mimeType = "image/bmp";
                    
                    String base64 = java.util.Base64.getEncoder().encodeToString(fotoBytes);
                    fotoDataUri = "data:" + mimeType + ";base64," + base64;
                    
                    // Substitui o nome da foto no markdown pelo data URI
                    markdown = markdown.replace(nomeFoto, fotoDataUri);
                } catch (IOException e) {
                    // Se não conseguir ler a foto, mantém o nome do arquivo
                }
            }
        }
        
        // Escapa o markdown para JavaScript
        String escapedMarkdown = escapeForJavaScript(markdown);
        
        String html = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        html, body {
                            overflow-x: hidden !important;
                            max-width: 100%% !important;
                        }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Segoe UI Emoji', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                            line-height: 1.6; padding: 20px; color: #333; background: #fff;
                            max-width: 100%%; margin: 0 auto;
                        }
                        #content {
                            max-width: 100%% !important;
                            overflow-wrap: break-word !important;
                            word-wrap: break-word !important;
                            word-break: break-word !important;
                        }
                        h1, h2, h3 { border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
                        h1 { font-size: 2em; } h2 { font-size: 1.5em; } h3 { font-size: 1.25em; }
                        pre { 
                            background-color: #f6f8fa; 
                            border-radius: 3px; 
                            padding: 16px; 
                            overflow: auto;
                            white-space: pre-wrap !important;
                            max-width: 100%% !important;
                        }
                        code { 
                            background-color: #f6f8fa; 
                            border-radius: 3px; 
                            padding: 2px 4px; 
                            font-family: 'Courier New', monospace;
                            max-width: 100%% !important;
                        }
                        table { 
                            border-collapse: collapse;
                            max-width: 100%% !important;
                        }
                        table th, table td { border: 1px solid #dfe2e5; padding: 6px 13px; }
                        img {
                            max-width: 100%% !important;
                            height: auto;
                        }
                        div[align="center"] {
                            text-align: center;
                        }
                        div[align="center"] img {
                            display: block;
                            margin: 0 auto;
                        }
                    </style>
                    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
                </head>
                <body>
                    <div id="content"></div>
                    <script>
                        const markdown = "%s";;
                        document.getElementById('content').innerHTML = marked.parse(markdown);
                    </script>
                </body>
                </html>
                """, escapedMarkdown);
        
        engine.loadContent(html);
    }
    
    /**
     * Escapa caracteres especiais para inserir o Markdown dentro do script JS.
     */
    private String escapeForJavaScript(String text) {
        if (text == null) return "";

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\'': sb.append("\\'"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32 || c > 126) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private String sanitizeFileName(String nome) {
        if (nome == null) return "";
        return nome.replaceAll("[<>:\"/\\|?*]", "_").trim();
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return ".jpg";
    }
    
    /**
     * Busca uma foto na pasta selecionada.
     * Prioriza arquivos com nome "foto" seguido de extensão de imagem.
     * Se não encontrar, busca qualquer arquivo de imagem na pasta.
     * 
     * @param pasta A pasta onde buscar a foto
     * @return O nome do arquivo da foto encontrada, ou null se não encontrar
     */
    private String buscarFotoNaPasta(File pasta) {
        if (pasta == null || !pasta.exists() || !pasta.isDirectory()) {
            return null;
        }
        
        File[] arquivos = pasta.listFiles();
        if (arquivos == null) {
            return null;
        }
        
        // Extensões de imagem suportadas
        String[] extensoesImagem = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
        
        // Primeiro, tenta encontrar arquivo com nome "foto" + extensão
        for (String extensao : extensoesImagem) {
            String nomeFotoEsperado = "foto" + extensao;
            File fotoEsperada = new File(pasta, nomeFotoEsperado);
            if (fotoEsperada.exists() && fotoEsperada.isFile()) {
                return nomeFotoEsperado;
            }
        }
        
        // Se não encontrou "foto.extensao", busca qualquer arquivo de imagem
        for (File arquivo : arquivos) {
            if (arquivo.isFile()) {
                String nomeArquivo = arquivo.getName().toLowerCase();
                for (String extensao : extensoesImagem) {
                    if (nomeArquivo.endsWith(extensao)) {
                        return arquivo.getName();
                    }
                }
            }
        }
        
        return null;
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

