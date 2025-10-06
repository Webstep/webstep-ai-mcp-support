package no.webstep.ai.mcp.protocol.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import no.webstep.ai.mcp.core.rpc.exceptions.JsonRpcServerException;
import no.webstep.ai.mcp.protocol.JsonRpcErrorCodes;
import no.webstep.ai.mcp.protocol.ProtocolStatics;

import java.util.List;

public class RpcJsonVersionEnforcer {
    private final String version = ProtocolStatics.VERSION;
    public boolean validJsonRpcVersion(List<JsonNode> body) {
        for (JsonNode jsonNode : body) {

            if (!version.equals(jsonNode.path("jsonrpc").textValue())) {
                return false;
            }
        }
        return true;
    }

    public void throwIfInvalidJsonRpcVersion(List<JsonNode> body) {
        if (!validJsonRpcVersion(body)) {
            throw new JsonRpcServerException(JsonRpcErrorCodes.INVALID_REQUEST, "Only version %s is supported".formatted(version));
        }
    }
}
