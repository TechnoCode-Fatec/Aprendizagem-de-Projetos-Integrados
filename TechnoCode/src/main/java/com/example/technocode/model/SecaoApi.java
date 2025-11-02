package com.example.technocode.model;

import com.example.technocode.model.dao.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe modelo para representar uma Seção API
 */
public class SecaoApi {
    private String emailAluno;
    private String semestreCurso;
    private Integer ano;
    private String semestreAno;
    private Integer versao;
    private String empresa;
    private String problema;
    private String solucao;
    private String linkRepositorio;
    private String tecnologias;
    private String contribuicoes;
    private String hardSkills;
    private String softSkills;

    /**
     * Constructor completo para SecaoApi
     */
    public SecaoApi(String emailAluno, String semestreCurso, Integer ano, String semestreAno,
                    Integer versao, String empresa, String problema, String solucao,
                    String linkRepositorio, String tecnologias, String contribuicoes,
                    String hardSkills, String softSkills) {
        this.emailAluno = emailAluno;
        this.semestreCurso = semestreCurso;
        this.ano = ano;
        this.semestreAno = semestreAno;
        this.versao = versao;
        this.empresa = empresa;
        this.problema = problema;
        this.solucao = solucao;
        this.linkRepositorio = linkRepositorio;
        this.tecnologias = tecnologias;
        this.contribuicoes = contribuicoes;
        this.hardSkills = hardSkills;
        this.softSkills = softSkills;
    }

    /**
     * Constructor parcial para identificação de seção (sem conteúdo)
     */
    public SecaoApi(String emailAluno, String semestreCurso, Integer ano, String semestreAno, Integer versao) {
        this.emailAluno = emailAluno;
        this.semestreCurso = semestreCurso;
        this.ano = ano;
        this.semestreAno = semestreAno;
        this.versao = versao;
    }

