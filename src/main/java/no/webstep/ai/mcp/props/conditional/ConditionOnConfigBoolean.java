package no.webstep.ai.mcp.props.conditional;

import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.props.McpConfig;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class ConditionOnConfigBoolean implements Condition {

    public abstract ConfigKey<Boolean> getKey();

    @Override
    public boolean matches(final ConditionContext ctx, final AnnotatedTypeMetadata md) {
        final Boolean enabled = McpConfig.get(getKey(), ctx.getEnvironment());
        return Boolean.TRUE.equals(enabled);
    }
}
