package io.lucky.injector;

public class MethodInvokeException extends RuntimeException {

    public MethodInvokeException(String message) {
        super(message);
    }

    public MethodInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
