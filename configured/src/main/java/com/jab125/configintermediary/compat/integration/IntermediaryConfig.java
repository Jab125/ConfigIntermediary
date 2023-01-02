package com.jab125.configintermediary.compat.integration;

import com.jab125.configintermediary.api.value.Config;
import com.jab125.configintermediary.api.value.ConfigValue;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.util.ConfigHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class IntermediaryConfig implements IModConfig {
    private final Config config;

    //private final List<>
    public IntermediaryConfig(Config config) {
        this.config = config;
    }

    @Override
    public void update(IConfigEntry iConfigEntry) {
        Set<IConfigValue<?>> changedValues = ConfigHelper.getChangedValues(iConfigEntry);
        if (!changedValues.isEmpty()) {
            for (IConfigValue<?> changedValue : changedValues) {
                if (changedValue instanceof IntermediaryValue<?> intermediaryValue) {
                    intermediaryValue.configValue.set(intermediaryValue.get());
                }
            }
            config.save();
        }
    }

    @Override
    public IConfigEntry getRoot() {
        return new IntermediaryFolderEntry(config);
    }

    @Override
    public ConfigType getType() {
        return ConfigType.UNIVERSAL;
    }

    @Override
    public String getFileName() {
        return FabricLoader.getInstance().getModContainer(getModId()).isPresent() ? FabricLoader.getInstance().getModContainer(getModId()).get().getMetadata().getName() : getModId();
    }

    @Override
    public boolean isChanged() {
        return !ConfigHelper.gatherAllConfigValues(this).stream().anyMatch(a -> a.get().equals(a.getDefault()));
    }

    @Override
    public String getModId() {
        return config.getId();
    }

    @Override
    public void loadWorldConfig(Path path, Consumer<IModConfig> consumer) throws IOException {
        // no
    }

    @Override
    public void restoreDefaults() {
        for (IConfigValue<?> gatherAllConfigValue : ConfigHelper.gatherAllConfigValues(this)) {
            if (gatherAllConfigValue instanceof IntermediaryValue<?> intermediaryValue) {
                gatherAllConfigValue.restore();
                intermediaryValue.apply();
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}