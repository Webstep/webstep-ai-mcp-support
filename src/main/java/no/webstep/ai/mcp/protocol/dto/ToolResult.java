package no.webstep.ai.mcp.protocol.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.webstep.ai.mcp.protocol.dto.content.ToolContent;

public record ToolResult(
    ToolContent[] content,
    boolean isError,
    JsonNode structuredContent
) {

    public ToolResult(ToolContent content, boolean isError, JsonNode structuredContent) {
        this(new ToolContent[]{content}, isError, structuredContent);
    }
    public static ToolResult stopChain(ObjectMapper mapper, ToolResult mapped, Object payload) {
        com.fasterxml.jackson.databind.node.ObjectNode meta = mapper.createObjectNode();
        meta.put("stopChain", true);
        meta.set("payload", mapper.valueToTree(payload));
        if (mapped.structuredContent() != null) {
            meta.set("data", mapped.structuredContent());
        }
        return new ToolResult(mapped.content(), false, meta);
    }

}
