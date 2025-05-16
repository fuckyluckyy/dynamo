package io.lucky.injector;

public class DefinitionNotFoundException extends IllegalStateException {

    public DefinitionNotFoundException(String message) {
        super(message);
    }

    public DefinitionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
