package no.webstep.internals;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeHelper {
    public static String getStringWithDefault(JsonNode node, String defaultValue,String childName) {
        if (node != null && node.get(childName) instanceof JsonNode cp) {
            return node.get(childName).asText(defaultValue);
        }
        return defaultValue;
    }
}
