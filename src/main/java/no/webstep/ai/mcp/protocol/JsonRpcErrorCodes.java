package no.webstep.ai.mcp.protocol;

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
public final class JsonRpcErrorCodes {
    private JsonRpcErrorCodes() {}

    /**
     * Invalid JSON was received by the server.
     * Occurs when the request body cannot be parsed at all.
     */
    public static final int PARSE_ERROR      = -32700;

    /**
     * The JSON sent is not a valid Request object.
     * Example: not an object, missing "method", wrong envelope shape.
     */
    public static final int INVALID_REQUEST  = -32600;

    /**
     * The requested method does not exist / is not available.
     */
    public static final int METHOD_NOT_FOUND = -32601;

    /**
     * Invalid method parameters.
     * Example: wrong type, missing required argument.
     */
    public static final int INVALID_PARAMS   = -32602;

    /**
     * Internal JSON-RPC error.
     * Generic fallback when the server fails unexpectedly.
     */
    public static final int INTERNAL_ERROR   = -32603;
    public static final int PROCESSING_INTERRUPTED = -32604;

    /**
     * Reserved implementation-defined server error range.
     * All custom MCP codes must stay within -32000 … -32099.
     */
    public static final int SERVER_ERROR_MIN = -32099;
    public static final int SERVER_ERROR_MAX = -32000;

    // --- MCP-specific extensions ---

    /**
     * A tool execution exceeded the allowed time limit.
     * Use this instead of INTERNAL_ERROR when the root cause is a timeout.
     */
    public static final int TIMEOUT          = -32001;

    /**
     * A tool failed during execution in a controlled way
     * (e.g. external API returned an error, script crashed).
     * Use this instead of INTERNAL_ERROR when the failure is in the tool.
     */
    public static final int TOOL_EXECUTION   = -32002;

    /**
     * A request was explicitly cancelled (by user, client, or system).
     * Different from TIMEOUT: this is an active cancellation,
     * not a passive expiry.
     */
    public static final int CANCELLATION     = -32003;
}
