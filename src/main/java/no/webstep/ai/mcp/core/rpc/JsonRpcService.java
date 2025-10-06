package no.webstep.ai.mcp.core.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.protocol.cursor.JsonRpcProtocolHelper;
import no.webstep.ai.mcp.core.rpc.exceptions.JsonRpcServerException;
import no.webstep.ai.mcp.protocol.JsonRpcErrorCodes;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class JsonRpcService {

    private final JsonRpcExecution jsonRpcExecution;
    private final JsonRpcExecutorManager rpcExecutorManager;
    private final JsonRpcProtocolHelper jsonRpcEnvelopeFactory;
    private final Clock clock;


    public class RpcCallable implements Callable<Optional<ObjectNode>> {
        private final JsonNode raw;
        private final int index;

        public RpcCallable(JsonNode raw, int index) {
            this.raw = raw;
            this.index = index;
        }

        public JsonNode getRaw() {
            return raw;
        }

        @Override
        public Optional<ObjectNode> call() {
            return jsonRpcExecution.execute(raw, index);
        }

    }


    public ArrayNode execute(List<JsonNode> nodes) {
        return execute(nodes,null);
    }

    /**
     * @return Array of ObjectNodes
     */
    public ArrayNode execute(List<JsonNode> nodes, Duration timeoutHint) {
        final List<RpcCallable> plan = new ArrayList<>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            plan.add(new RpcCallable(nodes.get(i), i));
        }
        final List<Future<Optional<ObjectNode>>> executionResult;
        try {
            executionResult = rpcExecutorManager.run(plan, timeoutHint);

            final List<ObjectNode> result = new ArrayList<>();
            for (int i = 0; i < executionResult.size(); i++) {
                final Future<Optional<ObjectNode>> jsonNodes = executionResult.get(i);
                try {
                    //no return value if no id node by protocol rules
                    jsonNodes.get().ifPresent(result::add);
                } catch (ExecutionException e) {
                    log.error("Error while executing json RPC execution fell through. This should never happen", e);
                    throw new RuntimeException(e);
                } catch (CancellationException c) {
                    final RpcCallable rpcCallable = plan.get(i);
                    final JsonNode raw = rpcCallable.getRaw();
                    final JsonNode idNode = (raw != null && raw.isObject()) ? raw.get("id") : null;
                    final Optional<ObjectNode> theToolTimedOut = jsonRpcEnvelopeFactory.errorEnvelope(
                            idNode,
                            JsonRpcErrorCodes.TIMEOUT,
                            "The tool timed out");
                    if (theToolTimedOut.isPresent()) {
                        result.add(theToolTimedOut.get());
                    }
                }
            }
            return jsonRpcEnvelopeFactory.toArray(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JsonRpcServerException(JsonRpcErrorCodes.PROCESSING_INTERRUPTED,
                    "Server was interrupted. Likely shutting down.");
        }
    }


}