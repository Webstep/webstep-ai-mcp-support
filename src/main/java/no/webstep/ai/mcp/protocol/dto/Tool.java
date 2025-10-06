package no.webstep.ai.mcp.protocol.dto;

import com.fasterxml.jackson.databind.JsonNode;

// A tool definition per MCP.
public record Tool(
        String name,
        String description,
        JsonNode inputSchema,
        JsonNode outputSchema
) {
}
