package io.lucky.injector;

public class ConstructorInvokeException extends RuntimeException {

    public ConstructorInvokeException(String message) {
        super(message);
    }

    public ConstructorInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
