package no.webstep.ai.mcp.protocol.rest;

import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.McpKeys;
import no.webstep.ai.mcp.props.conditional.ConditionOnConfigBoolean;


public class McpRestControllerCondition extends ConditionOnConfigBoolean {

    @Override
    public ConfigKey<Boolean> getKey() {
        return McpKeys.REST;
    }
}
