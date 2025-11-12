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
 * Classe modelo para representar um Aluno
 */
public class Aluno {
    private String nome;
    private String email;
    private String senha;
    private String orientador; // email do orientador
    private String professorTG; // email do professor de TG
    private String curso;

    /**
     * Constructor completo para Aluno
     * @param nome Nome do aluno
     * @param email Email do aluno
     * @param senha Senha do aluno
     * @param orientador Email do orientador
     * @param professorTG Email do professor de TG
     * @param curso Curso do aluno (pode ser null)
     */
    public Aluno(String nome, String email, String senha, String orientador, String professorTG, String curso) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.orientador = orientador;
        this.professorTG = professorTG;
        this.curso = curso;
    }

    /**
     * Constructor sem senha (útil para consultas)
     */
    public Aluno(String nome, String email, String orientador, String curso) {
        this.nome = nome;
        this.email = email;
        this.orientador = orientador;
        this.curso = curso;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrientador() {
        return orientador;
    }

    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getProfessorTG() {
        return professorTG;
    }

    public void setProfessorTG(String professorTG) {
        this.professorTG = professorTG;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cadastra um novo aluno no banco de dados
     */
    public void cadastrar() {
        String insertSql = "INSERT INTO aluno (nome, email, senha, orientador, professor_tg) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, this.nome);
            pst.setString(2, this.email);
            pst.setString(3, this.senha);
            pst.setString(4, this.orientador);
            pst.setString(5, this.professorTG); // pode ser null
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar aluno!", ex);
        }
    }

    /**
     * Busca lista de alunos por orientador
     */
    public static List<Map<String, String>> buscarPorOrientador(String emailOrientador) {
        List<Map<String, String>> alunos = new ArrayList<>();
        try (Connection conn = new Connector().getConnection()) {
            String selectAlunos = "SELECT nome, email FROM aluno WHERE orientador = ?";
            PreparedStatement pst = conn.prepareStatement(selectAlunos);
            pst.setString(1, emailOrientador);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Map<String, String> aluno = new HashMap<>();
                aluno.put("nome", rs.getString("nome"));
                aluno.put("email", rs.getString("email"));
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return alunos;
    }

    /**
     * Busca dados de um aluno por email
     */
    public static Map<String, String> buscarDadosPorEmail(String emailAluno) {
        Map<String, String> dadosAluno = new HashMap<>();
        try (Connection conn = new Connector().getConnection()) {
            String selectAluno = "SELECT nome, email FROM aluno WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(selectAluno);
            pst.setString(1, emailAluno);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                dadosAluno.put("nome", rs.getString("nome"));
                dadosAluno.put("email", rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar dados do aluno", e);
        }
        return dadosAluno;
    }

    /**
     * Verifica se existe um aluno com email e senha fornecidos (para login)
     */
    public static boolean autenticar(String email, String senha) {
        try (Connection con = new Connector().getConnection()) {
            String sql = "SELECT * FROM aluno WHERE email = ? AND senha = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, senha);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

