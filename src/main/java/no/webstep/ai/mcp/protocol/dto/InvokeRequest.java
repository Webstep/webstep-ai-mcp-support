package no.webstep.ai.mcp.protocol.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record InvokeRequest(
        JsonNode args,                 // positional args, can be null
        boolean positional
) {


}
