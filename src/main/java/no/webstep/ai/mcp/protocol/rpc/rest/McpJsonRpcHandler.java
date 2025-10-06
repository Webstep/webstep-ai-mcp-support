package no.webstep.ai.mcp.protocol.rpc.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.rpc.JsonRpcService;
import no.webstep.ai.mcp.protocol.rpc.RpcJsonVersionEnforcer;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
public class McpJsonRpcHandler {

    private final JsonRpcService jsonRpcService;

    private final RpcJsonVersionEnforcer rpcJsonVersionEnforcer;

    public ResponseEntity<JsonNode> handle(JsonNode body, Duration timeoutHint) {
        final List<JsonNode> jsonNodes = body.isArray()
                ? body.valueStream().toList()
                : List.of(body);
        rpcJsonVersionEnforcer.throwIfInvalidJsonRpcVersion(jsonNodes);
        final ArrayNode execute = jsonRpcService.execute(jsonNodes, timeoutHint);

        if (execute.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (body.isArray()) {
            return ResponseEntity.ok(execute);
        } else {
            return ResponseEntity.ok(execute.get(0));
        }
    }

}
