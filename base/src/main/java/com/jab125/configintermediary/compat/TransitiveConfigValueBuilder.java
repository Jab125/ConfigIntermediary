package com.jab125.configintermediary.compat;

import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;
import com.jab125.configintermediary.api.value.Config;
import com.jab125.configintermediary.api.value.ConfigValue;
import com.jab125.configintermediary.api.value.TransitiveConfigValue;

import java.util.HashMap;
import java.util.Map;

public class TransitiveConfigValueBuilder {
    final Delegate delegate;
    final Map<String, ConfigValue> values = new HashMap<>();

    private TransitiveConfigValueBuilder(Delegate delegate) {
        this.delegate = delegate;
    }

    public static TransitiveConfigValueBuilder builder(Delegate delegate) {
        return new TransitiveConfigValueBuilder(delegate);
    }

    public static ConfigBuilder builder(ConfigDelegate delegate) {
        return new ConfigBuilder(delegate);
    }

    public void set(String name, ConfigValue value) {
        values.put(name, value);
    }

    public TransitiveConfigValue build() {
        return new TransitiveConfigValue() {
            @Override
            public ConfigValue get(String name) {
                return values.get(name);
            }

            @Override
            public void set(String name, Object value) {
                values.get(name).set(value);
            }

            @Override
            public Object get() {
                return delegate.get();
            }

            @Override
            public Object getDefaultValue() {
                return delegate.getAsDefault();
            }

            @Override
            public void resetToDefault() {
                delegate.set(getDefaultValue());
            }

            @Override
            public Map<String,ConfigValue> getAll() {
                return Map.copyOf(values);
            }

            @Override
            public void set(Object value) {
                delegate.set(value);
            }

            @Override
            public Class<?> getType() {
                return delegate.getType();
            }

            @Override
            public String toString() {
                return values.toString();
            }
        };
    }

    public static abstract class Delegate {
        public abstract Object getAsDefault();

        public abstract Object get();

        public abstract void set(Object val);

        public abstract Class<?> getType();
    }

    public static abstract class ConfigDelegate extends Delegate {
        Config futureConfig;

        public abstract void save();

        public abstract void load();

        abstract void onLoad(LoadEvent loadEvent);

        abstract void onSave(SaveEvent saveEvent);

        protected Config getFutureConfig() {
            return futureConfig;
        }

        abstract String getId();

        void resetConfig() {
            set(getAsDefault());
        }
    }

    public static class ConfigBuilder extends TransitiveConfigValueBuilder {
        private ConfigBuilder(ConfigDelegate delegate) {
            super(delegate);
        }

        @Override
        public Config build() {
            var l = new Config() {
                @Override
                public ConfigValue get(String name) {
                    return values.get(name);
                }

                @Override
                public void set(String name, Object value) {
                    values.get(name).set(value);
                }

                @Override
                public Map<String,ConfigValue> getAll() {
                    return Map.copyOf(values);
                }

                @Override
                public Object get() {
                    return delegate.get();
                }

                @Override
                public Object getDefaultValue() {
                    return delegate.getAsDefault();
                }

                @Override
                public void resetToDefault() {
                    ((ConfigDelegate)delegate).resetConfig();
                }

                @Override
                public void set(Object value) {
                    delegate.set(value);
                }

                @Override
                public void save() {
                    ((ConfigDelegate) delegate).save();
                }

                @Override
                public void load() {
                    ((ConfigDelegate) delegate).load();
                }

                @Override
                public void registerSaveListener(SaveEvent save) {
                    ((ConfigDelegate) delegate).onSave(save);
                }

                @Override
                public void registerLoadListener(LoadEvent load) {
                    ((ConfigDelegate) delegate).onLoad(load);
                }

                @Override
                public Class<?> getType() {
                    return delegate.getType();
                }

                @Override
                public String getId() {
                    return ((ConfigDelegate) delegate).getId();
                }

                @Override
                public String toString() {
                    return values.toString();
                }
            };
            ((ConfigDelegate) delegate).futureConfig = l;
            return l;
        }

        @Override
        public void set(String name, ConfigValue value) {
            super.set(name, value);
        }
    }
}
