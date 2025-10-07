package no.webstep.ai.mcp.exception;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public class JsonRpcServerException extends RuntimeException {
    private final JsonRpcErrorCode rpcCode; // in -32000..-32099
    private final Optional<HttpStatus> optionalHttpStatus;

    public JsonRpcServerException(JsonRpcErrorCode rpcCode, String message, HttpStatus httpStatus) {
        this(rpcCode, message, httpStatus, null);
    }

    public JsonRpcServerException(JsonRpcErrorCode rpcCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.rpcCode = rpcCode;
        this.optionalHttpStatus = Optional.ofNullable(httpStatus);
    }

    public JsonRpcServerException(JsonRpcErrorCode rpcCode, String message) {
        this(rpcCode, message, null, null);
    }

    public JsonRpcServerException(JsonRpcErrorCode rpcCode, String message, Throwable cause) {
        this(rpcCode, message, null, cause);
    }

    public JsonRpcErrorCode code() {
        return rpcCode;
    }

    public JsonRpcErrorCode getRpcCode() {
        return rpcCode;
    }

    public Optional<HttpStatus> getOptionalHttpStatus() {
        return optionalHttpStatus;
    }
}
