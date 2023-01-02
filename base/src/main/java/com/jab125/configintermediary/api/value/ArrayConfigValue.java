package com.jab125.configintermediary.api.value;

@SuppressWarnings("unused")
public abstract class ArrayConfigValue extends ConfigValue {
    public abstract ConfigValue get(int a);

    public abstract void set(int a, Object value);

    public abstract int size();
}
