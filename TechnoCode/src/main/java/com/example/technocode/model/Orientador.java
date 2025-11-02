package com.example.technocode.model;

import com.example.technocode.dao.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe modelo para representar um Orientador
 */
public class Orientador {
    private String nome;
    private String email;
    private String senha;

    /**
     * Constructor completo para Orientador
     * @param nome Nome do orientador
     * @param email Email do orientador
     * @param senha Senha do orientador
     */
    public Orientador(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    /**
     * Constructor sem senha (útil para consultas)
     */
    public Orientador(String nome, String email) {
        this.nome = nome;
        this.email = email;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // ============ MÉTODOS DAO ============

    /**
     * Cadastra um novo orientador no banco de dados
     */
    public void cadastrar() {
        String insertSql = "INSERT INTO orientador (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection con = new Connector().getConnection();
             PreparedStatement pst = con.prepareStatement(insertSql)) {

            pst.setString(1, this.nome);
            pst.setString(2, this.email);
            pst.setString(3, this.senha);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar orientador!", ex);
        }
    }

    /**
     * Busca lista de emails de todos os orientadores
     */
    public static List<String> listarEmails() {
        List<String> nomes = new ArrayList<>();
        try (Connection con = new Connector().getConnection()) {
            String selectSql = "SELECT email FROM orientador";
            PreparedStatement pst = con.prepareStatement(selectSql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar orientadores", e);
        }
        return nomes;
    }

    /**
     * Busca orientadores retornando um Map com nome -> email
     */
    public static Map<String, String> buscarTodos() {
        Map<String, String> lista = new LinkedHashMap<>();
        String sql = "SELECT email, nome FROM orientador ORDER BY nome";
        try (Connection c = new Connector().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.put(rs.getString("nome"), rs.getString("email")); // chave = nome, valor = email
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca email do orientador pelo nome
     */
    public static String buscarEmailPorNome(String nomeOrientador) {
        String sql = "SELECT email FROM orientador WHERE nome = ?";
        try (Connection c = new Connector().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomeOrientador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifica se existe um orientador com email e senha fornecidos (para login)
     */
    public static boolean autenticar(String email, String senha) {
        try (Connection con = new Connector().getConnection()) {
            String sql = "SELECT * FROM orientador WHERE email = ? AND senha = ?";
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

