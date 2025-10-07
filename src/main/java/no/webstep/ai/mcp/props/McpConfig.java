package no.webstep.ai.mcp.props;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public final class McpConfig {

    private final Binder binder;
    private final ConcurrentMap<ConfigKey<?>, Object> cache = new ConcurrentHashMap<>();

    private final static Set<ConfigKey<?>> alerted = ConcurrentHashMap.newKeySet();

    public McpConfig(final Environment env) {
        this.binder = Binder.get(env);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final ConfigKey<T> key) {
        return (T) cache.computeIfAbsent(key, k -> getFromEnv((ConfigKey<T>) k, binder));
    }

    // Stateless helpers (no cache) – useful for Conditions/startup
    public static <T> T get(final ConfigKey<T> key, final Binder binder) {
        return getFromEnv(key, binder);
    }

    public static <T> T get(final ConfigKey<T> key, final Environment env) {
        return getFromEnv(key, Binder.get(env));
    }

    private static <T> T getFromEnv(final ConfigKey<T> key, final Binder binder) {
        final T raw = binder.bind(key.key(), Bindable.of(key.type())).orElse(null);
        final T value = (raw == null) ? key.defaultValue() : raw;
        if (raw == null) {
            if (alerted.add(key)) {
                log.warn("Configuration '{}' not set, using default: {}", key.key(), value);
            }
        }
        return (key.verify() != null) ? key.verify().apply(value, key) : value;
    }

    public void invalidate(final ConfigKey<?> key) { cache.remove(key); }

    public void invalidateAll() { cache.clear(); }
}
