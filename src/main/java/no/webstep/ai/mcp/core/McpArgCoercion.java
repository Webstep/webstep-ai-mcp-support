package no.webstep.ai.mcp.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.exception.InvalidParamsException;
import no.webstep.ai.mcp.protocol.dto.InvokeRequest;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationDetails;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class McpArgCoercion {

    private final ObjectMapper mapper;

    public Object[] coerceArgs(ToolInvocationDetails details, InvokeRequest request) {
        final Class<?>[] types = details.method().getParameterTypes();
        final Object[] out = new Object[types.length];
        if (types.length == 0) {
            return out;
        }
        final Map<String, Object> argsByName = request.argsByName();
        if (argsByName != null && !argsByName.isEmpty()) {
            for (int i = 0; i < types.length; i++) {
                final String paramName = details.parameterNames()[i].name();
                final Object rawJsonValue = argsByName.get(paramName);
                out[i] = mapper.convertValue(rawJsonValue, types[i]);
            }
            return out;
        }
        final List<Object> argsByPosition =  request.args();
        if (argsByPosition == null) {
            throw new InvalidParamsException("No arguments found. Expected %s arguments".formatted(types.length));
        }
        if (argsByPosition.size() != types.length) {
            throw new InvalidParamsException("Positional arg count %s expected %s"
                    .formatted(argsByPosition.size(), types.length));
        }
        for (int i = 0; i < types.length; i++) {
            out[i] = mapper.convertValue(argsByPosition.get(i), types[i]);
        }
        return out;
    }
}
