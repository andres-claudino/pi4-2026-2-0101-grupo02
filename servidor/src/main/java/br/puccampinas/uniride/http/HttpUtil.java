package br.puccampinas.uniride.http;

import br.puccampinas.uniride.service.ApiException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Funções auxiliares para ler requisições e escrever respostas JSON.
 */
public final class HttpUtil {

    public static final Gson GSON = new Gson();

    /** Tamanho máximo aceito para o corpo de uma requisição (evita requisições gigantes). */
    private static final int LIMITE_CORPO = 16 * 1024;

    private HttpUtil() {
    }

    public static String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int lidos;
            while ((lidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, lidos);
                if (out.size() > LIMITE_CORPO) {
                    throw new ApiException(413, "Requisição muito grande.");
                }
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    /** Lê o token do cabeçalho "Authorization: Bearer <token>". */
    public static String lerToken(HttpExchange exchange) {
        String cabecalho = exchange.getRequestHeaders().getFirst("Authorization");
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            return null;
        }
        return cabecalho.substring("Bearer ".length()).trim();
    }

    public static void enviarJson(HttpExchange exchange, int status, Object corpo) throws IOException {
        byte[] bytes = GSON.toJson(corpo).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void enviarVazio(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
    }

    /** Envia {"erro": "...", "campos": {...}} — formato padrão de erro da API. */
    public static void enviarErro(HttpExchange exchange, int status, String mensagem, Map<String, String> campos)
            throws IOException {
        JsonObject corpo = new JsonObject();
        corpo.addProperty("erro", mensagem);
        if (campos != null && !campos.isEmpty()) {
            corpo.add("campos", GSON.toJsonTree(campos));
        }
        enviarJson(exchange, status, corpo);
    }

    public static void enviarErro(HttpExchange exchange, int status, String mensagem) throws IOException {
        enviarErro(exchange, status, mensagem, null);
    }
}
