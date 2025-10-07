# Webstep MCP Support

A Spring Boot library that exposes your Java methods as Model Context Protocol (MCP) tools over JSON-RPC 2.0. It
implements the core MCP 2.0 methods:

- `initialize`
- `tools/list`
- `tools/call`
- `notifications/initialized` (no-op)

It also provides optional REST helpers for browsing and invoking tools.

## Quick start

### 1) Add dependency

Maven:

```xml

<dependency>
    <groupId>no.webstep.ai</groupId>
    <artifactId>mcp.support</artifactId>
    <version>0.0.1</version>
</dependency>
```

### 2) Enable the configuration

Import the provided configuration into your Spring app:

```java
import no.webstep.ai.mcp.McpConfiguration;

@SpringBootApplication
@Import(McpConfiguration.class)
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

### 3) Define a tool provider

```java

@Service
public class DateTimeTool implements McpToolProvider {

    @McpTool(description = "Add n units to a ZonedDateTime")
    public ZonedDateTime addToZonedDateTime(ZonedDateTime ts, long n, ChronoUnit unit) {
        return ts.plus(n, unit);
    }
}
```

### 4) Run

```maven
mvn spring-boot:run
```

### Endpoints (off by deaults)

#### MCP JSON-RPC (primary)

`webstep.ai.mcp.jsonrpc-rest=true`

POST /mcp/jsonrpc
Accepts single or batch JSON-RPC 2.0 requests.
There is no security on the endpoint
The handler class can be used in your own controller

`no.webstep.ai.mcp.protocol.rpc.rest.McpJsonRpcHandler`

##### initialize

```
curl -s localhost:8080/mcp/jsonrpc -H 'Content-Type: application/json' -d '{
"jsonrpc": "2.0",
"id": 1,
"method": "initialize",
"params": { "protocolVersion": "2.0", "capabilities": {} }
}'
}'
```

##### List tools:

```
curl -s localhost:8080/mcp/jsonrpc -H 'Content-Type: application/json' -d '{
"jsonrpc": "2.0",
"id": 2,
"method": "tools/list",
"params": { "limit": 200 }
}'
```

##### Call a tool:

```
curl -s localhost:8080/mcp/jsonrpc -H 'Content-Type: application/json' -d '{
"jsonrpc": "2.0",
"id": 3,
"method": "tools/call",
"params": {
"name": "datetime.addToZonedDateTime",
"arguments": {
"argsByName": { "ts": "2025-10-03T12:00:00Z", "n": 5, "unit": "MINUTES" }
}
}
}'
```

### Config keys

| Key                                              | Type    | Default                                  | Description                                                                                      |
|--------------------------------------------------|---------|------------------------------------------|--------------------------------------------------------------------------------------------------|
| `webstep.ai.mcp.jackson-customizer`              | Boolean | `true`                                   | Should the server configure Spring’s default Jackson customizer.                                 |
| `webstep.ai.mcp.tool-auto-discoverer`            | Boolean | `true`                                   | Should the server install all `@McpTool` from `McpToolProvider` beans.                           |
| `webstep.ai.mcp.rest`                            | Boolean | `false`                                  | Should the server provide default REST endpoint to run tools.                                    |
| `webstep.ai.mcp.server-name`                     | String  | `Webstep-MCP-Server`                     | The reported server name. Must be non-blank.                                                     |
| `webstep.ai.mcp.server-version`                  | String  | `dev`                                    | The reported server version. Must be non-blank.                                                  |
| `webstep.ai.mcp.jsonrpc-rest`                    | Boolean | `false`                                  | Enable JSON-RPC over REST endpoint.                                                              |
| `webstep.ai.mcp.jsonrpc-use-virtual-threads`     | Boolean | `true`                                   | Execute JSON-RPC requests on virtual threads instead of platform threads.                        |
| `webstep.ai.mcp.jsonrpc-min-timeout-seconds`     | Integer | `1`                                      | Minimum per-request timeout in seconds. Values are clamped between 1 and 3600.                   |
| `webstep.ai.mcp.jsonrpc-max-timeout-seconds`     | Integer | `240`                                    | Maximum per-request timeout in seconds. Values are clamped between 1 and 3600.                   |
| `webstep.ai.mcp.jsonrpc-default-timeout-seconds` | Integer | `60`                                     | Default per-request timeout in seconds. Values are clamped between 1 and 3600.                   |
| `webstep.ai.mcp.jsonrpc-batch-max-thread-count`  | Integer | `min(max(2, availableProcessors×2), 32)` | Maximum worker threads for JSON-RPC batch execution. Only relevant if not using virtual threads. |
| `webstep.ai.mcp.jsonrpc-batch-max-queue-size`    | Integer | `1000`                                   | Maximum number of queued JSON-RPC batch requests. Only relevant if not using virtual threads.    |
