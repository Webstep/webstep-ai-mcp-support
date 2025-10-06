package no.webstep.ai.mcp.core.rpc;

import no.webstep.ai.mcp.core.rpc.handlers.JsonRpcMethodHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonRpcRouter {
    private final Map<String, JsonRpcMethodHandler> handlers;

    public JsonRpcRouter(List<JsonRpcMethodHandler> discovered) {
        this.handlers = discovered.stream().collect(Collectors.toMap(JsonRpcMethodHandler::method, h -> h));
    }

    public JsonRpcMethodHandler handlerFor(String method) {
        return handlers.get(method);
    }
}
