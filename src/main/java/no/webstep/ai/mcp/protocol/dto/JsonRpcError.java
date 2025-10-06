package no.webstep.ai.mcp.protocol.dto;

public record JsonRpcError(int code, String message, Object data) {}
