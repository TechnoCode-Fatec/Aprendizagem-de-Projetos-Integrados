package com.example.technocode.model;

import com.example.technocode.model.dao.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe modelo para representar um Professor de TG
 */
public class ProfessorTG {
    private String nome;
    private String email;
    private String senha;
    private String disciplina; // TG1, TG2 ou TG1/TG2

    /**
     * Constructor completo para ProfessorTG
     * @param nome Nome do professor
     * @param email Email do professor
     * @param senha Senha do professor
     * @param disciplina Disciplina (TG1, TG2 ou TG1/TG2)
     */
    public ProfessorTG(String nome, String email, String senha, String disciplina) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.disciplina = disciplina;
    }

    /**
     * Constructor sem senha (útil para consultas)
     */
    public ProfessorTG(String nome, String email, String disciplina) {
        this.nome = nome;
        this.email = email;
        this.disciplina = disciplina;
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

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cadastra um novo professor de TG no banco de dados
     */
    public void cadastrar() {
        String insertSql = "INSERT INTO professor_tg (nome, email, senha, disciplina) VALUES (?, ?, ?, ?)";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, this.nome);
            pst.setString(2, this.email);
            pst.setString(3, this.senha);
            pst.setString(4, this.disciplina);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar professor de TG!", ex);
        }
    }

    /**
     * Verifica se existe um professor de TG com email e senha fornecidos (para login)
     */
    public static boolean autenticar(String email, String senha) {
        try (Connection con = new Connector().getConnection()) {
            String sql = "SELECT * FROM professor_tg WHERE email = ? AND senha = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, senha);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Busca dados de um professor de TG por email
     */
    public static Map<String, String> buscarDadosPorEmail(String emailProfessor) {
        Map<String, String> dadosProfessor = new HashMap<>();
        try (Connection conn = new Connector().getConnection()) {
            String selectProfessor = "SELECT nome, email, disciplina FROM professor_tg WHERE email = ?";
            PreparedStatement pst = conn.prepareStatement(selectProfessor);
            pst.setString(1, emailProfessor);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                dadosProfessor.put("nome", rs.getString("nome"));
                dadosProfessor.put("email", rs.getString("email"));
                dadosProfessor.put("disciplina", rs.getString("disciplina"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar dados do professor de TG", e);
        }
        return dadosProfessor;
    }

    /**
     * Busca professores de TG agrupados por disciplina
     * Retorna um Map onde a chave é o formato "TG 1 - Nome Professor" e o valor é o email do professor
     */
    public static Map<String, String> buscarProfessoresPorDisciplina() {
        Map<String, String> professoresMap = new HashMap<>();
        try (Connection conn = new Connector().getConnection()) {
            String sql = "SELECT nome, email, disciplina FROM professor_tg ORDER BY disciplina, nome";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String disciplina = rs.getString("disciplina");
                
                // Formata como "TG 1 - Nome Professor"
                String chave = formatarDisciplinaComProfessor(disciplina, nome);
                professoresMap.put(chave, email);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar professores de TG", e);
        }
        return professoresMap;
    }

    /**
     * Formata a disciplina com o nome do professor no formato "TG 1 - Nome Professor"
     */
    private static String formatarDisciplinaComProfessor(String disciplina, String nomeProfessor) {
        // Converte "TG1" para "TG 1", "TG2" para "TG 2", mantém "TG1/TG2" como está
        String disciplinaFormatada;
        if (disciplina.equals("TG1")) {
            disciplinaFormatada = "TG 1";
        } else if (disciplina.equals("TG2")) {
            disciplinaFormatada = "TG 2";
        } else {
            disciplinaFormatada = disciplina; // TG1/TG2
        }
        
        return disciplinaFormatada + " - " + nomeProfessor;
    }

    /**
     * Extrai o email do professor a partir do texto formatado "TG 1 - Nome Professor"
     * ou retorna null se não encontrar
     */
    public static String extrairEmailDoTextoFormatado(String textoFormatado) {
        Map<String, String> professoresMap = buscarProfessoresPorDisciplina();
        return professoresMap.get(textoFormatado);
    }
}

