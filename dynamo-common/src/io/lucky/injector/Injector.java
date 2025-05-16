package io.lucky.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public interface Injector {

    static Injector create() {
        return create(new HashSet<>());
    }

    static Injector create(Set<InjectorDefinition> definitions) {
        DefinitionCache definitionCache = new DefinitionCache(definitions);
        return new DependencyInjector(definitionCache);
    }

    <T> T createInstance(Class<T> type, @Nullable Object @Nullable ... additionalDependencies);

    <T> @NotNull T invoke(@NotNull Constructor<T> constructor, @Nullable Object @Nullable ... additionalDependencies);

    @NotNull Object invoke(@NotNull Method method, @Nullable Object @Nullable ... additionalDependencies);

    <T> void add(@Nullable String name, @NotNull T value);

    <T> @Nullable T get(@Nullable String name, @NotNull Class<? extends T> type);

    @NotNull Stream<?> stream();
}
