package dev.trocolis.finator.commons.problem;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * EXERCÍCIO 0.1.0: veja docs/exercises/0.1.0-validation-problem.md.
 *
 * <p>Usa o {@link Validator} gerenciado pelo Quarkus para gerar violações reais,
 * com as mensagens em português configuradas em application.properties.
 */
@QuarkusTest
@Tag("exercise")
class ValidationProblemFactoryTest {

    private static final URI INSTANCE = URI.create("/api/v1/users");

    record Customer(@NotBlank String name, @NotBlank @Email String email, @Positive int age) {
    }

    record Order(@Valid Customer customer) {
    }

    @Inject
    Validator validator;

    @Test
    void buildsValidationProblemForSingleField() {
        var violations = validator.validate(new Customer("", "ana@example.com", 30));

        Problem problem = ValidationProblemFactory.fromViolations(violations, INSTANCE);

        assertThat(problem.type()).isEqualTo(URI.create("urn:finator:problem:validation"));
        assertThat(problem.title()).isEqualTo("Requisição inválida");
        assertThat(problem.status()).isEqualTo(400);
        assertThat(problem.detail()).isEqualTo("1 campo inválido");
        assertThat(problem.instance()).isEqualTo(INSTANCE);
        assertThat(problem.errors()).extracting(FieldViolation::field).containsExactly("name");
    }

    @Test
    void sortsErrorsByFieldName() {
        var violations = validator.validate(new Customer("", "not-an-email", 0));

        Problem problem = ValidationProblemFactory.fromViolations(violations, INSTANCE);

        assertThat(problem.errors()).extracting(FieldViolation::field).containsExactly("age", "email", "name");
        assertThat(problem.detail()).isEqualTo("3 campos inválidos");
    }

    @Test
    void keepsOriginalMessages() {
        Set<ConstraintViolation<Customer>> violations = validator.validate(new Customer("", "not-an-email", 0));

        Problem problem = ValidationProblemFactory.fromViolations(violations, INSTANCE);

        Set<String> expected = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertThat(problem.errors()).extracting(FieldViolation::message).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void countsEachFieldOnceEvenWithManyViolations() {
        // "   " viola @NotBlank e @Email ao mesmo tempo: duas violações, um campo só
        var violations = validator.validate(new Customer("Ana", "   ", 30));

        Problem problem = ValidationProblemFactory.fromViolations(violations, INSTANCE);

        assertThat(problem.errors()).hasSize(2).extracting(FieldViolation::field).containsOnly("email");
        assertThat(problem.errors()).extracting(FieldViolation::message).isSorted();
        assertThat(problem.detail()).isEqualTo("1 campo inválido");
    }

    @Test
    void usesOnlyTheLastNameOfNestedPaths() {
        // O caminho da violação é "customer.email"; o app espera apenas "email"
        var violations = validator.validate(new Order(new Customer("Ana", "not-an-email", 30)));

        Problem problem = ValidationProblemFactory.fromViolations(violations, INSTANCE);

        assertThat(problem.errors()).extracting(FieldViolation::field).containsExactly("email");
    }

    @Test
    void rejectsEmptyViolations() {
        assertThatThrownBy(() -> ValidationProblemFactory.fromViolations(Set.of(), INSTANCE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ValidationProblemFactory.fromViolations(null, INSTANCE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
