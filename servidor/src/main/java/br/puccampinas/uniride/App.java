package br.puccampinas.uniride;

import br.puccampinas.uniride.http.ArquivosEstaticosHandler;
import br.puccampinas.uniride.http.AuthHandler;
import br.puccampinas.uniride.repository.UsuarioRepository;
import br.puccampinas.uniride.repository.UsuarioRepositoryMemoria;
import br.puccampinas.uniride.service.AuthService;
import br.puccampinas.uniride.service.SessaoService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Ponto de entrada do servidor UniRide.
 *
 * Sobe um servidor HTTP que atende a API (/api/...) e as telas web (arquivos em resources/public).
 * Porta padrão: 8080 (pode ser alterada pela variável de ambiente PORT).
 */
public class App {

    public static void main(String[] args) throws IOException {
        int porta = lerPorta();

        // Por enquanto os dados ficam em memória. Para usar banco, basta trocar
        // esta implementação por outra que também implemente UsuarioRepository.
        UsuarioRepository usuarios = new UsuarioRepositoryMemoria();
        SessaoService sessoes = new SessaoService();
        AuthService authService = new AuthService(usuarios, sessoes);

        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/api/auth/", new AuthHandler(authService));
        servidor.createContext("/", new ArquivosEstaticosHandler("/public"));
        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();

        System.out.println("UniRide rodando em http://localhost:" + porta);
    }

    private static int lerPorta() {
        String porta = System.getenv("PORT");
        if (porta == null || porta.trim().isEmpty()) {
            return 8080;
        }
        return Integer.parseInt(porta.trim());
    }
}
