package no.webstep.ai.mcp.props;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Slf4j
public final class Verifiers {
    private static final Set<String> PRINTED = ConcurrentHashMap.newKeySet();

    public static BiFunction<String, ConfigKey<String>, String> notBlank() {
        return (val, key) -> {
            if (val == null || val.isBlank()) {
                return logDefault(key.key(), key.defaultValue());
            }
            return val;
        };
    }

    public static BiFunction<Integer, ConfigKey<Integer>, Integer> clampInt(int min, int max) {
        return (val, key) -> {
            if (val == null) {
                return logDefault(key.key(), key.defaultValue());
            } else if (val < min) {
                return logClamped(key.key(), min, max, val, min);
            } else if (val > max) {
                return logClamped(key.key(), min, max, val, max);
            }
            return val;
        }
                ;
    }

    private static <T> T logDefault(@NonNull String key, @NonNull T value) {
        if (PRINTED.add(key)) {
            log.info("'{}' is not set. Using {}", key, value);
        }
        return value;
    }

    private static int logClamped(@NonNull String key, int min, int max, Integer val, int used) {
        if (PRINTED.add(key)) {
            logClamped(key, min, max, val, min);
        }
        return used;
    }

    private Verifiers() {
    }
}
