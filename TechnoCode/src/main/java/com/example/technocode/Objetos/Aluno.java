package com.example.technocode.Objetos;

public class Aluno {
    private String email;
    private String emailFatec;
    private String nome;
    private String ra;
    private String curso;

    public Aluno(String email, String emailFatec, String nome, String ra, String curso) {
        this.email = email;
        this.emailFatec = emailFatec;
        this.nome = nome;
        this.ra = ra;
        this.curso = curso;
    }

    public String getEmail() { return email; }
    public String getEmailFatec() { return emailFatec; }
    public String getNome() { return nome; }
    public String getRa() { return ra; }
    public String getCurso() { return curso; }
}