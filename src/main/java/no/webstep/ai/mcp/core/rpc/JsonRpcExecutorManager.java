package no.webstep.ai.mcp.core.rpc;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.props.McpConfig;
import no.webstep.internals.NamedThreadFactory;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


@Slf4j
public class JsonRpcExecutorManager {

    private final ExecutorService executorService;
    private final McpConfig mcpConfig;

    private final Duration minTimeout;
    private final Duration maxTimeout;
    private final Duration defaultTimeout;

    public JsonRpcExecutorManager(McpConfig mcpConfig) {
        this.mcpConfig = mcpConfig;
        if (mcpConfig.get(McpRpcKeys.USE_VIRTUAL_THREADS)) {
            log.info("Using virtual threads");
            executorService = createVirtualExecutor();
        } else {
            log.info("Using cpu bound threads");
            executorService = createCpuBoundExecutor(mcpConfig.get(McpRpcKeys.MAX_THREAD_COUNT),
                    mcpConfig.get(McpRpcKeys.MAX_QUEUE_SIZE));
        }

        minTimeout = Duration.ofSeconds(mcpConfig.get(McpRpcKeys.MIN_TIMEOUT_SECONDS));
        maxTimeout = Duration.ofSeconds(mcpConfig.get(McpRpcKeys.MAX_TIMEOUT_SECONDS));
        defaultTimeout = Duration.ofSeconds(mcpConfig.get(McpRpcKeys.DEFAULT_TIMEOUT_SECONDS));
        if (maxTimeout.compareTo(minTimeout) < 0) {
            throw new IllegalArgumentException("maxTimeout must be >= than minTimeout");
        }
    }

    public <T> Future<T> run(Callable<T> item, Duration timeoutHint) throws InterruptedException {
        return run(List.of(item), timeoutHint).get(0);
    }


    public <T> List<Future<T>> run(List<? extends Callable<T>> items,
                                   Duration timeoutHint) throws InterruptedException {
        return executorService.invokeAll(
                items,
                Math.clamp(Objects.requireNonNullElse(timeoutHint, defaultTimeout).toMillis(),
                        minTimeout.toMillis(),
                        maxTimeout.toMillis()),
                TimeUnit.MILLISECONDS
        );
    }


    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }


    public ExecutorService createVirtualExecutor() {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("mcp-jsonrpc-io-batch-", 0).factory());
    }


    public ExecutorService createCpuBoundExecutor(int maxThreads, int queSize) {
        return new ThreadPoolExecutor(
                maxThreads, maxThreads,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queSize),
                new NamedThreadFactory("mcp-jsonrpc-cpu-batch-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
