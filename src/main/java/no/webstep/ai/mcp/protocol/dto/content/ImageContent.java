package no.webstep.ai.mcp.protocol.dto.content;

import java.util.Map;

public record ImageContent(
    String type,
    String data,      // base64
    String mimeType,  // e.g., "image/png"
    Map<String,Object> annotations
) implements ToolContent {
    public ImageContent(String data, String mimeType) { this("image", data, mimeType, null); }
}
