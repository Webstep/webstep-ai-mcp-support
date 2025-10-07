package no.webstep.ai.mcp.protocol.rpc.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.exception.McpApi;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@McpApi
@Conditional(McpJsonRpcRestControllerCondition.class)
public class McpJsonRpcController {
    private final McpJsonRpcHandler jsonRpcHandler;

    @PostMapping(value = "/mcp/jsonrpc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> handle(@RequestBody JsonNode body,
                                           @RequestHeader(value = "Prefer", required = false) String prefer) {
        return jsonRpcHandler.handle(body, parsePreferWaitMs(prefer));
    }

    /**
     * https://datatracker.ietf.org/doc/html/rfc7240#section-4.3
     */
    private static Duration parsePreferWaitMs(String prefer) {
        if (prefer == null) return null;
        for (String value : prefer.split(",")) {
            value = value.trim();
            if (value.startsWith("wait=")) {
                try {
                    return Duration.ofSeconds(Long.parseLong(value.substring(5)));
                } catch (Exception ignore) {
                    log.debug("Ignoring {} of prefer header {} because it is not a number", value, prefer);
                }
            }
        }
        return null;
    }
}
