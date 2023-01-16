package com.jab125.configintermediary.compat;

import com.jab125.configintermediary.ConfigIntermediary;
import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;
import com.jab125.configintermediary.api.value.*;
import com.jab125.configintermediary.mixin.autoconfig.ConfigManagerAccessor;
import com.jab125.configintermediary.util.ReflectionUtils;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Compatibility with Auto Config (included by Cloth Config)
 */
@ApiStatus.Internal
public class AutoConfigCompat {
    /**
     * Placeholder for transitive values.
     */
    private static final ConfigValue TRANSITIVE = new ConfigValue() {
        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public void resetToDefault() {

        }

        @Override
        public void set(Object value) {

        }

        @Override
        public Class<?> getType() {
            return null;
        }
    };
    public static Logger LOGGER = LoggerFactory.getLogger("config-intermediary-auto-config-compat");

    /**
     * @param data The config
     * @return A {@link Config} object with all the config values the config contains.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static Config register(ConfigHolder<?> data) {
        if (data instanceof ConfigManager<?> configManager && configManager instanceof ConfigManagerAccessor accessor) {
            String modId = accessor.getDefinition().name();
            LOGGER.info("Found config for: " + modId);
            Object config = configManager.getConfig();
            return createObjectBased(((Supplier<Object>) () -> {
                try {
                    return configManager.getConfigClass().getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).get(), modId, config, configManager);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "UnstableApiUsage"})
    private static Config createObjectBased(Object defaultObject, String modId, @NotNull Object config, ConfigManager configManager) {

        var builder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.ConfigDelegate() {
            @Override
            public void save() {
                configManager.save();
            }

            @Override
            public void load() {
                configManager.load();
            }

            @Override
            public Object getAsDefault() {
                return defaultObject;
            }

            @Override
            public Object get() {
                return configManager.getConfig();
            }

            @Override
            public void set(Object val) {
                configManager.setConfig((ConfigData) val);
            }

            @Override
            public void onSave(SaveEvent saveEvent) {
                configManager.registerSaveListener((configHolder, configData) -> saveEvent.onSave(getFutureConfig(), configData));
            }

            @Override
            public void onLoad(LoadEvent loadEvent) {
                configManager.registerLoadListener(((configHolder, configData) -> loadEvent.onLoad(getFutureConfig(), configData)));
            }

            @Override
            public Class<?> getType() {
                return defaultObject.getClass();
            }

            @Override
            public String getId() {
                return modId;
            }

            @Override
            public String getFileName() {
                return getFileName(configManager.getSerializer());
            }

            private String getFileName(ConfigSerializer serializer) {
                try {
                    var q = serializer.getClass().getDeclaredMethod("getConfigPath");
                    q.setAccessible(true);
                    return (String) q.invoke(serializer);
                } catch (Exception e) {
                    return getId();
                }
            }
        });
        for (Field field : defaultObject.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            var l = createConfigValue(ReflectionUtils.get(defaultObject, field), isTransitive(field), field.getType(), () -> ReflectionUtils.get(config, field), (s) -> ReflectionUtils.set(config, s, field));
            if (l == TRANSITIVE) {
                l = createNestedObjectBased(field.getName(), field.getType(), config);
            }
            builder.set(field.getName(), l);
        }
        var d = builder.build();
        ConfigIntermediary.configs.put(modId, d);
        return d;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "UnstableApiUsage"})
    private static Config createObjectBased(String modId, @NotNull Object config, ConfigManager configManager) {

        var builder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.ConfigDelegate() {
            private final Supplier<Object> a = () -> {
                try {
                    return configManager.getConfigClass().getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            @Override
            public void save() {
                configManager.save();
            }

            @Override
            public void load() {
                configManager.load();
            }

            @Override
            public Object getAsDefault() {
                return a.get();
            }

            @Override
            public Object get() {
                return configManager.getConfig();
            }

            @Override
            public void set(Object val) {
                configManager.setConfig((ConfigData) val);
            }

            @Override
            public void onSave(SaveEvent saveEvent) {
                configManager.registerSaveListener((configHolder, configData) -> saveEvent.onSave(getFutureConfig(), configData));
            }

            @Override
            public void onLoad(LoadEvent loadEvent) {
                configManager.registerLoadListener(((configHolder, configData) -> loadEvent.onLoad(getFutureConfig(), configData)));
            }

            @Override
            public Class<?> getType() {
                return null;
            }

            @Override
            public String getId() {
                return modId;
            }

            @Override
            public String getFileName() {
                return getFileName(configManager.getSerializer());
            }

            private String getFileName(ConfigSerializer serializer) {
                try {
                    var q = serializer.getClass().getDeclaredMethod("getConfigPath");
                    q.setAccessible(true);
                    return (String) q.invoke(serializer);
                } catch (Exception e) {
                    return getId();
                }
            }
        });
        for (Field field : config.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            var l = createConfigValue(isTransitive(field), field.getType(), () -> ReflectionUtils.get(config, field), (s) -> ReflectionUtils.set(config, s, field));
            if (l == TRANSITIVE) {
                l = createNestedObjectBased(field.getName(), field.getType(), config);
            }
            builder.set(field.getName(), l);
        }
        var d = builder.build();
        ConfigIntermediary.configs.put(modId, d);
        return d;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ConfigValue createConfigValue(Object defaultObject, boolean transitive, Class<?> type, Supplier<?> getter, @SuppressWarnings("rawtypes") Consumer setter) {
        if (!transitive) {
            if (type.isArray()) {
                return new ArrayConfigValue() {
                    @Override
                    public ConfigValue get(int a) {
                        return createConfigValue(isTransitive(getType()), getType().componentType(), () -> Array.get(get(), a), (c) -> set(a, c));
                    }

                    @Override
                    public void set(int a, Object value) {
                        Array.set(get(), a, value);
                    }

                    @Override
                    public int size() {
                        return Array.getLength(get());
                    }

                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public Object getDefaultValue() {
                        return defaultObject;
                    }

                    @Override
                    public void resetToDefault() {
                        set(defaultObject);
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
            } else if (List.class.isAssignableFrom(type)) {
                return new ArrayConfigValue() {
                    @Override
                    public ConfigValue get(int a) {
                        return createConfigValue(((List<?>)defaultObject).get(a), isTransitive(getType()), getAs(List.class).get(a).getClass(), () -> get(a), (s) -> set(a, s));
                    }

                    @Override
                    public void set(int a, Object value) {
                        get(a).set(value);
                    }

                    @Override
                    public int size() {
                        return getAs(List.class).size();
                    }

                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public Object getDefaultValue() {
                        return defaultObject;
                    }

                    @Override
                    public void resetToDefault() {
                        set(defaultObject);
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
            } else {
                return new ObjectConfigValue() {
                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public Object getDefaultValue() {
                        return defaultObject;
                    }

                    @Override
                    public void resetToDefault() {
                        this.set(defaultObject);
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
            }
        } else {
            return TRANSITIVE;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ConfigValue createConfigValue(boolean transitive, Class<?> type, Supplier<?> getter, @SuppressWarnings("rawtypes") Consumer setter) {
        if (!transitive) {
            if (type.isArray()) {
                return new ArrayConfigValue() {
                    private final Object a = get();
                    @Override
                    public ConfigValue get(int a) {
                        return createConfigValue(isTransitive(getType()), getType().componentType(), () -> Array.get(get(), a), (c) -> set(a, c));
                    }

                    @Override
                    public void set(int a, Object value) {
                        Array.set(get(), a, value);
                    }

                    @Override
                    public int size() {
                        return Array.getLength(get());
                    }

                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public Object getDefaultValue() {
                        return a;
                    }

                    @Override
                    public void resetToDefault() {
                        set(a);
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
            } else if (List.class.isAssignableFrom(type)) {
                return new ArrayConfigValue() {
                    private final Object a = get();
                    @Override
                    public ConfigValue get(int a) {
                        return createConfigValue(isTransitive(getType()), getAs(List.class).get(a).getClass(), () -> get(a), (s) -> set(a, s));
                    }

                    @Override
                    public void set(int a, Object value) {
                        get(a).set(value);
                    }

                    @Override
                    public int size() {
                        return getAs(List.class).size();
                    }

                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public Object getDefaultValue() {
                        return a;
                    }

                    @Override
                    public void resetToDefault() {
                        set(a);
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
            } else {
                return new ObjectConfigValue() {
                    private final Object a = get();
                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public Object getDefaultValue() {
                        return a;
                    }

                    @Override
                    public void resetToDefault() {
                        this.set(a);
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
            }
        } else {
            return TRANSITIVE;
        }
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SimplifiableIfStatement"})
    private static boolean isTransitive(Field field) {
        if (field.isAnnotationPresent(ConfigEntry.Gui.TransitiveObject.class)) return true;
        if (field.isAnnotationPresent(ConfigEntry.Gui.CollapsibleObject.class)) return true;
        if (field.getType().isArray()) return false;
        if (field.getType().isPrimitive()) return false;
        if (field.getType().equals(String.class)) return false;
        if (field.getType().equals(Field.class)) return false;
        if (field.getType().getFields().length == 0) return false;
        return Arrays.stream(field.getType().getFields()).noneMatch((fieldd) -> Modifier.isStatic(fieldd.getModifiers()) || Modifier.isFinal(fieldd.getModifiers()));
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SimplifiableIfStatement"})
    private static boolean isTransitive(Class<?> type) {
        if (type.isArray()) return false;
        if (type.isPrimitive()) return false;
        if (type.equals(String.class)) return false;
        if (type.equals(Field.class)) return false;
        if (type.getFields().length == 0) return false;
        return Arrays.stream(type.getFields()).noneMatch((fieldd) -> Modifier.isStatic(fieldd.getModifiers()) || Modifier.isFinal(fieldd.getModifiers()));
    }

    private static TransitiveConfigValue createNestedObjectBased(String objName, Class<?> type, Object parent) {
        var builder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.Delegate() {
            private final Object a = ReflectionUtils.get(parent, ReflectionUtils.get(parent.getClass(), objName));
            @Override
            public Object getAsDefault() {
                return a;
            }

            @Override
            public Object get() {
                return ReflectionUtils.get(parent, ReflectionUtils.get(parent.getClass(), objName));
            }

            @Override
            public void set(Object val) {
                ReflectionUtils.set(parent, val, ReflectionUtils.get(parent.getClass(), objName));
            }

            @Override
            public Class<?> getType() {
                return type;
            }
        });
        for (Field field : builder.delegate.get().getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            var l = createConfigValue(isTransitive(field), field.getType(), () -> ReflectionUtils.get(builder.delegate.get(), field), (s) -> ReflectionUtils.set(builder.delegate.get(), s, field));
            if (l == TRANSITIVE) {
                l = createNestedObjectBased(field.getName(), field.getType(), builder.delegate.get());
            }
            builder.set(field.getName(), l);
        }
        return builder.build();
    }
}
