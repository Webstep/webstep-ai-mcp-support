package no.webstep.ai.mcp.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.webstep.ai.mcp.protocol.ProtocolStatics;
import no.webstep.internals.ExceptionStringifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.UUID;

@RestControllerAdvice(annotations = McpApi.class)
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler {

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> badJson(Exception e) {
        final String exceptionId = UUID.randomUUID().toString();
        log.debug("Invalid request exceptionId={}", exceptionId, e);
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                JsonRpcErrorCode.PARSE_ERROR.code(),
                e,
                exceptionId);
    }

    //validation error not used now but extensions might use annotations etc
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidParams(MethodArgumentNotValidException e) {
        final String exceptionId = UUID.randomUUID().toString();
        log.debug("Invalid params, exceptionId={}", exceptionId, e);
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                JsonRpcErrorCode.INVALID_PARAMS.code(),
                e,
                exceptionId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> fallback(Exception e) {
        final String exceptionId = UUID.randomUUID().toString();
        log.warn("Unhandled exception exceptionId={}", exceptionId, e);
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                JsonRpcErrorCode.INTERNAL_ERROR.code(),
                e,
                exceptionId);
    }

    @ExceptionHandler({JsonRpcServerException.class})
    public ResponseEntity<Map<String, Object>> fallThrough(JsonRpcServerException e) {
        final String exceptionId = UUID.randomUUID().toString();
        log.debug("Invalid request", e);
        return errorResponse(
                e.getOptionalHttpStatus().orElse(HttpStatus.INTERNAL_SERVER_ERROR),
                e.code().code(),
                e,
                exceptionId);

    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus httpStatus,
                                                              int code,
                                                              Exception e,
                                                              String exceptionId) {
        return ResponseEntity
                .status(httpStatus)
                .body(Map.of(
                        "jsonrpc", ProtocolStatics.VERSION,
                        "error", Map.of(
                                "code", code,
                                "message", e.getMessage(),
                                "data", Map.of(
                                        "exceptionId", exceptionId,
                                        "detail", ExceptionStringifier.justCauses(e)
                                )
                        )
                ));
    }


}
