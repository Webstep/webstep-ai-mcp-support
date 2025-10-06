package no.webstep.ai.mcp.core.schema.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.schema.SchemaBuilder;
import no.webstep.ai.mcp.core.tool.datatypes.TextAndStructured;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationParamInfo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public final class SchemaBuilderVictoolsImpl implements SchemaBuilder {

    private final ObjectMapper mapper;

    private final SchemaGenerator generator = new SchemaGenerator(
            new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                    .with(new JacksonModule())
                    .with(Option.SCHEMA_VERSION_INDICATOR)              // adds "$schema" automatically
                    .with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT) // strict by default
                    .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                    .build());

    @Override
    public JsonNode buildParametersSchema(List<ToolInvocationParamInfo> specs) {
        final ObjectNode root = mapper.createObjectNode()
                .put("$schema", "https://json-schema.org/draft/2020-12/schema")
                .put("type", "object");
        final ObjectNode properties = root.putObject("properties");
        final ArrayNode required = root.putArray("required");

        for (ToolInvocationParamInfo paramInfo : specs) {
            properties.set(paramInfo.name(), safeGenerate(paramInfo.type()));
            if (paramInfo.required()) {
                required.add(paramInfo.name());
            }
        }
        if (required.size() == 0) {
            root.remove("required");
        }
        root.put("additionalProperties", false);
        return root;
    }


    @Override
    public JsonNode buildReturnSchema(Method method) {
        final Class<?> returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            final ObjectNode node = mapper.createObjectNode();
            node.put("$schema", "https://json-schema.org/draft/2020-12/schema");
            node.put("type", "null");
            return node;
        }
        final Type genericReturnType = method.getGenericReturnType();
        Type target = genericReturnType;

        if (genericReturnType instanceof ParameterizedType pt
                && pt.getRawType() instanceof Class<?> raw
                && TextAndStructured.class.isAssignableFrom(raw)) {
            final Type[] args = pt.getActualTypeArguments();
            if (args != null && args.length == 1) {
                target = args[0];
            }
        }
        final JsonNode schema = safeGenerate(target);
        if (schema instanceof ObjectNode on && !on.has("$schema")) {
            on.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        }
        return schema;
    }

    public JsonNode safeGenerate(Type t) {
        try {
            return generator.generateSchema(t);
        } catch (Throwable ex) {
            log.warn("Could not generate schema", ex);
            ObjectNode any = mapper.createObjectNode();
            any.put("type", "object");
            any.put("additionalProperties", true);
            return any;
        }
    }
}
