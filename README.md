# UniRide

UniRide é um aplicativo de caronas entre estudantes da PUC-Campinas, criado para facilitar o deslocamento entre a universidade e outros locais, conectando estudantes passageiros e motoristas de forma prática e acessível.

## Integrantes

- Andres Olate Gonzalez Claudino (@andres-claudino)
- Ibrahim Fleury De Camargo Madeira Neto (@ibrahimfcmneto)
- João Gabriel Altarujo (@joaoaltarujo)
- Leonardo Carlos Barboza Filho (@Leonardo-cbf)
- Tonianni Leitão Calsolari (@TonianniCalsolari)
- Victor Hugo Asvolinsque (@victorasvolinsque)

## Projeto

Projeto Integrador - 2026/2

- Turma: 0101
- Grupo: 02

## Finalidade deste repositório

Este repositório reúne a documentação acadêmica do Projeto Integrador, incluindo escopo, relatórios, decisões gerais e informações do grupo. O código da aplicação é mantido em repositórios independentes.

## Repositórios do projeto

| Repositório | Responsabilidade |
|---|---|
| `pi4-2026-2-0101-grupo02` | Documentação e arquivos acadêmicos |
| `pi4-0101-g2-frontend` | Aplicativo mobile em Flutter e Dart |
| `pi4-0101-g2-backend-1` | API REST em Java e Spring Boot, regras de negócio e PostgreSQL |
| `pi4-0101-g2-backend-2` | Servidor Java puro da disciplina, baseado em sockets e threads |

## Documentação

- [Escopo do projeto](docs/escopo.pdf)

## Executar o sistema em uma máquina limpa

### 1. Pré-requisitos

- Git;
- Docker Desktop com Docker Compose;
- Flutter e Android SDK apenas para executar o aplicativo.

Clone os quatro repositórios dentro da mesma pasta, mantendo esta organização:

```text
UniRide/
├── pi4-2026-2-0101-grupo02/
├── pi4-0101-g2-backend-1/
├── pi4-0101-g2-backend-2/
└── pi4-0101-g2-frontend/
```

### 2. Configurar o ambiente

Na raiz deste repositório acadêmico:

```powershell
Copy-Item .env.example .env
```

Abra `.env` e substitua `troque_esta_senha` por uma senha local. O arquivo `.env` é ignorado pelo Git e não deve ser versionado.

### 3. Subir banco e backends

```powershell
docker compose up --build
```

O Compose aguarda o PostgreSQL e o Backend 2 ficarem prontos antes de iniciar o Backend 1. Em um banco novo, o Backend 1 executa automaticamente as migrations Flyway V1–V9; não existe um script de schema paralelo.

Serviços e portas:

| Serviço | Porta | Uso |
|---|---:|---|
| PostgreSQL | 5432 (rede interna do Compose) | Persistência |
| Backend 2 | 3000/TCP | Ranking de compatibilidade |
| Backend 1 | 8080/HTTP | API consumida pelo Flutter |

O profile `demo`, usado somente pelo Compose, cria dados idempotentes. Todas as
contas abaixo usam a senha `Demo123!`:

| Perfil | Nome | E-mail | RA |
|---|---|---|---|
| Motorista | Lucas Almeida | `lucas.almeida@puccampinas.edu.br` | `91000001` |
| Motorista | Mariana Costa | `mariana.costa@puccampinas.edu.br` | `91000002` |
| Motorista | Rafael Martins | `rafael.martins@puccampinas.edu.br` | `91000003` |
| Motorista | Beatriz Souza | `beatriz.souza@puccampinas.edu.br` | `91000004` |
| Motorista | Gabriel Oliveira | `gabriel.oliveira@puccampinas.edu.br` | `91000005` |
| Passageira | Ana Ferreira | `ana.ferreira@puccampinas.edu.br` | `91000006` |
| Passageiro | Pedro Lima | `pedro.lima@puccampinas.edu.br` | `91000007` |
| Passageira | Camila Rocha | `camila.rocha@puccampinas.edu.br` | `91000008` |
| Passageiro | Felipe Santos | `felipe.santos@puccampinas.edu.br` | `91000009` |
| Passageira | Julia Ribeiro | `julia.ribeiro@puccampinas.edu.br` | `91000010` |
| Passageiro | Thiago Nunes | `thiago.nunes@puccampinas.edu.br` | `91000011` |

O conjunto demonstrativo contém 12 caronas futuras disponíveis, distribuídas
entre os dois sentidos e os dois campi, cinco caronas concluídas e uma carona
cancelada. Também inclui 22 solicitações em diferentes estados, 15 avaliações e
20 notificações para permitir a demonstração dos fluxos do MVP. As datas são
calculadas em relação ao dia de execução. A inicialização normal, sem o profile
`demo`, não cria dados fictícios.

Confirme a API abrindo `http://localhost:8080/caronas`. Para encerrar:

```powershell
docker compose down
```

Para apagar também o volume e testar novamente com banco vazio:

```powershell
docker compose down -v
docker compose up --build
```

### 4. Executar o Flutter

Em outro terminal, dentro de `pi4-0101-g2-frontend`:

```powershell
flutter pub get
flutter run -d chrome
```

Flutter Web usa `http://localhost:8080`. No Android Emulator, o aplicativo usa `http://10.0.2.2:8080`, pois `localhost` apontaria para o próprio emulador. Para um aparelho físico, informe o IP do computador:

```powershell
flutter run --dart-define=API_BASE_URL=http://192.168.0.10:8080
```
