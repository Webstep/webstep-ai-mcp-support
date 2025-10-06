package no.webstep.ai.mcp.protocol.dto.content;

import java.util.Map;

public record ResourceLinkContent(
    String type,
    String uri,
    String name,
    String description,
    String mimeType,
    Map<String,Object> annotations
) implements ToolContent {
    public ResourceLinkContent(String uri, String name, String description, String mimeType) {
        this("resource_link", uri, name, description, mimeType, null);
    }
}
