package no.webstep.ai.mcp.protocol.dto.content;

public record EmbeddedResourceContent(
    String type,
    EmbeddedResource resource
) implements ToolContent {
    public EmbeddedResourceContent(EmbeddedResource resource) { this("resource",resource); }
}

