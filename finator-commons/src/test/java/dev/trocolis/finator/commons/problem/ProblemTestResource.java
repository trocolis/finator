package dev.trocolis.finator.commons.problem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;

/**
 * Endpoints que existem só nos testes, para provocar cada tipo de erro
 * e verificar a resposta produzida pelos {@link ProblemExceptionMappers}.
 */
@Path("/test/problems")
public class ProblemTestResource {

    /** Corpo de exemplo com regras de validação variadas. */
    public record SignUpRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @Positive int age) {
    }

    @POST
    @Path("/sign-up")
    public void signUp(@Valid SignUpRequest request) {
        // Nada a fazer: se chegou aqui, a requisição é válida
    }

    @GET
    @Path("/conflict")
    public void conflict() {
        throw new WebApplicationException("Registro alterado por outra requisição", 409);
    }

    @GET
    @Path("/unexpected")
    public void unexpected() {
        // Mensagem que imita um vazamento: o teste garante que ela não chega ao cliente
        throw new IllegalStateException("SELECT * FROM users WHERE password = 'segredo'");
    }
}
