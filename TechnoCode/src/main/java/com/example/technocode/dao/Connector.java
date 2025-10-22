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

    public void cadastrarAluno(String nome, String email, String senha, String orientador) {
        String insertSql = "INSERT INTO aluno (nome, email, senha, orientador) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, nome);
            pst.setString(2, email);
            pst.setString(3, senha);
            pst.setString(4, orientador);
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
            String selectAlunos = "SELECT nome, email, orientador FROM aluno WHERE orientador = ?";
            PreparedStatement pst = conn.prepareStatement(selectAlunos);
            pst.setString(1, orientador);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Map<String, String> aluno = new HashMap<>();
                aluno.put("nome", rs.getString("nome"));
                aluno.put("email", rs.getString("email"));
                aluno.put("curso", rs.getString("orientador")); // se quiser exibir o orientador no lugar de “curso”
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
            String insertSql = "insert into secao_apresentacao (aluno, nome, idade,curso" +
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

}
