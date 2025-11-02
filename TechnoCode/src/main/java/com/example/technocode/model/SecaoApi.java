package com.example.technocode.model;

/**
 * Classe modelo para representar uma Seção API
 */
public class SecaoApi {
    private String emailAluno;
    private String semestreCurso;
    private Integer ano;
    private String semestreAno;
    private Integer versao;
    private String empresa;
    private String problema;
    private String solucao;
    private String linkRepositorio;
    private String tecnologias;
    private String contribuicoes;
    private String hardSkills;
    private String softSkills;

    /**
     * Constructor completo para SecaoApi
     */
    public SecaoApi(String emailAluno, String semestreCurso, Integer ano, String semestreAno,
                    Integer versao, String empresa, String problema, String solucao,
                    String linkRepositorio, String tecnologias, String contribuicoes,
                    String hardSkills, String softSkills) {
        this.emailAluno = emailAluno;
        this.semestreCurso = semestreCurso;
        this.ano = ano;
        this.semestreAno = semestreAno;
        this.versao = versao;
        this.empresa = empresa;
        this.problema = problema;
        this.solucao = solucao;
        this.linkRepositorio = linkRepositorio;
        this.tecnologias = tecnologias;
        this.contribuicoes = contribuicoes;
        this.hardSkills = hardSkills;
        this.softSkills = softSkills;
    }

    /**
     * Constructor parcial para identificação de seção (sem conteúdo)
     */
    public SecaoApi(String emailAluno, String semestreCurso, Integer ano, String semestreAno, Integer versao) {
        this.emailAluno = emailAluno;
        this.semestreCurso = semestreCurso;
        this.ano = ano;
        this.semestreAno = semestreAno;
        this.versao = versao;
    }

    // Getters e Setters
    public String getEmailAluno() {
        return emailAluno;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public String getSemestreCurso() {
        return semestreCurso;
    }

    public void setSemestreCurso(String semestreCurso) {
        this.semestreCurso = semestreCurso;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSemestreAno() {
        return semestreAno;
    }

    public void setSemestreAno(String semestreAno) {
        this.semestreAno = semestreAno;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public String getLinkRepositorio() {
        return linkRepositorio;
    }

    public void setLinkRepositorio(String linkRepositorio) {
        this.linkRepositorio = linkRepositorio;
    }

    public String getTecnologias() {
        return tecnologias;
    }

    public void setTecnologias(String tecnologias) {
        this.tecnologias = tecnologias;
    }

    public String getContribuicoes() {
        return contribuicoes;
    }

    public void setContribuicoes(String contribuicoes) {
        this.contribuicoes = contribuicoes;
    }

    public String getHardSkills() {
        return hardSkills;
    }

    public void setHardSkills(String hardSkills) {
        this.hardSkills = hardSkills;
    }

    public String getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(String softSkills) {
        this.softSkills = softSkills;
    }
}

