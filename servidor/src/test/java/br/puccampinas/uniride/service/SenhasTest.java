package br.puccampinas.uniride.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhasTest {

    @Test
    void hashNaoContemSenhaEmTextoPuro() {
        assertFalse(Senhas.gerarHash("senha123").contains("senha123"));
    }

    @Test
    void mesmaSenhaGeraHashesDiferentes() {
        assertNotEquals(Senhas.gerarHash("senha123"), Senhas.gerarHash("senha123"));
    }

    @Test
    void confereSenhaCorretaERecusaErrada() {
        String hash = Senhas.gerarHash("senha123");

        assertTrue(Senhas.conferir("senha123", hash));
        assertFalse(Senhas.conferir("senha124", hash));
    }

    @Test
    void recusaHashMalformado() {
        assertFalse(Senhas.conferir("senha123", "qualquer-coisa"));
    }
}
