package com.jab125.configintermediary.compat;

import com.google.common.collect.ImmutableMap;
import com.jab125.configintermediary.ConfigIntermediary;
import com.jab125.configintermediary.api.entrypoint.ConfigRegistration;
import com.jab125.configintermediary.api.value.Config;
import com.jab125.configintermediary.compat.integration.IntermediaryConfig;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.ClientHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Map;
import java.util.Set;

public class IntermediaryIntegration implements ModInitializer, ConfigRegistration {
    private Map<String, Set<IModConfig>> configs;
    @Override
    public void onConfigRegistered(Config config) {

    }

    @Override
    public void onInitialize() {
        ClientHandler.getProviders().add(modContainer -> {
            if (configs == null) {
                var l = new ImmutableMap.Builder<String, Set<IModConfig>>();
                ConfigIntermediary.configs.forEach((a, b) -> {
                    if (FabricLoader.getInstance().getModContainer(a).isEmpty()) return;
                    l.put(FabricLoader.getInstance().getModContainer(a).get().getMetadata().getId(), Set.of(new IntermediaryConfig(b)));
                });
                configs = l.build();
            }
            return configs.getOrDefault(modContainer.getMetadata().getId(), Set.of());
        });
    }
}
