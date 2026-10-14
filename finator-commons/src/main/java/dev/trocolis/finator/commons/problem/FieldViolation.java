package dev.trocolis.finator.commons.problem;

/**
 * Um campo inválido dentro de um {@link Problem} de validação.
 *
 * @param field   nome do campo, como o cliente o enviou no JSON (ex.: {@code email})
 * @param message mensagem da regra violada, já traduzida para o idioma configurado
 */
public record FieldViolation(String field, String message) {
}
