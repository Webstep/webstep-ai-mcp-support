package no.webstep.ai.mcp.core;

import no.webstep.ai.mcp.protocol.dto.InvokeRequest;
import no.webstep.ai.mcp.protocol.dto.Tool;
import no.webstep.ai.mcp.protocol.dto.ToolResult;

import java.util.List;

public interface McpToolService {
    List<Tool> listTools(int start, int limit);

    ToolResult callTool(String toolName, InvokeRequest request);

}
