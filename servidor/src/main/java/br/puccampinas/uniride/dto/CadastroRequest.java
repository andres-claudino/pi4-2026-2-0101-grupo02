package br.puccampinas.uniride.dto;

/** Corpo JSON de POST /api/auth/cadastro. */
public class CadastroRequest {

    public String nome;
    public String email;
    public String ra;
    public String senha;

    public CadastroRequest() {
    }

    public CadastroRequest(String nome, String email, String ra, String senha) {
        this.nome = nome;
        this.email = email;
        this.ra = ra;
        this.senha = senha;
    }
}
