package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.value.ConfigValue;
import com.jab125.configintermediary.api.value.TransitiveConfigValue;
import com.mrcrayfish.configured.api.IAllowedEnums;

import java.util.HashSet;
import java.util.Set;

public class IntermediaryEnumValue<T extends Enum<T>> extends IntermediaryValue<T> implements IAllowedEnums<T> {
    public IntermediaryEnumValue(TransitiveConfigValue config, String name, ConfigValue configValue) {
        super(config, name, configValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<T> getAllowedValues()
    {
        Set<T> allowedValues = new HashSet<>();
        T[] enums = (T[]) this.valueOnInit.getClass().getEnumConstants();
        for(T e : enums)
        {
            if(enums.getClass().getComponentType().isAssignableFrom(e.getClass()))
            {
                allowedValues.add(e);
            }
        }
        return allowedValues;
    }
}
