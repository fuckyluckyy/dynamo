package io.lucky.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

final class DefinitionCache {

    private final Set<InjectorDefinition> definitions;

    DefinitionCache(Set<InjectorDefinition> definitions) {
        this.definitions = definitions;
    }

    public <T> void cacheDefinition(@Nullable String name, @NotNull T value) {
        definitions.add(new InjectorDefinition(name, value));
    }

    public <T> void removeDefinition(@Nullable String name, @NotNull Class<? extends T> type) {
        definitions.removeIf(invalidatePrediction(name, type));
    }


    public <T> Optional<InjectorDefinition> getDefinition(@Nullable String name, @NotNull Class<? extends T> type) {
        return streamDefinitions().filter(retrievePrediction(name, type)).findAny();
    }

    private <T> @NotNull Predicate<InjectorDefinition> retrievePrediction(@Nullable String name, @NotNull Class<? extends T> type) {
        return definition -> Objects.equals(definition.name(), name) && type.isAssignableFrom(definition.value().getClass());
    }

    public @NotNull Stream<InjectorDefinition> streamDefinitions() {
        return definitions.stream();
    }

    private <T> @NotNull Predicate<InjectorDefinition> invalidatePrediction(@Nullable String name, @NotNull Class<? extends T> type) {
        return definition -> Objects.equals(definition.name(), name) && type.isAssignableFrom(definition.value().getClass());
    }
}
