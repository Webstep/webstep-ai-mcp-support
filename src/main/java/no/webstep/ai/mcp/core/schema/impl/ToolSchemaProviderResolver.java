package no.webstep.ai.mcp.core.schema.impl;

import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.schema.SchemaBuilder;

@RequiredArgsConstructor
public class ToolSchemaProviderResolver {
    private final SchemaBuilderVictoolsImpl defaultImpl;


    public SchemaBuilder resolve(Class<? extends SchemaBuilder> type) {
        if (type == SchemaBuilder.DefaultProvider.class) {
            return defaultImpl;
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate " + type.getName(), e);
        }
    }
}
