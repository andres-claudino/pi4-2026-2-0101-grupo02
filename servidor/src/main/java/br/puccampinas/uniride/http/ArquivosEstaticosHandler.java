package br.puccampinas.uniride.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Serve as telas web (HTML, CSS, JS e imagens) que ficam em src/main/resources/public.
 */
public class ArquivosEstaticosHandler implements HttpHandler {

    private static final Map<String, String> TIPOS = new HashMap<>();

    static {
        TIPOS.put("html", "text/html; charset=utf-8");
        TIPOS.put("css", "text/css; charset=utf-8");
        TIPOS.put("js", "application/javascript; charset=utf-8");
        TIPOS.put("json", "application/json; charset=utf-8");
        TIPOS.put("svg", "image/svg+xml");
        TIPOS.put("png", "image/png");
        TIPOS.put("jpg", "image/jpeg");
        TIPOS.put("ico", "image/x-icon");
    }

    private final String pastaBase;

    public ArquivosEstaticosHandler(String pastaBase) {
        this.pastaBase = pastaBase;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String caminho = exchange.getRequestURI().getPath();
            if (caminho.equals("/")) {
                caminho = "/index.html";
            }

            // Bloqueia tentativas de acessar arquivos fora da pasta public
            if (caminho.contains("..") || !exchange.getRequestMethod().equals("GET")) {
                enviarTexto(exchange, 404, "Não encontrado");
                return;
            }

            InputStream arquivo = getClass().getResourceAsStream(pastaBase + caminho);
            if (arquivo == null) {
                enviarTexto(exchange, 404, "Não encontrado");
                return;
            }

            byte[] bytes;
            try (InputStream in = arquivo) {
                bytes = lerTudo(in);
            }
            exchange.getResponseHeaders().set("Content-Type", tipoDoArquivo(caminho));
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        } finally {
            exchange.close();
        }
    }

    private static String tipoDoArquivo(String caminho) {
        int ponto = caminho.lastIndexOf('.');
        String extensao = ponto >= 0 ? caminho.substring(ponto + 1).toLowerCase() : "";
        String tipo = TIPOS.get(extensao);
        return tipo != null ? tipo : "application/octet-stream";
    }

    private static byte[] lerTudo(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int lidos;
        while ((lidos = in.read(buffer)) != -1) {
            out.write(buffer, 0, lidos);
        }
        return out.toByteArray();
    }

    private static void enviarTexto(HttpExchange exchange, int status, String texto) throws IOException {
        byte[] bytes = texto.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
