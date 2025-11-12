package com.example.technocode.model;

import com.example.technocode.model.dao.Connector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe modelo para representar uma Seção de Apresentação
 */
public class SecaoApresentacao {
    private String emailAluno;
    private String nome;
    private Date idade; // Data de nascimento
    private String curso;
    private Integer versao;
    private String motivacao;
    private String historico;
    private String historicoProfissional;
    private String linkGithub;
    private String linkLinkedin;
    private String principaisConhecimentos;

    /**
     * Constructor completo para SecaoApresentacao
     */
    public SecaoApresentacao(String emailAluno, String nome, Date idade, String curso,
                            Integer versao, String motivacao, String historico, String historicoProfissional,
                            String linkGithub, String linkLinkedin, String principaisConhecimentos) {
        this.emailAluno = emailAluno;
        this.nome = nome;
        this.idade = idade;
        this.curso = curso;
        this.versao = versao;
        this.motivacao = motivacao;
        this.historico = historico;
        this.historicoProfissional = historicoProfissional;
        this.linkGithub = linkGithub;
        this.linkLinkedin = linkLinkedin;
        this.principaisConhecimentos = principaisConhecimentos;
    }

    /**
     * Constructor parcial para identificação de seção (sem conteúdo)
     */
    public SecaoApresentacao(String emailAluno, Integer versao) {
        this.emailAluno = emailAluno;
        this.versao = versao;
    }

    // Getters e Setters
    public String getEmailAluno() {
        return emailAluno;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getIdade() {
        return idade;
    }

    public void setIdade(Date idade) {
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getMotivacao() {
        return motivacao;
    }

    public void setMotivacao(String motivacao) {
        this.motivacao = motivacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getHistoricoProfissional() {
        return historicoProfissional;
    }

    public void setHistoricoProfissional(String historicoProfissional) {
        this.historicoProfissional = historicoProfissional;
    }

    public String getLinkGithub() {
        return linkGithub;
    }

    public void setLinkGithub(String linkGithub) {
        this.linkGithub = linkGithub;
    }

    public String getLinkLinkedin() {
        return linkLinkedin;
    }

    public void setLinkLinkedin(String linkLinkedin) {
        this.linkLinkedin = linkLinkedin;
    }

    public String getPrincipaisConhecimentos() {
        return principaisConhecimentos;
    }

    public void setPrincipaisConhecimentos(String principaisConhecimentos) {
        this.principaisConhecimentos = principaisConhecimentos;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cadastra uma nova seção de apresentação no banco de dados
     */
    public void cadastrar() {
        try (Connection con = new Connector().getConnection()) {
            String insertSql = "INSERT INTO secao_apresentacao (aluno, nome, idade, curso, versao, motivacao, historico, historico_profissional, link_github, link_linkedin, principais_conhecimentos) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(insertSql);
            pst.setString(1, this.emailAluno);
            pst.setString(2, this.nome);
            pst.setDate(3, this.idade);
            pst.setString(4, this.curso);
            pst.setInt(5, this.versao);
            pst.setString(6, this.motivacao);
            pst.setString(7, this.historico);
            pst.setString(8, this.historicoProfissional);
            pst.setString(9, this.linkGithub);
            pst.setString(10, this.linkLinkedin);
            pst.setString(11, this.principaisConhecimentos);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao inserir nova Sessão Apresentação!", ex);
        }
    }

    /**
     * Busca lista de seções de apresentação de um aluno (versões mais recentes)
     */
    public static List<Map<String, String>> buscarSecoesPorAluno(String emailAluno) {
        List<Map<String, String>> secoesApresentacao = new ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            // Busca apenas a versão mais recente de cada seção de apresentação
            String selectSecoesApresentacao = "SELECT nome, versao " +
                    "FROM secao_apresentacao s1 " +
                    "WHERE aluno = ? AND versao = (" +
                    "    SELECT MAX(versao) " +
                    "    FROM secao_apresentacao s2 " +
                    "    WHERE s2.aluno = s1.aluno" +
                    ")";
            PreparedStatement pst = conn.prepareStatement(selectSecoesApresentacao);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                String versao = rs.getString("versao");

                Map<String, String> secao = new HashMap<>();
                secao.put("id", nome);
                secao.put("empresa", "Apresentação");
                secao.put("versao", versao);
                secao.put("tipo", "apresentacao");
                secoesApresentacao.add(secao);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return secoesApresentacao;
    }

    /**
     * Busca a próxima versão disponível para uma seção de apresentação
     */
    public static int getProximaVersao(String emailAluno) {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT MAX(versao) as max_versao FROM secao_apresentacao WHERE aluno = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int maxVersao = rs.getInt("max_versao");
                return maxVersao + 1;
            }
            return 1; // Primeira versão
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar próxima versão de apresentação", e);
        }
    }

    /**
     * Busca o histórico de versões de apresentação do aluno
     */
    public static List<Map<String, String>> buscarHistoricoVersoes(String emailAluno) {
        List<Map<String, String>> historico = new ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT nome, versao FROM secao_apresentacao WHERE aluno = ? ORDER BY versao DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Map<String, String> versao = new HashMap<>();
                versao.put("nome", rs.getString("nome"));
                versao.put("versao", rs.getString("versao"));
                versao.put("tipo", "apresentacao");
                historico.add(versao);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico de apresentações", e);
        }
        return historico;
    }

    /**
     * Verifica se existe feedback para uma seção de apresentação
     */
    public static boolean verificarFeedback(String emailAluno, int versao) {
        String sql = "SELECT COUNT(*) as count FROM secao_apresentacao WHERE aluno = ? AND versao = ? " +
                     "AND (status_nome IS NOT NULL OR status_idade IS NOT NULL OR status_curso IS NOT NULL " +
                     "OR status_motivacao IS NOT NULL OR status_historico IS NOT NULL OR status_github IS NOT NULL " +
                     "OR status_linkedin IS NOT NULL OR status_conhecimentos IS NOT NULL OR status_historico_profissional IS NOT NULL)";
        try (Connection c = new Connector().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, emailAluno);
            ps.setInt(2, versao);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca o horário do feedback para uma seção de apresentação
     * @return String formatada com data e hora, ou null se não existir feedback
     * Nota: Como os feedbacks agora estão na própria tabela secao_apresentacao,
     * retornamos null pois não há mais um campo horario separado para feedback
     */
    public static String buscarHorarioFeedback(String emailAluno, int versao) {
        // No novo esquema, não há mais um campo horario específico para feedback
        // Os feedbacks estão diretamente na tabela secao_apresentacao
        // Retornamos null por enquanto, ou pode-se usar horario_secao se necessário
        return null;
    }
}

