package com.jab125.configintermediary.compat;

import com.jab125.configintermediary.ConfigIntermediary;
import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;
import com.jab125.configintermediary.api.value.Config;
import com.jab125.configintermediary.api.value.ConfigValue;
import com.jab125.configintermediary.api.value.ObjectConfigValue;
import com.jab125.configintermediary.mixin.modmenu.EnumConfigOptionAccessor;
import com.jab125.configintermediary.util.ReflectionUtils;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.config.ModMenuConfigManager;
import com.terraformersmc.modmenu.config.option.BooleanConfigOption;
import com.terraformersmc.modmenu.config.option.EnumConfigOption;
import com.terraformersmc.modmenu.config.option.StringSetConfigOption;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;

public class ModMenuCompat {
    public static Config register() {
        var builder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.ConfigDelegate() {
            @Override
            public void save() {
                ModMenuConfigManager.save();
            }

            @Override
            public void load() {
                ModMenuConfigManager.initializeConfig();
            }

            @Override
            public void onLoad(LoadEvent loadEvent) {
                // TODO
            }

            @Override
            public void onSave(SaveEvent saveEvent) {
                // TODO
            }

            @Override
            public Object getAsDefault() {
                return null;
            }

            @Override
            public void resetConfig() {
                for (ConfigValue value : getFutureConfig().getAll().values()) {
                    value.resetToDefault();
                }
            }

            @Override
            public Object get() {
                return null; // static
            }

            @Override
            public void set(Object val) {
                // static
            }

            @Override
            public Class<?> getType() {
                return void.class;
            }

            @Override
            public String getId() {
                return "modmenu";
            }
        });
        for (Field field : ModMenuConfig.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && (BooleanConfigOption.class.isAssignableFrom(field.getType()) || EnumConfigOption.class.isAssignableFrom(field.getType()) || StringSetConfigOption.class.isAssignableFrom(field.getType()))) {
                try {
                    builder.set(getConfigKey(field), createConfigValue(field));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        var l = builder.build();
        ConfigIntermediary.configs.put("modmenu", l);
        return l;
    }

    @SuppressWarnings({"rawtypes"})
    private static String getConfigKey(Field field) throws IllegalAccessException {
        return switch (asEnum(field.getType())) {
            case BOOLEAN -> ((BooleanConfigOption) field.get(null)).getKey();
            case ENUM -> ((EnumConfigOption) field.get(null)).getKey();
            case STRING_SET -> ((StringSetConfigOption) field.get(null)).getKey();
        };
    }

    private static ConfigValue createConfigValue(Field field) throws IllegalAccessException {
        return switch (asEnum(field.getType())) {
            case BOOLEAN -> new ObjectConfigValue() {

                @Override
                public Object get() {
                    try {
                        return ((BooleanConfigOption) field.get(null)).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                public Object getDefaultValue() {
                    return ((BooleanConfigOption)ReflectionUtils.get(null, field)).getDefaultValue();
                }

                @Override
                public void resetToDefault() {
                    set(getDefaultValue());
                }

                @Override
                public void set(Object value) {
                    try {
                        ((BooleanConfigOption) field.get(null)).setValue((boolean) value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Class<?> getType() {
                    return boolean.class;
                }
            };
            case ENUM -> new ObjectConfigValue() {

                @SuppressWarnings("rawtypes")
                @Override
                public Object get() {
                    try {
                        return ((EnumConfigOption) field.get(null)).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @SuppressWarnings("rawtypes")
                @Override
                public Object getDefaultValue() {
                    return ((EnumConfigOption) ReflectionUtils.get(null, field)).getDefaultValue();
                }

                @Override
                public void resetToDefault() {
                    set(getDefaultValue());
                }

                @SuppressWarnings({"rawtypes", "unchecked"})
                @Override
                public void set(Object value) {
                    try {
                        ((EnumConfigOption) field.get(null)).setValue((Enum) value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Class<?> getType() {
                    try {
                        return ((EnumConfigOptionAccessor) field.get(null)).getEnumClass();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Enum.class;
                }
            };
            case STRING_SET -> new ObjectConfigValue() { // TODO: implement string sets properly

                @Override
                public Object get() {
                    try {
                        return ((StringSetConfigOption) field.get(null)).getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Set.of();
                    }
                }

                @Override
                public Object getDefaultValue() {
                    return ((StringSetConfigOption) ReflectionUtils.get(null, field)).getDefaultValue();
                }

                @Override
                public void resetToDefault() {
                    set(getDefaultValue());
                }

                @SuppressWarnings("unchecked")
                @Override
                public void set(Object value) {
                    try {
                        ((StringSetConfigOption) field.get(null)).setValue((Set<String>) value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Class<?> getType() {
                    return Set/*<String>*/.class;
                }
            };
        };
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static ConfigType asEnum(Class<?> clazz) {
        return Arrays.stream(ConfigType.values()).filter(a -> a.clazz.equals(clazz)).findFirst().get();
    }

    private enum ConfigType {
        BOOLEAN(BooleanConfigOption.class),
        ENUM(EnumConfigOption.class),
        STRING_SET(StringSetConfigOption.class);
        private final Class<?> clazz;

        ConfigType(Class<?> clazz) {
            this.clazz = clazz;
        }
    }
}
