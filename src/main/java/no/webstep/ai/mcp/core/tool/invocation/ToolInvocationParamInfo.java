package no.webstep.ai.mcp.core.tool.invocation;

import java.lang.reflect.Type;

public record ToolInvocationParamInfo(String name, Type type, boolean required) {
}
