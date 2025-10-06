package no.webstep.ai.mcp.core.rpc.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.McpToolService;
import no.webstep.ai.mcp.core.exception.InvalidParamsException;
import no.webstep.ai.mcp.protocol.dto.InvokeRequest;
import no.webstep.ai.mcp.protocol.dto.ToolResult;

import java.util.Objects;

@RequiredArgsConstructor
public class ToolsCallHandler implements JsonRpcMethodHandler {
    private final McpToolService toolService;
    private final ObjectMapper mapper;

    @Override
    public String method() {
        return "tools/call";
    }

    @Override
    public JsonNode handle(JsonNode params) {
        if (params == null) {
            throw new InvalidParamsException("Missing params");
        }
        if (!params.hasNonNull("name")) {
            throw new InvalidParamsException("Missing param: name");
        }

        final String name = params.get("name").asText();
        //Known issue some LLMs flatten this structure leaving arguments in the params node
        final JsonNode argsNode = Objects.requireNonNullElse(params.get("arguments"), params);
        final InvokeRequest request = mapper.convertValue(argsNode, InvokeRequest.class);

        final ToolResult result = toolService.callTool(name, request);
        return mapper.valueToTree(result);
    }
}
