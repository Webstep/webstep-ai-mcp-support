package no.webstep.ai.mcp.protocol.dto;

import com.fasterxml.jackson.databind.JsonNode;

// Request params
public record ToolsCallParams(
    String name,
    JsonNode arguments
) {}
