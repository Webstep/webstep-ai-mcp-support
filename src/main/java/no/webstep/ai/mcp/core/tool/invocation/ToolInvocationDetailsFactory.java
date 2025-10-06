package no.webstep.ai.mcp.core.tool.invocation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.schema.SchemaBuilder;
import no.webstep.ai.mcp.core.schema.impl.ToolSchemaProviderResolver;
import no.webstep.ai.mcp.core.tool.McpTool;
import no.webstep.ai.mcp.core.tool.McpToolProvider;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class ToolInvocationDetailsFactory {

    private final ParameterNameDiscoverer nameDisc = new DefaultParameterNameDiscoverer();
    private final ToolSchemaProviderResolver toolSchemaProviderResolver;
    private final OptionalParamDetector optionalParamDetector;


    public ToolInvocationDetails getMcpToolMethod(McpToolProvider owner, Method method, McpTool annotation) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(annotation, "annotation");

        final String baseName = annotation.name().isBlank() ? method.getName() : annotation.name();
        final String toolName = annotation.omitClassName() ? baseName : owner.name() + "_" + baseName;

        if (!method.canAccess(owner)) {
            method.setAccessible(true);
        }

        final Type[] genericParamTypes = method.getGenericParameterTypes(); // preserves generics and arrays
        final String[] discovered = Optional.ofNullable(nameDisc.getParameterNames(method)).orElse(new String[0]);
        final Parameter[] reflectParams = method.getParameters();

        final List<ToolInvocationParamInfo> params = new ArrayList<>(genericParamTypes.length);
        for (int i = 0; i < genericParamTypes.length; i++) {
            final String pName =
                    (i < discovered.length && discovered[i] != null && !discovered[i].isBlank())
                            ? discovered[i]
                            : "arg" + i;

            final boolean optional = optionalParamDetector.isOptional(reflectParams[i]);
            final boolean required = !optional;

            params.add(new ToolInvocationParamInfo(
                    pName,
                    genericParamTypes[i], // <-- full Type (e.g., List<String>, ZonedDateTime[], etc.)
                    required
            ));
        }



        // Build schemas using Type-aware param metadata
        final JsonNode inputSchema = toolSchemaProviderResolver.resolve(annotation.parametersJsonSchema()).buildParametersSchema(params);
        final JsonNode outputSchema = toolSchemaProviderResolver.resolve(annotation.returnValueJsonSchema()).buildReturnSchema(method);

        return new ToolInvocationDetails(
                toolName,
                annotation.description(),
                owner,
                method,
                params.toArray(new ToolInvocationParamInfo[0]),
                inputSchema,
                outputSchema,
                annotation.content()
        );
    }


}
