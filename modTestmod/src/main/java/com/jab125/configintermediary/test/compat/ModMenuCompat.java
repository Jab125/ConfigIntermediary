package com.jab125.configintermediary.test.compat;

import com.mrcrayfish.configured.integration.ModMenuConfigFactory;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.example.ExampleConfig;

import java.util.Map;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return new ModMenuConfigFactory().getProvidedConfigScreenFactories().get("config-intermediary-test-mod");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return Map.of(
                "cloth-basic-math", ((ConfigScreenFactory<?>) s -> AutoConfig.getConfigScreen(ExampleConfig.class, s).get())
        );
    }
}
