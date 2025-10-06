package no.webstep.ai.mcp.core.rpc.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.props.McpConfig;
import no.webstep.ai.mcp.McpKeys;
import no.webstep.ai.mcp.protocol.ProtocolStatics;
import no.webstep.ai.mcp.protocol.dto.Capabilities;
import no.webstep.ai.mcp.protocol.dto.ToolsCapability;

@Slf4j
public class InitializeHandler implements JsonRpcMethodHandler {
    private final ObjectNode result;

    public InitializeHandler(ObjectMapper mapper, McpConfig mcpConfig) {
        final Capabilities capabilities = new Capabilities(new ToolsCapability(false));
        final ObjectNode serverInfo = mapper.createObjectNode()
                .put("name", mcpConfig.get(McpKeys.SERVER_NAME))
                .put("version", mcpConfig.get(McpKeys.SERVER_VERSION));

        result = mapper.createObjectNode();
        result.put("protocolVersion", ProtocolStatics.VERSION);
        result.set("capabilities", mapper.valueToTree(capabilities));
        result.set("serverInfo", serverInfo);

    }

    @Override
    public String method() {
        return "initialize";
    }

    @Override
    public JsonNode handle(JsonNode params) {
        return result.deepCopy();
    }
}
