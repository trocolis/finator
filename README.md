# Finator

API para gestão financeira pessoal, consumida por um aplicativo mobile.
Valores a receber e a pagar com periodicidade, prazos, ajustes, multas e juros,
além de estatísticas sobre esses dados.

Feita com **Java 25**, **Quarkus 3.40** e **PostgreSQL**, em arquitetura de
microsserviços.

## Arquitetura

```
                  App mobile
                      │ HTTPS
        ┌─────────────┴──────────────┐
        ▼                            ▼
┌──────────────────┐        ┌──────────────────┐
│ identity-service │  JWKS  │  ledger-service  │
│ usuários, família├───────►│ contas, títulos, │
│ JWT              │        │ parcelas         │
└────────┬─────────┘        └────────┬─────────┘
         ▼                           ▼
   finator_identity            finator_ledger      (PostgreSQL)
```

| Módulo | Porta | Responsabilidade |
|---|---|---|
| `finator-commons` | — | Biblioteca técnica: erros RFC 9457, verificação de configuração |
| `identity-service` | 8081 | Usuários, famílias, autenticação e emissão de JWT |
| `ledger-service` | 8082 | Contas, categorias, títulos, parcelas e efetivações |

As decisões de arquitetura ficam registradas em [`docs/adr`](docs/adr/README.md).

## Pré-requisitos
- JDK 25
- Docker, para os testes de integração (o Quarkus Dev Services sobe um
  PostgreSQL descartável)
- Um PostgreSQL acessível, para rodar os serviços em modo dev

O Maven não precisa estar instalado: use o wrapper `./mvnw`.

## Primeiros passos

1. Crie os bancos e usuários (uma vez, como administrador do PostgreSQL):
   ```bash
   psql -h <host> -U postgres \
        -v identity_password='...' -v ledger_password='...' \
        -f deploy/postgres/create-databases.sql
   ```
2. Configure as variáveis locais:
   ```bash
   cp .env.example .env   # preencha host e senhas; o .env não vai para o git
   ```
3. Suba um serviço em modo dev (com live reload):
   ```bash
   scripts/dev.sh identity   # http://localhost:8081
   scripts/dev.sh ledger     # http://localhost:8082
   ```

### Endpoints operacionais (em cada serviço)
| Caminho | Uso |
|---|---|
| `/q/health/live` | Liveness: o processo está vivo |
| `/q/health/ready` | Readiness: pronto para tráfego (inclui o banco) |
| `/q/openapi` | Contrato OpenAPI |
| `/q/swagger-ui` | Swagger UI (só em dev) |
| `/q/dev-ui` | Dev UI do Quarkus (só em dev) |

## Testes

```bash
./mvnw verify                                  # todos os módulos (exige Docker)
./mvnw verify -pl finator-commons              # só a biblioteca (não exige Docker)
./mvnw test -Pexercises -pl finator-commons    # só os exercícios pendentes
```

Os testes marcados com `@Tag("exercise")` são exercícios em aberto e ficam fora do
build padrão. Veja [`docs/exercises`](docs/exercises/README.md).

## Container

```bash
./mvnw package -DskipTests
cd identity-service
docker build -f src/main/docker/Dockerfile.jvm -t finator/identity-service .
docker run --env-file ../.env -p 8081:8081 finator/identity-service
```

A imagem base é multi-arquitetura (amd64 e arm64).

## Como o projeto evolui
- **Versão** `RELEASE.FEATURE.HOTFIX` (X.Y.Z), registrada no
  [`CHANGELOG.md`](CHANGELOG.md) e numa tag `vX.Y.Z`.
- Cada versão começa pelo **refinamento**: histórias com critérios de aceite e
  estimativa em [`docs/stories`](docs/stories).
- O trabalho acontece numa branch `feat/X.Y.Z-<assunto>`, com commits no padrão
  Conventional Commits, e entra na `main` por **pull request** revisado e com a
  CI verde.
