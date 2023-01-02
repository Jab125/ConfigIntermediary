package com.jab125.configintermediary.api.value;

import java.util.Map;

@SuppressWarnings("unused")
public abstract class TransitiveConfigValue extends ConfigValue {
    public abstract ConfigValue get(String name);

    public abstract void set(String name, Object value);

    public abstract Map<String, ConfigValue> getAll();
}
