# UniRide — Servidor

Servidor em Java 8 (sem framework, usando o `HttpServer` do próprio JDK) que atende a API do UniRide e as telas web.

> **Os dados ficam em memória.** Ao reiniciar o servidor, usuários e sessões são apagados.
> A integração com banco será feita depois (ver [Próximos passos](#próximos-passos)).

## Requisitos

- JDK 8 ou superior
- Maven 3.6+

## Como rodar

```bash
cd servidor
mvn compile exec:java
```

Acesse **http://localhost:8080**. Para usar outra porta: `PORT=9090 mvn compile exec:java`.

Para gerar um `.jar` executável:

```bash
mvn package
java -jar target/uniride-servidor.jar
```

## Testes

```bash
mvn test
```

> Se o Maven disser `Tests are skipped`, o seu `settings.xml` está pulando os testes. Rode
> `mvn test -Dmaven.test.skip=false -DskipTests=false`.

## Estrutura

```
src/main/java/br/puccampinas/uniride/
├── App.java                 # sobe o servidor e liga as peças
├── http/                    # recebe as requisições HTTP (rotas)
│   ├── AuthHandler.java     #   /api/auth/*
│   ├── ArquivosEstaticosHandler.java  # serve as telas (resources/public)
│   └── HttpUtil.java        #   leitura de JSON / respostas
├── service/                 # regras de negócio
│   ├── AuthService.java     #   cadastro, login, logout, validações
│   ├── SessaoService.java   #   tokens de sessão
│   ├── Senhas.java          #   hash de senha (PBKDF2)
│   └── ApiException.java    #   erro com status HTTP + mensagem
├── repository/              # acesso aos dados
│   ├── UsuarioRepository.java         # interface
│   └── UsuarioRepositoryMemoria.java  # implementação em memória (atual)
├── model/Usuario.java
└── dto/                     # formato dos JSONs de entrada/saída

src/main/resources/public/   # telas web (HTML/CSS/JS)
├── login.html  cadastro.html  inicio.html
├── css/style.css
└── js/api.js  login.js  cadastro.js  inicio.js
```

## API

Todas as rotas recebem e devolvem JSON. Em caso de erro, a resposta tem o formato:

```json
{ "erro": "Mensagem para exibir", "campos": { "email": "E-mail já cadastrado." } }
```

| Método | Rota | Corpo | Sucesso |
|---|---|---|---|
| POST | `/api/auth/cadastro` | `{nome, email, ra, senha}` | `201` `{id, nome, email, ra}` |
| POST | `/api/auth/login` | `{email, senha}` | `200` `{token, usuario}` |
| GET | `/api/auth/me` | — (cabeçalho `Authorization: Bearer <token>`) | `200` `{id, nome, email, ra}` |
| POST | `/api/auth/logout` | — (cabeçalho `Authorization: Bearer <token>`) | `204` |

Erros: `400` dados inválidos · `401` login/sessão inválidos · `409` e-mail ou RA já cadastrado.

### Regras de cadastro

- **Nome:** 3 a 100 caracteres.
- **E-mail:** precisa ser institucional, `@puccampinas.edu.br` (constante `AuthService.DOMINIO_EMAIL`). Não pode repetir.
- **RA:** somente números (5 a 12 dígitos). Não pode repetir.
- **Senha:** 8 a 72 caracteres, com pelo menos uma letra e um número. Guardada só como hash PBKDF2.

A sessão dura 8 horas. No login, a mensagem de erro é a mesma para e-mail inexistente e senha errada.

## Próximos passos

- **Banco de dados:** criar uma classe que implemente `UsuarioRepository` (ex.: `UsuarioRepositoryJdbc`) e trocar a instância em `App.java`.
- **E2 — Perfil:** a tela `inicio.html` é provisória; ela vai virar a tela de perfil (com a opção "quero oferecer caronas").
