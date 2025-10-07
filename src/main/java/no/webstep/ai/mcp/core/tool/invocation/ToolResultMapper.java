package no.webstep.ai.mcp.core.tool.invocation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.tool.datatypes.*;
import no.webstep.ai.mcp.protocol.dto.ToolResult;
import no.webstep.ai.mcp.protocol.dto.content.*;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public final class ToolResultMapper {

    private final ObjectMapper mapper;

    private static String dequoteIfJsonString(CharSequence cs) {
        final String s = cs.toString();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return s;
    }

    public ToolResult map(Object result, ContentStyle expected) {
        if (result == null) {
            return new ToolResult(new TextContent("null"), false, NullNode.getInstance());
        }

        switch (expected) {
            case TEXT:
                if (result instanceof TextContent tc)
                    return new ToolResult(tc, false, toStructured(result));
                if (result instanceof CharSequence cs)
                    return new ToolResult(new TextContent(dequoteIfJsonString(cs)), false, toStructured(result));
                break;

            case TEXT_AND_STRUCTURED:
                if (result instanceof CharSequence cs2) {
                    return new ToolResult(new TextContent(dequoteIfJsonString(cs2)), false, toStructured(cs2.toString()));
                } else if (result instanceof TextAndStructured<?> tas) {
                    return new ToolResult(
                            new TextContent(String.valueOf(tas.text())),
                            false,
                            toStructured(tas.structured())
                    );
                }
                return new ToolResult(new TextContent(toPreview(result)), false, toStructured(result));
            case STRUCTURED:
                return new ToolResult((ToolContent[]) null, false, toStructured(result));
            case IMAGE:
                if (result instanceof ImageContent ic) {
                    return new ToolResult(ic, false, toStructured(result));
                } else if (result instanceof ImageBinary(byte[] data, String mimeType)) {
                    return new ToolResult(new ImageContent(base64(data), mimeType), false, toStructured(result));
                }
                break;
            case AUDIO:
                if (result instanceof AudioContent ac) {
                    return new ToolResult(ac, false, toStructured(result));
                }
                if (result instanceof AudioBinary(byte[] data, String mimeType)) {
                    return new ToolResult(new AudioContent(base64(data), mimeType), false, toStructured(result));
                }
                break;

            case RESOURCE_LINK:
                if (result instanceof ResourceLinkContent rlc) {
                    return new ToolResult(rlc, false, toStructured(result));
                }
                if (result instanceof LinkTarget(String uri, String name, String description, String mimeType)) {
                    return new ToolResult(new ResourceLinkContent(uri, name, description, mimeType), false, toStructured(result));
                }
                break;
            case EMBEDDED_RESOURCE:
                if (result instanceof EmbeddedResourceContent erc) {
                    return new ToolResult(erc, false, toStructured(result));
                }
                if (result instanceof EmbeddedTextResource(String uri, String title, String mimeType, String text)) {
                    return new ToolResult(
                            new EmbeddedResourceContent(
                                    new EmbeddedResource(uri, title, mimeType, text)), false, toStructured(result));
                }
                break;
        }
        throw new IllegalStateException("Return type " + result.getClass().getName() + " does not match declared content style " + expected);
    }

    private JsonNode toStructured(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        }

        if (value instanceof JsonNode node) {
            return node;
        }

        if (value instanceof java.util.Optional<?> opt) {
            return opt.isPresent() ? toStructured(opt.get()) : NullNode.getInstance();
        }

        try {
            return mapper.valueToTree(value);
        } catch (RuntimeException e) {
            log.warn("valueToTree failed for {}: {}", value.getClass().getName(), e);
            return NullNode.getInstance();
        }
    }

    private String toPreview(Object value) {
        try {
            final JsonNode node = mapper.valueToTree(value);
            final String compact = node.isContainerNode() ? node.toString() : node.asText();
            int cap = 4096;
            if (compact.length() > cap) {
                return compact.substring(0, cap) + " … (truncated)";
            }
            return compact;
        } catch (RuntimeException e) {
            return String.valueOf(value);
        }
    }

    private static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

}
