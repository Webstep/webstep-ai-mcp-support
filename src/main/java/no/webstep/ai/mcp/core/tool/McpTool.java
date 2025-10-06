package no.webstep.ai.mcp.core.tool;

import no.webstep.ai.mcp.core.schema.SchemaBuilder;
import no.webstep.ai.mcp.core.tool.invocation.OptionalParamDetector;
import no.webstep.ai.mcp.core.tool.datatypes.ContentStyle;

import java.lang.annotation.*;

/**
 * Marks a method as an MCP tool, making it discoverable and callable via the MCP protocol.
 * <p>
 * Usage notes:
 * <ul>
 *   <li>Parameters should be simple POJOs or primitive types that can be serialized to JSON.</li>
 *   <li>Optional parameters can be declared using {@code @Nullable} or annotated as optional
 *       (see {@link OptionalParamDetector}).</li>
 *   <li>Return types should be serializable to JSON. For more complex data, use wrapper DTOs.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McpTool {
    /**
     * Explicit name of the tool. If not set, the Java method name is used.
     */
    String name() default "";

    /**
     * A human-readable description of the tool. Recommended if the class/method/parameter
     * names are not self-explanatory, as this is shown to LLMs for tool selection.
     */
    String description() default "";

    /**
     * Whether to omit the provider name prefix when generating the tool identifier.
     * <p>
     * By default, the tool name is formed as {@code providerName_methodName}, where
     * {@code providerName} comes from {@link McpToolProvider#name()}.
     * For example, if {@code McpToolProvider.name()} returns {@code "mathtools"} and the method is
     * {@code add}, the full tool identifier becomes {@code mathtools_add}.
     */
    boolean omitClassName() default false;

    /**
     * Specifies how results from this tool are exposed to the model.
     * <ul>
     *   <li>{@link ContentStyle#TEXT_AND_STRUCTURED} (default) – results are returned
     *       both as plain text and as structured JSON.</li>
     *   <li>{@link ContentStyle#TEXT_ONLY} – results are returned as plain text only.</li>
     *   <li>{@link ContentStyle#STRUCTURED_ONLY} – results are returned as structured JSON only.</li>
     * </ul>
     */
    ContentStyle content() default ContentStyle.TEXT_AND_STRUCTURED;

    /**
     * If you do not wish to use the default schema generator
     */
    Class<? extends SchemaBuilder> parametersJsonSchema() default SchemaBuilder.DefaultProvider.class;

    /**
     * If you do not wish to use the default schema generator
     */
     Class<? extends SchemaBuilder> returnValueJsonSchema() default SchemaBuilder.DefaultProvider.class;





}
