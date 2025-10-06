package no.webstep.ai.mcp.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.McpArgCoercion;
import no.webstep.ai.mcp.core.McpToolService;
import no.webstep.ai.mcp.protocol.dto.InvokeRequest;
import no.webstep.ai.mcp.protocol.dto.Tool;
import no.webstep.ai.mcp.protocol.dto.ToolResult;
import no.webstep.ai.mcp.protocol.dto.content.TextContent;
import no.webstep.ai.mcp.core.McpToolProviderRegistry;
import no.webstep.ai.mcp.core.tool.invocation.StopChainThrowable;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationDetails;
import no.webstep.ai.mcp.core.tool.invocation.ToolResultMapper;
import no.webstep.internals.ExceptionStringifier;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DefaultMcpToolService implements McpToolService {

    private final McpToolProviderRegistry registry;
    private final ObjectMapper mapper;
    private final ToolResultMapper resultMapper;
    private final McpArgCoercion coercion;

    @Override
    public List<Tool> listTools(int start, int limit) {
        final List<ToolInvocationDetails> all = registry.listAll().stream()
                .sorted(Comparator.comparing(ToolInvocationDetails::name))
                .toList();

        final int end = Math.min(start + Math.max(1, limit), all.size());
        final List<ToolInvocationDetails> page = all.subList(start, end);

        return page.stream()
                .map(this::toTool)
                .toList();
    }


    @Override
    public ToolResult callTool(String toolName, InvokeRequest req) {
        final InvokeRequest request = McpRequestNormalizer.normalize(req);

        final ToolInvocationDetails toolMethod = registry.findByName(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tool: " + toolName));

        final Object[] argv = coercion.coerceArgs(toolMethod, request);
        final Object target = toolMethod.owner();

        try {
            final Object result = toolMethod.method().invoke(target, argv);
            return resultMapper.map(result, toolMethod.contentStyle());
        } catch (StopChainThrowable stop) {
            log.info("Tool '{}' requested to stop the execution plan early", toolName);
            return ToolResult.stopChain(mapper,
                    resultMapper.map(stop.payload(), toolMethod.contentStyle()),
                    stop.payload());
        } catch (Exception e) {
            log.warn("Tool '{}' failed", toolName, e);
            final TextContent text = new TextContent("ERROR: " + ExceptionStringifier.justCauses(e));
            return new ToolResult(text, true, null);
        }
    }

    private Tool toTool(ToolInvocationDetails b) {
        return new Tool(b.name(), b.description(), b.inputSchema(), b.outputSchema());
    }


}
