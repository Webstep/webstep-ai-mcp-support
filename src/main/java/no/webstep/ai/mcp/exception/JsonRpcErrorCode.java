package no.webstep.ai.mcp.exception;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * JSON-RPC 2.0 and MCP-specific error codes.
 *
 * <p>
 * JSON-RPC 2.0 defines five standard errors:
 * -32700, -32600, -32601, -32602, -32603.
 * <br>
 * The range -32000 to -32099 is reserved for server-defined extensions
 * (used here for MCP-specific cases).
 * </p>
 */
public enum JsonRpcErrorCode {
    /**
     * Invalid JSON was received by the server.
     * Occurs when the request body cannot be parsed at all.
     */
    PARSE_ERROR(-32700),

    /**
     * The JSON sent is not a valid Request object.
     * Example: not an object, missing "method", wrong envelope shape.
     */
    INVALID_REQUEST(-32600),

    /**
     * The requested method does not exist / is not available.
     */
    METHOD_NOT_FOUND(-32601),

    /**
     * Invalid method parameters.
     * Example: wrong type, missing required argument.
     */
    INVALID_PARAMS(-32602, InvalidParamsException::new),

    /**
     * Internal JSON-RPC error.
     * Generic fallback when the server fails unexpectedly.
     */
    INTERNAL_ERROR(-32603),

    /**
     * Internal processing was interrupted (e.g. server shutdown).
     */
    PROCESSING_INTERRUPTED(-32604),

    // --- MCP-specific extensions (reserved range -32000 … -32099) ---

    /**
     * A tool execution exceeded the allowed time limit.
     * Use this instead of INTERNAL_ERROR when the root cause is a timeout.
     */
    TIMEOUT(-32001),

    /**
     * A tool failed during execution in a controlled way
     * (e.g. external API returned an error, script crashed).
     */
    TOOL_EXECUTION(-32002);


    private final int code;

    private final BiFunction<String, Throwable, JsonRpcServerException> exceptionCreator;

    JsonRpcErrorCode(int code) {
        this(code, null);
    }

    JsonRpcErrorCode(int code, BiFunction<String, Throwable, JsonRpcServerException> exceptionCreator) {
        this.code = code;
        this.exceptionCreator = Objects.requireNonNullElse(
                exceptionCreator,
                (s, throwable) -> new JsonRpcServerException(this, s, throwable));

    }

    public int code() {
        return code;
    }

    public JsonRpcServerException exception(String message) {
        return exceptionCreator.apply(message, null);
    }

    public JsonRpcServerException exception(String message, Throwable cause) {
        return exceptionCreator.apply(message, cause);
    }
}
