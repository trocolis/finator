# Changelog

Versionamento: `RELEASE.FEATURE.HOTFIX`.

## [0.1.0] — não lançada
### Adicionado
- Monorepo Maven multi-módulo com Java 25 e Quarkus 3.40.1 (`finator-commons`,
  `identity-service`, `ledger-service`) e Maven Wrapper.
- Erros no formato Problem Details (RFC 9457) para todos os serviços, com URNs
  estáveis por tipo de erro e respostas 500 sem detalhes internos.
- Verificação de configuração obrigatória: o serviço não sobe se faltar a
  conexão com o banco.
- Health checks de liveness e readiness, contrato OpenAPI e migrações Flyway
  com schema próprio por serviço.
- Dockerfile JVM multi-arquitetura com usuário não-root.
- Script idempotente de criação de bancos e usuários por serviço.
- CI no GitHub Actions rodando `./mvnw verify` em pushes e PRs.
- Documentação: histórias do 0.1.0, ADRs 0001 a 0004 e o primeiro exercício.
