package com.example.technocode.model;

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
}

