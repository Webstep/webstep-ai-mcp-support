package no.webstep.ai.mcp.protocol.cursor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.webstep.ai.mcp.protocol.ProtocolStatics;

import java.util.List;
import java.util.Optional;

public final class JsonRpcProtocolHelper {

    private final ObjectMapper mapper;

    public JsonRpcProtocolHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Optional<ObjectNode> result(JsonNode id, JsonNode result) {
        if (id == null) {
            return Optional.empty();
        }
        final ObjectNode env = createWrapper(id);
        if (result != null) {
            env.set("result", result);
        }
        return Optional.of(env);
    }


    public Optional<ObjectNode> result(JsonNode id, List<JsonNode> result) {
        if (id == null) {
            return Optional.empty();
        }
        final ObjectNode env = createWrapper(id);
        if (result != null) {
            ArrayNode jsonNodes = mapper.createArrayNode().addAll(result);
            env.set("result", jsonNodes);
        }
        return Optional.of(env);
    }


    public Optional<ObjectNode> errorEnvelope(JsonNode id, int code, String message) {
        if (id == null) {
            return Optional.empty();
        }
        final ObjectNode env = createWrapper(id);

        final ObjectNode error = mapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);

        env.set("error", error);
        return Optional.of(env);
    }

    public ObjectNode errorEnvelope(int code, String message) {
        final ObjectNode env = createWrapper(null);

        final ObjectNode error = mapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);

        env.set("error", error);
        return env;
    }

    private ObjectNode createWrapper(JsonNode id) {
        final ObjectNode env = mapper.createObjectNode();
        env.put("jsonrpc", ProtocolStatics.VERSION);
        env.set("id", id == null ? NullNode.instance : id);
        return env;
    }

    public ArrayNode toArray(List<ObjectNode> list) {
        return mapper.createArrayNode().addAll(list);
    }
}
