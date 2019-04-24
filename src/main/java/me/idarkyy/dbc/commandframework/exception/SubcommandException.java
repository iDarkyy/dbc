package me.idarkyy.dbc.commandframework.exception;

public class SubcommandException extends RuntimeException {
    public SubcommandException(String message) {
        super(message);
    }

    public SubcommandException(Throwable cause) {
        super(cause);
    }

    public SubcommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
