package com.jab125.configintermediary.config;

import com.jab125.configintermediary.ConfigIntermediary;
import com.jab125.configintermediary.api.entrypoint.ConfigRegistration;
import com.jab125.configintermediary.api.event.LoadEvent;
import com.jab125.configintermediary.api.event.SaveEvent;
import com.jab125.configintermediary.api.value.ObjectConfigValue;
import com.jab125.configintermediary.compat.TransitiveConfigValueBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class IntermediaryConfiguredConfig implements ModInitializer {
    public static boolean enabled = false;
    private static Properties properties;
    public static void load() throws IOException {
        properties = new Properties();
        if (!FabricLoader.getInstance().getConfigDir().resolve("intermediary-config/configured.properties").toFile().exists()) {
            FabricLoader.getInstance().getConfigDir().resolve("intermediary-config/configured.properties").toFile().getParentFile().mkdirs();
            FabricLoader.getInstance().getConfigDir().resolve("intermediary-config/configured.properties").toFile().createNewFile();
            properties.setProperty("enabled", String.valueOf(false));
            properties.store(Files.newBufferedWriter(FabricLoader.getInstance().getConfigDir().resolve("intermediary-config/configured.properties")), "Whether to automatically load generate configured config screens.");
        } else {
            properties.load(Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve("intermediary-config/configured.properties")));
        }
        if (properties.containsKey("enabled")) {
            try {
                enabled = Boolean.parseBoolean(properties.getProperty("enabled"));
            } catch (Exception e) {
                enabled = false;
            }
        }
    }

    public static void save() throws IOException {
        properties.store(Files.newBufferedWriter(FabricLoader.getInstance().getConfigDir().resolve("intermediary-config/configured.properties")), "Whether to automatically load generate configured config screens.");
    }

    @Override
    public void onInitialize() {
        initConfig();
    }

    private static void initConfig() {
        try {
            TransitiveConfigValueBuilder.ConfigBuilder configBuilder = TransitiveConfigValueBuilder.builder(new TransitiveConfigValueBuilder.ConfigDelegate() {
                @Override
                public void save() {
                    try {
                        IntermediaryConfiguredConfig.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void load() {
                    try {
                        IntermediaryConfiguredConfig.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoad(LoadEvent loadEvent) {

                }

                @Override
                public void onSave(SaveEvent saveEvent) {

                }

                @Override
                public String getId() {
                    return "config-intermediary-configured";
                }

                @Override
                public Object getAsDefault() {
                    return null;
                }

                @Override
                public Object get() {
                    return null;
                }

                @Override
                public void set(Object val) {

                }

                @Override
                public Class<?> getType() {
                    return void.class;
                }

                @Override
                public void resetConfig() {
                    properties.setProperty("enabled", "false");
                    enabled = false;
                }
            });
            configBuilder.set("enabled", new ObjectConfigValue() {
                @Override
                public Object get() {
                    return Boolean.parseBoolean(properties.getProperty("enabled"));
                }

                @Override
                public Object getDefaultValue() {
                    return false;
                }

                @Override
                public void resetToDefault() {
                    properties.setProperty("enabled", "false");
                }

                @Override
                public void set(Object value) {
                    properties.setProperty("enabled", String.valueOf(value));
                }

                @Override
                public Class<?> getType() {
                    return boolean.class;
                }

                @Override
                public boolean requiresGameRestart() {
                    return true;
                }

                @Override
                public Text getComment() {
                    return Text.literal("Whether to generate configuration screens via Configured.");
                }
            });
            var l = configBuilder.build();
            ConfigIntermediary.configs.put("config-intermediary-configured", l);
            FabricLoader.getInstance().getEntrypoints("config-intermediary:registration", ConfigRegistration.class).forEach(a -> a.onConfigRegistered(l));
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
