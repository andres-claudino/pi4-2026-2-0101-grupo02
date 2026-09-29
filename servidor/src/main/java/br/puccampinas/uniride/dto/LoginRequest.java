package br.puccampinas.uniride.dto;

/** Corpo JSON de POST /api/auth/login. */
public class LoginRequest {

    public String email;
    public String senha;

    public LoginRequest() {
    }

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
