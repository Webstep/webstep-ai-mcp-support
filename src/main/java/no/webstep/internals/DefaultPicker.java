package no.webstep.internals;

import org.slf4j.Logger;

public class DefaultPicker {
    public static int clampedInt(int value,
                                 int min,
                                 int max,
                                 int defaultValue) {
        return clampedInt(value, min, max, defaultValue, null, null);
    }

    public static int clampedInt(int value,
                                 int min,
                                 int max,
                                 int defaultValue,
                                 String propertyKey,
                                 Logger log) {
        if (value < min || value > max) {
            if (log != null) {
                log.warn("'{}' ({}) is < {} or > {} (or not set). Defaulting to {}.",
                        propertyKey, value, min, max, defaultValue);
            }
            return defaultValue;
        }
        return value;
    }
}
