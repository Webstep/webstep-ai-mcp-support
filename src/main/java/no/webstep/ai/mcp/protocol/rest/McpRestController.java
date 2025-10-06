package no.webstep.ai.mcp.protocol.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.McpToolService;
import no.webstep.ai.mcp.protocol.cursor.CursorHandler;
import no.webstep.ai.mcp.protocol.McpApi;
import no.webstep.ai.mcp.protocol.dto.*;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Conditional(McpRestControllerCondition.class)
@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
@Slf4j
@McpApi
public class McpRestController {

    private final McpToolService toolService;
    private final CursorHandler cursorHandler;

    @GetMapping(value = "/capabilities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Capabilities capabilities() {
        return new Capabilities(new ToolsCapability(false));
    }

    @GetMapping(value = "/tools", produces = MediaType.APPLICATION_JSON_VALUE)
    public ToolsListResult listTools(
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "limit", required = false, defaultValue = "200") int limit) {
        final int start = cursorHandler.decodeCursor(cursor);
        final List<Tool> tools = toolService.listTools(start, limit);
        final String nextCursor = cursorHandler.nextCursor(start, limit);
        return new ToolsListResult(tools, nextCursor);
    }

    @PostMapping(value = "/tools/{name}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ToolResult callTool(@PathVariable String name, @RequestBody(required = false) InvokeRequest req) throws Exception {
        return toolService.callTool(name, req);
    }


}
