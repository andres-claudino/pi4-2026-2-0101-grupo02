package br.puccampinas.uniride.dto;

/** Resposta de login: token de sessão + dados públicos do usuário. */
public class LoginResponse {

    public final String token;
    public final UsuarioResponse usuario;

    public LoginResponse(String token, UsuarioResponse usuario) {
        this.token = token;
        this.usuario = usuario;
    }
}
