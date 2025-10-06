package no.webstep.ai.mcp;

import no.webstep.ai.mcp.core.McpArgCoercion;
import no.webstep.ai.mcp.core.McpToolProviderRegistry;
import no.webstep.ai.mcp.core.impl.DefaultMcpToolService;
import no.webstep.ai.mcp.core.rpc.JsonRpcExecution;
import no.webstep.ai.mcp.core.rpc.JsonRpcExecutorManager;
import no.webstep.ai.mcp.core.rpc.JsonRpcRouter;
import no.webstep.ai.mcp.core.rpc.JsonRpcService;
import no.webstep.ai.mcp.core.rpc.handlers.InitializeHandler;
import no.webstep.ai.mcp.core.rpc.handlers.NotificationsInitializedHandler;
import no.webstep.ai.mcp.core.rpc.handlers.ToolsCallHandler;
import no.webstep.ai.mcp.core.rpc.handlers.ToolsListHandler;
import no.webstep.ai.mcp.core.schema.WebstepMcpJacksonCustomizer;
import no.webstep.ai.mcp.core.schema.impl.SchemaBuilderVictoolsImpl;
import no.webstep.ai.mcp.core.schema.impl.ToolSchemaProviderResolver;
import no.webstep.ai.mcp.core.tool.autoinstall.McpToolsAutoInstaller;
import no.webstep.ai.mcp.core.tool.invocation.OptionalParamDetector;
import no.webstep.ai.mcp.core.tool.invocation.ToolInvocationDetailsFactory;
import no.webstep.ai.mcp.core.tool.invocation.ToolResultMapper;
import no.webstep.ai.mcp.props.McpConfig;
import no.webstep.ai.mcp.protocol.rest.RestExceptionHandler;
import no.webstep.ai.mcp.protocol.cursor.CursorHandler;
import no.webstep.ai.mcp.protocol.cursor.JsonRpcProtocolHelper;
import no.webstep.ai.mcp.protocol.rest.McpRestController;
import no.webstep.ai.mcp.protocol.rpc.RpcJsonVersionEnforcer;
import no.webstep.ai.mcp.protocol.rpc.rest.McpJsonRpcController;
import no.webstep.ai.mcp.protocol.rpc.rest.McpJsonRpcHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({

        // Tool registry and invocation plumbing
        McpConfig.class,
        McpToolProviderRegistry.class,
        ToolInvocationDetailsFactory.class,
        OptionalParamDetector.class,
        ToolResultMapper.class,
        McpToolsAutoInstaller.class,

        // Schema generation
        ToolSchemaProviderResolver.class,
        SchemaBuilderVictoolsImpl.class,
        WebstepMcpJacksonCustomizer.class,

        // Router & engine
        JsonRpcRouter.class,
        JsonRpcExecution.class,
        JsonRpcExecutorManager.class,
        JsonRpcProtocolHelper.class,
        JsonRpcService.class,
        //protocol
        McpArgCoercion.class,
        JsonRpcProtocolHelper.class,
        CursorHandler.class,
        RpcJsonVersionEnforcer.class,

        // Handlers
        InitializeHandler.class,
        NotificationsInitializedHandler.class,
        ToolsListHandler.class,
        ToolsCallHandler.class,

        //rest
        McpJsonRpcHandler.class,
        RestExceptionHandler.class,

        McpRestController.class,
        McpJsonRpcController.class,


        // Core service & helpers
        DefaultMcpToolService.class,
})
public class McpConfiguration {
}