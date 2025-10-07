package no.webstep.ai.mcp.core.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.rpc.handlers.JsonRpcMethodHandler;
import no.webstep.ai.mcp.exception.InvalidParamsException;
import no.webstep.ai.mcp.exception.JsonRpcErrorCode;
import no.webstep.ai.mcp.exception.JsonRpcServerException;
import no.webstep.ai.mcp.protocol.ProtocolStatics;
import no.webstep.ai.mcp.protocol.cursor.JsonRpcProtocolHelper;
import no.webstep.internals.ExceptionStringifier;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JsonRpcExecution {
    private final JsonRpcRouter router;
    private final JsonRpcProtocolHelper jsonRpcEnvelopeFactory;
    /**
     * @param raw   the json to execute
     * @param payloadIndex for identifying broken messages
     * @return Optional (notifications should not have a return value)
     */
    public Optional<ObjectNode> execute(JsonNode raw, int payloadIndex) {
        final JsonNode idNode = (raw != null && raw.isObject()) ? raw.get("id") : null;
        if (!raw.isObject()) {
            log.warn("A JSON RPC envelope node ({}) is not an object", payloadIndex);
            return Optional.of(
                    jsonRpcEnvelopeFactory.errorEnvelope(
                            JsonRpcErrorCode.INVALID_REQUEST,
                            "Invalid JSON RPC request at element %s".formatted(payloadIndex)));

        }
        final String jsonrpc = raw.path("jsonrpc").asText(null);
        if (!ProtocolStatics.VERSION.equals(jsonrpc)) {
            return jsonRpcEnvelopeFactory.errorEnvelope(idNode,
                    JsonRpcErrorCode.INVALID_REQUEST, "Unsupported version (%s). Should be 2.0"
                            .formatted(jsonrpc));
        }
        final String method = raw.path("method").asText(null);
        if (method == null) {
            return jsonRpcEnvelopeFactory.errorEnvelope(idNode,
                    JsonRpcErrorCode.INVALID_REQUEST, "Method not specified");
        }
        final JsonRpcMethodHandler handler = router.handlerFor(method);
        if (handler == null) {
            return jsonRpcEnvelopeFactory.errorEnvelope(idNode,
                    JsonRpcErrorCode.METHOD_NOT_FOUND, "Method not found (%s)".formatted(method));
        }
        final JsonNode params = raw.has("params") ? raw.get("params") : null;
        try {
            final JsonNode result = handler.handle(params);
            return jsonRpcEnvelopeFactory.result(idNode, result);
        } catch (InvalidParamsException e) {
            return jsonRpcEnvelopeFactory.errorEnvelope(idNode,
                    JsonRpcErrorCode.INVALID_PARAMS,
                    "Invalid params: %s".formatted(ExceptionStringifier.justCauses(e)));
        } catch (JsonRpcServerException e) {
            return jsonRpcEnvelopeFactory.errorEnvelope(idNode,
                    e.code(),
                    "Method '%s' failed: %s".formatted(method, ExceptionStringifier.justCauses(e)));
        } catch (Exception e) {
            log.error("Unhandled JSON-RPC error for method {}", method, e);
            return jsonRpcEnvelopeFactory.errorEnvelope(idNode,
                    JsonRpcErrorCode.TOOL_EXECUTION,
                    "Method '%s' failed: %s".formatted(method, ExceptionStringifier.justCauses(e)));
        }
    }
}
