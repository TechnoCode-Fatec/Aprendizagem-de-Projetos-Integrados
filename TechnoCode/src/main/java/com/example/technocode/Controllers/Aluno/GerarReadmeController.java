package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Controllers.LoginController;
import com.example.technocode.model.Aluno;
import com.example.technocode.model.dao.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
    private Button btnAdicionarFoto;
    
    @FXML
    private Button btnGerarReadme;
    
    @FXML
    private Button btnSalvarPasta;
    
    @FXML
    private VBox cardCodePreview;
    
    @FXML
    private Label labelSecoesAprovadas;
    
    @FXML
    private Label labelStatusFoto;
    
    @FXML
    private WebView webViewPreview;
    
    private String emailAluno;
    private File fotoSelecionada;
    private String nomeFoto;
    private String conteudoMD;
    private int totalSecoesAprovadas;
    
    @FXML
    public void initialize() {
        emailAluno = LoginController.getEmailLogado();
        
        // Conta seções aprovadas
        contarSecoesAprovadas();
        
        // Mantém o card Preview invisível até gerar o README
        if (cardCodePreview != null) {
            cardCodePreview.setVisible(false);
            cardCodePreview.setManaged(false);
        }
    }
    
    private void contarSecoesAprovadas() {
        try {
            Map<String, Integer> resultado = Aluno.contarSecoesPorAluno(emailAluno);
            totalSecoesAprovadas = resultado.getOrDefault("aprovadas", 0);
            labelSecoesAprovadas.setText("✓ Seções aprovadas: " + totalSecoesAprovadas);
        } catch (Exception e) {
            e.printStackTrace();
            labelSecoesAprovadas.setText("Erro ao carregar seções aprovadas");
        }
    }
    
    @FXML
    private void adicionarFoto(ActionEvent event) {
        Stage stage = (Stage) btnAdicionarFoto.getScene().getWindow();
        
        // Escolhe a foto
        FileChooser fotoChooser = new FileChooser();
        fotoChooser.setTitle("Escolha a foto do aluno");
        fotoChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
        );
        File fotoSelecionada = fotoChooser.showOpenDialog(stage);
        
        if (fotoSelecionada != null) {
            this.fotoSelecionada = fotoSelecionada;
            String extensao = getFileExtension(fotoSelecionada.getName());
                nomeFoto = "foto" + extensao;
            
            // Atualiza label de status
            labelStatusFoto.setText("Foto: " + fotoSelecionada.getName());
            
            // Habilita botão de gerar README
            btnGerarReadme.setDisable(false);
            
            mostrarAlerta("Sucesso", "Foto selecionada! Agora você pode gerar o README.");
        }
    }
    
    @FXML
    private void gerarReadme(ActionEvent event) {
        if (fotoSelecionada == null || nomeFoto == null) {
            mostrarAlerta("Aviso", "Por favor, adicione uma foto primeiro.");
            return;
        }
        
        if (totalSecoesAprovadas == 0) {
            mostrarAlerta("Aviso", "Você não possui seções aprovadas para gerar o README.");
            return;
        }
        
        // Gera o conteúdo MD apenas com seções aprovadas
        gerarConteudoMDComSecoesAprovadas();
        
        // Mostra o card Preview agora que o README foi gerado
        if (cardCodePreview != null) {
            cardCodePreview.setVisible(true);
            cardCodePreview.setManaged(true);
        }
        
        // Carrega o preview com o conteúdo gerado
        carregarPreview(conteudoMD);
        
        // Habilita botão de salvar pasta
        btnSalvarPasta.setVisible(true);
        btnSalvarPasta.setManaged(true);
        
        mostrarAlerta("Sucesso", "README gerado com sucesso! Agora você pode salvar a pasta.");
    }
    
    @FXML
    private void salvarPasta(ActionEvent event) {
        if (conteudoMD == null || conteudoMD.isBlank()) {
            mostrarAlerta("Aviso", "Por favor, gere o README primeiro.");
            return;
        }
        
        if (fotoSelecionada == null) {
            mostrarAlerta("Aviso", "Por favor, adicione uma foto primeiro.");
            return;
        }
        
        try {
            Stage stage = (Stage) btnSalvarPasta.getScene().getWindow();
            
            // Permite selecionar o diretório onde será criada a pasta "portfolio"
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecione onde criar a pasta 'portfolio'");
            
            File diretorioSelecionado = directoryChooser.showDialog(stage);
            
            if (diretorioSelecionado == null) {
                return;
            }
            
            // Cria a pasta "portfolio" dentro do diretório selecionado
            File pastaPortfolio = new File(diretorioSelecionado, "portfolio");
            
            // Se a pasta não existir, cria ela
            if (!pastaPortfolio.exists()) {
                if (!pastaPortfolio.mkdirs()) {
                    mostrarAlerta("Erro", "Não foi possível criar a pasta 'portfolio'.");
                    return;
                }
            }
            
            // Copia a foto para a pasta portfolio
            String extensao = getFileExtension(fotoSelecionada.getName());
            String nomeFotoFinal = "foto" + extensao;
            File fotoDestino = new File(pastaPortfolio, nomeFotoFinal);
            Files.copy(fotoSelecionada.toPath(), fotoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // Atualiza o nome da foto no markdown se necessário
            String conteudoFinal = conteudoMD;
            if (!conteudoFinal.contains(nomeFotoFinal)) {
                conteudoFinal = conteudoFinal.replace(nomeFoto, nomeFotoFinal);
            }
            
            // Salva o README.md na pasta portfolio
            File arquivoMD = new File(pastaPortfolio, "README.md");
            try (FileWriter writer = new FileWriter(arquivoMD)) {
                writer.write(conteudoFinal);
            }
            
            mostrarAlerta("Sucesso", "Pasta 'portfolio' criada com sucesso!\n\nLocal: " + pastaPortfolio.getAbsolutePath() + "\n\nA pasta contém:\n- README.md\n- " + nomeFotoFinal);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao salvar pasta: " + e.getMessage());
        }
    }
    
    
    private void gerarConteudoMDComSecoesAprovadas() {
        try {
            Map<String, String> dadosAluno = Aluno.buscarDadosPorEmail(emailAluno);
            String nomeAluno = dadosAluno.get("nome");
            String emailAlunoDados = dadosAluno.get("email");
            
            if (nomeAluno == null || nomeAluno.isBlank()) {
                conteudoMD = "# README.md\n\n*Erro ao carregar dados do aluno.*";
                return;
            }
            
            // Busca apenas seções aprovadas
            Map<String, String> apresentacao = buscarApresentacaoAprovada();
            List<Map<String, String>> secoesApi = buscarSecoesApiAprovadas();
            
            conteudoMD = gerarConteudoMD(nomeAluno, emailAlunoDados, apresentacao, secoesApi, nomeFoto);
        } catch (Exception e) {
            e.printStackTrace();
            conteudoMD = "# Erro\n\n*Erro ao gerar README: " + e.getMessage() + "*";
        }
    }
    
    private Map<String, String> buscarApresentacaoAprovada() {
        Map<String, String> apresentacao = new java.util.HashMap<>();
        try (Connection conn = new Connector().getConnection()) {
            // Busca apenas apresentação aprovada (onde todos os campos estão aprovados)
            String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, " +
                        "link_github, link_linkedin, principais_conhecimentos " +
                        "FROM secao_apresentacao " +
                        "WHERE aluno = ? AND versao = (SELECT MAX(versao) FROM secao_apresentacao WHERE aluno = ?) " +
                        "AND status_nome = 'Aprovado' AND status_idade = 'Aprovado' " +
                        "AND status_curso = 'Aprovado' AND status_motivacao = 'Aprovado' " +
                        "AND status_historico = 'Aprovado' AND status_historico_profissional = 'Aprovado' " +
                        "AND status_github = 'Aprovado' AND status_linkedin = 'Aprovado' " +
                        "AND status_conhecimentos = 'Aprovado'";
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
    
    private List<Map<String, String>> buscarSecoesApiAprovadas() {
        List<Map<String, String>> secoesApi = new java.util.ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            // Busca apenas seções API aprovadas (onde todos os campos estão aprovados)
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
                        "AND sa.status_empresa = 'Aprovado' AND sa.status_descricao_empresa = 'Aprovado' " +
                        "AND sa.status_repositorio = 'Aprovado' AND sa.status_problema = 'Aprovado' " +
                        "AND sa.status_solucao = 'Aprovado' AND sa.status_tecnologias = 'Aprovado' " +
                        "AND sa.status_contribuicoes = 'Aprovado' AND sa.status_hard_skills = 'Aprovado' " +
                        "AND sa.status_soft_skills = 'Aprovado' " +
                        "ORDER BY sa.ano ASC, sa.semestre_ano ASC";
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
        md.append("## Contatos\n");
        md.append("<ul style=\"display: flex; flex-direction: column; align-items: left;\">\n");
        
        String github = apresentacao.getOrDefault("link_github", "");
        if (!github.isBlank()) {
            md.append("    <li style=\"margin-bottom: 10px;\">\n");
            md.append("         <a href=\"").append(github).append("\"><img align=\"center\" alt=\"GitHub\" src = \"https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white\"/></a>\n");
            md.append("    </li>\n");
        }
        
        String linkedin = apresentacao.getOrDefault("link_linkedin", "");
        if (!linkedin.isBlank()) {
            md.append("    <li style=\"margin-bottom: 10px\">\n");
            md.append("        <a href=\"").append(linkedin).append("\" target=\"_blank\"><img align=\"center\" alt=\"Linkedin\" src=\"https://img.shields.io/badge/-LinkedIn-%230077B5?style=for-the-badge&logo=linkedin&logoColor=white\" target=\"_blank\"></a>\n");
            md.append("    </li>\n");
        }
        
        md.append("    <li style=\"margin-bottom: 10px\">\n");
        md.append("        <a href = \"mailto:").append(emailAluno).append("\"><img align=\"center\" alt=\"Gmail\" src=\"https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white\" target=\"_blank\"></a>\n");
        md.append("    </li>\n");
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
        if (markdown == null || markdown.isBlank()) {
            markdown = "# README.md\n\n*Nenhum conteúdo disponível.*";
        }
        
        WebEngine engine = webViewPreview.getEngine();
        
        // Se há uma foto selecionada, precisa converter para data URI
        String fotoDataUri = "";
        if (nomeFoto != null && fotoSelecionada != null && fotoSelecionada.exists()) {
                try {
                    // Converte a imagem para data URI
                byte[] fotoBytes = Files.readAllBytes(fotoSelecionada.toPath());
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
                e.printStackTrace();
            }
        }
        
        // Processa o markdown mantendo HTML bruto
        String processedContent = processMarkdownWithHtml(markdown);
        
        // Escapa para HTML (para inserir diretamente no HTML, preservando tags HTML)
        String htmlEscapedContent = escapeHtmlForEmbedding(processedContent);
        
        String html = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        html, body {
                            overflow-x: hidden !important;
                            max-width: 100%% !important;
                            margin: 0;
                            padding: 0;
                        }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Segoe UI Emoji', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                            line-height: 1.6; 
                            padding: 20px; 
                            color: #24292e; 
                            background: #fff;
                            max-width: 100%%; 
                            margin: 0 auto;
                        }
                        #content {
                            max-width: 100%% !important;
                            overflow-wrap: break-word !important;
                            word-wrap: break-word !important;
                            word-break: break-word !important;
                        }
                        h1, h2, h3, h4, h5, h6 { 
                            border-bottom: 1px solid #eaecef; 
                            padding-bottom: 0.3em; 
                            margin-top: 24px;
                            margin-bottom: 16px;
                        }
                        h1 { font-size: 2em; } 
                        h2 { font-size: 1.5em; } 
                        h3 { font-size: 1.25em; }
                        h4 { font-size: 1em; }
                        p {
                            margin-bottom: 16px;
                        }
                        ul, ol {
                            margin-bottom: 16px;
                            padding-left: 2em;
                        }
                        li {
                            margin-bottom: 8px;
                        }
                        pre { 
                            background-color: #f6f8fa; 
                            border-radius: 6px; 
                            padding: 16px; 
                            overflow: auto;
                            white-space: pre-wrap !important;
                            max-width: 100%% !important;
                            font-size: 85%%;
                            line-height: 1.45;
                        }
                        code { 
                            background-color: rgba(175, 184, 193, 0.2); 
                            border-radius: 3px; 
                            padding: 2px 4px; 
                            font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
                            font-size: 85%%;
                        }
                        pre code {
                            background-color: transparent;
                            padding: 0;
                        }
                        table { 
                            border-collapse: collapse;
                            max-width: 100%% !important;
                            margin-bottom: 16px;
                            width: 100%%;
                        }
                        table th, table td { 
                            border: 1px solid #dfe2e5; 
                            padding: 6px 13px; 
                        }
                        table th {
                            font-weight: 600;
                            background-color: #f6f8fa;
                        }
                        img {
                            max-width: 100%% !important;
                            height: auto;
                            display: inline-block;
                        }
                        div[align="center"] {
                            text-align: center;
                        }
                        div[align="center"] img {
                            display: block;
                            margin: 0 auto;
                        }
                        hr {
                            border: none;
                            border-top: 1px solid #eaecef;
                            margin: 24px 0;
                        }
                        a {
                            color: #0366d6;
                            text-decoration: none;
                            cursor: default;
                            pointer-events: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        /* Estilos para badges */
                        a img[src*="img.shields.io"] {
                            margin: 2px;
                            vertical-align: middle;
                        }
                        ul[style*="flex"] {
                            display: flex;
                            flex-direction: column;
                            align-items: flex-start;
                            list-style: none;
                            padding-left: 0;
                        }
                        ul[style*="flex"] li {
                            margin-bottom: 10px;
                        }
                    </style>
                </head>
                <body>
                    <div id="content" style="display:none;">%s</div>
                    <div id="loading">Carregando...</div>
                    <script>
                        const contentDiv = document.getElementById('content');
                        const loadingDiv = document.getElementById('loading');
                        const rawContent = contentDiv.innerHTML;
                        
                        function renderContent() {
                            // Primeiro, tenta usar marked.js se disponível
                            if (typeof marked !== 'undefined') {
                                try {
                                    // Configura marked para GitHub Flavored Markdown
                                    marked.setOptions({
                                        breaks: true,
                                        gfm: true,
                                        headerIds: true,
                                        mangle: false
                                    });
                                    
                                    // Processa o markdown - HTML bruto será mantido
                                    let htmlContent = marked.parse(rawContent);
                                    
                                    // Insere o conteúdo processado diretamente
                                    contentDiv.innerHTML = htmlContent;
                                    
                                    // Desabilita todos os links após inserir o conteúdo
                                    disableAllLinks(contentDiv);
                                    
                                    loadingDiv.style.display = 'none';
                                    contentDiv.style.display = 'block';
                                    return;
                                } catch (e) {
                                    console.error('Erro ao renderizar com marked:', e);
                                }
                            }
                            
                            // Se marked não funcionou ou não está disponível, mostra HTML diretamente
                            // Isso garante que badges HTML apareçam corretamente
                            contentDiv.innerHTML = rawContent;
                            
                            // Desabilita todos os links após inserir o conteúdo
                            disableAllLinks(contentDiv);
                            
                            loadingDiv.style.display = 'none';
                            contentDiv.style.display = 'block';
                        }
                        
                        // Função para desabilitar todos os links
                        function disableAllLinks(container) {
                            const links = container.querySelectorAll('a');
                            links.forEach(function(link) {
                                // Previne o comportamento padrão do link
                                link.addEventListener('click', function(e) {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    return false;
                                });
                                // Remove o atributo href ou o substitui por javascript:void(0)
                                link.setAttribute('href', 'javascript:void(0)');
                                // Adiciona estilo para mostrar que não é clicável
                                link.style.cursor = 'default';
                                link.style.pointerEvents = 'none';
                            });
                        }
                        
                        // Tenta carregar marked.js
                        var script = document.createElement('script');
                        script.src = 'https://cdn.jsdelivr.net/npm/marked@11.1.1/marked.min.js';
                        script.onload = function() {
                            // Aguarda um pouco para garantir que marked está totalmente carregado
                            setTimeout(renderContent, 50);
                        };
                        script.onerror = function() {
                            // Se marked não carregar, renderiza HTML diretamente
                            // Isso garante que badges apareçam mesmo sem marked
                            renderContent();
                        };
                        document.head.appendChild(script);
                        
                        // Timeout de segurança: se marked demorar muito, renderiza diretamente
                        setTimeout(function() {
                            if (loadingDiv.style.display !== 'none') {
                                renderContent();
                            }
                        }, 2000);
                    </script>
                </body>
                </html>
                """, htmlEscapedContent);
        
        try {
            // Armazena o HTML para uso no listener
            final String finalHtml = html;
            
            // Previne navegação interceptando mudanças de URL
            engine.locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals("about:blank") && !newValue.startsWith("data:")) {
                    // Cancela a navegação recarregando o conteúdo original
                    engine.loadContent(finalHtml);
                }
            });
            
            engine.loadContent(html);
        } catch (Exception e) {
            e.printStackTrace();
            // Tenta carregar novamente com conteúdo simples
            String htmlSimples = "<html><body><p>Erro ao carregar preview. Tente novamente.</p></body></html>";
            engine.loadContent(htmlSimples);
        }
    }
    
    /**
     * Processa o markdown mantendo HTML bruto (como badges) intacto.
     * O marked.js será usado apenas para converter markdown, mantendo HTML existente.
     */
    private String processMarkdownWithHtml(String markdown) {
        // O markdown já contém HTML bruto (badges), então apenas retornamos
        // O marked.js vai processar o markdown mas manterá o HTML bruto
        return markdown;
    }
    
    /**
     * Escapa HTML para inserir diretamente no HTML (preserva tags HTML mas escapa caracteres problemáticos)
     */
    private String escapeHtmlForEmbedding(String html) {
        if (html == null) return "";
        // Não escapa tags HTML, apenas caracteres que podem quebrar o HTML
        return html.replace("</script>", "<\\/script>")
                   .replace("</style>", "<\\/style>")
                   .replace("<!--", "<\\!--");
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
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return ".jpg";
    }
    
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

