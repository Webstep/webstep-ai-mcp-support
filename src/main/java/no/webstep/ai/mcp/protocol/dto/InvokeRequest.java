package no.webstep.ai.mcp.protocol.dto;

import java.util.List;
import java.util.Map;

public record InvokeRequest(
        List<Object> args,                 // positional args, can be null
        Map<String, Object> argsByName     // named args, preferred, can be null
) {

    public static InvokeRequest empty() {
        return new InvokeRequest(null,java.util.Collections.emptyMap());
    }

}
