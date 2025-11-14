package com.example.technocode.Controllers.Aluno;

import com.example.technocode.Services.NavigationService;
import com.example.technocode.model.dao.Connector;
import com.example.technocode.model.SecaoApresentacao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class AlunoVisualizarApresentacaoController {

    // Identificador da seção usando classe modelo
    private SecaoApresentacao secaoApresentacao;

    @FXML private TextArea alunoTextNome;
    @FXML private TextArea alunoTextIdade;
    @FXML private TextArea alunoTextCurso;
    @FXML private TextArea alunoTextMotivacao;
    @FXML private TextArea alunoTextHistorico;
    @FXML private TextArea alunoTextGithub;
    @FXML private TextArea alunoTextLinkedin;
    @FXML private TextArea alunoTextConhecimentos;
    @FXML private Button btnFeedback;

    // Recebe identificador da secao e carrega dados
    public void setIdentificadorSecao(String aluno, int versao) {
        // Cria objeto SecaoApresentacao para identificar a seção
        this.secaoApresentacao = new SecaoApresentacao(aluno, versao);
        carregarSecaoAluno();
        if (btnFeedback != null && secaoApresentacao != null) {
            boolean existe = SecaoApresentacao.verificarFeedback(secaoApresentacao.getEmailAluno(), secaoApresentacao.getVersao());
            btnFeedback.setDisable(!existe);
        }
    }

    // Carrega dados da secao_apresentacao
    public void carregarSecaoAluno() {
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;
        String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos " +
                "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";
        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, secaoApresentacao.getEmailAluno());
            pst.setInt(2, secaoApresentacao.getVersao());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    if (alunoTextNome != null) alunoTextNome.setText(rs.getString("nome"));
                    if (alunoTextIdade != null) {
                        String dataNascimentoStr = rs.getString("idade"); // ex: "2002-05-10"
                        if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                            LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);
                            LocalDate hoje = LocalDate.now();
                            int idade = Period.between(dataNascimento, hoje).getYears();
                            alunoTextIdade.setText(String.valueOf(idade));
                        }
                    }
                    if (alunoTextCurso != null) alunoTextCurso.setText(rs.getString("curso"));
                    if (alunoTextMotivacao != null) alunoTextMotivacao.setText(rs.getString("motivacao"));
                    if (alunoTextHistorico != null) alunoTextHistorico.setText(rs.getString("historico"));
                    if (alunoTextGithub != null) alunoTextGithub.setText(rs.getString("link_github"));
                    if (alunoTextLinkedin != null) alunoTextLinkedin.setText(rs.getString("link_linkedin"));
                    if (alunoTextConhecimentos != null) alunoTextConhecimentos.setText(rs.getString("principais_conhecimentos"));
                }
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar seção do aluno", e);
        }
    }

    @FXML
    private void verFeedback(ActionEvent event) throws IOException {
        if (secaoApresentacao != null) {
            final String emailAluno = secaoApresentacao.getEmailAluno();
            final int versao = secaoApresentacao.getVersao();
            
            NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-feedback-apresentacao.fxml",
                controller -> {
                    if (controller instanceof AlunoFeedbackApresentacaoController) {
                        ((AlunoFeedbackApresentacaoController) controller).setIdentificadorSecao(emailAluno, versao);
                    }
                });
        }
    }

    @FXML
    private void verHistorico(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/aluno-historico.fxml");
    }

    @FXML
    private void voltarTelaInicial(ActionEvent event) throws IOException {
        NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/sessoes-atuais.fxml",
            controller -> {
                if (controller instanceof SessoesAtuaisAlunoController) {
                    ((SessoesAtuaisAlunoController) controller).recarregarSecoes();
                }
            });
    }

    private void mostrarErro(String titulo, Exception e) {
        System.err.println(titulo + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Carrega os dados da versão atual de apresentação e abre o formulário preenchido
     * para criar uma nova versão baseada na anterior
     */
    @FXML
    private void carregarVersaoAnteriorApresentacao(ActionEvent event) {
        if (secaoApresentacao == null || secaoApresentacao.getEmailAluno() == null) return;

        String sql = "SELECT nome, idade, curso, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos " +
                     "FROM secao_apresentacao WHERE aluno = ? AND versao = ?";

        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, secaoApresentacao.getEmailAluno());
            ps.setInt(2, secaoApresentacao.getVersao());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtém todos os dados da versão atual
                    String nome = rs.getString("nome");
                    String dataNascimento = rs.getString("idade"); // Armazena como data no banco
                    String curso = rs.getString("curso");
                    String motivacao = rs.getString("motivacao");
                    String historico = rs.getString("historico");
                    String historicoProfissional = rs.getString("historico_profissional");
                    String github = rs.getString("link_github");
                    String linkedin = rs.getString("link_linkedin");
                    String conhecimentos = rs.getString("principais_conhecimentos");
                    
                    // Carrega o formulário e preenche os dados
                    final String nomeFinal = nome;
                    final String dataNascimentoFinal = dataNascimento;
                    final String cursoFinal = curso;
                    final String motivacaoFinal = motivacao;
                    final String historicoFinal = historico;
                    final String historicoProfissionalFinal = historicoProfissional;
                    final String githubFinal = github;
                    final String linkedinFinal = linkedin;
                    final String conhecimentosFinal = conhecimentos;
                    
                    NavigationService.navegarParaTelaInterna(event, "/com/example/technocode/Aluno/formulario-apresentacao.fxml",
                        controller -> {
                            if (controller instanceof FormularioApresentacaoController) {
                                ((FormularioApresentacaoController) controller).setDadosVersaoAnterior(
                                    nomeFinal, dataNascimentoFinal, cursoFinal, motivacaoFinal,
                                    historicoFinal, historicoProfissionalFinal, githubFinal, linkedinFinal, conhecimentosFinal
                                );
                            }
                        });
                    
                } else {
                    System.err.println("Seção de apresentação não encontrada para criar nova versão");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar dados da versão anterior de apresentação: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erro ao carregar formulário de apresentação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
