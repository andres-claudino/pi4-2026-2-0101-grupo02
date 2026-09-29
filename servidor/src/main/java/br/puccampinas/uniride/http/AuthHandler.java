package br.puccampinas.uniride.http;

import br.puccampinas.uniride.dto.CadastroRequest;
import br.puccampinas.uniride.dto.LoginRequest;
import br.puccampinas.uniride.service.ApiException;
import br.puccampinas.uniride.service.AuthService;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Rotas de autenticação:
 *
 *   POST /api/auth/cadastro  {nome, email, ra, senha}  -> 201 {id, nome, email, ra}
 *   POST /api/auth/login     {email, senha}            -> 200 {token, usuario}
 *   GET  /api/auth/me        (Authorization: Bearer)   -> 200 {id, nome, email, ra}
 *   POST /api/auth/logout    (Authorization: Bearer)   -> 204
 *
 * Erros: {"erro": "mensagem", "campos": {"campo": "mensagem"}}
 */
public class AuthHandler implements HttpHandler {

    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String rota = exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath();
            switch (rota) {
                case "POST /api/auth/cadastro":
                    CadastroRequest cadastro = HttpUtil.GSON.fromJson(HttpUtil.lerCorpo(exchange), CadastroRequest.class);
                    HttpUtil.enviarJson(exchange, 201, authService.cadastrar(cadastro));
                    break;
                case "POST /api/auth/login":
                    LoginRequest login = HttpUtil.GSON.fromJson(HttpUtil.lerCorpo(exchange), LoginRequest.class);
                    HttpUtil.enviarJson(exchange, 200, authService.login(login));
                    break;
                case "GET /api/auth/me":
                    HttpUtil.enviarJson(exchange, 200, authService.usuarioLogado(HttpUtil.lerToken(exchange)));
                    break;
                case "POST /api/auth/logout":
                    authService.logout(HttpUtil.lerToken(exchange));
                    HttpUtil.enviarVazio(exchange, 204);
                    break;
                default:
                    HttpUtil.enviarErro(exchange, 404, "Rota não encontrada.");
            }
        } catch (ApiException e) {
            HttpUtil.enviarErro(exchange, e.getStatus(), e.getMessage(), e.getCampos());
        } catch (JsonParseException e) {
            HttpUtil.enviarErro(exchange, 400, "JSON inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.enviarErro(exchange, 500, "Erro interno no servidor.");
        } finally {
            exchange.close();
        }
    }
}
