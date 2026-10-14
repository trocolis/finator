package dev.trocolis.finator.commons.problem;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.net.URI;

/**
 * Converte exceções em respostas Problem Details para todos os serviços (ADR-0004).
 *
 * <p>No RESTEasy Reactive, um método anotado com {@link ServerExceptionMapper} trata a
 * exceção do tipo do seu parâmetro <b>e das subclasses</b>. Quando mais de um método serve,
 * vence o do tipo mais específico. Por isso o tratador de {@link Exception} só é usado
 * quando nenhum outro se aplica: ele é a rede de segurança para erros não previstos.
 *
 * <p>Esta classe fica num jar compartilhado. O Quarkus só a encontra porque o
 * {@code jandex-maven-plugin} gera um índice das classes do jar no build.
 */
public class ProblemExceptionMappers {

    private static final Logger LOG = Logger.getLogger(ProblemExceptionMappers.class);

    /**
     * Violações de Bean Validation ({@code @NotBlank}, {@code @Email} etc.) viram 400
     * com a lista de campos inválidos.
     *
     * <p>A validação de parâmetros REST lança {@code ResteasyReactiveViolationException},
     * subclasse de {@link ConstraintViolationException}, então ela também cai aqui.
     */
    @ServerExceptionMapper
    public RestResponse<Problem> mapValidation(ConstraintViolationException exception, UriInfo uriInfo) {
        Problem problem = ValidationProblemFactory.fromViolations(
                exception.getConstraintViolations(), instanceOf(uriInfo));
        return toResponse(problem);
    }

    /**
     * Exceções HTTP do Jakarta REST ({@code NotFoundException}, {@code ForbiddenException} etc.)
     * mantêm o status original. O tipo é {@code about:blank}, que pela RFC significa
     * "o erro é exatamente o que o status HTTP diz", e o título é a frase padrão do status.
     */
    @ServerExceptionMapper
    public RestResponse<Problem> mapWebApplication(WebApplicationException exception, UriInfo uriInfo) {
        int status = exception.getResponse().getStatus();
        Response.StatusType statusType = Response.Status.fromStatusCode(status);
        String title = statusType != null ? statusType.getReasonPhrase() : "HTTP " + status;
        Problem problem = new Problem(
                Problem.ABOUT_BLANK, title, status, exception.getMessage(), instanceOf(uriInfo), null);
        return toResponse(problem);
    }

    /**
     * Qualquer outra exceção é um defeito: registra o stack trace completo no log e
     * devolve ao cliente apenas uma mensagem genérica. Detalhes internos (nomes de
     * classe, SQL, caminhos) ajudam quem ataca a API e não ajudam o usuário.
     */
    @ServerExceptionMapper
    public RestResponse<Problem> mapUnexpected(Exception exception, UriInfo uriInfo) {
        URI instance = instanceOf(uriInfo);
        LOG.errorf(exception, "Erro inesperado ao processar %s", instance);
        return toResponse(Problem.of(
                ProblemType.INTERNAL,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                instance));
    }

    /** Monta a resposta com o status do Problem e o media type da RFC 9457. */
    private static RestResponse<Problem> toResponse(Problem problem) {
        return RestResponse.ResponseBuilder.<Problem>create(problem.status())
                .entity(problem)
                .type(Problem.MEDIA_TYPE)
                .build();
    }

    /** Caminho relativo da requisição (ex.: {@code /api/v1/users}), sem host nem porta. */
    private static URI instanceOf(UriInfo uriInfo) {
        return URI.create(uriInfo.getPath());
    }
}
