package no.webstep.ai.mcp.core.rpc.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.McpToolService;
import no.webstep.ai.mcp.protocol.cursor.CursorHandler;
import no.webstep.ai.mcp.protocol.dto.Tool;
import no.webstep.ai.mcp.protocol.dto.ToolsListResult;
import no.webstep.internals.JsonNodeHelper;

import java.util.List;

@RequiredArgsConstructor
public class ToolsListHandler implements JsonRpcMethodHandler {
    private final CursorHandler cursorHandler;
    private final McpToolService toolService;
    private final ObjectMapper mapper;


    @Override
    public String method() {
        return "tools/list";
    }

    @Override
    public JsonNode handle(JsonNode params) {
        final int limit = (params != null && params.has("limit")) ? Math.max(1, params.get("limit").asInt(200)) : 200;
        final int start = cursorHandler.decodeCursor(JsonNodeHelper.getStringWithDefault(params, null, "cursor"));
        final List<Tool> tools = toolService.listTools(start, limit);
        final ToolsListResult toolsListResult = new ToolsListResult(tools, cursorHandler.nextCursor(start, limit));
        return mapper.valueToTree(toolsListResult);
    }
}
