package no.webstep.ai.mcp.protocol.dto.content;

import java.util.Map;

public record TextContent(
        String type,
        String text,
        Map<String, Object> annotations
) implements ToolContent {
    public TextContent(String text) {
        this("text", text, null);
    }
}
