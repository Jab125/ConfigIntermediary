package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.value.ConfigValue;
import com.jab125.configintermediary.api.value.TransitiveConfigValue;
import com.mrcrayfish.configured.api.IConfigValue;
import net.minecraft.text.Text;

import java.util.List;

@SuppressWarnings("unchecked")
public class IntermediaryValue<T> implements IConfigValue<T> {
    protected final TransitiveConfigValue config;
    protected final String name;
    protected final ConfigValue configValue;
    protected final Object valueOnInit;
    protected final Object defaultValue;
    protected Object proposedValue;
    public IntermediaryValue(TransitiveConfigValue config, String name, ConfigValue configValue) {
        this.config = config;
        this.configValue = configValue;
        this.name = name;
        this.valueOnInit = configValue.get();
        this.defaultValue = configValue.getDefaultValue();
        set((T) valueOnInit);
    }
    @Override
    public T get() {
        return (T) this.proposedValue;
    }

    @Override
    public T getDefault() {
        return (T) this.defaultValue;
    }

    @Override
    public void set(T t) {
        this.proposedValue = t;
    }

    @Override
    public boolean isValid(T t) {
        return true; //TODO
    }

    @Override
    public boolean isDefault() {
        return this.get().equals(this.defaultValue);
    }

    @Override
    public boolean isChanged() {
        return !get().equals(valueOnInit);
    }

    @Override
    public void restore() {
        proposedValue = getDefault();
    }

    @Override
    public Text getComment() {
        if (configValue.getComment() != null)
        return configValue.getComment();
        return null;
    }

    @Override
    public String getTranslationKey() {
        return "todo." + name; // TODO
    }

    @Override
    public Text getValidationHint() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void cleanCache() {
        // nothing to clean yet
    }

    @Override
    public boolean requiresWorldRestart() {
        return false;
    }

    @Override
    public boolean requiresGameRestart() {
        return configValue.requiresGameRestart();
    }

    public static <V> V lastValue(List<V> list, V defaultValue) {
        if(list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return defaultValue;
    }

    public void apply() {
        configValue.set(proposedValue);
    }
}
