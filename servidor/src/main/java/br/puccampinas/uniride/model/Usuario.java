package br.puccampinas.uniride.model;

import java.time.Instant;

/**
 * Estudante cadastrado no UniRide.
 */
public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String ra;
    private String senhaHash;
    private Instant criadoEm;

    public Usuario(String nome, String email, String ra, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.ra = ra;
        this.senhaHash = senhaHash;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getRa() {
        return ra;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
