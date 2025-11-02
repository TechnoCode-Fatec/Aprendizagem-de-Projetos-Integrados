package com.example.technocode.model;

/**
 * Classe modelo para representar um Aluno
 */
public class Aluno {
    private String nome;
    private String email;
    private String senha;
    private String orientador; // email do orientador
    private String curso;

    /**
     * Constructor completo para Aluno
     * @param nome Nome do aluno
     * @param email Email do aluno
     * @param senha Senha do aluno
     * @param orientador Email do orientador
     * @param curso Curso do aluno
     */
    public Aluno(String nome, String email, String senha, String orientador, String curso) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.orientador = orientador;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
}

