package io.lucky.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

final class DependencyInjector implements Injector {

    private final DefinitionCache definitionCache;


    DependencyInjector(DefinitionCache definitionCache) {
        this.definitionCache = definitionCache;
    }

    @Override
    public <T> T createInstance(Class<T> type, @Nullable Object @Nullable ... additionalDependencies) {
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                //noinspection unchecked
                return invoke((Constructor<T>) constructor);
            }
        }

        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            return invoke(constructor, additionalDependencies);
        } catch (Exception exception) {
            throw new ConstructorInvokeException("Default constructor not found, or isn't annotated by %s".formatted(Inject.class), exception);
        }
    }

    @Override
    public <T> @NotNull T invoke(@NotNull Constructor<T> constructor, @Nullable Object @Nullable ... additionalDependencies) {
        Object[] dependencies = getDependencies(constructor.getParameters(), additionalDependencies);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(dependencies);
        } catch (Exception exception) {
            throw new ConstructorInvokeException("Failed to invoke %s".formatted(constructor), exception);
        }
    }

    @Override
    public @NotNull Object invoke(@NotNull Method method, @Nullable Object @Nullable ... additionalDependencies) {
        Object[] dependencies = getDependencies(method.getParameters(), additionalDependencies);
        try {
            method.setAccessible(true);
            return method.invoke(dependencies);
        } catch (Exception exception) {
            throw new MethodInvokeException("Failed to invoke %s".formatted(method), exception);
        }
    }

    private @Nullable Object @NotNull [] getDependencies(@NotNull Parameter[] parameters, @Nullable Object @Nullable [] additionalDependencies) {
        Object[] dependencies = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            Inject inject = parameter.getAnnotation(Inject.class);
            String name = inject != null ? inject.value() : "";

            Class<?> type = parameter.getType();

            Object dependency = get(name, type);
            if (dependency != null) {
                dependencies[i] = dependency;
                continue;
            }

            if (additionalDependencies != null) {
                boolean found = false;
                for (Object additionalDependency : additionalDependencies) {
                    if (additionalDependency == null) {
                        continue;
                    }

                    if (type.isAssignableFrom(additionalDependency.getClass())) {
                        found = true;
                        dependencies[i] = additionalDependency;
                        break;
                    }
                }

                if (found) {
                    continue;
                }
            }

            String formattedExceptionMessage = getDefinitionNotFoundExceptionFormat(additionalDependencies).formatted(type, method);
            throw new DefinitionNotFoundException(formattedExceptionMessage);
        }
        return dependencies;
    }

    private String getDefinitionNotFoundExceptionFormat(@Nullable Object[] additionalDependencies) {
        return "Definition of %s wasn't found in cache"
                + (additionalDependencies == null ? "" : ", nor in provided additional dependencies")
                + ", make sure that it is registered before invoking %s!";
    }

    @Override
    public <T> void add(@Nullable String name, @NotNull T value) {
        definitionCache.cacheDefinition(name, value);
    }

    @Override
    public <T> @Nullable T get(@Nullable String name, @NotNull Class<? extends T> type) {
        return definitionCache.getDefinition(name, type).map(InjectorDefinition::value).map(type::cast).orElse(null);
    }

    @Override
    public @NotNull Stream<?> stream() {
        return definitionCache.streamDefinitions().map(InjectorDefinition::value);
    }
}
