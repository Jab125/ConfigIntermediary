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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IntermediaryIntegration implements ModInitializer, ConfigRegistration {
    private Map<String, Set<IModConfig>> configs = new HashMap<>();
    @Override
    public void onConfigRegistered(Config config) {
        configs.put(config.getId(), Set.of(new IntermediaryConfig(config)));
    }

    @Override
    public void onInitialize() {
        ClientHandler.getProviders().add(modContainer -> configs.getOrDefault(modContainer.getMetadata().getId(), Set.of()));
    }
}
