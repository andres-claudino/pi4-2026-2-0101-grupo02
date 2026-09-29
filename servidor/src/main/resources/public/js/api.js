// Funções compartilhadas para conversar com o servidor Java e guardar a sessão.

const CHAVE_TOKEN = "uniride.token";

const Sessao = {
    salvar(token) {
        localStorage.setItem(CHAVE_TOKEN, token);
    },
    token() {
        return localStorage.getItem(CHAVE_TOKEN);
    },
    limpar() {
        localStorage.removeItem(CHAVE_TOKEN);
    },
};

/**
 * Faz uma requisição à API. Em caso de erro lança um objeto
 * { status, mensagem, campos } com a resposta do servidor.
 */
async function chamarApi(metodo, caminho, corpo) {
    const opcoes = { method: metodo, headers: {} };

    if (corpo !== undefined) {
        opcoes.headers["Content-Type"] = "application/json";
        opcoes.body = JSON.stringify(corpo);
    }
    const token = Sessao.token();
    if (token) {
        opcoes.headers["Authorization"] = "Bearer " + token;
    }

    let resposta;
    try {
        resposta = await fetch(caminho, opcoes);
    } catch (e) {
        throw { status: 0, mensagem: "Não foi possível conectar ao servidor.", campos: {} };
    }

    if (resposta.status === 204) {
        return null;
    }
    const dados = await resposta.json().catch(() => ({}));
    if (!resposta.ok) {
        throw {
            status: resposta.status,
            mensagem: dados.erro || "Erro inesperado.",
            campos: dados.campos || {},
        };
    }
    return dados;
}

// ---- Ajudantes de formulário ----

function mostrarAlerta(elemento, mensagem, tipo) {
    elemento.textContent = mensagem;
    elemento.className = "alerta visivel " + tipo;
}

function esconderAlerta(elemento) {
    elemento.className = "alerta";
    elemento.textContent = "";
}

function marcarErroCampo(form, nome, mensagem) {
    const input = form.elements[nome];
    if (!input) return;
    const campo = input.closest(".campo");
    campo.classList.add("invalido");
    campo.querySelector(".erro-campo").textContent = mensagem;
}

function limparErrosCampos(form) {
    form.querySelectorAll(".campo").forEach((campo) => {
        campo.classList.remove("invalido");
        const erro = campo.querySelector(".erro-campo");
        if (erro) erro.textContent = "";
    });
}
