package dev.trocolis.finator.commons.config;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste unitário puro: monta um {@link SmallRyeConfig} em memória, sem subir o Quarkus,
 * e verifica cada forma de uma propriedade ser considerada ausente.
 */
class RequiredConfigCheckTest {

    private static SmallRyeConfig configWith(Map<String, String> properties) {
        // addDefaultInterceptors liga a expansão de ${...}, como no Quarkus
        return new SmallRyeConfigBuilder()
                .addDefaultInterceptors()
                .withSources(new PropertiesConfigSource(properties, "test", 100))
                .build();
    }

    @Test
    void acceptsResolvedProperties() {
        var config = configWith(Map.of(
                "db.host", "localhost",
                "db.url", "jdbc:postgresql://${db.host}/app"));

        assertThat(RequiredConfigCheck.findMissing(config, List.of("db.host", "db.url"))).isEmpty();
    }

    @Test
    void reportsMissingBlankAndUnresolvableProperties() {
        var config = configWith(Map.of(
                "db.user", "   ",
                "db.url", "jdbc:postgresql://${UNDEFINED_HOST_VARIABLE}/app"));

        assertThat(RequiredConfigCheck.findMissing(config, List.of("db.password", "db.user", "db.url")))
                .containsExactly("db.password", "db.user", "db.url");
    }
}
