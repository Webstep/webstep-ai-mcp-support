package no.webstep.ai.mcp.props;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public record ConfigKey<T>(
        @NonNull String key,
        @NonNull Class<T> type,
        @NonNull T defaultValue,
        @Nullable String shortDescription,
        @Nullable BiFunction<T, ConfigKey<T>, T> verify
) {

    public static <T> ConfigKeyBuilder<T> builder(String key, T defaultValue) {
        return new ConfigKeyBuilder(key, defaultValue);
    }

    public static <T,U extends T> ConfigKeyBuilder<T> builder(String key, Class<T> type, U defaultValue) {
        return new ConfigKeyBuilder(key, type).withType(type);
    }

    public static final class ConfigKeyBuilder<T> {

        private final String key;
        private Class<T> type;
        private final T defaultValue;
        private String shortDescription;
        private BiFunction<T, ConfigKey<T>, T> verify;

        public ConfigKeyBuilder(@NonNull String key, @NonNull T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public ConfigKeyBuilder<T> withType(@Nullable Class<T> type) {
            this.type = type;
            return this;
        }

        public ConfigKeyBuilder<T> withDescription(@Nullable String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public ConfigKeyBuilder<T> withVerify(@Nullable BiFunction<T, ConfigKey<T>, T> verify) {
            this.verify = verify;
            return this;
        }

        public ConfigKey<T> build() {
            return new ConfigKey<>(key, Objects.requireNonNullElse(type, (Class<T>) defaultValue.getClass()), defaultValue, shortDescription, verify);
        }
    }
}
