package no.webstep.ai.mcp.core.rpc;

import no.webstep.ai.mcp.props.ConfigKey;
import no.webstep.ai.mcp.McpKeys;
import no.webstep.ai.mcp.props.Verifiers;

public class McpRpcKeys {

    private static final String PREFIX = McpKeys.PREFIX;

    public static final ConfigKey<Boolean> REST = ConfigKey.builder(
                    PREFIX + "jsonrpc-rest", false)
            .withDescription("Enable JSON-RPC over REST endpoint.")
            .build();

    public static final ConfigKey<Boolean> USE_VIRTUAL_THREADS = ConfigKey.builder(
                    PREFIX + "jsonrpc-use-virtual-threads", true)
            .withDescription("Execute JSON-RPC requests on virtual threads instead of platform threads.")
            .build();

    public static final ConfigKey<Integer> MIN_TIMEOUT_SECONDS = ConfigKey.builder(
                    PREFIX + "jsonrpc-min-timeout-seconds", 1)
            .withDescription("Minimum per-request timeout in seconds. An invocation can hint of a timeout. " +
                    "It must be between min and max or be clamped.")
            .withVerify(Verifiers.clampInt(1, 3600))
            .build();

    public static final ConfigKey<Integer> MAX_TIMEOUT_SECONDS = ConfigKey.builder(
                    PREFIX + "jsonrpc-max-timeout-seconds", 240)
            .withDescription("Maximum per-request timeout in seconds. An invocation can hint of a timeout. " +
                    "It must be between min and max or be clamped.")
            .withVerify(Verifiers.clampInt(1, 3600))
            .build();

    public static final ConfigKey<Integer> DEFAULT_TIMEOUT_SECONDS = ConfigKey.builder(
                    PREFIX + "jsonrpc-default-timeout-seconds", 60)
            .withDescription("Default per-request timeout in seconds.")
            .withVerify(Verifiers.clampInt(1, 3600))
            .build();

    public static final ConfigKey<Integer> MAX_THREAD_COUNT = ConfigKey.builder(
                    PREFIX + "jsonrpc-batch-max-thread-count",
                    Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() * 2, 32)))
            .withType(Integer.class)
            .withDescription("Maximum number of worker threads for JSON-RPC batch execution. " +
                    "Only matters if not using platform threads.")
            .withVerify(Verifiers.clampInt(1, 1_000))
            .build();

    public static final ConfigKey<Integer> MAX_QUEUE_SIZE = ConfigKey.builder(
                    PREFIX + "jsonrpc-batch-max-queue-size", 1_000)
            .withType(Integer.class)
            .withDescription("Maximum number of queued JSON-RPC batch requests. " +
                    "Only matters if not using platform threads.")
            .withVerify(Verifiers.clampInt(1, 1_000_000))
            .build();

}
