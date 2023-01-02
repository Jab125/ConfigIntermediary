package com.jab125.configintermediary.compat;

import com.jab125.configintermediary.ConfigIntermediary;
import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;
import com.jab125.configintermediary.api.value.*;
import com.jab125.configintermediary.mixin.owoconfig.ConfigWrapperAccessor;
import com.jab125.configintermediary.util.ReflectionUtils;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.NumberReflection;
import net.minecraft.util.Identifier;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OwoConfigCompat {
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

    public static <T> Config register(ConfigWrapper<T> wrapper, Class<T> configClass) {
        var builder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.ConfigDelegate() {

            @Override
            public void save() {
                wrapper.save();
            }

            @Override
            public void load() {
                wrapper.load();
            }

            @Override
            void onLoad(LoadEvent loadEvent) {
                // TODO
            }

            @Override
            void onSave(SaveEvent saveEvent) {
                // TODO
            }

            @Override
            String getId() {
                return wrapper.name();
            }

            @Override
            public Object getAsDefault() {
                try {
                    return configClass.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object get() {
                var d =  ((ConfigWrapperAccessor) wrapper).getInstance();
                System.out.println("d: " + d.getClass());
                return d;
            }

            @Override
            public void set(Object val) {
                ((ConfigWrapperAccessor) wrapper).setInstance(val);
            }

            @Override
            public Class<?> getType() {
                return configClass;
            }
        });
        wrapper.allOptions().forEach(((key, option) -> {
            var l = createConfigValue(option);
            if (l == TRANSITIVE) {
                l = createNestedObjectBased(key.name(), option.clazz(), ((ConfigWrapperAccessor)wrapper).getInstance());
            }
            builder.set(key.name(), l);
        }));
        var l = builder.build();
        ConfigIntermediary.configs.put(wrapper.name(), l);
        return l;
    }

    @SuppressWarnings("unchecked")
    private static <T> ConfigValue createConfigValue(Option<T> option) {
       // return createConfigValue(isTransitive(option), option.clazz(), option::value, (s) -> option.set((T) s));
        boolean transitive = isTransitive(option);
        Class<?> type = option.clazz();
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
                        return option.value();
                    }
                    @Override
                    public Object getDefaultValue() {
                        return option.defaultValue();
                    }

                    @Override
                    public void resetToDefault() {
                        set(getDefaultValue());
                    }

                    @Override
                    public void set(Object value) {
                        option.set((T) value);
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
                        return option.value();
                    }

                    @Override
                    public void set(Object value) {
                        option.set((T) value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                    @Override
                    public Object getDefaultValue() {
                        return option.defaultValue();
                    }

                    @Override
                    public void resetToDefault() {
                        set(getDefaultValue());
                    }
                };
            } else {
                return new ObjectConfigValue() {
                    @Override
                    public Object get() {
                        return option.value();
                    }

                    @Override
                    public void set(Object value) {
                        option.set((T) value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }

                    private final Object default_ = get();
                    @Override
                    public Object getDefaultValue() {
                        return default_;
                    }

                    @Override
                    public void resetToDefault() {
                        set(getDefaultValue());
                    }
                };
            }
        } else {
            return TRANSITIVE;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean isTransitive(Option<?> option) {
        Class<?> clazz = option.clazz();
        if (clazz.isArray()) return false;
        if (NumberReflection.isNumberType(clazz)) return false;
        else if (String.class.isAssignableFrom(option.clazz())) return false;
        else if (Identifier.class.isAssignableFrom(option.clazz())) return false;
        else if (Enum.class.isAssignableFrom(option.clazz())) return false;
        else return !List.class.isAssignableFrom(option.clazz());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean isTransitive(Field field) {
        Class<?> clazz = field.getType();
        if (clazz.isArray()) return false;
        if (NumberReflection.isNumberType(clazz)) return false;
        else if (String.class.isAssignableFrom(field.getType())) return false;
        else if (Identifier.class.isAssignableFrom(field.getType())) return false;
        else if (Enum.class.isAssignableFrom(field.getType())) return false;
        else return !List.class.isAssignableFrom(field.getType());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean isTransitive(Class<?> clazz) {
        if (clazz.isArray()) return false;
        if (NumberReflection.isNumberType(clazz)) return false;
        else if (String.class.isAssignableFrom(clazz)) return false;
        else if (Identifier.class.isAssignableFrom(clazz)) return false;
        else if (Enum.class.isAssignableFrom(clazz)) return false;
        else return !List.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ConfigValue createConfigValue(boolean transitive, Class<?> type, Supplier<?> getter, Consumer setter) {
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
                    private final Object default_ = get();
                    @Override
                    public Object getDefaultValue() {
                        return default_;
                    }

                    @Override
                    public void resetToDefault() {
                        set(getDefaultValue());
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
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }

                    private final Object default_ = get();
                    @Override
                    public Object getDefaultValue() {
                        return default_;
                    }

                    @Override
                    public void resetToDefault() {
                        set(getDefaultValue());
                    }
                };
            } else {
                return new ObjectConfigValue() {
                    @Override
                    public Object get() {
                        return getter.get();
                    }

                    @Override
                    public void set(Object value) {
                        setter.accept(value);
                    }

                    @Override
                    public Class<?> getType() {
                        return type;
                    }

                    private final Object default_ = get();
                    @Override
                    public Object getDefaultValue() {
                        return default_;
                    }

                    @Override
                    public void resetToDefault() {
                        set(getDefaultValue());
                    }
                };
            }
        } else {
            return TRANSITIVE;
        }
    }

    private static TransitiveConfigValue createNestedObjectBased(String objName, Class<?> type, Object parent) {
        var builder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.Delegate() {
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

            private final Object f = get();
            @Override
            public Object getAsDefault() {
                return f;
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
