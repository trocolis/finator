package dev.trocolis.finator.commons.problem;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Corpo de uma resposta de erro no formato Problem Details (RFC 9457, ver ADR-0004).
 *
 * <p>É um {@code record}: imutável, com {@code equals}/{@code hashCode}/{@code toString}
 * gerados pelo compilador. Objetos imutáveis podem ser compartilhados entre threads
 * sem sincronização, o que importa num servidor que atende muitas requisições ao mesmo tempo.
 *
 * @param type     identificador estável do tipo de erro (URN ou {@code about:blank})
 * @param title    resumo legível do tipo de erro; não muda de uma ocorrência para outra
 * @param status   código HTTP, repetido no corpo para quem só enxerga o JSON
 * @param detail   explicação específica desta ocorrência
 * @param instance caminho da requisição que originou o erro
 * @param errors   extensão do Finator: campos inválidos, presente só em erros de validação
 */
// NON_EMPTY omite do JSON os campos nulos e a lista de erros vazia
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Problem(
        URI type,
        String title,
        int status,
        String detail,
        URI instance,
        List<FieldViolation> errors) {

    /** Media type definido pela RFC 9457 para este formato. */
    public static final String MEDIA_TYPE = "application/problem+json";

    /** Tipo genérico da RFC: o significado do erro é apenas o do próprio status HTTP. */
    public static final URI ABOUT_BLANK = URI.create("about:blank");

    /**
     * Construtor compacto: roda antes de atribuir os campos e é o lugar para validar
     * e normalizar. Aqui ele copia a lista para um {@link List#copyOf} imutável, assim
     * quem criou o Problem não consegue alterá-lo depois por fora.
     */
    public Problem {
        Objects.requireNonNull(type, "type é obrigatório");
        Objects.requireNonNull(title, "title é obrigatório");
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    /** Cria um Problem de um tipo conhecido do Finator, sem lista de campos. */
    public static Problem of(ProblemType type, String detail, URI instance) {
        return new Problem(type.uri(), type.title(), type.status(), detail, instance, List.of());
    }
}
