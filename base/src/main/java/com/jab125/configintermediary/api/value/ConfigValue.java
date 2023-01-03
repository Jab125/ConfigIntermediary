package com.jab125.configintermediary.api.value;

import java.util.Arrays;

@SuppressWarnings("unused")
public abstract class ConfigValue {
    public abstract Object get();

    public abstract Object getDefaultValue();

    public abstract void resetToDefault();

    public abstract void set(Object value);

    public abstract Class<?> getType();

    @SuppressWarnings("unchecked")
    public <V> V getAs(Class<V> type) {
        return (V) get();
    }

    public boolean requiresGameRestart() {
        return false;
    }

    @Override
    public String toString() {
        if (get().getClass().isArray()) {
            var element = get();
            Class<?> eClass = element.getClass();

            if (eClass.isArray()) {
                if (eClass == byte[].class)
                    return Arrays.toString((byte[]) element);
                else if (eClass == short[].class)
                    return Arrays.toString((short[]) element);
                else if (eClass == int[].class)
                    return Arrays.toString((int[]) element);
                else if (eClass == long[].class)
                    return Arrays.toString((long[]) element);
                else if (eClass == char[].class)
                    return Arrays.toString((char[]) element);
                else if (eClass == float[].class)
                    return Arrays.toString((float[]) element);
                else if (eClass == double[].class)
                    return Arrays.toString((double[]) element);
                else if (eClass == boolean[].class)
                    return Arrays.toString((boolean[]) element);
                else if (eClass == Object[].class)
                    return Arrays.toString((Object[]) element);
            }
        }
        return get().toString();
    }

    public ConfigValue getAsConfigValue() {
        return this;
    }

    public ObjectConfigValue getAsObjectConfigValue() {
        return (ObjectConfigValue) this;
    }

    public ArrayConfigValue getAsArrayConfigValue() {
        return (ArrayConfigValue) this;
    }

    public Config getAsConfig() {
        return (Config) this;
    }

    public TransitiveConfigValue getAsTransitiveConfigValue() {
        return (TransitiveConfigValue) this;
    }
}
