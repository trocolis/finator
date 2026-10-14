# ADR-0003 — Biblioteca compartilhada apenas técnica

Status: Aceito

## Contexto
Os serviços precisam de comportamentos transversais idênticos, como o formato
de erro da API. Copiar esse código em cada serviço gera divergência; compartilhar
**regras de negócio** entre serviços, por outro lado, cria acoplamento: uma
mudança em uma regra obrigaria a reimplantar todos os serviços juntos.

## Decisão
- O módulo `finator-commons` contém **apenas código técnico**: formato de erro,
  utilitários de infraestrutura e, futuramente, propagação de contexto.
- É proibido colocar entidades, regras de negócio ou DTOs de um serviço nele.
- A integração entre serviços se dá por **contratos** (HTTP e mensagens),
  verificados por testes de contrato, e não por classes compartilhadas.

## Consequências
- (+) Comportamento transversal consistente sem duplicação.
- (−) Toda mudança no `finator-commons` afeta todos os serviços; ela precisa
  ser compatível com versões anteriores.
