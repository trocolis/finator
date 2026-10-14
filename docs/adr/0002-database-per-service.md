# ADR-0002 — Um banco de dados por serviço

Status: Aceito

## Contexto
Se dois serviços compartilham tabelas, qualquer mudança de esquema vira uma
mudança coordenada, e o isolamento entre eles deixa de existir na prática.

## Decisão
- Cada serviço tem seu **próprio database** e **seu próprio usuário** no
  PostgreSQL (`finator_identity`, `finator_ledger`).
- O usuário de um serviço não tem permissão de conexão no banco do outro.
- O esquema de cada banco é versionado pelo Flyway dentro do próprio serviço
  (`src/main/resources/db/migration`).
- Por economia, os databases ficam na mesma instância do PostgreSQL. Separar em
  instâncias diferentes no futuro não exige mudança de código.

## Consequências
- (+) Cada serviço evolui o próprio esquema sem coordenação.
- (−) Consultas que cruzam dados de serviços diferentes não podem ser feitas
  com `JOIN`; elas exigem chamadas entre serviços ou um modelo de leitura
  alimentado por eventos (previsto para o `insights-service`).
