package no.webstep.ai.mcp.protocol.dto.content;

public record EmbeddedResource(
    String uri,
    String title,
    String mimeType,
    String text
) {}
