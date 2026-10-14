package dev.trocolis.finator.commons.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Impede que o serviço suba com configuração obrigatória faltando.
 *
 * <p><b>Por que existe:</b> quando a URL do banco referencia uma variável de ambiente
 * inexistente, o Quarkus não falha. Ele trata o datasource como "não configurado",
 * desativa o datasource, pula o Flyway e o health check continua respondendo UP.
 * O serviço parece saudável, mas quebra na primeira consulta. Esta classe transforma
 * esse erro silencioso em uma falha imediata e explícita na inicialização.
 *
 * <p>Cada serviço lista as propriedades obrigatórias em
 * {@code finator.startup.required-properties}, normalmente só nos profiles dev e prod.
 */
@ApplicationScoped
public class RequiredConfigCheck {

    private final Config config;
    private final Optional<List<String>> requiredProperties;

    // Injeção pelo construtor: dependências explícitas e campos final
    RequiredConfigCheck(Config config,
                        @ConfigProperty(name = "finator.startup.required-properties")
                        Optional<List<String>> requiredProperties) {
        this.config = config;
        this.requiredProperties = requiredProperties;
    }

    /**
     * Observa o {@link StartupEvent}, disparado quando a aplicação termina de subir.
     * Uma exceção lançada aqui aborta a inicialização.
     */
    void onStart(@Observes StartupEvent event) {
        List<String> missing = findMissing(config, requiredProperties.orElse(List.of()));
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Configuração obrigatória ausente: " + String.join(", ", missing)
                            + ". Confira as variáveis de ambiente (modelo em .env.example).");
        }
    }

    /**
     * Devolve as propriedades da lista que não têm valor utilizável.
     *
     * <p>Uma propriedade conta como ausente quando não existe, quando está em branco
     * ou quando referencia uma expressão que não pode ser resolvida, como
     * {@code ${VARIAVEL_INEXISTENTE}}. Neste último caso o SmallRye Config lança
     * {@link NoSuchElementException} na leitura.
     */
    static List<String> findMissing(Config config, List<String> propertyNames) {
        List<String> missing = new ArrayList<>();
        for (String name : propertyNames) {
            try {
                Optional<String> value = config.getOptionalValue(name, String.class);
                if (value.isEmpty() || value.get().isBlank()) {
                    missing.add(name);
                }
            } catch (NoSuchElementException unresolvedExpression) {
                missing.add(name);
            }
        }
        return missing;
    }
}
