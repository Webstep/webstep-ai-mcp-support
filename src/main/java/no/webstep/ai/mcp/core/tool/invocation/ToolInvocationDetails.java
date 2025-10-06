package no.webstep.ai.mcp.core.tool.invocation;

import com.fasterxml.jackson.databind.JsonNode;
import no.webstep.ai.mcp.core.tool.datatypes.ContentStyle;
import no.webstep.ai.mcp.core.tool.McpToolProvider;

import java.lang.reflect.Method;

public record ToolInvocationDetails(String name,
                                    String description,
                                    McpToolProvider owner,
                                    Method method,
                                    ToolInvocationParamInfo[] parameterNames,
                                    JsonNode inputSchema,
                                    JsonNode outputSchema,
                                    ContentStyle contentStyle) {

}

