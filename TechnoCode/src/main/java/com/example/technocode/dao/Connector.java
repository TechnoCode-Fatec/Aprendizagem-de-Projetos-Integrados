package com.example.technocode.dao;

import com.example.technocode.model.Aluno;
import com.example.technocode.model.Orientador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável apenas pela conexão com o banco de dados e autenticação
 */
public class Connector {
    
    /**
     * Obtém uma conexão com o banco de dados
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/technotg?useTimezone=true&serverTimezone=UTC", "technocode", "pass123");
    }

    /**
     * Realiza login verificando se é Aluno ou Orientador
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return "Aluno", "Orientador" ou null se não encontrado
     */
    public String login(String email, String senha) {
        // Primeiro verifica se é orientador
        if (Orientador.autenticar(email, senha)) {
            return "Orientador";
        }
        // Se não for orientador, verifica se é aluno
        if (Aluno.autenticar(email, senha)) {
            return "Aluno";
        }
        return null;
    }
}
