package no.webstep.ai.mcp.core.tool.invocation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.tool.datatypes.*;
import no.webstep.ai.mcp.protocol.dto.ToolResult;
import no.webstep.ai.mcp.protocol.dto.content.*;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public final class ToolResultMapper {

    private final ObjectMapper mapper;

    public ToolResult map(Object result, ContentStyle expected) {
        if (result == null) return new ToolResult(new TextContent("null"), false, NullNode.getInstance());

        switch (expected) {
            case TEXT:
                if (result instanceof TextContent tc) return new ToolResult(tc, false, toStructured(result));
                if (result instanceof CharSequence cs)
                    return new ToolResult(new TextContent(cs.toString()), false, toStructured(result));
                break;

            case STRUCTURED:
                return new ToolResult((ToolContent[]) null, false, toStructured(result));

            case TEXT_AND_STRUCTURED:
                if (result instanceof CharSequence cs2)
                    return new ToolResult(new TextContent(cs2.toString()), false, toStructured(cs2.toString()));
                if (result instanceof TextAndStructured<?>) {
                    TextAndStructured textAndStructured = (TextAndStructured<?>) result;
                    return new ToolResult(new TextContent(textAndStructured.text()), false, toStructured(textAndStructured.structured()));
                }
                return new ToolResult(new TextContent(toPreview(result)), false, toStructured(result));

            case IMAGE:
                if (result instanceof ImageContent ic) return new ToolResult(ic, false, toStructured(result));
                if (result instanceof ImageBinary(byte[] data, String mimeType))
                    return new ToolResult(new ImageContent(base64(data), mimeType), false, toStructured(result));
                break;

            case AUDIO:
                if (result instanceof AudioContent ac) return new ToolResult(ac, false, toStructured(result));
                if (result instanceof AudioBinary(byte[] data, String mimeType))
                    return new ToolResult(new AudioContent(base64(data), mimeType), false, toStructured(result));
                break;

            case RESOURCE_LINK:
                if (result instanceof ResourceLinkContent rlc) return new ToolResult(rlc, false, toStructured(result));
                if (result instanceof LinkTarget(String uri, String name, String description, String mimeType))
                    return new ToolResult(new ResourceLinkContent(uri, name, description, mimeType), false, toStructured(result));
                break;

            case EMBEDDED_RESOURCE:
                if (result instanceof EmbeddedResourceContent erc)
                    return new ToolResult(erc, false, toStructured(result));
                if (result instanceof EmbeddedTextResource(String uri, String title, String mimeType, String text))
                    return new ToolResult(new EmbeddedResourceContent(new EmbeddedResource(uri, title, mimeType, text)), false, toStructured(result));
                break;
        }

        throw new IllegalStateException("Return type " + result.getClass().getName() + " does not match declared content style " + expected);
    }

    private JsonNode toStructured(Object value) {
        try {
            return mapper.valueToTree(value);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    private String toPreview(Object value) {
        try {
            return mapper.valueToTree(value).toString();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

}
