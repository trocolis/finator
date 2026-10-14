# Exercícios

Cada versão traz um ou dois exercícios. O código de produção vem com um
`TODO`, e os testes já estão escritos e marcados com `@Tag("exercise")`.

## Como funciona
1. Leia o enunciado da versão nesta pasta.
2. Rode só os exercícios: `./mvnw test -Pexercises -pl <módulo>`.
   No começo os testes falham.
3. Implemente até todos passarem.
4. Remova o `@Tag("exercise")` dos testes: a partir daí eles entram no build
   normal e na CI.
5. Faça o commit na branch da versão e peça revisão no PR.

Os testes marcados como exercício ficam fora do `./mvnw verify` padrão
(propriedade `surefire.excludedGroups` no parent pom). Assim a CI continua verde
enquanto o exercício está pendente.

| Versão | Exercício | Módulo | Status |
|---|---|---|---|
| 0.1.0 | [Problem de validação](0.1.0-validation-problem.md) | `finator-commons` | Pendente |
