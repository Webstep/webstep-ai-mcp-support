package no.webstep.ai.mcp.core.rpc.handlers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.McpToolProviderRegistry;
import no.webstep.ai.mcp.core.McpToolService;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationDetails;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationParamInfo;
import no.webstep.ai.mcp.exception.JsonRpcErrorCode;
import no.webstep.ai.mcp.exception.JsonRpcServerException;
import no.webstep.ai.mcp.protocol.dto.ToolResult;

import java.util.Arrays;

@RequiredArgsConstructor
public class ToolsCallHandler implements JsonRpcMethodHandler {
    private final McpToolService toolService;
    private final McpToolProviderRegistry registry;
    private final ObjectMapper mapper;

    @Override
    public String method() {
        return "tools/call";
    }

    private static Object primitiveDefault(Class<?> raw) {
        if (raw == boolean.class) return false;
        if (raw == byte.class) return (byte) 0;
        if (raw == short.class) return (short) 0;
        if (raw == int.class) return 0;
        if (raw == long.class) return 0L;
        if (raw == float.class) return 0f;
        if (raw == double.class) return 0d;
        if (raw == char.class) return '\u0000';
        throw new IllegalArgumentException("Unknown primitive: " + raw);
    }

    @Override
    public JsonNode handle(JsonNode jsonRpcParamsNode) {
        if (jsonRpcParamsNode == null) {
            throw JsonRpcErrorCode.INVALID_PARAMS.exception("Missing params");
        }
        if (!jsonRpcParamsNode.hasNonNull("name")) {
            throw JsonRpcErrorCode.INVALID_PARAMS.exception("Missing param: name");
        }
        final JsonNode node = jsonRpcParamsNode.get("name");
        if (!node.isTextual()) {
            throw JsonRpcErrorCode.INVALID_PARAMS.exception("Param: name is not textual");
        }
        return handle(node.asText(), jsonRpcParamsNode.get("arguments"));
    }

    public JsonNode handle(String toolName, JsonNode argumentsNode) {
        final ToolInvocationDetails invocation = registry.findByName(toolName)
                .orElseThrow(() -> new JsonRpcServerException(JsonRpcErrorCode.METHOD_NOT_FOUND,
                        "No method exists named '%s'".formatted(toolName)));
        return handle(invocation, argumentsNode);
    }

    public JsonNode handle(ToolInvocationDetails invocation, JsonNode argumentsNode) {
        if (argumentsNode == null) {
            throw JsonRpcErrorCode.INVALID_PARAMS.exception("Arguments node 'arguments' does not exist");
        }

        final ToolResult result = toolService.callTool(invocation, getParams(invocation, argumentsNode));
        if (result == null) {
            return mapper.nullNode();
        }
        return mapper.valueToTree(result);
    }

    private Object[] getParams(ToolInvocationDetails invocation, JsonNode argumentsNode) {
        final ToolInvocationParamInfo[] toolInvocationParamInfos = invocation.parameterNames();
        final Object[] params = new Object[toolInvocationParamInfos.length];
        final JsonNode argsRoot;
        boolean byName;
        if (argumentsNode.isObject()) {
            if (argumentsNode.has("argsByName")) {
                final JsonNode node = argumentsNode.get("argsByName");
                if (!node.isObject()) {
                    throw JsonRpcErrorCode.INVALID_PARAMS.exception("'argsByName' must be an object");
                }
                argsRoot = node;
                byName = true;
            } else if (argumentsNode.has("args")) {
                final JsonNode args = argumentsNode.get("args");
                if (args.isArray()) {
                    byName = false;
                    argsRoot = args;
                } else {
                    //assume the tool asks for "args"
                    byName = true;
                    argsRoot = argumentsNode;
                }
            } else {
                argsRoot = argumentsNode;
                byName = true;
            }
        } else if (argumentsNode.isArray()) {
            byName = false;
            argsRoot = argumentsNode;
        } else {
            throw JsonRpcErrorCode.INVALID_PARAMS.exception("Could not find well-formed 'arguments' node");
        }
        if (!byName) {
            final int size = argsRoot.size();
            if (size != params.length) {
                throw JsonRpcErrorCode.INVALID_PARAMS.exception("Method expects params %s but you provide %s"
                        .formatted(Arrays.toString(toolInvocationParamInfos), size));
            }
        }
        for (int i = 0; i < params.length; i++) {
            final ToolInvocationParamInfo param = toolInvocationParamInfos[i];
            final JavaType javaType = mapper.getTypeFactory().constructType(param.type());

            final JsonNode node = byName ? argsRoot.get(param.name()) : argsRoot.get(i);
            if (node != null && !node.isNull()) {
                try {
                    params[i] = mapper.convertValue(node, javaType);
                } catch (Exception e) {
                    throw JsonRpcErrorCode.INVALID_PARAMS.exception("Argument %s can't be parsed"
                            .formatted(param), e);
                }
            } else {
                if (param.required()) {
                    throw JsonRpcErrorCode.INVALID_PARAMS.exception("Argument %s can't be null"
                            .formatted(param));
                }
                if (javaType.isPrimitive()) {
                    params[i] = primitiveDefault(javaType.getRawClass());
                } else {
                    params[i] = null;
                }
            }
        }
        return params;
    }


}
