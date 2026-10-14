# ADR-0004 — Erros no formato Problem Details (RFC 9457)

Status: Aceito

## Contexto
O app mobile precisa interpretar erros de vários serviços. Sem um padrão, cada
endpoint inventa o próprio formato, e o cliente acaba cheio de casos especiais.

## Decisão
Toda resposta de erro segue a [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)
com o media type `application/problem+json`:

```json
{
  "type": "urn:finator:problem:validation",
  "title": "Requisição inválida",
  "status": 400,
  "detail": "2 campos inválidos",
  "instance": "/api/v1/users",
  "errors": [
    { "field": "email", "message": "deve ser um endereço de e-mail válido" },
    { "field": "name", "message": "não deve estar em branco" }
  ]
}
```

- `type` é uma URN estável (`urn:finator:problem:<código>`); o app pode usar o
  valor para decidir o que fazer.
- `errors` é uma extensão, presente só em erros de validação.
- Erros 500 nunca trazem stack trace, nome de classe ou SQL; o detalhe fica só
  no log do servidor.

## Consequências
- (+) Um único tratador de erros no app.
- (+) Padrão de mercado, suportado por bibliotecas de vários clientes.
