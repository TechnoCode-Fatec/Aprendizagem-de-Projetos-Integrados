package com.example.technocode.dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Connector {
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/technotg?useTimezone=true&serverTimezone=UTC", "technocode", "pass123");
    }

    public void cadastrarAluno(String nome, String email, String senha, String orientador, String curso) {
        String insertSql = "INSERT INTO aluno (nome, email, senha, orientador, curso) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, nome);
            pst.setString(2, email);
            pst.setString(3, senha);
            pst.setString(4, orientador);
            pst.setString(5, curso);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar aluno!", ex);
        }
    }

    public void cadastrarOrientador(String nome, String email, String senha) {
        String insertSql = "INSERT INTO orientador (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, nome);
            pst.setString(2, email);
            pst.setString(3, senha);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar orientador!", ex);
        }
    }

    public List<Map<String,String>> alunos(String orientador) {
        Connection conn = null;
        List<Map<String,String>> alunos = new ArrayList<>();
        try{
            conn = getConnection();
            String selectAlunos = "SELECT nome, email, curso FROM aluno WHERE orientador = ?";
            PreparedStatement pst = conn.prepareStatement(selectAlunos);
            pst.setString(1, orientador);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Map<String, String> aluno = new HashMap<>();
                aluno.put("nome", rs.getString("nome"));
                aluno.put("email", rs.getString("email"));
                aluno.put("curso", rs.getString("curso"));
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return alunos;
    }

    public List<String> orientadores(){
        Connection con = null;
        List<String> nomes = new ArrayList<>();
        try{
            con = getConnection();
            String selectSql = "SELECT email FROM orientador";
            PreparedStatement pst = con.prepareStatement(selectSql);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                nomes.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar orientadores", e);
        }
        return nomes;
    }

    public String login(String email, String senha){
        Connection con = null;
        try{
            con = getConnection();
            String sqlOrientador = "SELECT * FROM orientador WHERE email = ? AND senha = ?";
            PreparedStatement pst = con.prepareStatement(sqlOrientador);
            pst.setString(1, email);
            pst.setString(2, senha);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return "Orientador";
            }
            String sqlAluno = "SELECT * FROM aluno WHERE email = ? AND senha = ?";
            pst = con.prepareStatement(sqlAluno);
            pst.setString(1, email);
            pst.setString(2, senha);
            rs = pst.executeQuery();
            if(rs.next()){
                return "Aluno";
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void cadastrarSessaoApi(String emailAluno, String semestreCurso, Integer ano,
                                   String semestreAno, Integer versao, String empresa,
                                   String problema, String solucao, String linkRepositorio,
                                   String tecnologias, String contribuicoes, String hardSkills, String softSkills){
        Connection con = null;
        try {
            con = getConnection();
            String insertSql = "insert into secao_api (aluno, semestre_curso, ano, semestre_ano, versao, empresa, problema, solucao, link_repositorio, tecnologias, contribuicoes,hard_skills, soft_skills) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(insertSql);
            pst.setString(1, emailAluno);
            pst.setString(2, semestreCurso);
            pst.setInt(3, ano);
            pst.setString(4, semestreAno);
            pst.setInt(5, versao);
            pst.setString(6, empresa);
            pst.setString(7, problema);
            pst.setString(8, solucao);
            pst.setString(9, linkRepositorio);
            pst.setString(10, tecnologias);
            pst.setString(11, contribuicoes);
            pst.setString(12, hardSkills);
            pst.setString(13, softSkills);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao inserir nova Sessão API!", ex);
        }finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Erro ao fechar conexão", ex);
            }
        }
    }

    public void cadastrarApresentacao(String emailAluno, String nome, Date idade,
                                      String curso, Integer versao, String motivacao,
                                      String historico, String linkGithub, String linkLinkedin,
                                      String principaisConhecimentos){
        Connection con = null;
        try{
            con = getConnection();
            String insertSql = "insert into secao_apresentacao (aluno, nome, idade,curso," +
                    "versao, motivacao, historico, link_github, link_linkedin, principais_conhecimentos) " +
                    "values (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(insertSql);
            pst.setString(1, emailAluno);
            pst.setString(2, nome);
            pst.setDate(3, idade);
            pst.setString(4, curso);
            pst.setInt(5, versao);
            pst.setString(6, motivacao);
            pst.setString(7, historico);
            pst.setString(8, linkGithub);
            pst.setString(9, linkLinkedin);
            pst.setString(10, principaisConhecimentos);
            pst.executeUpdate();
        }catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao inserir nova Sessão Apresentação!", ex);
        }finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Erro ao fechar conexão", ex);
            }
        }
    }

    public List<Map<String,String>> secoesApi(String emailAluno){
        Connection conn = null;
        List<Map<String,String>> secoesApi = new ArrayList<>();
        try{
            conn = getConnection();
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
                secao.put("id", semestreCurso + " " + ano + "/" + semestreAno);
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

    public Map<String, String> buscarDadosAluno(String emailAluno) {
        Connection conn = null;
        Map<String, String> dadosAluno = new HashMap<>();
        try {
            conn = getConnection();
            String selectAluno = "SELECT nome, email, curso FROM aluno WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(selectAluno);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                dadosAluno.put("nome", rs.getString("nome"));
                dadosAluno.put("email", rs.getString("email"));
                dadosAluno.put("curso", rs.getString("curso"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar dados do aluno", e);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dadosAluno;
    }

    public List<Map<String,String>> secoesApresentacao(String emailAluno){
        Connection conn = null;
        List<Map<String,String>> secoesApresentacao = new ArrayList<>();
        try{
            conn = getConnection();
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
     * Busca a próxima versão disponível para uma seção de apresentação do aluno
     */
    public int getProximaVersaoApresentacao(String emailAluno) {
        Connection conn = null;
        try {
            conn = getConnection();
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
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca a próxima versão disponível para uma seção de API do aluno
     */
    public int getProximaVersaoApi(String emailAluno, String semestreCurso, int ano, String semestreAno) {
        Connection conn = null;
        try {
            conn = getConnection();
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
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca o histórico de versões de apresentação do aluno
     */
    public List<Map<String,String>> historicoVersoesApresentacao(String emailAluno) {
        Connection conn = null;
        List<Map<String,String>> historico = new ArrayList<>();
        try {
            conn = getConnection();
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
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return historico;
    }

    /**
     * Busca o histórico de versões de API do aluno
     */
    public List<Map<String,String>> historicoVersoesApi(String emailAluno) {
        Connection conn = null;
        List<Map<String,String>> historico = new ArrayList<>();
        try {
            conn = getConnection();
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
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return historico;
    }


}
