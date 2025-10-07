package no.webstep.ai.mcp.core.rpc.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import no.webstep.ai.mcp.exception.InvalidParamsException;
import no.webstep.ai.mcp.exception.JsonRpcServerException;

public interface JsonRpcMethodHandler {
    /** The JSON-RPC method name this handler serves (e.g., "tools/call"). */
    String method();

    /**
     * Handle the call and return the JSON "result" node.
     * For notifications (no "id"), return null; controller will send 204.
     */
    JsonNode handle(JsonNode params) throws InvalidParamsException, JsonRpcServerException;
}