    // Getters e Setters
    public String getEmailAluno() {
        return emailAluno;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public String getSemestreCurso() {
        return semestreCurso;
    }

    public void setSemestreCurso(String semestreCurso) {
        this.semestreCurso = semestreCurso;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSemestreAno() {
        return semestreAno;
    }

    public void setSemestreAno(String semestreAno) {
        this.semestreAno = semestreAno;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public String getLinkRepositorio() {
        return linkRepositorio;
    }

    public void setLinkRepositorio(String linkRepositorio) {
        this.linkRepositorio = linkRepositorio;
    }

    public String getTecnologias() {
        return tecnologias;
    }

    public void setTecnologias(String tecnologias) {
        this.tecnologias = tecnologias;
    }

    public String getContribuicoes() {
        return contribuicoes;
    }

    public void setContribuicoes(String contribuicoes) {
        this.contribuicoes = contribuicoes;
    }

    public String getHardSkills() {
        return hardSkills;
    }

    public void setHardSkills(String hardSkills) {
        this.hardSkills = hardSkills;
    }

    public String getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(String softSkills) {
        this.softSkills = softSkills;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cadastra uma nova seção API no banco de dados
     */
    public void cadastrar() {
        try (Connection con = new Connector().getConnection()) {
            String insertSql = "INSERT INTO secao_api (aluno, semestre_curso, ano, semestre_ano, versao, empresa, problema, solucao, link_repositorio, tecnologias, contribuicoes, hard_skills, soft_skills) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(insertSql);
            pst.setString(1, this.emailAluno);
            pst.setString(2, this.semestreCurso);
            pst.setInt(3, this.ano);
            pst.setString(4, this.semestreAno);
            pst.setInt(5, this.versao);
            pst.setString(6, this.empresa);
            pst.setString(7, this.problema);
            pst.setString(8, this.solucao);
            pst.setString(9, this.linkRepositorio);
            pst.setString(10, this.tecnologias);
            pst.setString(11, this.contribuicoes);
            pst.setString(12, this.hardSkills);
            pst.setString(13, this.softSkills);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao inserir nova Sessão API!", ex);
        }
    }

    /**
     * Busca lista de seções API de um aluno (versões mais recentes)
     */
    public static List<Map<String, String>> buscarSecoesPorAluno(String emailAluno) {
        List<Map<String, String>> secoesApi = new ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            // Busca apenas a versão mais recente de cada seção API
            String selectSecoesApi = "SELECT semestre_curso, ano, semestre_ano, versao, empresa " +
                    "FROM secao_api s1 " +
                    "WHERE aluno = ? AND versao = (" +
                    "    SELECT MAX(versao) " +
                    "    FROM secao_api s2 " +
                    "    WHERE s2.aluno = s1.aluno " +
                    "    AND s2.semestre_curso = s1.semestre_curso " +
                    "    AND s2.ano = s1.ano " +
                    "    AND s2.semestre_ano = s1.semestre_ano" +
                    ")";
            PreparedStatement pst = conn.prepareStatement(selectSecoesApi);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String semestreCurso = rs.getString("semestre_curso");
                String ano = rs.getString("ano");
                String semestreAno = rs.getString("semestre_ano");
                String versao = rs.getString("versao");
                String empresa = rs.getString("empresa");

                Map<String, String> secao = new HashMap<>();
                secao.put("id", semestreCurso + " " + ano.substring(0,4) + "/" + semestreAno);
                secao.put("empresa", empresa);
                secao.put("semestre_curso", semestreCurso);
                secao.put("ano", ano);
                secao.put("semestre_ano", semestreAno);
                secao.put("versao", versao);
                secoesApi.add(secao);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return secoesApi;
    }

    /**
     * Busca a próxima versão disponível para uma seção de API
     */
    public static int getProximaVersao(String emailAluno, String semestreCurso, int ano, String semestreAno) {
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT MAX(versao) as max_versao FROM secao_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            pst.setString(2, semestreCurso);
            pst.setInt(3, ano);
            pst.setString(4, semestreAno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int maxVersao = rs.getInt("max_versao");
                return maxVersao + 1;
            }
            return 1; // Primeira versão
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar próxima versão de API", e);
        }
    }

    /**
     * Busca o histórico de versões de API do aluno
     */
    public static List<Map<String, String>> buscarHistoricoVersoes(String emailAluno) {
        List<Map<String, String>> historico = new ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT semestre_curso, ano, semestre_ano, versao, empresa FROM secao_api WHERE aluno = ? ORDER BY versao DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Map<String, String> versao = new HashMap<>();
                versao.put("semestre_curso", rs.getString("semestre_curso"));
                versao.put("ano", rs.getString("ano"));
                versao.put("semestre_ano", rs.getString("semestre_ano"));
                versao.put("versao", rs.getString("versao"));
                versao.put("empresa", rs.getString("empresa"));
                versao.put("tipo", "api");
                historico.add(versao);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico de APIs", e);
        }
        return historico;
    }

    /**
     * Verifica se existe feedback para uma seção de API
     */
    public static boolean verificarFeedback(String emailAluno, String semestreCurso, int ano, String semestreAno, int versao) {
        String sql = "SELECT COUNT(*) as count FROM feedback_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ?";
        try (Connection c = new Connector().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, emailAluno);
            ps.setString(2, semestreCurso);
            ps.setInt(3, ano);
            ps.setString(4, semestreAno);
            ps.setInt(5, versao);
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
     * Busca o horário do feedback para uma seção de API
     * @return String formatada com data e hora, ou null se não existir feedback
     */
    public static String buscarHorarioFeedback(String emailAluno, String semestreCurso, String ano, String semestreAno, int versao) {
        String sql = "SELECT DATE_FORMAT(horario, '%d/%m/%Y às %H:%i') as horario_formatado " +
                     "FROM feedback_api WHERE aluno = ? AND semestre_curso = ? AND ano = ? AND semestre_ano = ? AND versao = ? LIMIT 1";
        try (Connection con = new Connector().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, emailAluno);
            ps.setString(2, semestreCurso);
            ps.setString(3, ano);
            ps.setString(4, semestreAno);
            ps.setInt(5, versao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("horario_formatado");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

