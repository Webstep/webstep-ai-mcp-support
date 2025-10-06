package no.webstep.ai.mcp.core.tool.autoinstall;

import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.McpToolProviderRegistry;
import no.webstep.ai.mcp.core.tool.McpToolProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;

import java.util.List;

@Conditional(ToolAutoDiscovererCondition.class)
@Slf4j
public class McpToolsAutoInstaller {

    @Autowired
    void toolCallbacks(McpToolProviderRegistry mcpToolProviderRegistry,
                       List<McpToolProvider> providers) {
        for (McpToolProvider provider : providers) {
            mcpToolProviderRegistry.install(provider);
        }
    }
}
