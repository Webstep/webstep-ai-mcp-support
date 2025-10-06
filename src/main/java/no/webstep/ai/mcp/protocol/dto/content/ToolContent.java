package no.webstep.ai.mcp.protocol.dto.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextContent.class,        name = "text"),
    @JsonSubTypes.Type(value = ResourceLinkContent.class,name = "resource_link"),
    @JsonSubTypes.Type(value = EmbeddedResourceContent.class, name = "resource")
})
public sealed interface ToolContent permits AudioContent, EmbeddedResourceContent, ImageContent, ResourceLinkContent, TextContent {}
