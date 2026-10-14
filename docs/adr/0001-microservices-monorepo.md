# ADR-0001 — Microsserviços em monorepo

Status: Aceito

## Contexto
O sistema tem responsabilidades com ritmos de mudança e requisitos diferentes:
identidade e autenticação, registro financeiro (contas, títulos, parcelas) e,
mais adiante, estatísticas e notificações. O projeto também serve para praticar
arquitetura distribuída: comunicação entre serviços, consistência eventual,
Sagas e testes de contrato.

## Decisão
- O sistema é dividido em serviços independentes, começando por
  `identity-service` e `ledger-service`.
- Todos ficam em um **monorepo** Maven multi-módulo, com um parent pom que
  centraliza a versão do Java, do Quarkus e dos plugins.
- Cada serviço é implantado separadamente e tem seu próprio banco (ver ADR-0002).

## Consequências
- (+) Um único PR pode alterar o contrato e seus dois lados ao mesmo tempo.
- (+) Versões de dependências ficam alinhadas sem esforço.
- (−) Existe a tentação de acoplar serviços por código compartilhado; a ADR-0003
  limita o que pode ser compartilhado.
- (−) Mais peças para operar do que em um monólito: exige observabilidade
  (traces distribuídos) desde cedo.
