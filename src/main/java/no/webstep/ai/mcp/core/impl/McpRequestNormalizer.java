package no.webstep.ai.mcp.core.impl;

import no.webstep.ai.mcp.protocol.dto.InvokeRequest;

public final class McpRequestNormalizer {
    private McpRequestNormalizer() {}

    static InvokeRequest normalize(InvokeRequest in) {
        if (in == null || (in.args() == null && in.argsByName() == null)) {
            return InvokeRequest.empty();
        }
        return in;
    }
}
