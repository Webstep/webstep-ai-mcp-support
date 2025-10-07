package no.webstep.ai.mcp.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamsException extends JsonRpcServerException {

    public InvalidParamsException(String message) {
        this(message, null);
    }

    public InvalidParamsException(String message, Throwable cause) {
        super(JsonRpcErrorCode.INVALID_PARAMS, message, HttpStatus.BAD_REQUEST);
    }
}

