package no.webstep.ai.mcp.exampletool;

import lombok.RequiredArgsConstructor;
import no.webstep.ai.mcp.core.tool.McpTool;
import no.webstep.ai.mcp.core.tool.McpToolProvider;
import no.webstep.ai.mcp.core.tool.OptionalParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DateTimeTool implements McpToolProvider {

    private final Clock clock;

    @McpTool
    public Instant nowUtc() {
        return Instant.now(clock);
    }

    @McpTool
    public LocalDate today() {
        return LocalDate.now(clock);
    }

    @McpTool(description = "This year if no year is provided")
    public boolean isLeapYear(@Nullable Integer year) {
        if (year == null || year == 0) {
            return today().isLeapYear();
        }
        return LocalDate.of(year, 1, 1).isLeapYear();
    }

    @McpTool
    public String format(ZonedDateTime isoInstant, String pattern, String zoneId) {
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of(zoneId));
        return fmt.format(isoInstant);
    }

    @McpTool
    public LocalDate addToLocalDate(LocalDate localDate, long n, ChronoUnit chronoUnit) {
        return localDate.plus(n, chronoUnit);
    }

    @McpTool
    public ZonedDateTime addToZonedDateTime(ZonedDateTime zonedDateTime, long n, ChronoUnit chronoUnit) {
        return zonedDateTime.plus(n, chronoUnit);
    }

    @McpTool(description = "Nearest second if unit is not specified")
    public ZonedDateTime truncateZonedDateTimeTo(ZonedDateTime zonedDateTime, @OptionalParam ChronoUnit chronoUnit) {
        return zonedDateTime.truncatedTo(chronoUnit == null ? ChronoUnit.SECONDS : chronoUnit);
    }
}

