package no.webstep.ai.mcp;

import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.props.Verifiers;

public final class McpKeys {
    public static final String PREFIX = "webstep.ai.mcp.";

    public static final ConfigKey<Boolean> JACKSON_CUSTOMIZER = ConfigKey.builder(
            PREFIX + "jackson-customizer", true)
            .withDescription("Should the server configure Springs default jackson customizer.")
            .build();

    public static final ConfigKey<Boolean> TOOL_AUTO_DISCOVERER =ConfigKey.builder(
            PREFIX + "tool-auto-discoverer", true)
            .withDescription("Should server install all @McpTool from McpToolProvider beans.")
            .build();

    public static final ConfigKey<Boolean> REST = ConfigKey.builder(
            PREFIX + "rest", false)
            .withDescription("Should the server provide default rest endpoint to run tools.")
            .build();

    public static final ConfigKey<String> SERVER_NAME = ConfigKey.builder(
            PREFIX + "server-name", "Webstep-MCP-Server")
            .withVerify(Verifiers.notBlank())
            .build();

    public static final ConfigKey<String> SERVER_VERSION = ConfigKey.builder(
            PREFIX + "server-version", "dev")
            .withVerify(Verifiers.notBlank())
            .build();


    private McpKeys() {
    }
}
