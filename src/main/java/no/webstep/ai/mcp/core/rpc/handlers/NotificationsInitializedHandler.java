package no.webstep.ai.mcp.core.rpc.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotificationsInitializedHandler implements JsonRpcMethodHandler {
    private final ObjectMapper mapper;
    @Override public String method() { return "notifications/initialized"; }
    @Override public JsonNode handle(JsonNode params) {
        return mapper.createArrayNode();
    }
}
