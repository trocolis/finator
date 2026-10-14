package dev.trocolis.finator.commons.problem;

import jakarta.validation.ConstraintViolation;

import java.net.URI;
import java.util.Set;

/**
 * Converte violações de Bean Validation em um {@link Problem} do tipo
 * {@link ProblemType#VALIDATION}.
 *
 * <p>Classe utilitária: {@code final} e com construtor privado, porque só tem
 * métodos estáticos e não faz sentido instanciá-la nem estendê-la.
 */
public final class ValidationProblemFactory {

    private ValidationProblemFactory() {
    }

    /**
     * EXERCÍCIO 0.1.0: veja docs/exercises/0.1.0-validation-problem.md.
     *
     * <p>Regras:
     * <ol>
     *   <li>{@code violations} nulo ou vazio lança {@link IllegalArgumentException}.</li>
     *   <li>{@code type}, {@code title} e {@code status} vêm de {@link ProblemType#VALIDATION}.</li>
     *   <li>Cada violação gera um {@link FieldViolation}; o campo é o <b>último nome</b> do
     *       caminho da violação ({@code create.request.email} vira {@code email}).</li>
     *   <li>A lista sai ordenada por campo e, no mesmo campo, por mensagem.</li>
     *   <li>{@code detail} conta os campos <b>distintos</b>: "1 campo inválido" ou
     *       "N campos inválidos".</li>
     * </ol>
     *
     * @param violations violações produzidas pelo Hibernate Validator
     * @param instance   caminho da requisição que falhou
     * @return o Problem pronto para ser serializado
     */
    public static Problem fromViolations(Set<? extends ConstraintViolation<?>> violations, URI instance) {
        // TODO Exercício 0.1.0: implemente e rode ./mvnw test -Pexercises -pl finator-commons
        throw new UnsupportedOperationException("Exercício 0.1.0 ainda não resolvido");
    }
}
