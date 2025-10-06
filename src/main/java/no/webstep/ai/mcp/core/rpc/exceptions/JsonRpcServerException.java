package no.webstep.ai.mcp.core.rpc.exceptions;

public class JsonRpcServerException extends RuntimeException {
    private final int code; // in -32000..-32099

    public JsonRpcServerException(int code,String message) {
        super(message);
        this.code = code;
    }

    public JsonRpcServerException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int code() {
        return code;
    }
}
