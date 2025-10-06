package no.webstep.ai.mcp.core.tool.datatypes;

public enum ContentStyle {
    /**
     * CharSequence or TextContent
     */
    TEXT,
    /**
     * POJO/JsonNode only (no primary text)
     */
    STRUCTURED,
    /**
     * POJO/JsonNode and CharSequence; both emitted
     */
    TEXT_AND_STRUCTURED,
    /**
     * ImageBinary or ImageContent
     */
    IMAGE,
    /**
     * AudioBinary or AudioContent
     */
    AUDIO,
    /**
     * LinkTarget or ResourceLinkContent
     */
    RESOURCE_LINK,
    /**
     * EmbeddedTextResource or EmbeddedResourceContent
     */
    EMBEDDED_RESOURCE
}
