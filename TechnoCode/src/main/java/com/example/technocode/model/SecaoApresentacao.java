package com.example.technocode.model;

import java.sql.Date;

/**
 * Classe modelo para representar uma Seção de Apresentação
 */
public class SecaoApresentacao {
    private String emailAluno;
    private String nome;
    private Date idade; // Data de nascimento
    private String curso;
    private Integer versao;
    private String motivacao;
    private String historico;
    private String linkGithub;
    private String linkLinkedin;
    private String principaisConhecimentos;

    /**
     * Constructor completo para SecaoApresentacao
     */
    public SecaoApresentacao(String emailAluno, String nome, Date idade, String curso,
                            Integer versao, String motivacao, String historico,
                            String linkGithub, String linkLinkedin, String principaisConhecimentos) {
        this.emailAluno = emailAluno;
        this.nome = nome;
        this.idade = idade;
        this.curso = curso;
        this.versao = versao;
        this.motivacao = motivacao;
        this.historico = historico;
        this.linkGithub = linkGithub;
        this.linkLinkedin = linkLinkedin;
        this.principaisConhecimentos = principaisConhecimentos;
    }

    /**
     * Constructor parcial para identificação de seção (sem conteúdo)
     */
    public SecaoApresentacao(String emailAluno, Integer versao) {
        this.emailAluno = emailAluno;
        this.versao = versao;
    }

    // Getters e Setters
    public String getEmailAluno() {
        return emailAluno;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getIdade() {
        return idade;
    }

    public void setIdade(Date idade) {
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getMotivacao() {
        return motivacao;
    }

    public void setMotivacao(String motivacao) {
        this.motivacao = motivacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getLinkGithub() {
        return linkGithub;
    }

    public void setLinkGithub(String linkGithub) {
        this.linkGithub = linkGithub;
    }

    public String getLinkLinkedin() {
        return linkLinkedin;
    }

    public void setLinkLinkedin(String linkLinkedin) {
        this.linkLinkedin = linkLinkedin;
    }

    public String getPrincipaisConhecimentos() {
        return principaisConhecimentos;
    }

    public void setPrincipaisConhecimentos(String principaisConhecimentos) {
        this.principaisConhecimentos = principaisConhecimentos;
    }
}

