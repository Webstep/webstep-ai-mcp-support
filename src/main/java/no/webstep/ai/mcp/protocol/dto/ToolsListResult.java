package no.webstep.ai.mcp.protocol.dto;

import java.util.List;

public record ToolsListResult(
    List<Tool> tools,
    String nextCursor // may be null
) {}