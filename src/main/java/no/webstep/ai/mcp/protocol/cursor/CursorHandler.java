package no.webstep.ai.mcp.protocol.cursor;

import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.McpToolProviderRegistry;
import no.webstep.ai.mcp.exception.InvalidCursorException;

import java.util.Base64;

@RequiredArgsConstructor
public class CursorHandler {

    private final McpToolProviderRegistry mcpToolProviderRegistry;


    public int decodeCursor(String cursorString) {
        if (cursorString == null || cursorString.isBlank()) {
            return 0;
        }
        try {
            final int cursor = Integer.parseInt(new String(Base64.getUrlDecoder().decode(cursorString)));
            if (cursor < 0) {
                throw new InvalidCursorException("Negative cursors are not possible");
            }
            return cursor;
        } catch (Exception exception) {
            throw new InvalidCursorException("The cursor could not be base64 decoded", exception);
        }
    }

    public String nextCursor(int start, int limit) {
        final int total = mcpToolProviderRegistry.size();
        final int end = Math.min(start + Math.max(1, limit), total);
        return (end < total) ? encodeCursor(end) : null;
    }

    private static String encodeCursor(int index) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(Integer.toString(index).getBytes());
    }
}
