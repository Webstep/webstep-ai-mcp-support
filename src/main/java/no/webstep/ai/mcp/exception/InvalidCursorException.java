package no.webstep.ai.mcp.exception;

public class InvalidCursorException extends InvalidParamsException {
    public InvalidCursorException(String message) {
        super(message);
    }

    public InvalidCursorException(String message, Throwable cause) {
        super(message, cause);
    }
}