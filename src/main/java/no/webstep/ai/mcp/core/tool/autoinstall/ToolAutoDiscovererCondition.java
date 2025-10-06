package no.webstep.ai.mcp.core.tool.autoinstall;

import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.McpKeys;
import no.webstep.ai.mcp.props.conditional.ConditionOnConfigBoolean;


public class ToolAutoDiscovererCondition extends ConditionOnConfigBoolean {

    @Override
    public ConfigKey<Boolean> getKey() {
        return McpKeys.TOOL_AUTO_DISCOVERER;
    }
}
