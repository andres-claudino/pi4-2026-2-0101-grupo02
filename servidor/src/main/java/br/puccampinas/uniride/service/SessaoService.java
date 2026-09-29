package br.puccampinas.uniride.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controla as sessões de login. Cada login gera um token aleatório que o aplicativo
 * envia no cabeçalho "Authorization: Bearer <token>" nas próximas requisições.
 *
 * As sessões ficam em memória: reiniciar o servidor desloga todo mundo.
 */
public class SessaoService {

    private static final Duration DURACAO_PADRAO = Duration.ofHours(8);

    private final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final Duration duracao;

    public SessaoService() {
        this(DURACAO_PADRAO);
    }

    public SessaoService(Duration duracao) {
        this.duracao = duracao;
    }

    public String criar(long usuarioId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessoes.put(token, new Sessao(usuarioId, Instant.now().plus(duracao)));
        return token;
    }

    /** Devolve o id do usuário dono do token, se o token existir e não tiver expirado. */
    public Optional<Long> buscarUsuarioId(String token) {
        if (token == null) {
            return Optional.empty();
        }
        Sessao sessao = sessoes.get(token);
        if (sessao == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(sessao.expiraEm)) {
            sessoes.remove(token);
            return Optional.empty();
        }
        return Optional.of(sessao.usuarioId);
    }

    public void encerrar(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }

    private static class Sessao {
        final long usuarioId;
        final Instant expiraEm;

        Sessao(long usuarioId, Instant expiraEm) {
            this.usuarioId = usuarioId;
            this.expiraEm = expiraEm;
        }
    }
}
