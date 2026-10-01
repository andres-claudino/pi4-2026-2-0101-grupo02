package br.puccampinas.uniride.service;

import br.puccampinas.uniride.dto.CadastroRequest;
import br.puccampinas.uniride.dto.LoginRequest;
import br.puccampinas.uniride.dto.LoginResponse;
import br.puccampinas.uniride.dto.UsuarioResponse;
import br.puccampinas.uniride.model.Usuario;
import br.puccampinas.uniride.repository.UsuarioRepository;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Regras de cadastro, login e logout de usuários (entregável E1).
 */
public class AuthService {

    /** Apenas e-mails institucionais da PUC-Campinas podem se cadastrar. */
    public static final String DOMINIO_EMAIL = "puccampinas.edu.br";

    public static final int NOME_MIN = 3;
    public static final int NOME_MAX = 100;
    public static final int SENHA_MIN = 8;
    public static final int SENHA_MAX = 72;

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$");
    private static final Pattern FORMATO_RA = Pattern.compile("^\\d{5,12}$");
    private static final String MSG_LOGIN_INVALIDO = "E-mail ou senha inválidos.";

    private final UsuarioRepository usuarios;
    private final SessaoService sessoes;

    public AuthService(UsuarioRepository usuarios, SessaoService sessoes) {
        this.usuarios = usuarios;
        this.sessoes = sessoes;
    }

    /**
     * Cria a conta do estudante. Lança {@link ApiException} 400 se algum campo for inválido
     * ou 409 se e-mail/RA já estiverem em uso.
     *
     * É synchronized para evitar que duas requisições simultâneas cadastrem o mesmo e-mail.
     */
    public synchronized UsuarioResponse cadastrar(CadastroRequest req) {
        if (req == null) {
            throw new ApiException(400, "Preencha os dados do cadastro.");
        }
        String nome = limpar(req.nome);
        String email = normalizarEmail(req.email);
        String ra = limpar(req.ra);
        String senha = req.senha == null ? "" : req.senha;

        Map<String, String> erros = new LinkedHashMap<>();

        if (nome.isEmpty()) {
            erros.put("nome", "Informe seu nome.");
        } else if (nome.length() < NOME_MIN || nome.length() > NOME_MAX) {
            erros.put("nome", "O nome deve ter entre " + NOME_MIN + " e " + NOME_MAX + " caracteres.");
        }

        if (email.isEmpty()) {
            erros.put("email", "Informe seu e-mail.");
        } else if (!FORMATO_EMAIL.matcher(email).matches()) {
            erros.put("email", "E-mail inválido.");
        } else if (!email.endsWith("@" + DOMINIO_EMAIL)) {
            erros.put("email", "Use seu e-mail institucional (@" + DOMINIO_EMAIL + ").");
        }

        if (ra.isEmpty()) {
            erros.put("ra", "Informe seu RA.");
        } else if (!FORMATO_RA.matcher(ra).matches()) {
            erros.put("ra", "O RA deve conter apenas números.");
        }

        if (senha.isEmpty()) {
            erros.put("senha", "Informe uma senha.");
        } else if (senha.length() < SENHA_MIN || senha.length() > SENHA_MAX) {
            erros.put("senha", "A senha deve ter entre " + SENHA_MIN + " e " + SENHA_MAX + " caracteres.");
        } else if (!senha.matches(".*[A-Za-z].*") || !senha.matches(".*\\d.*")) {
            erros.put("senha", "A senha deve ter pelo menos uma letra e um número.");
        }

        if (!erros.isEmpty()) {
            throw new ApiException(400, "Verifique os campos destacados.", erros);
        }

        if (usuarios.buscarPorEmail(email).isPresent()) {
            throw new ApiException(409, "Já existe uma conta com este e-mail.", campo("email", "E-mail já cadastrado."));
        }
        if (usuarios.buscarPorRa(ra).isPresent()) {
            throw new ApiException(409, "Já existe uma conta com este RA.", campo("ra", "RA já cadastrado."));
        }

        Usuario usuario = usuarios.salvar(new Usuario(nome, email, ra, Senhas.gerarHash(senha)));
        return new UsuarioResponse(usuario);
    }

    /**
     * Confere e-mail e senha e abre uma sessão. Em caso de falha a mensagem é sempre a mesma,
     * para não revelar se o e-mail existe ou não.
     */
    public LoginResponse login(LoginRequest req) {
        String email = req == null ? "" : normalizarEmail(req.email);
        String senha = req == null || req.senha == null ? "" : req.senha;

        if (email.isEmpty() || senha.isEmpty()) {
            throw new ApiException(400, "Informe e-mail e senha.");
        }

        Optional<Usuario> encontrado = usuarios.buscarPorEmail(email);
        if (!encontrado.isPresent() || !Senhas.conferir(senha, encontrado.get().getSenhaHash())) {
            throw new ApiException(401, MSG_LOGIN_INVALIDO);
        }

        Usuario usuario = encontrado.get();
        String token = sessoes.criar(usuario.getId());
        return new LoginResponse(token, new UsuarioResponse(usuario));
    }

    /** Devolve o usuário logado a partir do token, ou lança 401 se a sessão não for válida. */
    public UsuarioResponse usuarioLogado(String token) {
        return sessoes.buscarUsuarioId(token)
                .flatMap(usuarios::buscarPorId)
                .map(UsuarioResponse::new)
                .orElseThrow(() -> new ApiException(401, "Sessão expirada. Faça login novamente."));
    }

    public void logout(String token) {
        sessoes.encerrar(token);
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String normalizarEmail(String email) {
        return limpar(email).toLowerCase(Locale.ROOT);
    }

    private static Map<String, String> campo(String nome, String mensagem) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(nome, mensagem);
        return m;
    }
}
