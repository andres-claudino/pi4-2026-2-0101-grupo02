package br.puccampinas.uniride.service;

import br.puccampinas.uniride.dto.CadastroRequest;
import br.puccampinas.uniride.dto.LoginRequest;
import br.puccampinas.uniride.dto.LoginResponse;
import br.puccampinas.uniride.dto.UsuarioResponse;
import br.puccampinas.uniride.repository.UsuarioRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService auth;

    @BeforeEach
    void setUp() {
        auth = new AuthService(new UsuarioRepositoryMemoria(), new SessaoService());
    }

    private CadastroRequest cadastroValido() {
        return new CadastroRequest("Maria Silva", "maria.silva@puccampinas.edu.br", "22012345", "senha123");
    }

    @Test
    void cadastraUsuarioValido() {
        UsuarioResponse usuario = auth.cadastrar(cadastroValido());

        assertEquals(1L, usuario.id);
        assertEquals("Maria Silva", usuario.nome);
        assertEquals("maria.silva@puccampinas.edu.br", usuario.email);
        assertEquals("22012345", usuario.ra);
    }

    @Test
    void normalizaEmailAoCadastrar() {
        CadastroRequest req = cadastroValido();
        req.email = "  Maria.Silva@PUCCampinas.edu.br ";

        assertEquals("maria.silva@puccampinas.edu.br", auth.cadastrar(req).email);
    }

    @Test
    void recusaCamposVaziosComErroPorCampo() {
        ApiException e = assertThrows(ApiException.class,
                () -> auth.cadastrar(new CadastroRequest("", "", "", "")));

        assertEquals(400, e.getStatus());
        assertTrue(e.getCampos().containsKey("nome"));
        assertTrue(e.getCampos().containsKey("email"));
        assertTrue(e.getCampos().containsKey("ra"));
        assertTrue(e.getCampos().containsKey("senha"));
    }

    @Test
    void recusaEmailForaDoDominioDaPuc() {
        CadastroRequest req = cadastroValido();
        req.email = "maria@gmail.com";

        ApiException e = assertThrows(ApiException.class, () -> auth.cadastrar(req));
        assertEquals(400, e.getStatus());
        assertTrue(e.getCampos().get("email").contains("institucional"));
    }

    @Test
    void recusaRaComLetras() {
        CadastroRequest req = cadastroValido();
        req.ra = "22A12345";

        ApiException e = assertThrows(ApiException.class, () -> auth.cadastrar(req));
        assertTrue(e.getCampos().containsKey("ra"));
    }

    @Test
    void recusaSenhaFraca() {
        CadastroRequest curta = cadastroValido();
        curta.senha = "abc1";
        assertTrue(assertThrows(ApiException.class, () -> auth.cadastrar(curta)).getCampos().containsKey("senha"));

        CadastroRequest semNumero = cadastroValido();
        semNumero.senha = "somenteletras";
        assertTrue(assertThrows(ApiException.class, () -> auth.cadastrar(semNumero)).getCampos().containsKey("senha"));
    }

    @Test
    void recusaEmailDuplicado() {
        auth.cadastrar(cadastroValido());
        CadastroRequest outro = cadastroValido();
        outro.ra = "99999999";

        ApiException e = assertThrows(ApiException.class, () -> auth.cadastrar(outro));
        assertEquals(409, e.getStatus());
        assertTrue(e.getCampos().containsKey("email"));
    }

    @Test
    void recusaRaDuplicado() {
        auth.cadastrar(cadastroValido());
        CadastroRequest outro = cadastroValido();
        outro.email = "outra.pessoa@puccampinas.edu.br";

        ApiException e = assertThrows(ApiException.class, () -> auth.cadastrar(outro));
        assertEquals(409, e.getStatus());
        assertTrue(e.getCampos().containsKey("ra"));
    }

    @Test
    void loginComDadosCorretosDevolveTokenValido() {
        auth.cadastrar(cadastroValido());

        LoginResponse resp = auth.login(new LoginRequest("MARIA.SILVA@puccampinas.edu.br", "senha123"));

        assertNotNull(resp.token);
        assertEquals("Maria Silva", resp.usuario.nome);
        assertEquals("Maria Silva", auth.usuarioLogado(resp.token).nome);
    }

    @Test
    void loginComSenhaErradaFalhaCom401() {
        auth.cadastrar(cadastroValido());

        ApiException e = assertThrows(ApiException.class,
                () -> auth.login(new LoginRequest("maria.silva@puccampinas.edu.br", "errada123")));
        assertEquals(401, e.getStatus());
    }

    @Test
    void loginComEmailInexistenteTemMesmaMensagemQueSenhaErrada() {
        auth.cadastrar(cadastroValido());

        ApiException inexistente = assertThrows(ApiException.class,
                () -> auth.login(new LoginRequest("ninguem@puccampinas.edu.br", "senha123")));
        ApiException senhaErrada = assertThrows(ApiException.class,
                () -> auth.login(new LoginRequest("maria.silva@puccampinas.edu.br", "errada123")));

        assertEquals(senhaErrada.getMessage(), inexistente.getMessage());
    }

    @Test
    void loginSemDadosFalhaCom400() {
        assertEquals(400, assertThrows(ApiException.class, () -> auth.login(new LoginRequest("", ""))).getStatus());
        assertEquals(400, assertThrows(ApiException.class, () -> auth.login(null)).getStatus());
    }

    @Test
    void logoutInvalidaToken() {
        auth.cadastrar(cadastroValido());
        String token = auth.login(new LoginRequest("maria.silva@puccampinas.edu.br", "senha123")).token;

        auth.logout(token);

        assertEquals(401, assertThrows(ApiException.class, () -> auth.usuarioLogado(token)).getStatus());
    }

    @Test
    void tokenInvalidoOuAusenteFalhaCom401() {
        assertEquals(401, assertThrows(ApiException.class, () -> auth.usuarioLogado("inventado")).getStatus());
        assertEquals(401, assertThrows(ApiException.class, () -> auth.usuarioLogado(null)).getStatus());
    }

    @Test
    void sessaoExpirada() {
        AuthService authExpira = new AuthService(new UsuarioRepositoryMemoria(), new SessaoService(Duration.ofMillis(-1)));
        authExpira.cadastrar(cadastroValido());
        String token = authExpira.login(new LoginRequest("maria.silva@puccampinas.edu.br", "senha123")).token;

        assertEquals(401, assertThrows(ApiException.class, () -> authExpira.usuarioLogado(token)).getStatus());
    }
}
