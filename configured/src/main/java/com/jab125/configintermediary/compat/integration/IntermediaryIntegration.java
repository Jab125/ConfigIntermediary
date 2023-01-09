package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.entrypoint.ConfigRegistration;
import com.jab125.configintermediary.api.value.Config;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.ClientHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jab125.configintermediary.config.IntermediaryConfiguredConfig.enabled;

public class IntermediaryIntegration implements ModInitializer, ConfigRegistration {
    private static final Map<String, Set<IModConfig>> configs = new HashMap<>();
    @Override
    public void onConfigRegistered(Config config) {
        if (FabricLoader.getInstance().isModLoaded("configured"))
        configs.put(config.getId(), Set.of(new IntermediaryConfig(config)));
    }

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("configured")) {
            Supplier<Supplier<Runnable>> a = () -> () -> () -> ClientHandler.getProviders().add(modContainer -> !enabled ? Set.of() : configs.getOrDefault(modContainer.getMetadata().getId(), Set.of()));
            a.get().get().run();
        }
    }
}
