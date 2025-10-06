package no.webstep.ai.mcp.core.schema;

import com.fasterxml.jackson.databind.JsonNode;
import no.webstep.ai.mcp.core.tool.McpTool;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationParamInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public interface SchemaBuilder {
    JsonNode buildParametersSchema(List<ToolInvocationParamInfo> specs);

    JsonNode buildReturnSchema(Method method);
    abstract class DefaultProvider implements SchemaBuilder {

    }
}
