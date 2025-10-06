package no.webstep.ai.mcp.protocol.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.core.exception.InvalidParamsException;
import no.webstep.ai.mcp.core.rpc.exceptions.InvalidCursorException;
import no.webstep.ai.mcp.protocol.McpApi;
import no.webstep.internals.ExceptionStringifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.UUID;

@RestControllerAdvice(annotations = McpApi.class)
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler {

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidCursorException.class, InvalidParamsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badJson(Exception e) {
        log.debug("Invalid request", e);
        return Map.of("error", Map.of(
                        "code", HttpStatus.BAD_REQUEST.value(),
                        "message", "Invalid request",
                        "detail", ExceptionStringifier.justCauses(e)
                )
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> fallback(Exception e) {
        final String exceptionId = UUID.randomUUID().toString();
        log.warn("Unhandled exception exceptionId={}", exceptionId, e);
        return Map.of("error", Map.of(
                        "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "message", "An error occurred",
                        "exceptionId", exceptionId,
                        "detail", ExceptionStringifier.justCauses(e)
                )
        );
    }
}
