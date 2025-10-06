package no.webstep.ai.mcp.protocol.rpc.rest;

import no.webstep.ai.mcp.core.rpc.McpRpcKeys;
import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.props.conditional.ConditionOnConfigBoolean;


public class McpJsonRpcRestControllerCondition extends ConditionOnConfigBoolean {

    @Override
    public ConfigKey<Boolean> getKey() {
        return McpRpcKeys.REST;
    }
}
