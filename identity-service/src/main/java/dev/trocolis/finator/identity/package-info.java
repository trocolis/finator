/**
 * Identidade: usuários, famílias, sessões e emissão de tokens JWT.
 *
 * <h2>Organização em camadas (Clean Architecture)</h2>
 * Cada funcionalidade do serviço será dividida em três camadas, e as dependências
 * apontam sempre para dentro:
 * <pre>
 *   infrastructure  →  application  →  domain
 * </pre>
 * <ul>
 *   <li><b>domain</b>: entidades, value objects e regras de negócio em Java puro.
 *       Não conhece Quarkus, JPA nem HTTP, por isso é testável sem subir nada.</li>
 *   <li><b>application</b>: casos de uso que orquestram o domínio e definem as
 *       portas (interfaces) que precisam do mundo externo, como repositórios.</li>
 *   <li><b>infrastructure</b>: adaptadores que implementam as portas, como
 *       endpoints REST, repositórios Panache e clientes de outros serviços.</li>
 * </ul>
 */
package dev.trocolis.finator.identity;
