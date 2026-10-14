package dev.trocolis.finator.identity;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

/**
 * Endpoints de operação, que o orquestrador de containers e o app consultam.
 *
 * <p>Teste de integração: o Quarkus Dev Services sobe um PostgreSQL real em
 * container, então este teste exige Docker. O Flyway aplica as migrações nesse
 * banco antes do primeiro teste rodar.
 */
@QuarkusTest
class OperationalEndpointsTest {

    @Test
    void livenessIsUp() {
        // Liveness: "o processo está vivo?". Se falhar, o orquestrador reinicia o container.
        given()
                .when().get("/q/health/live")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void readinessChecksTheDatabase() {
        // Readiness: "posso receber tráfego?". Inclui a conexão com o banco.
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks.name", hasItem(containsString("Database connections health check")));
    }

    @Test
    void publishesOpenApiContract() {
        given()
                .queryParam("format", "json")
                .when().get("/q/openapi")
                .then()
                .statusCode(200)
                .body("info.title", equalTo("Finator Identity API"));
    }
}
