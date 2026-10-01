package br.puccampinas.uniride.dto;

import br.puccampinas.uniride.model.Usuario;

/** Dados do usuário que podem ser enviados ao aplicativo (nunca inclui a senha). */
public class UsuarioResponse {

    public final long id;
    public final String nome;
    public final String email;
    public final String ra;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.ra = usuario.getRa();
    }
}
