package no.webstep.ai.mcp.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.webstep.ai.mcp.McpConfiguration;
import no.webstep.ai.mcp.exampletool.DateTimeTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end JSON-RPC tests against the MCP server using the DateTimeTool.
 */
@SpringBootTest(properties = {"webstep.ai.mcp.jsonrpc-rest=true"})
@Import({DateTimeTool.class, McpConfiguration.class})
@AutoConfigureMockMvc
class McpJsonRpcE2ETest {

    public static final String PATH = "/mcp/jsonrpc";
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock clock() {
            return Clock.fixed(Instant.parse("2025-10-03T12:34:56Z"), ZoneOffset.UTC);
        }
    }

    @Test
    void initialize_returnsServerInfoAndCapabilities() throws Exception {
        final ObjectNode params = mapper.createObjectNode()
                .put("protocolVersion", "2.0")
                .set("capabilities", mapper.createObjectNode());

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rpc("1", "initialize", params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.result.protocolVersion").value("2.0")) // server-supported version
                .andExpect(jsonPath("$.result.capabilities.tools").exists())
                .andExpect(jsonPath("$.result.serverInfo.name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.result.serverInfo.version", not(emptyOrNullString())));
    }


    @Test
    void notifications_initialized_returns204NoContent() throws Exception {
        final ObjectNode body = mapper.createObjectNode();
        body.put("jsonrpc", "2.0");
        body.put("method", "notifications/initialized");

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void tools_list_returnsToolsAndNextCursorMaybeNull() throws Exception {
        final ObjectNode params = mapper.createObjectNode()
                .put("limit", 200);

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rpc("2", "tools/list", params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.result.tools", isA(java.util.List.class)))
                .andExpect(jsonPath("$.result.tools.length()", greaterThan(0)))
                .andExpect(jsonPath("$.result.tools[*].name", hasItem("datetimetool_nowUtc")));
    }

    @Test
    void tools_call_missingParam_yieldsErrorCode() throws Exception {
        final ObjectNode args = mapper.createObjectNode()
                .put("isoInstant", "2025-10-03T12:34:56Z")
                .put("pattern", "yyyy-MM-dd HH:mm:ss");

        final ObjectNode params = mapper.createObjectNode()
                .put("name", "datetimetool_format")
                .set("arguments", args);

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rpc("3", "tools/call", params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value("3"))
                // JSON-RPC succeeded, but tool returned an isError payload
                .andExpect(jsonPath("$.error.code").value(-32602));
    }

    @Test
    void tools_call_nowUtc_ok() throws Exception {
        final ObjectNode args = mapper.createObjectNode(); // no args

        final ObjectNode params = mapper.createObjectNode()
                .put("name", "datetimetool_nowUtc")
                .set("arguments", args);

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rpc("4", "tools/call", params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(jsonPath("$.result.isError").value(false))
                .andExpect(jsonPath("$.result.content").exists());
    }

    @Test
    void callFormatTool_withInvalidPattern_returnsToolError() throws Exception {
        final String badPatternRequest = """
                {
                  "jsonrpc": "2.0",
                  "id": "bad-pattern-test",
                  "method": "tools/call",
                  "params": {
                    "name": "datetimetool_format",
                    "arguments": {
                      "argsByName":{
                          "isoInstant": "2025-10-03T12:34:56Z",
                          "pattern": "bad[",
                          "zoneId": "UTC"
                      }
                    }
                  }
                }
                """;

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPatternRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value("bad-pattern-test"))
                .andExpect(jsonPath("$.result.isError").value(true))
                .andExpect(jsonPath("$.result.content[0].text").exists());
    }

    @Test
    void tools_call_truncateZonedDateTimeTo_omittingOptionalUnit_defaultsToSeconds() throws Exception {
        final ObjectNode argsByName = mapper.createObjectNode()
                .put("zonedDateTime", "2025-10-03T12:34:56.789Z");

        final ObjectNode arguments = mapper.createObjectNode()
                .set("argsByName", argsByName);

        final ObjectNode params = mapper.createObjectNode()
                .put("name", "datetimetool_truncateZonedDateTimeTo")
                .set("arguments", arguments);

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rpc("5", "tools/call", params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.id").value("5"))
                .andExpect(jsonPath("$.result.isError").value(false))
                .andExpect(jsonPath("$.result.content").exists());
    }

    @Test
    void batch_tools_call_acceptsPerItemTimeoutHint() throws Exception {
        final var batch = mapper.createArrayNode();

        // First request with timeout hint
        final ObjectNode params1 = mapper.createObjectNode();
        params1.put("name", "datetimetool_nowUtc");
        params1.set("arguments", mapper.createObjectNode());
        params1.set("options", mapper.createObjectNode().put("timeoutMs", 123));

        final ObjectNode req1 = mapper.createObjectNode();
        req1.put("jsonrpc", "2.0");
        req1.put("id", "b1");
        req1.put("method", "tools/call");
        req1.set("params", params1);

        // Second request without timeout
        final ObjectNode params2 = mapper.createObjectNode();
        params2.put("name", "datetimetool_nowUtc");
        params2.set("arguments", mapper.createObjectNode());

        final ObjectNode req2 = mapper.createObjectNode();
        req2.put("jsonrpc", "2.0");
        req2.put("id", "b2");
        req2.put("method", "tools/call");
        req2.set("params", params2);

        batch.add(req1);
        batch.add(req2);

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(batch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jsonrpc").value("2.0"))
                .andExpect(jsonPath("$[0].id").value("b1"))
                .andExpect(jsonPath("$[1].jsonrpc").value("2.0"))
                .andExpect(jsonPath("$[1].id").value("b2"));
    }


    private String rpc(String id, String method, JsonNode params) throws Exception {
        ObjectNode root = mapper.createObjectNode();
        root.put("jsonrpc", "2.0");
        root.put("id", id);
        root.put("method", method);
        if (params != null) root.set("params", params);
        // Ensure a stable charset when converting to bytes/strings
        return root.toString();
    }
}
