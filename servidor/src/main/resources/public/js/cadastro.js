const formCadastro = document.getElementById("form-cadastro");
const alertaCadastro = document.getElementById("alerta");
const botaoCadastrar = document.getElementById("botao-cadastrar");

const DOMINIO_EMAIL = "@puccampinas.edu.br";

// Validação no navegador para dar retorno rápido. O servidor valida tudo de novo.
function validarCadastro(dados) {
    const erros = {};

    if (dados.nome.length < 3) {
        erros.nome = "Informe seu nome completo.";
    }
    if (!dados.email) {
        erros.email = "Informe seu e-mail.";
    } else if (!dados.email.toLowerCase().endsWith(DOMINIO_EMAIL)) {
        erros.email = "Use seu e-mail institucional (" + DOMINIO_EMAIL + ").";
    }
    if (!/^\d{5,12}$/.test(dados.ra)) {
        erros.ra = "O RA deve conter apenas números.";
    }
    if (dados.senha.length < 8 || !/[A-Za-z]/.test(dados.senha) || !/\d/.test(dados.senha)) {
        erros.senha = "A senha deve ter 8+ caracteres, com letra e número.";
    }
    if (dados.confirmacao !== dados.senha) {
        erros.confirmacao = "As senhas não conferem.";
    }
    return erros;
}

formCadastro.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    esconderAlerta(alertaCadastro);
    limparErrosCampos(formCadastro);

    const campos = formCadastro.elements;
    const dados = {
        nome: campos.nome.value.trim(),
        email: campos.email.value.trim(),
        ra: campos.ra.value.trim(),
        senha: campos.senha.value,
        confirmacao: campos.confirmacao.value,
    };

    const erros = validarCadastro(dados);
    if (Object.keys(erros).length > 0) {
        Object.entries(erros).forEach(([nome, msg]) => marcarErroCampo(formCadastro, nome, msg));
        mostrarAlerta(alertaCadastro, "Verifique os campos destacados.", "erro");
        return;
    }

    botaoCadastrar.disabled = true;
    botaoCadastrar.textContent = "Criando conta...";
    try {
        const usuario = await chamarApi("POST", "/api/auth/cadastro", {
            nome: dados.nome,
            email: dados.email,
            ra: dados.ra,
            senha: dados.senha,
        });
        sessionStorage.setItem("uniride.aviso", "Conta criada com sucesso! Faça login para continuar.");
        sessionStorage.setItem("uniride.email", usuario.email);
        location.href = "/login.html";
    } catch (erro) {
        Object.entries(erro.campos).forEach(([nome, msg]) => marcarErroCampo(formCadastro, nome, msg));
        mostrarAlerta(alertaCadastro, erro.mensagem, "erro");
    } finally {
        botaoCadastrar.disabled = false;
        botaoCadastrar.textContent = "Criar conta";
    }
});
