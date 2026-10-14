package dev.trocolis.finator.commons.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

/**
 * Teste de integração dos mappers: sobe uma aplicação Quarkus mínima com o
 * {@link ProblemTestResource} e faz requisições HTTP reais com o RestAssured.
 */
@QuarkusTest
class ProblemExceptionMappersTest {

    @Test
    void unknownPathReturnsNotFoundProblem() {
        given()
                .when().get("/test/problems/does-not-exist")
                .then()
                .statusCode(404)
                .contentType(Problem.MEDIA_TYPE)
                .body("type", equalTo("about:blank"))
                .body("title", equalTo("Not Found"))
                .body("status", equalTo(404))
                .body("instance", equalTo("/test/problems/does-not-exist"));
    }

    @Test
    void webApplicationExceptionKeepsItsStatus() {
        given()
                .when().get("/test/problems/conflict")
                .then()
                .statusCode(409)
                .contentType(Problem.MEDIA_TYPE)
                .body("title", equalTo("Conflict"))
                .body("detail", equalTo("Registro alterado por outra requisição"));
    }

    @Test
    void unexpectedExceptionDoesNotLeakInternals() {
        given()
                .when().get("/test/problems/unexpected")
                .then()
                .statusCode(500)
                .contentType(Problem.MEDIA_TYPE)
                .body("type", equalTo("urn:finator:problem:internal"))
                .body(not(containsString("SELECT")))
                .body(not(containsString("IllegalStateException")));
    }

    @Test
    void validRequestPasses() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Ana", "email": "ana@example.com", "age": 30}
                        """)
                .when().post("/test/problems/sign-up")
                .then()
                .statusCode(204);
    }

    @Test
    @Tag("exercise")
    void invalidRequestReturnsValidationProblem() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "", "email": "ana@example.com", "age": 30}
                        """)
                .when().post("/test/problems/sign-up")
                .then()
                .statusCode(400)
                .contentType(Problem.MEDIA_TYPE)
                .body("type", equalTo("urn:finator:problem:validation"))
                .body("detail", equalTo("1 campo inválido"))
                .body("instance", equalTo("/test/problems/sign-up"))
                .body("errors", hasSize(1))
                .body("errors[0].field", equalTo("name"));
    }
}
