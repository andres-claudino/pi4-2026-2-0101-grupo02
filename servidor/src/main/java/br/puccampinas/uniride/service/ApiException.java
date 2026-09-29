package br.puccampinas.uniride.service;

import java.util.Collections;
import java.util.Map;

/**
 * Erro esperado da regra de negócio, com o status HTTP e a mensagem que o aplicativo deve exibir.
 * Opcionalmente traz erros por campo (ex.: {"email": "E-mail inválido."}).
 */
public class ApiException extends RuntimeException {

    private final int status;
    private final Map<String, String> campos;

    public ApiException(int status, String mensagem) {
        this(status, mensagem, Collections.<String, String>emptyMap());
    }

    public ApiException(int status, String mensagem, Map<String, String> campos) {
        super(mensagem);
        this.status = status;
        this.campos = campos;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getCampos() {
        return campos;
    }
}
