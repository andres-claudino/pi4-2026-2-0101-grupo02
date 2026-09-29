const formLogin = document.getElementById("form-login");
const alertaLogin = document.getElementById("alerta");
const botaoEntrar = document.getElementById("botao-entrar");

// Mensagem vinda da tela de cadastro (ex.: "Conta criada!")
const aviso = sessionStorage.getItem("uniride.aviso");
if (aviso) {
    mostrarAlerta(alertaLogin, aviso, "sucesso");
    sessionStorage.removeItem("uniride.aviso");
}
const emailCadastrado = sessionStorage.getItem("uniride.email");
if (emailCadastrado) {
    formLogin.elements.email.value = emailCadastrado;
    formLogin.elements.senha.focus();
    sessionStorage.removeItem("uniride.email");
}

formLogin.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    esconderAlerta(alertaLogin);
    limparErrosCampos(formLogin);

    const email = formLogin.elements.email.value.trim();
    const senha = formLogin.elements.senha.value;

    let valido = true;
    if (!email) {
        marcarErroCampo(formLogin, "email", "Informe seu e-mail.");
        valido = false;
    }
    if (!senha) {
        marcarErroCampo(formLogin, "senha", "Informe sua senha.");
        valido = false;
    }
    if (!valido) return;

    botaoEntrar.disabled = true;
    botaoEntrar.textContent = "Entrando...";
    try {
        const resposta = await chamarApi("POST", "/api/auth/login", { email, senha });
        Sessao.salvar(resposta.token);
        location.href = "/inicio.html";
    } catch (erro) {
        mostrarAlerta(alertaLogin, erro.mensagem, "erro");
        formLogin.elements.senha.value = "";
    } finally {
        botaoEntrar.disabled = false;
        botaoEntrar.textContent = "Entrar";
    }
});
