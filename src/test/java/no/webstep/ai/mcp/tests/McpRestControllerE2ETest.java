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

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for the plain REST MCP controller (/mcp).
 */
@SpringBootTest(properties = {"webstep.ai.mcp.rest=true"})
@Import({DateTimeTool.class, McpConfiguration.class})
@AutoConfigureMockMvc
class McpRestControllerE2ETest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @TestConfiguration
    static class FixedClockConfig {

        // Different name + @Primary to avoid colliding with the app bean
        @Bean(name = "fixedTestClock")
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2025-10-03T12:34:56Z"), ZoneOffset.UTC);
        }
    }

    @Test
    void capabilities_returnsToolsCapability() throws Exception {
        mvc.perform(get("/mcp/capabilities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tools").exists())
                .andExpect(jsonPath("$.tools.listChanged").value(false));
    }


    @Test
    void listTools_paginates_and_respectsCursor() throws Exception {
        // First page with limit=1
        final String page1 = mvc.perform(get("/mcp/tools").param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tools", hasSize(1)))
                .andExpect(jsonPath("$.nextCursor", anyOf(notNullValue(), nullValue())))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        final JsonNode page1Json = mapper.readTree(page1);
        final String toolName1 = page1Json.path("tools").get(0).path("name").asText();
        final String nextCursor = page1Json.path("nextCursor").isNull() ? null : page1Json.path("nextCursor").asText();

        if (nextCursor != null && !nextCursor.isBlank()) {
            // Second page using nextCursor
            final String page2 = mvc.perform(get("/mcp/tools").param("cursor", nextCursor).param("limit", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tools", hasSize(1)))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            final JsonNode page2Json = mapper.readTree(page2);
            final String toolName2 = page2Json.path("tools").get(0).path("name").asText();

            // Basic sanity: second page should give a different tool than first (assuming >1 tool)
            assertNotEquals(toolName1, toolName2,
                    "Expected a different tool on the next page when limit=1");
        }
    }


    @Test
    void listTools_withInvalidCursor_returns400() throws Exception {
        mvc.perform(get("/mcp/tools").param("cursor", "!!not-base64!!"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }


    @Test
    void call_nowUtc_withEmptyBody_returnsOkResult() throws Exception {
        // Your controller allows null/empty request body for zero-arg tools
        mvc.perform(post("/mcp/tools/{name}", "datetimetool_nowUtc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError").value(false))
                .andExpect(jsonPath("$.content").exists());
    }


    @Test
    void call_format_withInvalidPattern_returnsToolError() throws Exception {
        final ObjectNode argsByName = mapper.createObjectNode()
                .put("isoInstant", "2025-10-03T12:34:56Z")
                .put("pattern", "bad[")
                .put("zoneId", "UTC");

        final ObjectNode invoke = mapper.createObjectNode().set("argsByName", argsByName);

        mvc.perform(post("/mcp/tools/{name}", "datetimetool_format")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoke.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError").value(true))
                .andExpect(jsonPath("$.content").exists());
    }


    @Test
    void call_truncateZonedDateTimeTo_omittingOptional_defaultsToSeconds() throws Exception {
        final ObjectNode argsByName = mapper.createObjectNode()
                .put("zonedDateTime", "2025-10-03T12:34:56.789Z");
        final ObjectNode invoke = mapper.createObjectNode().set("argsByName", argsByName);

        mvc.perform(post("/mcp/tools/{name}", "datetimetool_truncateZonedDateTimeTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoke.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError").value(false))
                .andExpect(jsonPath("$.content").exists());
    }


    @Test
    void call_addToLocalDate_withPositionalArgs_ok() throws Exception {
        final ObjectNode invoke = mapper.createObjectNode()
                .set("args", mapper.createArrayNode()
                        .add("2025-10-03")
                        .add(3)
                        .add("DAYS"));

        mvc.perform(post("/mcp/tools/{name}", "datetimetool_addToLocalDate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoke.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError").value(false))
                .andExpect(jsonPath("$.content").exists());
    }
}
