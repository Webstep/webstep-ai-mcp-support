package no.webstep.ai.mcp.core.schema.jackson;

import no.webstep.ai.mcp.McpKeys;
import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.props.conditional.ConditionOnConfigBoolean;


public class JacksonCustomizerCondition extends ConditionOnConfigBoolean {

    @Override
    public ConfigKey<Boolean> getKey() {
        return McpKeys.JACKSON_CUSTOMIZER;
    }
}
