package com.example.technocode.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connector {
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/technotg?useTimezone=true&serverTimezone=UTC", "technocode", "pass123");
    }

    public void cadastrarSessaoApi(String aluno, String semestreCurso, Integer ano, String semestreAno, Integer versao, String empresa, String problema, String solucao, String linkRepositorio, String tecnologias, String contribuicoes, String hardSkills, String softSkills){
        Connection con = null;
        try {
            con = getConnection();
            String insert_sql = "insert into technotg (aluno, semestre_curso, ano, semestre_ano, versao, empresa, problema, solucao, link_repositorio, tecnlogias, contribuicoes, hard_skills, soft_skills) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(insert_sql);
            pst.setString(1, aluno);
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
}
