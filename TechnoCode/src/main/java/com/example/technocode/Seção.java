package com.example.technocode;

public class Seção {
    private String nomeSecao;
    private String descricao;
    private String status;

    public Seção(String nomeSecao, String descricao, String status) {
        this.nomeSecao = nomeSecao;
        this.descricao = descricao;
        this.status = status;

    }

    public String getNomeSecao() { return nomeSecao; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
}