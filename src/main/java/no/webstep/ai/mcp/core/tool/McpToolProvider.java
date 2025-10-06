package no.webstep.ai.mcp.core.tool;

/**
 * Provides a logical grouping or namespace for {@link McpTool}-annotated methods.
 * <p>
 * A class implementing this interface can serve as a container for multiple tools.
 * By default, the provider's name is derived from the implementing class name,
 * lowercased, but this can be overridden if needed.
 * <p>
 * Example:
 * <pre>{@code
 * public class MathTools implements McpToolProvider {
 *
 *     @McpTool
 *     public int add(int a, int b) {
 *         return a + b;
 *     }
 * }
 *
 * // Tool name exposed: "mathtools_add"
 * }</pre>
 */
public interface McpToolProvider {

    /**
     * The provider name, used as a namespace prefix for tools in this class.
     * <p>
     * Default implementation returns the simple class name, lowercased.
     *
     * @return the provider name
     */
    default String name() {
        return getClass().getSimpleName().toLowerCase();
    }
}
