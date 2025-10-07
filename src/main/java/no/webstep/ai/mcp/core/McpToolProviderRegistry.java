package no.webstep.ai.mcp.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.tool.McpTool;
import no.webstep.ai.mcp.core.tool.McpToolProvider;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationDetails;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationDetailsFactory;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class McpToolProviderRegistry {

    private final Map<String, ToolInvocationDetails> byName = new HashMap<>();
    private final ToolInvocationDetailsFactory toolInvocationDetailsFactory;

    public synchronized void install(McpToolProvider mcpToolProvider) {
        Objects.requireNonNull(mcpToolProvider, "mcpToolProvider");

        final Class<?> clazz = AopUtils.getTargetClass(mcpToolProvider);

        final List<ToolInvocationDetails> discovered = new ArrayList<>();

        for (Method m : clazz.getMethods()) {
            final McpTool annotation = m.getAnnotation(McpTool.class);
            if (annotation == null) {
                continue;
            }
            discovered.add(toolInvocationDetailsFactory.getMcpToolMethod(mcpToolProvider, m, annotation));
        }

        if (discovered.isEmpty()) {
            log.warn("No @McpTool methods found on provider {}", clazz.getName());
            return;
        }

        for (ToolInvocationDetails t : discovered) {
            final ToolInvocationDetails toolInvocationDetails = byName.putIfAbsent(t.name(), t);
            if (toolInvocationDetails != null) {
                log.warn("Duplicate tool name for {} @ {}.{}.", t.name(), t.owner().getClass().getName(), t.method().getName());
                throw new IllegalStateException("Duplicate tool invocation found: " + toolInvocationDetails);
            }
            log.info("Installed McpTool {} from provider {}: ", t.name(), mcpToolProvider.getClass().getName());
        }

    }

    public synchronized Optional<ToolInvocationDetails> findByName(String name) {
        return Optional.ofNullable(byName.get(name));
    }


    public synchronized List<ToolInvocationDetails> listAll() {
        return byName.values().stream().toList();
    }

    public int size() {
        return byName.size();
    }
}
