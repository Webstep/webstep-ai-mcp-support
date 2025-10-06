package no.webstep.ai.mcp.protocol.dto.content;

import java.util.Map;

public record AudioContent(
    String type,
    String data,      // base64
    String mimeType,
    Map<String,Object> annotations
) implements ToolContent {
    public AudioContent(String data, String mimeType) { this("audio", data, mimeType, null); }
}
