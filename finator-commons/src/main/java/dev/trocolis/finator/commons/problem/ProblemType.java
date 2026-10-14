package dev.trocolis.finator.commons.problem;

import java.net.URI;

/**
 * Catálogo dos tipos de erro próprios do Finator.
 *
 * <p>Usar um {@code enum} em vez de strings soltas garante que o mesmo tipo tenha
 * sempre a mesma URN, o mesmo título e o mesmo status em todos os serviços. O app
 * mobile decide o que fazer olhando o campo {@code type}, então ele precisa ser estável.
 */
public enum ProblemType {

    VALIDATION("validation", "Requisição inválida", 400),
    INTERNAL("internal", "Erro interno", 500);

    /** Prefixo comum de todas as URNs de erro do Finator. */
    private static final String URN_PREFIX = "urn:finator:problem:";

    private final URI uri;
    private final String title;
    private final int status;

    ProblemType(String code, String title, int status) {
        this.uri = URI.create(URN_PREFIX + code);
        this.title = title;
        this.status = status;
    }

    public URI uri() {
        return uri;
    }

    public String title() {
        return title;
    }

    public int status() {
        return status;
    }
}
