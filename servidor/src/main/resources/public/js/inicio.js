async function carregarUsuario() {
    if (!Sessao.token()) {
        location.replace("/login.html");
        return;
    }
    try {
        const usuario = await chamarApi("GET", "/api/auth/me");
        const primeiroNome = usuario.nome.split(" ")[0];
        document.getElementById("saudacao").textContent = "Olá, " + primeiroNome + "!";
        document.getElementById("dado-nome").textContent = usuario.nome;
        document.getElementById("dado-email").textContent = usuario.email;
        document.getElementById("dado-ra").textContent = usuario.ra;
    } catch (erro) {
        // Token inválido ou expirado (ou servidor reiniciado): volta para o login
        Sessao.limpar();
        location.replace("/login.html");
    }
}

document.getElementById("botao-sair").addEventListener("click", async () => {
    try {
        await chamarApi("POST", "/api/auth/logout");
    } finally {
        Sessao.limpar();
        location.replace("/login.html");
    }
});

carregarUsuario();
