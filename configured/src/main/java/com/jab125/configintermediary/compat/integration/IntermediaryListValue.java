package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.value.ConfigValue;
import com.jab125.configintermediary.api.value.TransitiveConfigValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class IntermediaryListValue extends IntermediaryValue<List<?>> {

    protected final Function<List<?>, List<?>> converter;

    public IntermediaryListValue(TransitiveConfigValue config, String name, ConfigValue configValue) {
        super(config, name, configValue);
        this.converter = createConverter(configValue);
    }

    @Override
    public void set(List<?> value)
    {
        super.set(new ArrayList<>(value));
    }

    @Nullable
    private Function<List<?>, List<?>> createConverter(ConfigValue configValue)
    {
        List<?> original = configValue.getAs(List.class);
        if(original instanceof ArrayList)
        {
            return ArrayList::new;
        }
        else if(original instanceof LinkedList)
        {
            return LinkedList::new;
        }
        return null;
    }
}
